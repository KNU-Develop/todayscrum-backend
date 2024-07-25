package knu.kproject;


import knu.kproject.dto.UserDto.AdminUserDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.User;
import knu.kproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;



    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetUserInfo() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        when(userService.getMyInfo()).thenReturn(user);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Test User"));

        verify(userService, times(1)).getMyInfo();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testUpdateUserInfo() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Updated User");
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        when(userService.updateMyInfo(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Updated User"));

        verify(userService, times(1)).updateMyInfo(any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        when(userService.findById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Test User"));

        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminUpdateUserInfo() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Admin Updated User");
        AdminUserDto adminUserDto = new AdminUserDto();
        adminUserDto.setName("Admin Updated User");
        when(userService.updateUserInfo(any(AdminUserDto.class))).thenReturn(user);

        mockMvc.perform(put("/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Admin Updated User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Admin Updated User"));

        verify(userService, times(1)).updateUserInfo(any(AdminUserDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("DELETE SUCCESS"));

        verify(userService, times(1)).deleteUser(anyLong());
    }
}
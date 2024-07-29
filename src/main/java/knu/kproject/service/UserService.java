package knu.kproject.service;

import knu.kproject.entity.User;
import knu.kproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User getProjectUserData(String uid) {
        return userRepository.findById(uid).orElseThrow(() -> new NoSuchElementException("User is not define"));
    }
}

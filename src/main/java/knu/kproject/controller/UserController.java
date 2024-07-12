package knu.kproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
}

/*
Table users {
  id integer [primary key, increment] uuid/auto inc
  access_table
      social_login_provider varchar [note: 'e.g., Google, GitHub, Kakao']
      social_login_id varchar [note: 'External ID from the social login provider']
      access_token varchar
      refresh_token varchar
  calendar_id integer
  created_at timestamp
}
 */
package com.voting.system.usercandidate.feignController;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import resources.user.UserListRequestDTO;
import resources.user.UserListResponseDTO;
import resources.user.UserResponseDTO;
import resources.user.ValidateResponseDTO;

@FeignClient(name = "USER-SERVICE")
public interface UserController {

  /**
   * retrieve user by id
   *
   * @param id:id of user
   * @return :return user response else return null
   */
  @GetMapping("/users/{id}")
  public UserResponseDTO getUser(@CookieValue("Authorization") String token, @PathVariable("id") long id);

  /**
   * retrieve all the users from the userId
   *
   * @param userListRequestDTO: contains the list of ids for users
   * @return :return the list of users
   */
  @PostMapping("/users/find/users")
  public UserListResponseDTO findUsers(@CookieValue("Authorization") String token, @RequestBody UserListRequestDTO userListRequestDTO);

  /**
   * checks if user exists or not
   *
   * @param userId: id for user
   * @return : return true if user found else false
   */
  @GetMapping("/users/user/{userId}")
  public boolean isUserExists(@CookieValue("Authorization") String token, @PathVariable long userId);

  /**
   * function to validate the user token
   *
   * @param token: token to be validated containing user id
   * @return :return the user response containing the role, name and id of user if user found else return null
   */
  @GetMapping(value = "/users/validate")
  public ValidateResponseDTO validateToken(@CookieValue("Authorization") String token);

}
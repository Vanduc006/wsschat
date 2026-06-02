package com.ducnv.wsschat.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ducnv.wsschat.dto.PaginationDTO;
import com.ducnv.wsschat.dto.user.CreateUserDTO;
import com.ducnv.wsschat.dto.user.UpdateUserDTO;
import com.ducnv.wsschat.dto.user.UserDTO;
import com.ducnv.wsschat.entity.User;

public interface UserService {
   // create read update delete
   public UserDTO handleCreateUser(CreateUserDTO createUserDTO);

   public PaginationDTO<UserDTO> handleGetAllUsers(Pageable userPageable);

   public UserDTO handleGetUserById(Long id);

   public UserDTO handleUpdateUserById(Long id, UpdateUserDTO updateUserDTO);

   public void handleDeteleUserById(Long id);

   public UserDTO handleGetUserByUsername(String username);

//    public void handleUpdateRefreshToken(Long id, String resfreshToken);
}

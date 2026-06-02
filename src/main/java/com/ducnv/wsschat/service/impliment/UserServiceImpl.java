package com.ducnv.wsschat.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ducnv.wsschat.dto.MetaDTO;
import com.ducnv.wsschat.dto.PaginationDTO;
import com.ducnv.wsschat.dto.user.CreateUserDTO;
import com.ducnv.wsschat.dto.user.UpdateUserDTO;
import com.ducnv.wsschat.dto.user.UserDTO;
import com.ducnv.wsschat.entity.User;
import com.ducnv.wsschat.mapper.UserMapper;
import com.ducnv.wsschat.repository.UserRepository;
import com.ducnv.wsschat.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO handleCreateUser(CreateUserDTO createUserDTO) {
        // exist username
        if (this.userRepository.existsByUsernameAndDeletedAtIsNull(createUserDTO.username())) {
            throw new IllegalArgumentException("Username already exist");
        }
        if (this.userRepository.existsByEmailAndDeletedAtIsNull(createUserDTO.email())) {
            throw new IllegalArgumentException("Email already exist");
        }

        // return this.userRepository.save(newUser);
        // User createdUser = this.userRepository.save(newUser);
        User createdUser = this.userRepository.save(this.userMapper.toEntity(createUserDTO));

        return this.userMapper.toResponseDTO(createdUser);
    }

    public PaginationDTO<UserDTO> handleGetAllUsers(Pageable userPageable) {
        Page<User> currentPage = this.userRepository.findAllByDeletedAtIsNull(userPageable);
        // pageUser.getContent()
        
        List<UserDTO> list = currentPage.getContent().stream().map((user) -> {
            return this.userMapper.toResponseDTO(user);
        }).toList();

        return PaginationDTO.<UserDTO>builder()
            .meta(
                MetaDTO.builder()
                    .page(currentPage.getNumber())
                    .pageSize(currentPage.getSize())
                    .pages(currentPage.getTotalPages())
                    .total(currentPage.getTotalElements())
                    .build()
            )
            .result(list)
            .build();
    }

    public UserDTO handleGetUserById(Long id) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() ->
            new NoSuchElementException("User not found")
        );
        
        return this.userMapper.toResponseDTO(currentUser);
    }

    public UserDTO handleUpdateUserById(Long id, UpdateUserDTO updateUserDTO) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() ->
            new NoSuchElementException("User not found")
        );

        // if (this.userRepository.existsByUsernameAndDeletedAtIsNull(updateUserDTO.user)) {
        //     throw new IllegalArgumentException("Username already exist");
        // }

        if (this.userRepository.existsByEmailAndDeletedAtIsNull(updateUserDTO.email())) {
            throw new IllegalArgumentException("Email already exist");
        }

        this.userMapper.updateEntityFromDTO(updateUserDTO, currentUser);
        User updatedUser = this.userRepository.save(currentUser);

        return this.userMapper.toResponseDTO(updatedUser);
    }

    public void handleDeteleUserById(Long id) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
            new IllegalArgumentException("User not found")
        );

        currentUser.setDeletedAt(Instant.now());
        this.userRepository.save(currentUser);
    }

    public UserDTO handleGetUserByUsername(String username) {
        User currentUser = this.userRepository.findByUsernameAndDeletedAtIsNull(username).orElseThrow(() -> 
            new IllegalArgumentException("User not found")
        );

        return this.userMapper.toResponseDTO(currentUser);
    }

    // public void handleUpdateRefreshToken(Long id, String resfreshToken);
}

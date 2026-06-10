package com.ducnv.wsschat.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ducnv.wsschat.dto.usersession.CreateUserSessionDTO;
import com.ducnv.wsschat.dto.usersession.UserSessionDTO;
import com.ducnv.wsschat.entity.User;
import com.ducnv.wsschat.entity.UserSession;
import com.ducnv.wsschat.mapper.UserSessionMapper;
import com.ducnv.wsschat.repository.UserRepository;
import com.ducnv.wsschat.repository.UserSessionRepository;
import com.ducnv.wsschat.service.UserSessionService;
import com.ducnv.wsschat.utils.SecurityUtil;

import jakarta.transaction.Transactional;

@Service
public class UserSessionServiceImpl implements UserSessionService{
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final UserSessionMapper userSessionMapper;

    public UserSessionServiceImpl(UserSessionRepository userSessionRepository, UserRepository userRepository, UserSessionMapper userSessionMapper) {
        this.userSessionRepository = userSessionRepository;
        this.userRepository = userRepository;
        this.userSessionMapper = userSessionMapper;
    }

    @Override
    public UserSessionDTO hanldeCreateSession(CreateUserSessionDTO createUserSessionDTO) {
        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() ->
        new AccessDeniedException("Can't get auth user"));

        if (!createUserSessionDTO.user().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }
        
        this.userRepository.findByIdAndDeletedAtIsNull(createUserSessionDTO.user().getId()).orElseThrow(() ->
        new NoSuchElementException("User not found"));

        UserSession createdUserSession = this.userSessionRepository.save(this.userSessionMapper.toEntity(createUserSessionDTO));
        // BeanUtils.copyProperties(ownerId, createdUserSession);
        return this.userSessionMapper.toResponseDTO(createdUserSession);
    }

    @Override
    public UserSessionDTO handleValidRefreshToken(String refreshToken) {
        UserSession currentUserSession = this.userSessionRepository.findByRefreshTokenAndDeletedAtIsNull(refreshToken).orElseThrow(() -> 
        new NoSuchElementException("User session not found"));

        return this.userSessionMapper.toResponseDTO(currentUserSession);
    }

    @Override
    @Transactional
    public void hanldeRevokeUserSessionByRefreshToken(String refreshToken) {
        UserSession currentUserSession = this.userSessionRepository.findByRefreshTokenAndDeletedAtIsNull(refreshToken).orElseThrow(() -> 
        new NoSuchElementException("User session not found"));

        currentUserSession.setDeletedAt(Instant.now());
        this.userSessionRepository.save(currentUserSession);
    }

    @Override
    @Transactional
    public void handleRevokeAllUserSessionsByUserId(Long userId) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        List<UserSession> listUserSessions = this.userSessionRepository.findByUserAndDeletedAtIsNull(currentUser);
        listUserSessions.forEach(usersession -> {
            usersession.setDeletedAt(Instant.now());
            this.userSessionRepository.save(usersession);
        });
    }

    @Override
    public List<UserSessionDTO> handleGetAllUserSessionsByUserId(Long userId) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        return this.userSessionRepository.findByUserAndDeletedAtIsNull(currentUser).stream().map(usersession -> {
            return this.userSessionMapper.toResponseDTO(usersession);
        }).toList();
    }
}

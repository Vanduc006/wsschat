package com.ducnv.wsschat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ducnv.wsschat.entity.User;
import com.ducnv.wsschat.entity.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long>{
    // public Page<UserSession> findAllByUserAndDeletedAtIsNull();
    public List<UserSession> findByUserAndDeletedAtIsNull(User user);

    public Optional<UserSession> findByRefreshTokenAndDeletedAtIsNull(String refreshToken);
}

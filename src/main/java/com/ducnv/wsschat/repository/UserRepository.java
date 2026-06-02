package com.ducnv.wsschat.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ducnv.wsschat.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {
    public Optional<User> findByIdAndDeletedAtIsNull(Long id);
    public Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    public Boolean existsByUsernameAndDeletedAtIsNull(String username);
    public Boolean existsByEmailAndDeletedAtIsNull(String email);
    public Page<User> findAllByDeletedAtIsNull(Pageable userPageable);
}

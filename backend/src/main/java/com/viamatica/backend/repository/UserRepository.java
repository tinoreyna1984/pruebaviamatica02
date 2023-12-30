package com.viamatica.backend.repository;

import com.viamatica.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // implementación con clases Custom
    @Query("SELECT u.failedAttempts FROM User u WHERE u.username = ?1")
    public int getFailedAttempts(String username);
    @Query("SELECT u.accountNonLocked FROM User u WHERE u.username = ?1")
    public boolean isAccountNotLocked(String username);

    // dashboard
    @Query("SELECT count(*) FROM User u")
    public long totalUsers();
    @Query("SELECT count(*) FROM User u WHERE u.accountNonExpired = true")
    public long totalActiveUsers();
    @Query("SELECT count(*) FROM User u WHERE u.accountNonLocked = false")
    public long totalLockedUsers();
}

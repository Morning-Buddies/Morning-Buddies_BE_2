package com.ghpg.morningbuddies.auth.member.repository;

import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);

    @Transactional
    void deleteByRefresh(String refresh);

    Boolean existsByRefresh(String refresh);
}

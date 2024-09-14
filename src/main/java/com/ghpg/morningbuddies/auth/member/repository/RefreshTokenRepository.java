package com.ghpg.morningbuddies.auth.member.repository;

import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);

    @Query("select rt from RefreshToken rt " +
            "join fetch rt.member m " +
            "left join fetch m.groups g " +
            "where rt.refresh = :refresh")
    Optional<RefreshToken> findByRefreshWithMemberAndGroup(@Param("refresh") String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByEmail(String email);
}

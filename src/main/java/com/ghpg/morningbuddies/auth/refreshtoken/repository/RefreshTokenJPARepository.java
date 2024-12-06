package com.ghpg.morningbuddies.auth.refreshtoken.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.refreshtoken.entity.RefreshToken;

public interface RefreshTokenJPARepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByEmail(String email);

	@Modifying  // UPDATE/DELETE 쿼리임을 명시
	@Query("DELETE FROM RefreshToken r WHERE r.member.id = :memberId")
	void deleteByMemberId(Long memberId);

	void deleteByRefreshToken(String refresh);

	Boolean existsByRefreshToken(String refresh);

	@Transactional
	void deleteByEmail(String email);

	Optional<RefreshToken> findByRefreshToken(String refreshToken);

	boolean existsByMemberId(Long memberId);
}

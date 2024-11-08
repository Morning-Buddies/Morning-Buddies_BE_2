package com.ghpg.morningbuddies.auth.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByEmail(String email);

	@Query("select rt from RefreshToken rt " +
		"join fetch rt.member m " +
		"left join fetch m.groups g " +
		"where rt.refreshToken = :refresh")
	Optional<RefreshToken> findByRefreshWithMemberAndGroup(@Param("refresh") String refresh);

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

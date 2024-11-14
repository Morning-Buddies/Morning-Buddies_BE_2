package com.ghpg.morningbuddies.auth.refreshtoken.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.refreshtoken.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.refreshtoken.repository.RefreshTokenJPARepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCommandServiceImpl implements RefreshTokenCommandService {

	private final RefreshTokenJPARepository refreshTokenJPARepository;
	private final MemberJPARepository memberJPARepository;

	@Override
	public void saveNewRefreshToken(String email, String refreshToken) {
		Member member = memberJPARepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		refreshTokenJPARepository.deleteByMemberId(member.getId());

		refreshTokenJPARepository.save(RefreshToken.createRefreshToken(refreshToken, email, member));

		log.info("Refresh Token saved for email: {}", email);

	}

	@Override
	public void removeRefreshToken(String refreshToken) {
		refreshTokenJPARepository.deleteByRefreshToken(refreshToken);

	}
}


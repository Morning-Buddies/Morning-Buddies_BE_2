package com.ghpg.morningbuddies.auth.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.mapper.RefreshTokenMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberRepository memberRepository;

	@Override
	public void saveNewRefreshToken(String email, String refreshToken) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		refreshTokenRepository.deleteByMemberId(member.getId());

		refreshTokenRepository.save(RefreshTokenMapper.toRefreshToken(refreshToken, email, member));

		log.info("Refresh Token saved for email: {}", email);

	}
}


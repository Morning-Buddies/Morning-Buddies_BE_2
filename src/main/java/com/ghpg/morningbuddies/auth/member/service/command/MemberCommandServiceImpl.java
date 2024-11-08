package com.ghpg.morningbuddies.auth.member.service.command;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.dto.TokenDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.MemberGroup;
import com.ghpg.morningbuddies.auth.member.mapper.MemberMapper;
import com.ghpg.morningbuddies.auth.member.mapper.RefreshTokenMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberGroupRepository;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MemberRepository memberRepository;
	private final GroupRepository groupRepository;
	private final MemberGroupRepository memberGroupRepository;
	private final MemberMapper memberMapper;
	private final RefreshTokenCommandService refreshTokenCommandService;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public MemberResponseDto.MemberInfo join(MemberRequestDto.JoinDto request) {

		Member member = memberMapper.toMember(request);

		memberRepository.save(member);

		return memberMapper.toNewMemberInfo(member);

	}

	@Override
	public Void changePassword(MemberRequestDto.PasswordDto request) {
		Member currentMember = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		currentMember.changePassword(bCryptPasswordEncoder.encode(request.getPassword()));

		return null;
	}

	@Override
	public void updateFcmToken(MemberRequestDto.FcmTokenDto request) {
		Member currentMember = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		currentMember.updateFcmToken(request.getFcmToken(), request.getDeviceId());
	}

	// 그룹 탈퇴
	@Override
	public void leaveGroup(Long groupId) {
		Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupRepository.findById(groupId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		MemberGroup memberGroup = memberGroupRepository.findByMemberAndGroup(member, group)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.MEMBER_NOT_IN_GROUP));

		if (group.getLeader().equals(member)) {

			throw new GroupException(GlobalErrorCode.LEADER_CANNOT_LEAVE_GROUP);
		}

		memberGroupRepository.delete(memberGroup);

		group.setCurrentParticipantCount(group.getCurrentParticipantCount() - 1);

	}

	@Override
	public TokenDto.ReissueDto reissue(String oldRefreshToken) {
		// 검증은 Controller에서 함

		// 1. Token에서 정보 추출
		String email = jwtUtil.getEmail(oldRefreshToken);
		String role = jwtUtil.getRole(oldRefreshToken);

		// 5. 새로운 토큰 발급
		String newAccessToken = jwtUtil.createAccessToken(email, role);
		String newRefreshToken = jwtUtil.createRefreshToken(email, role);

		// 새로운 refresh token 저장
		refreshTokenCommandService.saveNewRefreshToken(email, newRefreshToken);

		return RefreshTokenMapper.toTokenDto(newAccessToken, newRefreshToken);
	}
}

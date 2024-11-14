package com.ghpg.morningbuddies.auth.member.service.command;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.dto.TokenDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.refreshtoken.repository.RefreshTokenJPARepository;
import com.ghpg.morningbuddies.auth.refreshtoken.service.RefreshTokenCommandService;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJPARepository;
import com.ghpg.morningbuddies.domain.membergroup.repository.MemberGroupJPARepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MemberJPARepository memberJPARepository;
	private final GroupJPARepository groupJPARepository;
	private final MemberGroupJPARepository memberGroupJPARepository;
	private final RefreshTokenCommandService refreshTokenCommandService;
	private final JwtUtil jwtUtil;
	private final RefreshTokenJPARepository refreshTokenJPARepository;

	@Override
	public MemberResponseDto.MemberInfo join(MemberRequestDto.JoinDto request) {

		Member member = Member.createMember(request, bCryptPasswordEncoder);

		memberJPARepository.save(member);

		return MemberResponseDto.MemberInfo.of(member);

	}

	@Override
	public Void changePassword(MemberRequestDto.PasswordDto request) {
		Member currentMember = memberJPARepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		currentMember.changePassword(bCryptPasswordEncoder.encode(request.getPassword()));

		return null;
	}

	@Override
	public Void withdraw() {
		Member currentMember = memberJPARepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		refreshTokenJPARepository.deleteByMemberId(currentMember.getId());
		currentMember.delete();

		return null;
	}

	@Override
	public Void updateFcmToken(MemberRequestDto.FcmTokenDto request) {
		Member currentMember = memberJPARepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		currentMember.updateFcmToken(request.getFcmToken(), request.getDeviceId());
		return null;
	}

	// 그룹 탈퇴
	@Override
	public void leaveGroup(Long groupId) {
		// Member member = memberJPARepository.findByEmail(SecurityUtil.getCurrentUserEmail())
		// 	.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// Groups group = groupJPARepository.findById(groupId)
		// 	.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));
		//
		// MemberGroup memberGroup = memberGroupJPARepository.findByMemberAndGroup(member, group)
		// 	.orElseThrow(() -> new GroupException(GlobalErrorCode.MEMBER_NOT_IN_GROUP));
		//
		// if (group.getLeader().equals(member)) {
		//
		// 	throw new GroupException(GlobalErrorCode.LEADER_CANNOT_LEAVE_GROUP);
		// }
		//
		// memberGroupJPARepository.delete(memberGroup);
		//
		// group.setCurrentParticipantCount(group.getCurrentParticipantCount() - 1);

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

		return TokenDto.ReissueDto.from(newAccessToken, newRefreshToken);
	}
}

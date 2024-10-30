package com.ghpg.morningbuddies.auth.member.service.command;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.MemberGroup;
import com.ghpg.morningbuddies.auth.member.mapper.MemberMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberGroupRepository;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

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

	@Override
	public MemberResponseDto.MemberInfo join(MemberRequestDto.JoinDto request) {
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new MemberException(GlobalErrorCode.MEMBER_ALREADY_EXIST);
		}

		Member member = memberMapper.toMember(request);

		memberRepository.save(member);

		return memberMapper.toNewMemberInfo(member);

	}

	@Override
	public void changePassword(MemberRequestDto.PasswordDto request) {
		Member currentMember = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		currentMember.changePassword(bCryptPasswordEncoder.encode(request.getPassword()));

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
}

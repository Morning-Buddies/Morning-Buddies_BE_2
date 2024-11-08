package com.ghpg.morningbuddies.auth.member.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.enums.UserRole;
import com.ghpg.morningbuddies.domain.group.mapper.GroupMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberMapper {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public Member toMember(MemberRequestDto.JoinDto request) {
		return Member.builder()
			.email(request.getEmail())
			.password(bCryptPasswordEncoder.encode(request.getPassword()))
			.firstName(request.getFirstName())
			.lastName(request.getLastName())
			.preferredWakeupTime(request.getPreferredWakeupTime())
			.phoneNumber(request.getPhoneNumber())
			.role(UserRole.ROLE_USER)
			.build();
	}

	public MemberResponseDto.MemberInfo toMemberInfo(Member member) {
		return MemberResponseDto.MemberInfo.builder()
			.id(member.getId())
			.profileImage(member.getProfileImageUrl())
			.firstName(member.getFirstName())
			.lastName(member.getLastName())
			.preferredWakeupTime(member.getPreferredWakeupTime())
			.successGameCount(GroupMapper.getCountSuccessGame(member))
			.groups(null)
			.build();
	}

	// 회원 가입 시, 회원 정보를 반환하는 메서드
	public MemberResponseDto.MemberInfo toNewMemberInfo(Member member) {
		return MemberResponseDto.MemberInfo.builder()
			.id(member.getId())
			.profileImage(member.getProfileImageUrl())
			.firstName(member.getFirstName())
			.lastName(member.getLastName())
			.preferredWakeupTime(member.getPreferredWakeupTime())
			.successGameCount(0)
			.groups(null)
			.build();
	}

	// 회원 정보를 요약해서 반환하는 메서드
	public static MemberResponseDto.MemberSummaryDTO toMemberSummaryDTO(Member member) {
		return MemberResponseDto.MemberSummaryDTO.builder()
			.id(member.getId())
			.firstName(member.getFirstName())
			.lastName(member.getLastName())
			.email(member.getEmail())
			.build();
	}

}

package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.UserRole;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public void join(MemberRequestDto.JoinDto request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new MemberException(GlobalErrorCode.MEMBER_ALREADY_EXIST);
                });

        Member member = Member.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .preferredWakeupTime(request.getPreferredWakeupTime())
                .phoneNumber(request.getPhoneNumber())
                .userRole(UserRole.ROLE_USER)
                .build();

        memberRepository.save(member);


    }

    @Override
    public void changePassword(MemberRequestDto.PasswordDto request) {
        Member currentMember = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail())
                .orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

        currentMember.changePassword(bCryptPasswordEncoder.encode(request.getPassword()));

    }
}

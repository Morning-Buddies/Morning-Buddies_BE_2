package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.CustomMemberDetails;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //DB에서 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(
                        () -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND)

                );

        return new CustomMemberDetails(member);

    }
}

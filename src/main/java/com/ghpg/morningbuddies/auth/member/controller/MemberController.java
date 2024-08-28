package com.ghpg.morningbuddies.auth.member.controller;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.service.MemberQueryService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/me")
    public CommonResponse<MemberResponseDto.MemberInfo> getCookie(@CookieValue(name = "refresh") String cookieValue) {
        return CommonResponse.onSuccess(memberQueryService.getMemberInfo());
    }
}

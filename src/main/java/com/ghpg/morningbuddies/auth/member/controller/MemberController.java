package com.ghpg.morningbuddies.auth.member.controller;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.service.MemberQueryService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/me")
    public CommonResponse<MemberResponseDto.MemberInfo> getMemberInfo(@CookieValue(name = "refresh") String refreshToken) {
        if (refreshToken == null) {
            return CommonResponse.onFailure(GlobalErrorCode.REFRESH_TOKEN_REQUIRED.getCode(), GlobalErrorCode.REFRESH_TOKEN_REQUIRED.getMessage(), null);
        }
        log.info("refreshToken: {}", refreshToken);

        return CommonResponse.onSuccess(memberQueryService.getMemberInfo(refreshToken));
    }
}

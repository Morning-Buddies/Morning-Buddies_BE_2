package com.ghpg.morningbuddies.auth.member.controller;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.service.MemberCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PostMapping("/join")
    public CommonResponse<String> join(@Valid @RequestBody MemberRequestDto.JoinDto request) {

        memberCommandService.join(request);

        return CommonResponse.onSuccess("Join Success");
    }

}

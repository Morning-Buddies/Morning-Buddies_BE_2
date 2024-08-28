package com.ghpg.morningbuddies.auth.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

public class MemberRequestDto {

    @Getter
    public static class JoinDto {

        @NotEmpty
        private String email;

        @NotEmpty
        private String password;

        @NotEmpty
        private String firstName;

        @NotEmpty
        private String lastName;

        @NotNull
        private LocalTime preferredWakeupTime;

        @NotEmpty
        private String phoneNumber;

    }
}

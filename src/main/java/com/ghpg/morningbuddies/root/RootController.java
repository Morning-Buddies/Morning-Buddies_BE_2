package com.ghpg.morningbuddies.root;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.global.common.CommonResponse;

@RestController
public class RootController {

	@GetMapping("/health")
	public String healthCheck() {
		return "I'm healthy!";
	}

	@GetMapping("/")
	public CommonResponse<String> root() {
		return CommonResponse.onSuccess("Hello, Morning Buddies!");
	}

}
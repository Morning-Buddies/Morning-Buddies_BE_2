package com.ghpg.morningbuddies.root;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.global.common.CommonResponse;

@RestController
@RequestMapping("/")
public class RootController {

	@GetMapping("/health")
	public CommonResponse<String> healthCheck() {
		return CommonResponse.onSuccess("I'm Healthy!");
	}

	@GetMapping("/")
	public CommonResponse<String> root() {
		return CommonResponse.onSuccess("Hello, Morning Buddies!");
	}

}
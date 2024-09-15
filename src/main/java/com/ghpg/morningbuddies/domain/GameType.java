package com.ghpg.morningbuddies.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameType {
	PUZZLE("퍼즐"),
	QUIZ("퀴즈"),
	WORD("단어"),
	NUMBER("숫자"),
	IMAGE("이미지"),
	VIDEO("동영상"),
	VOICE("음성"),
	TEXT("텍스트"),

	;
	private String name;

}


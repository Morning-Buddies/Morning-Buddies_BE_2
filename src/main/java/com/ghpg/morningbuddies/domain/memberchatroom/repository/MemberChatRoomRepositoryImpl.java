package com.ghpg.morningbuddies.domain.memberchatroom.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ghpg.morningbuddies.domain.memberchatroom.entity.MemberChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberChatRoomRepositoryImpl implements MemberChatRoomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<MemberChatRoom> findByMemberChatRoomIdWithMemberAndChatRoom(Long memberChatRoomId) {
		return null;
	}
}

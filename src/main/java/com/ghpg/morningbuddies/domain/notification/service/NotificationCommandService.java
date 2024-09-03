package com.ghpg.morningbuddies.domain.notification.service;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.group.entity.Groups;

public interface NotificationCommandService {
	void sendJoinRequestNotification(Member leader, Member requester, Groups group);

	void sendJoinRequestAcceptedNotification(Member member, Groups group);

	void sendJoinRequestRejectedNotification(Member member, Groups group);

}

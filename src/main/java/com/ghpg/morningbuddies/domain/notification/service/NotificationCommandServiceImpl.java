package com.ghpg.morningbuddies.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.notification.Notification;
import com.ghpg.morningbuddies.domain.notification.repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

	private final NotificationRepository notificationRepository;

	private final PushNotificationService pushNotificationService;

	@Override
	public void sendJoinRequestNotification(Member leader, Member requester, Groups group) {
		String message = String.format("%s님이 %s 그룹에 가입을 요청했습니다.", requester.getFirstName(), group.getGroupName());
		Notification notification = createNotification(leader, message, group);
		try {
			pushNotificationService.sendPushNotification(leader.getFcmToken(), "새로운 가입 요청", message);

		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendJoinRequestAcceptedNotification(Member member, Groups group) {
		String message = String.format("%s님의 %s 그룹 가입 요청이 수락되었습니다.", member.getFirstName(), group.getGroupName());
		Notification notification = createNotification(member, message, group);
		try {
			pushNotificationService.sendPushNotification(member.getFcmToken(), "가입 요청 수락", message);

		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendJoinRequestRejectedNotification(Member member, Groups group) {
		String message = String.format("%s님의 %s 그룹 가입 요청이 거절되었습니다.", member.getFirstName(), group.getGroupName());
		Notification notification = createNotification(member, message, group);
		try {
			pushNotificationService.sendPushNotification(member.getFcmToken(), "가입 요청 거절", message);

		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}

	}

	private Notification createNotification(Member member, String message, Groups group) {
		Notification notification = Notification.builder()
			.member(member)
			.message(message)
			.groups(group)
			.isRead(false)
			.build();
		return notificationRepository.save(notification);
	}
}

package com.ghpg.morningbuddies.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.notification.Notification;
import com.ghpg.morningbuddies.domain.notification.repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

	private final NotificationRepository notificationRepository;
	private final PushNotificationService pushNotificationService;

	@Override
	public void sendJoinRequestNotification(Member leader, Member requester, Groups group) {
		String title = "새로운 가입 요청";
		String message = String.format("%s님이 %s 그룹에 가입을 요청했습니다.", requester.getFirstName(), group.getGroupName());

		sendNotification(leader, title, message, group);
	}

	@Override
	public void sendJoinRequestAcceptedNotification(Member member, Groups group) {
		String title = "가입 요청 수락";
		String message = String.format("%s님의 %s 그룹 가입 요청이 수락되었습니다.", member.getFirstName(), group.getGroupName());

		sendNotification(member, title, message, group);
	}

	@Override
	public void sendJoinRequestRejectedNotification(Member member, Groups group) {
		String title = "가입 요청 거절";
		String message = String.format("%s님의 %s 그룹 가입 요청이 거절되었습니다.", member.getFirstName(), group.getGroupName());

		sendNotification(member, title, message, group);

	}

	private void sendNotification(Member member, String title, String message, Groups group){
		Notification notification = createAndSaveNotification(member, title, message, group);

		if (StringUtils.hasText(member.getFcmToken())){
			try {
				pushNotificationService.sendPushNotification(member.getFcmToken(), title, message);
			} catch (FirebaseMessagingException e) {
				e.printStackTrace();
			}
		}
	}

	private Notification createAndSaveNotification(Member member, String title, String message, Groups group) {
		Notification notification = Notification.builder()
				.member(member)
				.title(title)
				.message(message)
				.groups(group)
				.isRead(false)
				.build();
		return notificationRepository.save(notification);
	}

}

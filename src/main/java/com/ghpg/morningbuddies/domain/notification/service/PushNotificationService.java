package com.ghpg.morningbuddies.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

	private final FirebaseMessaging firebaseMessaging;

	public void sendPushNotification(String token, String title, String body) throws FirebaseMessagingException {
		Message message = Message.builder()
				.setToken(token)
				.setNotification(Notification.builder()
						.setTitle(title)
						.setBody(body)
						.build())
				.putData("title", title)
				.putData("body", body)
				.build();

		try {
			String response = firebaseMessaging.send(message);
			log.info("Successfully sent message: {}", response);
		} catch (FirebaseMessagingException e) {
			log.error("Failed to send push notification. Token: {}, Title: {}", token, title, e);
			throw e;
		}
	}
}
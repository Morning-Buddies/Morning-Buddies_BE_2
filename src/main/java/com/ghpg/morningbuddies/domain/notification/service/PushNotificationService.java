package com.ghpg.morningbuddies.domain.notification.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PushNotificationService {

	public void sendPushNotification(String token, String title, String body) throws FirebaseMessagingException {
		Notification notification = Notification.builder()
			.setTitle(title)
			.setBody(body)
			.build();

		Message message = Message.builder()
			.setToken(token)
			.setNotification(notification)
			.build();

		String response = FirebaseMessaging.getInstance().send(message);

		log.info("Successfully sent message: " + response);

	}
}
package com.ghpg.morningbuddies.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}


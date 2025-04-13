package org.example.notificationmicroservice.Repository;

import org.example.notificationmicroservice.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<Notification, Integer> {
}

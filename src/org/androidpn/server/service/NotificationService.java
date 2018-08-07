package org.androidpn.server.service;

import java.util.List;

import org.androidpn.server.model.Notification;

public interface NotificationService {
	void savaNotification(Notification notification);

	List<Notification> findNotificationsByUsername(String username);

	void deleteNotification(Notification notification);
	
	void deleteNotificationByUuid(String uuid);
}

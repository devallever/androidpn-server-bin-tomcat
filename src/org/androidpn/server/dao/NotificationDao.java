package org.androidpn.server.dao;

import java.util.List;

import org.androidpn.server.model.Notification;


public interface NotificationDao {
	void savaNotification(Notification notification);
	
	List<Notification> findNotificationsByUsername(String username);
	
	void deleteNotification(Notification notification);

}

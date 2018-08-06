package org.androidpn.server.dao.hibernate;

import java.util.List;

import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.model.Notification;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class NotificationDaoHibernate extends HibernateDaoSupport implements NotificationDao{

	public void savaNotification(Notification notification) {
		//存在则更新,不存在则添加
		getHibernateTemplate().saveOrUpdate(notification);
		//清理缓存
		getHibernateTemplate().flush();
	}

	@SuppressWarnings("unchecked")
	public List<Notification> findNotificationsByUsername(String username) {
		//从 Notification类中查找
		List<Notification> notifications = getHibernateTemplate().find("from Notification where username=?",
				username);
		if (notifications != null && notifications.size() > 0) {
			return notifications;
		}
		return null;
	}

	public void deleteNotification(Notification notification) {
		getHibernateTemplate().delete(notification);
	}

}

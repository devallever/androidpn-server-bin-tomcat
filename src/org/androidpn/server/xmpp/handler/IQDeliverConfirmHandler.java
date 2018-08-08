package org.androidpn.server.xmpp.handler;

import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.impl.NotificationServiceImpl;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

public class IQDeliverConfirmHandler extends IQHandler{
	
	private NotificationService notificationService;

	private static final String NAMESPACE = "androidpn:iq:deliverconfirm";
	
	public IQDeliverConfirmHandler() {
		notificationService = ServiceLocator.getNotificationService();
	}
	
	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		
		//防止session 无效
		IQ reply = null;
		ClientSession session = sessionManager.getSession(packet.getFrom());
        if (session == null) {
            log.error("Session not found for key " + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }
        
        //避免伪造请求
        if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
        	if (IQ.Type.set.equals(packet.getType())) {
        		Element element = packet.getChildElement();
            	String uuid = element.elementText("uuid");
            	System.out.println("deleteNotificationByUuid");
            	//删除数据库中的消息
            	notificationService.deleteNotificationByUuid(uuid);
            }
        }
		return null;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

}

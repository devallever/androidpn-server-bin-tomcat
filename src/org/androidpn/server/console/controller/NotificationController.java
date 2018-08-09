/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.console.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.sun.org.apache.bcel.internal.classfile.Field;

import sun.net.www.content.audio.basic;

/** 
 * A controller class to process the notification related requests.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationController extends MultiActionController {

    private NotificationManager notificationManager;
   

    public NotificationController() {
        notificationManager = new NotificationManager();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        // mav.addObject("list", null);
        mav.setViewName("notification/form");
        return mav;
    }

    public ModelAndView send(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String broadcast = null;
        String username = null;
        String alias = null;
        String tag = null;
        String title =  null;
        String message = null;
        String uri = null;
        String imageUrl = null;
        String apiKey = Config.getString("apiKey", "");
        logger.debug("apiKey=" + apiKey);

        DiskFileItemFactory factory = new DiskFileItemFactory();
    	ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
    	List<FileItem> fileItemList = servletFileUpload.parseRequest(request);
    	for (FileItem fileItem : fileItemList) {
			if ("broadcast".equals(fileItem.getFieldName())) {
				broadcast = fileItem.getString("utf-8");
			}else if("username".equals(fileItem.getFieldName())) {
				username = fileItem.getString("utf-8");
			}else if("alias".equals(fileItem.getFieldName())) {
				alias = fileItem.getString("utf-8");
			}else if("tag".equals(fileItem.getFieldName())) {
				tag = fileItem.getString("utf-8");
			}else if("title".equals(fileItem.getFieldName())) {
				title = fileItem.getString("utf-8");
			}else if("message".equals(fileItem.getFieldName())) {
				message = fileItem.getString("utf-8");
			}else if("uri".equals(fileItem.getFieldName())) {
				uri = fileItem.getString("utf-8");
			}else if("image".equals(fileItem.getFieldName())) {
				imageUrl = uploadImage(request, fileItem);
			}
		}
    	
        if (broadcast.equals("0")) {
            notificationManager.sendBroadcast(apiKey, title, message, uri, imageUrl);
        } else if (broadcast.equals("1")) {
        	notificationManager.sendNotifcationToUser(apiKey, username, title,
                    message, uri, imageUrl, true);
		} else if (broadcast.equals("2")) {
			//false 不与上一功能混合
			notificationManager.sendNotificationByAlias(apiKey, alias, title, message, uri, imageUrl, false);
		}else if (broadcast.equals("3")){
			notificationManager.sendNotificationByTag(apiKey, tag, title, message, uri,imageUrl, false);
		}
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:notification.do");
        return mav;
    }

    @SuppressWarnings("unused")
	private String uploadImage(HttpServletRequest request, FileItem fileItem)throws Exception{
    	String uploadPath = request.getServletContext().getRealPath("/upload");
    	System.out.println("uploadPaht = " + uploadPath);
    	File uploadDir = new java.io.File(uploadPath);
    	if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
    	if (fileItem != null && fileItem.getContentType().startsWith("image")) {
    		//getName()获取上传源文件文件名
    		//后缀名
    		String suffix = fileItem.getName().substring(fileItem.getName().indexOf("."));
    		String fileName = System.currentTimeMillis()  + suffix;
    		InputStream inputStream = fileItem.getInputStream();
    		FileOutputStream fileOutputStream = new FileOutputStream(uploadPath + "/" + fileName);
    		byte[] b = new byte[1024];
    		int len = 0;
    		while ((len = inputStream.read(b)) > 0) {
    			fileOutputStream.write(b, 0, len);
				fileOutputStream.flush();
			}
    		fileOutputStream.close();
    		inputStream.close();
    		
    		String address = request.getServerName();
    		int port = request.getServerPort();
    		String imageUrl = "http://" + "192.168.43.235" + ":" + port + "/upload/" + fileName;
    		System.out.println("imageUrl = " + imageUrl);
    		return imageUrl;
		}
    	return "";
    }
}

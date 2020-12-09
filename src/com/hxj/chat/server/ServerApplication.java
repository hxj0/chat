package com.hxj.chat.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.server.frame.ServerFrame;

public class ServerApplication {
	
	public static void main(String[] args) throws Exception {
		InetAddress address = InetAddress.getLocalHost();
		ServerFrame serverFrame = new ServerFrame();
		ServerFrame.ipAndPort.setText(address.getHostAddress()+":"+Constants.SERVER_PORT);
		ServerFrame.nameLabel.setText(address.getHostName());
		try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
			while(true) {
				Socket accept = serverSocket.accept();
				new ServerHandler(accept, serverFrame).start();
				Thread.sleep(50);
			}
		}  
	}
}

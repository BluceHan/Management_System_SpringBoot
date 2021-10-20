package com.renfei.qqserver.service;

import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;
import com.renfei.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class sendNewsToAllService implements Runnable{
    @Override
    public void run() {
        //为了可以多次推送新闻，使用while循环

        while(true){
            System.out.println("请输入服务器要推送的新闻，输入exit表示退出推送新闻服务");
            String news = Utility.readString(1000);

            if("exit".equals(news)){
                break;
            }

            //构建群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            message.setMsgType(MessageType.MESSAGE_ALL_MES);

            //遍历当前所有的通信线程
            HashMap<String, ServerConnectClientThread> hm = ManageServerConnectClientThread.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while(iterator.hasNext()){
                String onLineUserId = iterator.next();
                ServerConnectClientThread serverConnectClientThread = ManageServerConnectClientThread.getServerConnectClientThread(onLineUserId);
                Socket socket = serverConnectClientThread.getSocket();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

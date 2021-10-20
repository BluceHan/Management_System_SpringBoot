package com.renfei.qqclient.service;

import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * 该类提供和消息相关的服务
 */
public class MessageClientService {
    public static void sendMessageToOne(String content, String senderId, String receiverId){
        //构建Message
        Message message = new Message();
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setContent(content);
        message.setMsgType(MessageType.MESSAGE_COMM_MES);
        message.setSendTime(new Date().toString());

        //发送给服务端，需要获得客户端的通讯线程
        ClientConnectServerThread senderThread = ManageClientConnectServerThread.getClientConnectServerThread(senderId);
        Socket senderSocket = senderThread.getSocket();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(senderSocket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToAll(String content, String senderId){
        //构建Message
        Message message = new Message();
        message.setSender(senderId);
        message.setContent(content);
        message.setMsgType(MessageType.MESSAGE_ALL_MES);
        message.setSendTime(new Date().toString());

        //发送给服务端
        ClientConnectServerThread senderThread = ManageClientConnectServerThread.getClientConnectServerThread(senderId);
        Socket senderSocket = senderThread.getSocket();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(senderSocket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.renfei.qqserver.service;


import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类对应的对象和某个客户端保持通讯
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;   //客户端的userId

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public void run() {
        while(true){    //这里线程处于run状态，可以发送/接收消息
            try {
                System.out.println("服务端和客户端" + userId + "保持通讯，读取数据");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message)ois.readObject();
                //根据msg的类型，做相应的业务处理
                if(msg.getMsgType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    //客户端要在线用户列表
                    System.out.println(msg.getSender() + " 要在线用户列表");
                    String onlineUsers = ManageServerConnectClientThread.getOnlineUsers();
                    //返回message给客户端
                    Message message = new Message();
                    message.setMsgType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message.setContent(onlineUsers);
                    message.setReceiver(msg.getSender());

                    //写入到数据通道，返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);

                }else if(msg.getMsgType().equals(MessageType.MESSAGE_CLIENT_EXIT)){   //客户端要退出
                    System.out.println("用户" + msg.getSender() + "要退出系统");
                    //关闭与该用户通讯的socket
                    socket.close();

                    //并且将与该用户通讯的线程从集合中删除
                    ManageServerConnectClientThread.removeServerConnectClientThread(msg.getSender());

                    //退出这个线程
                    System.out.println("用户" + msg.getSender() + "已经退出系统");
                    break;
                } else if(msg.getMsgType().equals(MessageType.MESSAGE_COMM_MES)) {
                    String receiverId = msg.getReceiver();
                    //这里判断这个receiver是否在线
                    if(ManageServerConnectClientThread.isOnline(receiverId)){   //如果在线
                        ServerConnectClientThread receiverThread = ManageServerConnectClientThread.getServerConnectClientThread(receiverId);
                        Socket receiverSocket = receiverThread.getSocket();
                        ObjectOutputStream oos = new ObjectOutputStream(receiverSocket.getOutputStream());
                        oos.writeObject(msg);
                    }else{        //如果不在线，将msg写入offLineDb中
                        ArrayList<Message> messages = QQServer.getOffLineDb().getOrDefault(receiverId, new ArrayList<>());
                        messages.add(msg);
                        QQServer.getOffLineDb().put(receiverId,messages);
                        System.out.println(QQServer.getOffLineDb());
                    }

                } else if(msg.getMsgType().equals(MessageType.MESSAGE_ALL_MES)){
                    String[] onlineUsers = ManageServerConnectClientThread.getOnlineUsers().split(" ");
                    for(String receiverId : onlineUsers){
                        if(receiverId.equals(userId)){
                            continue;
                        }
                        ServerConnectClientThread receiverThread = ManageServerConnectClientThread.getServerConnectClientThread(receiverId);
                        Socket receiverSocket = receiverThread.getSocket();
                        ObjectOutputStream oos = new ObjectOutputStream(receiverSocket.getOutputStream());
                        oos.writeObject(msg);
                    }
                } else if(msg.getMsgType().equals(MessageType.MESSAGE_FILE_MES)){
                    //获取关联目标用户的线程以及socket
                    String msgReceiverId = msg.getReceiver();
                    ServerConnectClientThread receiverThread = ManageServerConnectClientThread.getServerConnectClientThread(msgReceiverId);
                    Socket receiverSocket = receiverThread.getSocket();
                    ObjectOutputStream oos = new ObjectOutputStream(receiverSocket.getOutputStream());
                    oos.writeObject(msg);
                }  else {
                    System.out.println("其他业务");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

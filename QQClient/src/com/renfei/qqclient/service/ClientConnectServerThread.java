package com.renfei.qqclient.service;

import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    //该线程需要持有socket
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    //为了以后更方便地得到socket，需要提供一个get方法
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        //因为线程需要在后台和服务器保持通信，我们可以使用while循环
        while(true){
            try {
                System.out.println("客户端线程，等待读取从服务器端发送的消息...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message)ois.readObject();  //如果服务器没有发送Message对象，程序会阻塞在这里
                //判断msg的类型，然后做相关业务处理
                //如果读取到的是 服务端返回的在线用户列表
                if(msg.getMsgType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    //取出在线列表信息，并展示
                    String[] online_users = msg.getContent().split(" ");
                    System.out.println("******在线用户列表******");
                    for (int i = 0; i < online_users.length; i++) {
                        System.out.println("用户： " + online_users[i]);
                    }
                } else if(msg.getMsgType().equals(MessageType.MESSAGE_COMM_MES)){
                    System.out.println("用户" + msg.getSender() + "对我说： "
                    + msg.getContent());
                } else if(msg.getMsgType().equals(MessageType.MESSAGE_ALL_MES)){
                    System.out.println("用户" + msg.getSender() + "对我说： "
                            + msg.getContent());
                } else if(msg.getMsgType().equals(MessageType.MESSAGE_FILE_MES)){
                    String sender = msg.getSender();
                    String dest = msg.getDest();
                    System.out.println("用户" + sender + "向我的" + dest + "发送了一份文件");
                    byte[] data = msg.getFileBytes();

                    //将data字节数组写入到dest指定的位置
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));
                    bos.write(data);
                    bos.close();
                }else{
                    System.out.println("其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

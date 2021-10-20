package com.renfei.qqclient.service;

import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;
import com.renfei.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {
    private User user = new User();
    //因为socket在其他地方也可能会被使用到，所以也要设置为一种属性
    private Socket socket;

    //根据userId 和 pwd到服务器验证用户是否合法
    public boolean checkUser(String userId, String pwd){
        boolean flag = false;
        //创建User对象
        user.setUserId(userId);
        user.setPassword(pwd);

        //连接到服务器，发送到user对象
        try {
            socket = new Socket(InetAddress.getByName("r900p"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);  //发送User对象给服务端

            //读取从服务端返回的Message信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message msg = (Message)ois.readObject();
            if(msg.getMsgType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)){
                //如果登录成功，就需要启动一个线程来持有这个socket
                //创建一个和服务器端保持通信的线程 ---> ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端的线程
                clientConnectServerThread.start();

                //为了客户端的扩展性，我们将线程放入到HashMap当中
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

                flag = true;
            }
            else{
                //如果登录失败，我们就不能启动连接服务器的线程，但无论如何都有socket，所以要关闭这个socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendsList(){
        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserId());

        //发送给服务器，需要的到当前线程的ObjectOutputStream对象
        try {
//            ObjectOutputStream oos =
//                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            new ObjectOutputStream(socket.getOutputStream()).writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //编写方法，退出客户端，并向服务端发送一个退出系统的message对象
    public void logout(){
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserId()); //一定要指明是哪个客户端要退出系统

        //发送message对象给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println("用户" + user.getUserId() + "退出系统");
            System.exit(0);        //结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

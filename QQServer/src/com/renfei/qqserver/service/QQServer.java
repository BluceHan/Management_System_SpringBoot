package com.renfei.qqserver.service;

import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;
import com.renfei.qqcommon.User;
import com.renfei.utils.JDBCUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务端，在监听9999端口，等待客户端的连接，并保持通信
 */
public class QQServer {
    private ServerSocket ss = null;
    private static ConcurrentHashMap<String, ArrayList<Message>> offLineDb = new ConcurrentHashMap<>();

    public ServerSocket getSs() {
        return ss;
    }

    public static ConcurrentHashMap<String, ArrayList<Message>> getOffLineDb() {
        return offLineDb;
    }

    public QQServer() {
        try {
            System.out.println("服务端在9999端口监听");
            //启动推送新闻的线程
            new Thread(new sendNewsToAllService()).start();
            ss = new ServerSocket(9999);

            while(true){   //监听是一直监听的，当和某个客户端建立连接以后，会继续监听
                Socket socket = ss.accept();
                //得到socket关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                User user = (User)ois.readObject();    //读取客户端发送过来的User对象

                //得到socket关联的输出流对象
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                //创建一个Message对象，准备回复客户端
                Message message = new Message();
                //验证用户的合法性
                String userId = user.getUserId();
                String passwd = user.getPassword();
                System.out.println("id=" + userId);
                System.out.println("passwd=" + passwd);
                if(JDBCUtils.checkUser(userId,passwd)){ //登录成功
                    System.out.println("有这个用户");
                    message.setMsgType(MessageType.MESSAGE_LOGIN_SUCCESS);
                    //将message回复给客户端
                    oos.writeObject(message);

                    //在服务端创建一个线程，持有socket，并保持通信
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                    serverConnectClientThread.start();

                    //把该线程对象放入集合中进行管理
                    ManageServerConnectClientThread.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);
                }else{    //登录失败
                    System.out.println("没有这个用户");
                    System.out.println("用户" + user.getUserId() + "登录失败");
                    message.setMsgType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //如果服务端退出了while循环，说明服务端不再监听，因此需要关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

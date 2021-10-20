package com.renfei.qqclient.service;

import java.util.HashMap;

/**
 * 管理客户端连接到服务器端的线程的一个类
 */
public class ManageClientConnectServerThread {
    //将多个线程放入到HashMap当中，key就是用户id, value就是线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    //将某一个线程加入到集合当中
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread thread){
        hm.put(userId, thread);
    }

    //通过一个userId可以得到对应的线程
    public static  ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }
}

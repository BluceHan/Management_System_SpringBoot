package com.renfei.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类用于管理和客户端通讯的线程
 */
public class ManageServerConnectClientThread {
    private static HashMap<String,ServerConnectClientThread> hm = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static void addServerConnectClientThread(String userId, ServerConnectClientThread thread){
        hm.put(userId, thread);
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hm.get(userId);
    }

    //返回在线用户列表
    public static String getOnlineUsers(){
        String onlineUserList = "";
        Iterator<String> iterator = hm.keySet().iterator();
        while(iterator.hasNext()){
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList.trim();
    }

    public static void removeServerConnectClientThread(String userId){
        hm.remove(userId);
    }

    public static boolean isOnline(String receiverId){
        Iterator<String> iterator = hm.keySet().iterator();
        while(iterator.hasNext()){
            if(iterator.next().equals(receiverId)){
                return true;
            }
        }

        return false;
    }
}

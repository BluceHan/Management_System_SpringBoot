package com.renfei.qqcommon;

/**
 * 表示消息类型
 */
public interface MessageType {
    //在接口中定义了一些常量
    String MESSAGE_LOGIN_SUCCESS = "1";  //表示登陆成功
    String MESSAGE_LOGIN_FAIL = "2";     //表示登录失败
    String MESSAGE_COMM_MES="3";         //表示普通的信息
    String MESSAGE_GET_ONLINE_FRIEND="4"; //表示获取在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND="5"; //表示服务端返回在线用户列表
    String MESSAGE_CLIENT_EXIT="6";       //表示客户端请求退出
    String MESSAGE_ALL_MES="7";           //表示群发消息
    String MESSAGE_FILE_MES="8";          //表示文件消息
    String MESSAGE_OFFINE="9";            //表示离线留言
}

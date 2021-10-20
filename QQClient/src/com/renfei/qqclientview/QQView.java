package com.renfei.qqclientview;

import com.renfei.qqclient.service.FileClientService;
import com.renfei.qqclient.service.MessageClientService;
import com.renfei.qqclient.service.UserClientService;
import com.renfei.qqclient.utils.Utility;

/**
 * 客户端的菜单界面
 */
public class QQView {
    private boolean loop = true;   //控制是否显示菜单
    private String key = "";       //用于接收用户的键盘输入
    private UserClientService service = new UserClientService();

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统......");
    }


    //显示主菜单
    private void mainMenu(){
        while(loop){
            System.out.println("=====欢迎登录网络通讯系统=====");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");

            key = Utility.readString(1);
            //根据用户的输入来处理不同的逻辑
            switch(key){
                case "1":
                    System.out.println("请输入用户id");
                    String userId = Utility.readString(50);
                    System.out.println("请输入密 码: ");
                    String password = Utility.readString(50);

                    //到服务端去验证该用户是否合法，这里有很多代码，我们可以编写一个类去实现，UserClientService[用户登录/用户注册]
                    boolean valid = service.checkUser(userId, password);

                    if(valid){
                        System.out.println("=====欢迎(用户" + userId + ")=====");
                        //进入二级菜单
                        while(loop){
                            System.out.println("\n=====网络通信二级菜单(用户 " + userId + " )=====");

                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");

                            System.out.println("请输入你的选择： ");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    service.onlineFriendsList();
                                    break;
                                case "2":
                                    System.out.println("请输入想说的话: ");
                                    String words = Utility.readString(100);
                                    MessageClientService.sendMessageToAll(words,userId);
                                    break;
                                case "3":
                                    System.out.println("请输入想要聊天的用户id: ");
                                    String receiverId = Utility.readString(50);
                                    System.out.println("请输入想说的话: ");
                                    String content = Utility.readString(100);
                                    //编写一个方法，将聊天内容发送给服务端
                                    MessageClientService.sendMessageToOne(content,userId,receiverId);
                                    break;
                                case "4":
                                    System.out.println("请输入源文件路径");
                                    String src = Utility.readString(100);
                                    System.out.println("请输入想要传输的目标用户的id");
                                    String destId = Utility.readString(50);
                                    System.out.println("请输入目标用户的文件保存路径");
                                    String dest = Utility.readString(100);
                                    FileClientService.sendFile(userId,destId,dest,src);
                                    break;
                                case "9":
                                    //调用方法退出系统
                                    service.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    }else{    //登录服务器失败
                        System.out.println("登录失败");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}

package com.renfei.qqclient.service;

import com.renfei.qqcommon.Message;
import com.renfei.qqcommon.MessageType;

import java.io.*;

public class FileClientService {
    public static void sendFile(String senderId, String receiverId, String dest, String src){
        //构建Message对象
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setSrc(src);
        message.setDest(dest);

        byte[] bytes = null;
        //读取源文件到字节数组
        try {
            bytes = streamToByteArray(new FileInputStream(src));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将字节数组封装到message
        message.setFileBytes(bytes);
        message.setFileLen(bytes.length);

        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] streamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] arr = new byte[1024];
        int readLen = 0;

        while((readLen = is.read(arr)) != -1){
            baos.write(arr, 0, readLen);
        }

        return baos.toByteArray();
    }
}

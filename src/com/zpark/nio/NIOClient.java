package com.zpark.nio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args)throws IOException {

        //创建ServerSocket ss = new ServerSocket();
        SocketChannel s = SocketChannel.open();
        //连接服务器
        s.connect(new InetSocketAddress("127.0.0.1",9999));
        //发送请求
       //创建ByteBuffered缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put("你是一个狗".getBytes());
        //把缓冲区写出去
        s.write(buffer);
        s.shutdownOutput();


        //接受响应
        //创建缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(1);
        //特殊流变量，可无限扩充
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true){
            buffer1.clear();
            int read = s.read(buffer1);
            if (read == -1)break;
            buffer1.flip();
            baos.write(buffer1.array(),0,read);
        }
        System.out.println("服务器收到请求:"+new String(baos.toByteArray()));


        s.close();
    }
}

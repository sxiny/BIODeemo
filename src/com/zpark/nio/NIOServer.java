package com.zpark.nio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOServer {
    public static void main(String[] args) throws IOException {
        //创建ServerSocket对象
        ServerSocketChannel ss = ServerSocketChannel.open();
        //设置服务监听端口
        ss.bind(new InetSocketAddress(9999));
        System.out.println("我在监听9999....");
        //创建线程池，设置线程数量
        ExecutorService pool = Executors.newFixedThreadPool(10);
        while (true){
            //等待请求到来  内部类使用外边变量要被final修饰
            final SocketChannel s = ss.accept();
            pool.submit(new Runnable() {
                @Override
                public void run() {

                    try {
                        //创建缓冲区
                        ByteBuffer buffer = ByteBuffer.allocate(2);
                        //特殊流变量，可无限扩充
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while (true){
                            //buffer.clear();
                            int read = s.read(buffer);
                            if (read == -1)break;
                            buffer.flip();
                            baos.write(buffer.array(),0,read);
                        }
                        System.out.println("服务器收到请求:"+new String(baos.toByteArray()));

                        //给出响应
                       ByteBuffer buffer1 = ByteBuffer.wrap(("时间"+new Date().toLocaleString()).getBytes());
                       s.write(buffer1);
                        s.shutdownOutput();


                    }catch (IOException e){

                        e.printStackTrace();

                    }finally {
                        //关闭资源
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}

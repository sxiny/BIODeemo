package com.zpark.snio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResponseProcessor {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void process(final SelectionKey key,final Selector selector){

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("响应客户端写操作...");
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteArrayOutputStream attachment = (ByteArrayOutputStream) key.attachment();
                    String res="服务器时间："+new Date().toLocaleString()+"\n";
                    ByteBuffer buffer=ByteBuffer.wrap(res.getBytes());
                    channel.write(buffer);
                    channel.socket().shutdownOutput();//告知客户端 写结束
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

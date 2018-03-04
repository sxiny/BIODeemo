package com.zpark.snio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestProcessor {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

        public static void process(final SelectionKey key, final Selector selector){

            executorService.submit(new Runnable() {
                @Override
                public void run() {

                    try{
                        System.out.println("响应客户端读操作...");
                        SocketChannel socketChannel = (SocketChannel)key.channel();
                        //读取通道 数据 套路
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        ByteBuffer buffer= ByteBuffer.allocate(1024);
                        while(true){
                            buffer.clear();
                            int n=socketChannel.read(buffer);
                            if(n==-1) break;
                            baos.write(buffer.array(),0,n);
                        }
                        String msg=new String(baos.toByteArray());//用户意图
                        System.out.println("读操作已读："+msg);
                       // socketChannel.register(selector,SelectionKey.OP_WRITE,baos);//注册 写
                        key.attach(baos);
                         NIOServerManyThread.addWriteEventToQuene(key,selector);
                        System.out.println("读时间注册成功");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

}

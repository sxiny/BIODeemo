package com.zpark.snio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class NIOServerManyThread {

    private static List<SelectionKey> writeQuene = new ArrayList<SelectionKey>();
    public  static void addWriteEventToQuene(SelectionKey key,Selector selector){

        synchronized (writeQuene){

            writeQuene.add(key);
        }
        selector.wakeup();
    }
    public static void main(String[] args) throws IOException {

        //创建ServerSocket对象
        ServerSocketChannel ss = ServerSocketChannel.open();
        //设置服务监听端口
        ss.bind(new InetSocketAddress(9999));

        //设置通道非阻塞
        ss.configureBlocking(false);
        //创建通道选择器
        Selector selector = Selector.open();
        //注册通道--》选择器注册列表
        ss.register(selector, SelectionKey.OP_ACCEPT);
        //遍历selectordeshijianduilei
        while (true){
            System.out.println("我在监听9999....");
            //获取时间列表中的事件个数
            int num = selector.select();
            if(num >0 ){
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){

                    SelectionKey key = iterator.next();

                    if(key.isAcceptable()){
                        System.out.println("响应客户端...");
                        //1.获取通道
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();

                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }else if (key.isReadable()){
                       /* Object attachment = key.attachment();
                        if (attachment == null){
                            key.attach(true);
                            RequestProcessor.process(key,selector);
                        }else{
                            System.out.println("已经有现成处理过了，无需重复处理");

                        }*/
                       key.cancel();//取消通道选择器对此通道的管理
                        RequestProcessor.process(key,selector);

                    }else if (key.isWritable()){
                       key.cancel();
                        ResponseProcessor.process(key,selector);
                    }
                    iterator.remove();
                }
            }else{

                //注册写事件
                while (writeQuene.size()>0){
                    System.out.println("main线程注册些事件");
                    SelectionKey key = writeQuene.remove(0);
                    SocketChannel channel = (SocketChannel)key.channel();
                    channel.register(selector,SelectionKey.OP_WRITE,key.attachment());

                }
            }

        }

        /*
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
        */
    }
}

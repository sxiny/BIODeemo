package com.zpark.snio;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;

public class NIOServer {

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

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()){

                    SelectionKey key = keys.next();

                    if(key.isAcceptable()){
                        System.out.println("响应客户端...");
                        //1.获取通道
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();

                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }else if (key.isReadable()){
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
                        System.out.println("服务器收到："+msg);
                        socketChannel.register(selector,SelectionKey.OP_WRITE,baos);//注册 写
                    }else if (key.isWritable()){

                        System.out.println("响应客户端写操作...");
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteArrayOutputStream attachment = (ByteArrayOutputStream) key.attachment();
                        String res="服务器时间："+new Date().toLocaleString()+"\n";
                        ByteBuffer buffer=ByteBuffer.wrap(res.getBytes());
                        channel.write(buffer);
                        channel.socket().shutdownOutput();//告知客户端 写结束

                        channel.close();
                    }
                    keys.remove();
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

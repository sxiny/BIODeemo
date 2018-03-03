package com.zpark.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    /**
     * 将请求转发和响应通过线程分离，实现服务器的高性能
     * ServerSocket负责请求转发，Socket负责处理请求与响应
     * 当启动服务端时，线程处于阻塞状态，只有当请求过来时才会转发请求，创建Socket
     * 因此不支持多线程并发访问，可以通过线程解决，而直接new Thread会导致服务器资源枯竭
     * 所以用线程池控制线程个数和利用率
     * @param args
     * @throws IOException
     */

    public static void main(String[] args)throws IOException{
        //创建ServerSocket对象
        ServerSocket ss = new ServerSocket();
        //设置服务监听端口
        ss.bind(new InetSocketAddress(9999));

        //创建线程池，设置线程数量
        ExecutorService pool = Executors.newFixedThreadPool(10);
        System.out.println("我在监听9999....");
        while (true){
           //等待请求到来  内部类使用外边变量要被final修饰
           final Socket s = ss.accept();
           pool.submit(new Runnable() {
               @Override
               public void run() {
                   try {
                       //处理用户请求
                       //创建输入流
                       InputStream request = s.getInputStream();
                       //桥转换封装字符流
                       InputStreamReader isr = new InputStreamReader(request);
                       //封装过滤流
                       BufferedReader br = new BufferedReader(isr);

                       StringBuilder sb = new StringBuilder();
                       String result = null;
                       while ((result =  br.readLine()) != null){

                           sb.append(result);
                       }

                       System.out.println("服务器收到请求"+sb.toString());

                       //创建输出流
                       OutputStream response = s.getOutputStream();
                       //封装过滤流
                       PrintWriter pw = new PrintWriter(response);
                       pw.println("时间："+new Date().toLocaleString());
                       pw.flush();
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

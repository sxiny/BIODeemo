package com.zpark.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BIOClient {


    public static void main(String[] args) throws IOException {
        //创建ServerSocket ss = new ServerSocket();
        Socket s = new Socket();
        //连接服务器
        s.connect(new InetSocketAddress("127.0.0.1",9999));
        //发送请求
        OutputStream outputStream = s.getOutputStream();
        PrintWriter pw = new PrintWriter(outputStream);
        pw.println('a');
        pw.println('b');
        pw.println('c');
        pw.flush();
        s.shutdownOutput();


        //接受响应
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

        System.out.println("客户端收到："+sb.toString());

        s.close();
    }
}

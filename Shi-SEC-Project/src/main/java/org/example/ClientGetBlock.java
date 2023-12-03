package org.example;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientGetBlock implements Runnable{

    Socket socket;
    int Flag,height;
    //返回结果集
    int NodeIndex,ReceivedHeight;
    byte[] BlockPart;
    public ClientGetBlock(Socket socket,int Flag,int height){
        this.socket=socket;
        this.Flag=Flag;
        this.height=height;
    }

    @Override
    public void run() {
        try {
            String ECHO = null;
            if(Flag==1){
                ECHO="READ";
            }else if(Flag==2){
                ECHO="READOUT";
            }
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            String SendMessage,ReceiveMessage;
            String[] ReceiveDataString;
            //输出流包装为打印流
            InetAddress ia = socket.getInetAddress();
            String clientIp = ia.getHostAddress();
            int clientPort = socket.getPort();

            SendMessage=ECHO+"_"+height;
            pw.write(SendMessage+"\n");//写入内存缓冲区
            pw.flush();//刷新缓存，向服务器端输出信息
            socket.shutdownOutput();//关闭输出流
            ReceiveMessage = br.readLine(); //循环读取客户端的信息
            //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
            ReceiveDataString = ReceiveMessage.split("_");
            NodeIndex=Integer.parseInt(ReceiveDataString[0]);
            ReceivedHeight=Integer.parseInt(ReceiveDataString[1]);
            BlockPart=ReceiveDataString[2].getBytes();
            socket.shutdownInput();
            pw.close();
            os.close();
            br.close();
            isr.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

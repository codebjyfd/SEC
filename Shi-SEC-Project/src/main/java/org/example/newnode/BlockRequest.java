package org.example.newnode;

import org.example.BlockStore;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class BlockRequest implements Runnable{

    byte[] BlockPart=null;
    int height,flag;
    Socket socket;

    InputStream is=null;
    InputStreamReader isr;
    BufferedReader br;
    OutputStream os;
    PrintWriter pw;
    int NodeIndex,ReceivedHeight;
    byte[] SendParts;

    public BlockRequest(Socket socket) throws IOException {
        this.socket=socket;
        is = socket.getInputStream();
        isr = new InputStreamReader(is, "UTF-8");
        br = new BufferedReader(isr);
        os = socket.getOutputStream();
        pw = new PrintWriter(os);
    }

    @Override
    public void run() {

        try {
            if(flag==1){
                String ECHO = null;
                ECHO="HEIGHT";
                /*InputStream is = null;
                is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);*/
                String SendMessage,ReceiveMessage;
                String[] ReceiveDataString;
                //输出流包装为打印流
                InetAddress ia = socket.getInetAddress();
                String clientIp = ia.getHostAddress();
                int clientPort = socket.getPort();

                SendMessage=ECHO+"_"+height;
                System.out.println("发送对区块"+height+"的请求");
                pw.write(SendMessage+"\n");//写入内存缓冲区
                pw.flush();//刷新缓存，向服务器端输出信息
                ReceiveMessage = br.readLine(); //循环读取客户端的信息
                //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
                Thread t = Thread.currentThread();
                System.out.println("接收到"+clientPort+"的消息，我是线程："+t);

                ReceiveDataString = ReceiveMessage.split("_");

                NodeIndex=Integer.parseInt(ReceiveDataString[0]);
                System.out.println("nodeindex:"+NodeIndex);
                ReceivedHeight=Integer.parseInt(ReceiveDataString[1]);
                System.out.println("height:"+ReceivedHeight);
                BlockPart=ReceiveDataString[2].getBytes();
                System.out.println("区块数据:"+BlockPart);
                //socket.shutdownOutput();//关闭输出流
                //socket.shutdownInput();
                /*pw.close();
                os.close();
                br.close();
                isr.close();
                is.close();*/
            }else if(flag==2){
                String ECHO = null;
                ECHO="SEND";
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                String SendMessage;

                SendMessage=ECHO+"_"+new String(SendParts);
                pw.write(SendMessage+"\n");//写入内存缓冲区
                pw.flush();//刷新缓存，向服务器端输出信息
                System.out.println("向"+socket.getPort()+"发送SEND消息");
                //socket.shutdownOutput();//关闭输出流
                /*pw.close();
                os.close();*/
            } else if (flag==3) {
                String ECHO = null;
                ECHO="HEIGHT";
                /*InputStream is = null;
                is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);*/
                String SendMessage,ReceiveMessage;
                String[] ReceiveDataString;
                //输出流包装为打印流
                InetAddress ia = socket.getInetAddress();
                String clientIp = ia.getHostAddress();
                int clientPort = socket.getPort();

                SendMessage=ECHO+"_"+height;
                pw.write(SendMessage+"\n");//写入内存缓冲区
                pw.flush();//刷新缓存，向服务器端输出信息
                ReceiveMessage = br.readLine(); //循环读取客户端的信息
                //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
                ReceiveDataString = ReceiveMessage.split("_");
                NodeIndex=Integer.parseInt(ReceiveDataString[0]);
                ReceivedHeight=Integer.parseInt(ReceiveDataString[1]);
                BlockPart=ReceiveDataString[2].getBytes();
                //socket.shutdownOutput();//关闭输出流
                //socket.shutdownInput();
                /*pw.close();
                os.close();
                br.close();
                isr.close();
                is.close();*/
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

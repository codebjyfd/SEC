package org.example.node;

import org.example.Node;
import org.rocksdb.RocksDBException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class NewNodeResponse {
    Node node;
    Socket socket;
    public NewNodeResponse(Node node) throws IOException {
        this.node=node;
        Init();
    }

    public void start() throws RocksDBException, IOException {
        if(node.NodeSum%node.R==0)BlockSynTwo();
        else BlockSynOne();
    }

    /*public byte[] Merge(byte[][] byteArray){
        return new byte[1];
    }*/

    private static byte[] mergeBytes(byte[]... values) {
        int lengthByte = 0;
        for (byte[] value : values) {
            lengthByte += value.length;
        }
        byte[] allBytes = new byte[lengthByte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, allBytes, countLength, b.length);
            countLength += b.length;
        }
        return allBytes;
    }

    private void BlockSynTwo() throws IOException, RocksDBException {
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //输出流包装为打印流
        InetAddress ia = socket.getInetAddress();
        String clientIp = ia.getHostAddress();
        int clientPort = socket.getPort();
        while(true){
            String data = null;
            int i = 0;
            String ReceiveMessage = br.readLine(); //循环读取客户端的信息
            //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
            String[] ReceiveDataString = ReceiveMessage.split("_");
            String messageType = ReceiveDataString[0];
            String SendMessage;
            byte[][] byteArray=new byte[node.BlockPartStoredPerNode][node.ShardSize];
            if (messageType.equals("ACK")) {

                int height = Integer.parseInt(ReceiveDataString[1]);
                SendMessage="YES";
                pw.write(SendMessage+"\n");//写入内存缓冲区
                pw.flush();//刷新缓存，向服务器端输出信息
                System.out.println("返回YES信号");
            }else if(messageType.equals("HEIGHT")) {
                int height = Integer.parseInt(ReceiveDataString[1]);
                /*for(int j=0;j<node.BlockPartStoredPerNode;j++){
                    byteArray[i]=node.db.get(Integer.toString(height).getBytes());
                }

                SendMessage=new String(Merge(byteArray));*/
                byte[][] mergeArray = new byte[node.BlockPartStoredPerNode][node.ShardSize];
                byte[] BlockParts;
                for(int j=0;j<node.BlockPartStoredPerNode;j++){
                    mergeArray[j]=node.db.get((height+"_"+j).getBytes());
                }
                BlockParts=mergeArray[0];
                if(node.BlockPartStoredPerNode!=1){
                    for(int j=1;j<node.BlockPartStoredPerNode;j++){
                        BlockParts=mergeBytes(BlockParts,mergeArray[j]);
                    }
                }



                //向服务器端发送信息
                /*byte[] Block_Data=new byte[178360];
                Arrays.fill(Block_Data, (byte) 6);*/
                System.out.println(BlockParts.length);
                //SendMessage=new String(BlockParts);
                //SendMessage=node.Index+"_"+height+"_"+SendMessage;
                os.write(BlockParts);//写入内存缓冲区
                os.flush();//刷新缓存，向服务器端输出信息
                System.out.println("发送区块"+height);
            }else if(messageType.equals("STOP")){
                System.out.println("接收到STOP信号");
                break;
            }
        }
        socket.shutdownOutput();//关闭输出流
        socket.shutdownInput();
        pw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
    }

    private void BlockSynOne() throws IOException, RocksDBException {


        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //输出流包装为打印流
        InetAddress ia = socket.getInetAddress();
        String clientIp = ia.getHostAddress();
        int clientPort = socket.getPort();
        while(true){
            /*InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);*/
            String data = null;
            int i = 0;
            String ReceiveMessage = br.readLine(); //循环读取客户端的信息
            //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
            String[] ReceiveDataString = ReceiveMessage.split("_");
            String messageType = ReceiveDataString[0];
            String SendMessage;
            if (messageType.equals("HEIGHT")) {

                int height = Integer.parseInt(ReceiveDataString[1]);
                System.out.println("接收到对区块"+height+"的请求");
                int blockindex=node.BlockPartStoredPerNode-1;
                byte[] ss=node.db.get((height+"_"+blockindex).getBytes());
                System.out.println(ss.length);
                //SendMessage=new String(ss);

                //SendMessage=node.Index+"_"+height+"_"+SendMessage;
                System.out.println("向其发送:"+node.Index+"_"+height);
                os.write(ss);//写入内存缓冲区
                os.flush();//刷新缓存，向服务器端输出信息
                System.out.println("发送区块"+height);
            }else if(messageType.equals("SEND")) {
                System.out.println("接收到SEND");

            }else if(messageType.equals("STOP")){
                System.out.println("接收到STOP信号");
                break;
            }
            /*pw.close();
            os.close();
            br.close();
            isr.close();
            is.close();*/
        }
        socket.shutdownOutput();//关闭输出流
        socket.shutdownInput();
        pw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
    }

    public void Init() throws IOException {
        ServerSocket serverSocket = new ServerSocket(node.Port);
        Socket socket = null;
            socket = serverSocket.accept();
            this.socket=socket;
            InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            String clientIpAddress = socketAddress.getAddress().getHostAddress();
            /*count++;
            System.out.println("区块访问请求次数：" + count);
            InetAddress address = socket.getInetAddress();
            System.out.println("区块访问客户端IP：" + address.getHostAddress());*/
    }



}

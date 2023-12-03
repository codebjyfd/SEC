package org.example;

import org.example.BlockStore;
import org.example.ClientResponse;
import org.example.Node;
import org.example.NodeNet;
import org.example.node.NewNodeResponse;
import org.rocksdb.RocksDBException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws RocksDBException, IOException {
        int para1=Integer.parseInt(args[0]);
        int para2=Integer.parseInt(args[1]);
        int para3=Integer.parseInt(args[2]);
        Node node=new Node(para1,para2);
        //StoreEngine se=new StoreEngine(node);
        BlockStore bs=new BlockStore(node,200);
        //ListenEngine le=new ListenEngine(node,1);
        System.out.println("区块存储完成");
        NodeNet nn=new NodeNet(node);
        //nn.start();
        //区块读取
        if(para3==1){
            ClientResponse cr=new ClientResponse(node);
            cr.start();
        } else if (para3==2) {
            NewNodeResponse nnr=new NewNodeResponse(node);
            nnr.start();
        }


        //节点加入
        /**/
    }
}
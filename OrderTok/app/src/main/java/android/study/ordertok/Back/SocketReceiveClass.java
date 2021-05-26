package android.study.ordertok.Back;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.study.ordertok.FirstActivity;
import android.study.ordertok.InfoPackage.OrderInfo;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketReceiveClass implements Runnable {
    ServerSocket serverSocket;
    DBHelper dbHelper;
    Context context;
    Socket s;
    DataInputStream dis;
    String received;
    Handler handler = new Handler();

    public SocketReceiveClass(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9800);
            while(true){
                s = serverSocket.accept();
                dis = new DataInputStream(s.getInputStream());
                received = dis.readUTF();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CONSOLE", "SOCKET RECEIVED");
                        if(received.contains(",,,")){
                            String[] receivedArray = received.split(",,,");
                            if(receivedArray[0].equals("delete")){
                                if(dbHelper.getStoreInfo().getTableNumber()==Integer.parseInt(receivedArray[1])){
                                    OrderInfo orderInfo = dbHelper.getOneOrderInfo(Integer.parseInt(receivedArray[2]), receivedArray[3]);
                                    dbHelper.updateOrderInfoTotal(orderInfo, 0);
                                    if(dbHelper.getAllOrderInfo()!= null){
                                        List<OrderInfo> orderInfoList = new ArrayList<>();
                                        for(int k=0; k<dbHelper.getAllOrderInfo().size(); k++){
                                            OrderInfo newOrderInfo = dbHelper.getAllOrderInfo().get(k);
                                            if(newOrderInfo.getOrderTotal()>0 || newOrderInfo.getOrderCount()>0){
                                                orderInfoList.add(newOrderInfo);
                                            }
                                        }
                                        if(orderInfoList.size()<1){
                                            Intent firstIntent = new Intent(context, FirstActivity.class);
                                            context.startActivity(firstIntent);
                                        }
                                    }else{
                                        Intent firstIntent = new Intent(context, FirstActivity.class);
                                        context.startActivity(firstIntent);
                                    }
                                }
                            }else if(receivedArray[0].equals("add")){
                                if(dbHelper.getStoreInfo().getTableNumber()==Integer.parseInt(receivedArray[1])){
                                    OrderInfo orderInfo = dbHelper.getOneOrderInfo(Integer.parseInt(receivedArray[2]), receivedArray[3]);
                                    if(orderInfo!=null){
                                        dbHelper.updateOrderInfoTotal(orderInfo, orderInfo.getOrderTotal()+Integer.parseInt(receivedArray[7]));
                                    }else {
                                        orderInfo = new OrderInfo();
                                        orderInfo.setMenuId(Integer.parseInt(receivedArray[2]));
                                        orderInfo.setOrderKor(receivedArray[3]);
                                        orderInfo.setOrderEng(receivedArray[4]);
                                        orderInfo.setOrderChn(receivedArray[5]);
                                        orderInfo.setOrderJpn(receivedArray[6]);
                                        orderInfo.setOrderCount(0);
                                        orderInfo.setOrderTotal(Integer.parseInt(receivedArray[7]));
                                        orderInfo.setOrderPrice(Integer.parseInt(receivedArray[8]));
                                        dbHelper.insertOrder(orderInfo);
                                    }
                                }
                            }
                        } else if(received.equals("allClear")){
                            dbHelper.deleteAllOrderInfo();
                            Intent intent = new Intent(context, FirstActivity.class);
                            context.startActivity(intent);
                        }else if(received.equals("update")){
                            Intent intent = new Intent(context, FirstActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

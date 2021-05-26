package android.study.ordertokmenu.Back;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.study.ordertokmenu.FirstActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
                        if(received.equals("allClear")){
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

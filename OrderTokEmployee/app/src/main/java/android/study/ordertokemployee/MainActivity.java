package android.study.ordertokemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.study.ordertokemployee.Back.BaseAdapter;
import android.study.ordertokemployee.Back.DBHelper;
import android.study.ordertokemployee.Back.HttpConnect;
import android.study.ordertokemployee.Back.SocketReceiveClass;
import android.study.ordertokemployee.HolderPackage.OrderHolder;
import android.study.ordertokemployee.HolderPackage.TableHolder;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.study.ordertokemployee.InfoPackage.OrderInfo;
import android.study.ordertokemployee.InfoPackage.ServerInfo;
import android.study.ordertokemployee.InfoPackage.StoreInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    StoreInfo storeInfo;
    ServerInfo serverInfo;

    Button categoryResetButton, checkOldOrderButton, checkAllButton;
    GridView gridView;

    TableAdapter tableAdapter;
    OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dbHelper = new DBHelper(MainActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread resetThread = new Thread(new SocketReceiveClass(MainActivity.this));
        resetThread.start();
        Thread receiveThread = new Thread(new MyServer());
        receiveThread.start();

        checkOldOrderButton = findViewById(R.id.checkOldOrderButton);
        categoryResetButton = findViewById(R.id.categoryResetButton);
        checkAllButton = findViewById(R.id.checkAllButton);
        gridView = findViewById(R.id.gridView);
        tableAdapter = new TableAdapter(MainActivity.this, R.layout.table_item);
        gridView.setAdapter(tableAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckOrder();
            }
        }, 5000);

        // 재설정 버튼
        categoryResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resetCategoryIntent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(resetCategoryIntent);
                overridePendingTransition(0,0);
            }
        });

        checkOldOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderIntent = new Intent(MainActivity.this, OrderHistoryActivity.class);
                startActivity(orderIntent);
                overridePendingTransition(0,0);
            }
        });

        // 전체 확인 버튼
        checkAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.allCheck();
                onResume();
            }
        });
    }

    public void moveToDetail(OrderInfo orderInfo){
        Log.d("CONSOLE", "CLICKED TABLE : "+orderInfo.getTableNumber());
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra("tableNumber", orderInfo.getTableNumber());
        detailIntent.putExtra("orderDate", orderInfo.getOrderDate());
        startActivity(detailIntent);
        overridePendingTransition(0,0);
    }

    public void setTableGridView(){
        // 날짜 출력
        List<Integer> orderInfoList = dbHelper.selectAllOrderDate();
        if(orderInfoList!=null&&orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                int orderInfo = orderInfoList.get(i);
                // 해당 날짜에 맞는 테이블 번호 출력
                List<OrderInfo> tableNumberList = dbHelper.selectTableNumber(orderInfo);
                if(tableNumberList!=null&&tableNumberList.size()>0){
                    for(int j=0; j<tableNumberList.size(); j++){
                        OrderInfo tableInfo = tableNumberList.get(j);
                        tableAdapter.add(tableInfo);
                    }
                }
            }
        }
    }

    public void setOrderList(OrderInfo receivedOrderInfo){
    // 해당 테이블의 주문 리스트 가져오기
        List<OrderInfo> orderInfoList = dbHelper.selectTableOrder(receivedOrderInfo.getTableNumber(), receivedOrderInfo.getOrderDate());
        if(orderInfoList!=null&&orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                List<CategoryInfo> categoryInfoList = dbHelper.getSelectedCategoryInfo();
                if(categoryInfoList!=null&& categoryInfoList.size()>0){
                    for(int j=0; j<categoryInfoList.size(); j++){
                        CategoryInfo categoryInfo = categoryInfoList.get(j);
                        if(categoryInfo.getCategoryId()==orderInfo.getCategoryId() && orderInfo.getOrderCheck()==0){
                            orderAdapter.add(orderInfo);
                        }
                    }
                }
                if(orderInfo.getCategoryId()==0 && orderInfo.getOrderCheck()==0){
                    orderAdapter.add(orderInfo);
                }
            }
        }
    }

    class TableAdapter extends BaseAdapter<OrderInfo, TableHolder>{

        public TableAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(final TableHolder holder, View view) {
            holder.tableNumberText = view.findViewById(R.id.tableNumberText);
            holder.tableListView = view.findViewById(R.id.tableListView);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void setListItem(final TableHolder holder, final OrderInfo item) {
            holder.tableNumberText.setText(String.valueOf(item.getTableNumber()));
            orderAdapter = new OrderAdapter(MainActivity.this, R.layout.order_item);
            setOrderList(item);
            holder.tableListView.setAdapter(orderAdapter);
            holder.tableListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        moveToDetail(item);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    class OrderAdapter extends BaseAdapter<OrderInfo, OrderHolder>{

        public OrderAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(OrderHolder holder, View view) {
            holder.menuNameText = view.findViewById(R.id.menuNameText);
            holder.menuCountText = view.findViewById(R.id.menuCountText);
        }

        @Override
        public void setListItem(OrderHolder holder, OrderInfo item) {
            holder.menuNameText.setText(item.getOrderKor());
            if(item.getOrderCount()>0){
                holder.menuCountText.setText(String.valueOf(item.getOrderCount()));
            }else{
                holder.menuCountText.setText("");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void CheckOrder(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                int thisDate = (int) (new Date().getTime()/1000);
                Log.d("Console", "THIS DATE : "+thisDate);

                try {
                    JSONArray resultArray = new JSONArray(result);
                    if(resultArray.length()>0){
                        for(int k=0; k<resultArray.length(); k++){
                            OrderInfo orderInfo = new OrderInfo();
                            orderInfo.setMenuId(resultArray.getJSONObject(k).getInt("menuId"));
                            orderInfo.setCategoryId(resultArray.getJSONObject(k).getInt("categoryId"));
                            orderInfo.setOrderKor(resultArray.getJSONObject(k).getString("orderKor"));
                            orderInfo.setTableNumber(resultArray.getJSONObject(k).getInt("tableNumber"));
                            orderInfo.setOrderDate(thisDate);
                            orderInfo.setOrderCount(resultArray.getJSONObject(k).getInt("orderTotal"));
                            orderInfo.setOrderPrice(resultArray.getJSONObject(k).getInt("orderPrice"));
                            orderInfo.setOrderCheck(0);
                            dbHelper.insertOrderInfo(orderInfo);
                            if(k==resultArray.length()-1){
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(MainActivity.this, notification);
                                r.play();
                                tableAdapter.clear();
                                tableAdapter.notifyDataSetChanged();
                                setTableGridView();

                                Log.d("CONSOLE", "RESET!");
                            }
                        }

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokEmployee/checkOrder.php", "storeId="+storeInfo.getStoreId());
    }

    // 데이터 받을때 쓰는것
    class MyServer implements Runnable{
        ServerSocket serverSocket;
        Socket s;
        DataInputStream dis;
        String received;
        Handler handler = new Handler();
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(9700);
                while(true){
                    s = serverSocket.accept();
                    dis = new DataInputStream(s.getInputStream());
                    received = dis.readUTF();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Console", "SOCKET RECEIVED : "+received);
                            if(Integer.parseInt(received)>0) {
                                OrderInfo orderInfo = new OrderInfo();
                                orderInfo.setOrderCheck(0);
                                orderInfo.setCategoryId(0);
                                orderInfo.setOrderCount(0);
                                orderInfo.setTableNumber(Integer.parseInt(received));
                                orderInfo.setOrderKor("호출");
                                orderInfo.setMenuId(0);
                                orderInfo.setOrderDate(0);
                                orderInfo.setOrderPrice(0);
                                dbHelper.insertOrderInfo(orderInfo);
                                tableAdapter.clear();
                                setTableGridView();
                                tableAdapter.notifyDataSetChanged();
                            }else if(Integer.parseInt(received)==0){
                                CheckOrder();
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tableAdapter.clear();
        setTableGridView();
    }
}

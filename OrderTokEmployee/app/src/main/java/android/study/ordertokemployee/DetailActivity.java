package android.study.ordertokemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.study.ordertokemployee.Back.BaseAdapter;
import android.study.ordertokemployee.Back.DBHelper;
import android.study.ordertokemployee.Back.SocketReceiveClass;
import android.study.ordertokemployee.HolderPackage.DetailHolder;
import android.study.ordertokemployee.HolderPackage.OrderHolder;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.study.ordertokemployee.InfoPackage.OrderInfo;
import android.study.ordertokemployee.InfoPackage.ServerInfo;
import android.study.ordertokemployee.InfoPackage.StoreInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;

    ListView orderDetailListView;
    Button confirmButton, cancelButton;
    TextView tableDetailTitle;

    int tableNumber;
    int receivedOrderDate;
    OrderDetailAdapter orderDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dbHelper = new DBHelper(DetailActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread thread = new Thread(new SocketReceiveClass(DetailActivity.this));
        thread.start();

        orderDetailListView = findViewById(R.id.orderDetailListView);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
        tableDetailTitle = findViewById(R.id.tableDetailTitle);

        Intent receivedIntent = getIntent();
        tableNumber = receivedIntent.getIntExtra("tableNumber", 0);
        receivedOrderDate = receivedIntent.getIntExtra("orderDate", 0);

        if(tableNumber == 0 || receivedOrderDate ==0){
            Toast.makeText(DetailActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(returnIntent);
        }

        tableDetailTitle.setText(tableNumber+"번 테이블");

        orderDetailAdapter = new OrderDetailAdapter(DetailActivity.this, R.layout.detail_order_item);
        orderDetailListView.setAdapter(orderDetailAdapter);

        // 취소 버튼 클릭
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(returnIntent);
                overridePendingTransition(0,0);
            }
        });

        // 확인 버튼
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.allOrderCheck(tableNumber, receivedOrderDate);
                Intent returnIntent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(returnIntent);
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderDetailAdapter.clear();
        setOrderDetailList(tableNumber);
    }

    public void setOrderDetailList(int tableNumber){
        List<OrderInfo> orderInfoList = dbHelper.selectTableOrder(tableNumber,receivedOrderDate);
        if(orderInfoList!=null&&orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                List<CategoryInfo> categoryInfoList = dbHelper.getSelectedCategoryInfo();
                if(categoryInfoList!=null&&categoryInfoList.size()>0){
                    for(int j=0; j<categoryInfoList.size(); j++){
                        CategoryInfo categoryInfo = categoryInfoList.get(j);
                        if(categoryInfo.getCategoryId()==orderInfo.getCategoryId() && orderInfo.getOrderCheck()==0){
                            orderDetailAdapter.add(orderInfo);
                        }
                    }
                }
                if(orderInfo.getCategoryId()==0 && orderInfo.getOrderCheck()==0){
                    orderDetailAdapter.add(orderInfo);
                }
            }
        }
    }

    class OrderDetailAdapter extends BaseAdapter<OrderInfo, DetailHolder>{

        public OrderDetailAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(DetailHolder holder, View view) {
            holder.detailMenuNameText = view.findViewById(R.id.detailMenuNameText);
            holder.detailMenuCountText = view.findViewById(R.id.detailMenuCountText);
        }

        @Override
        public void setListItem(DetailHolder holder, OrderInfo item) {
            holder.detailMenuNameText.setText(item.getOrderKor());
            if(item.getOrderCount()>0){
                holder.detailMenuCountText.setText(String.valueOf(item.getOrderCount()));
            }else{
                holder.detailMenuCountText.setText("");
            }
        }
    }
}

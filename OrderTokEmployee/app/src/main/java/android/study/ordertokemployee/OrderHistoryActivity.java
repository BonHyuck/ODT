package android.study.ordertokemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.study.ordertokemployee.Back.BaseAdapter;
import android.study.ordertokemployee.Back.DBHelper;
import android.study.ordertokemployee.HolderPackage.OrderHistoryHolder;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.study.ordertokemployee.InfoPackage.OrderInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    DBHelper dbHelper;

    Button closeButton;
    ListView orderHistoryListView;
    OrderHistoryAdapter orderHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dbHelper = new DBHelper(OrderHistoryActivity.this);

        closeButton = findViewById(R.id.closeButton);
        orderHistoryListView = findViewById(R.id.orderHistoryListView);
        orderHistoryAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this, R.layout.order_history_item);
        orderHistoryListView.setAdapter(orderHistoryAdapter);
        setOrderHistoryAdapter();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                startActivity(returnIntent);
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderHistoryAdapter.clear();
        setOrderHistoryAdapter();
    }

    public void setOrderHistoryAdapter(){
        List<OrderInfo> orderInfoList = dbHelper.getCheckOrder();
        if(orderInfoList!=null && orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                Log.d("Console", "ORDERINFO : "+orderInfo.getOrderKor());
                orderHistoryAdapter.add(orderInfo);
            }
        }
    }

    class OrderHistoryAdapter extends BaseAdapter<OrderInfo, OrderHistoryHolder>{

        public OrderHistoryAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(OrderHistoryHolder holder, View view) {
            holder.oneHistoryName = view.findViewById(R.id.oneHistoryName);
            holder.oneHistoryCount = view.findViewById(R.id.oneHistoryCount);
            holder.oneHistoryNumber = view.findViewById(R.id.oneHistoryNumber);
            holder.oneHistoryDate = view.findViewById(R.id.oneHistoryDate);
        }

        /**
         * 자식클래스가 Override해야하는 추상 메서드 정의 2
         * 컴포넌트 할당이 완료된 holder와 표시해야 할 데이터가 저장된 item 객체를 전달 받아서
         * 데이터를 컴포넌트에 출력하는 기능을 구현해야한다.
         *
         * @param holder - Holder의 객체
         * @param item   - Holder에 표시할 내용을 저장하고 있는 데이터 객체
         */
        @Override
        public void setListItem(OrderHistoryHolder holder, OrderInfo item) {
            holder.oneHistoryName.setText(item.getOrderKor());
            holder.oneHistoryCount.setText(String.valueOf(item.getOrderCount()));
            holder.oneHistoryNumber.setText(String.valueOf(item.getTableNumber()));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            holder.oneHistoryDate.setText(df.format(new Date((long) item.getOrderDate()*1000)));
        }
    }
}

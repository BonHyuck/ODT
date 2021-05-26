package android.study.ordertok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.study.ordertok.Back.BaseAdapter;
import android.study.ordertok.Back.DBHelper;
import android.study.ordertok.Back.SocketReceiveClass;
import android.study.ordertok.HolderPackage.OrderHistoryHolder;
import android.study.ordertok.InfoPackage.MenuInfo;
import android.study.ordertok.InfoPackage.OrderInfo;
import android.study.ordertok.InfoPackage.ServerInfo;
import android.study.ordertok.InfoPackage.StoreInfo;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    // 필요객체 선언
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    int languageNumber;
    int totalValue;
    String[] orderHistoryTitleArray = {"주문 내역", "Order History", "序数历史", "注文履歴"};
    String[] orderHistoryMenuNameArray = {"메뉴 이름", "Menu Name", "菜单名", "メニュー名"};
    String[] orderHistoryMenuCountArray = {"수량", "Count", "伯爵", "カウント"};
    String[] orderHistoryMenuPriceArray = {"가격", "Price", "普赖斯", "価格"};
    String[] orderHistoryPaymentArray = {"결제 금액", "Payment Amount", "支付金额", "支払額"};
    String[] orderHistoryPerPersonArray = {" /1인", " /pp", " /一人", " /一人"};
    String[] orderHistoryCloseArray = {"닫 기","Close","关 门","閉 じ る"};
    String[] orderHistoryTotalArray = {"합 계","TOTAL","求 和","合 計"};
    String[] orderHistoryRestArray = {"나머지 : ", "Change", "余", "残り"};
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    // 레이아웃 요소 선언
    TextView orderHistoryTitle, orderHistoryMenuName, orderHistoryMenuCount, orderHistoryMenuPrice, orderHistoryPerson, orderHistoryPayment, orderHistoryNewTotal, orderHistoryPerPerson, orderHistoryRest, orderHistoryOldTotal, orderHistoryTotalString;
    ListView orderHistoryListView;
    OrderHistoryAdapter orderHistoryAdapter;
    Button orderHistoryMinus, orderHistoryPlus, orderHistoryClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        dbHelper = new DBHelper(OrderHistoryActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        Intent intent = getIntent();
        languageNumber = intent.getIntExtra("languageNumber", 0);
        if(languageNumber<0||languageNumber>3){
            Intent returnIntent = new Intent(OrderHistoryActivity.this, LanguageActivity.class);
            startActivity(returnIntent);
        }

        Thread thread = new Thread(new SocketReceiveClass(OrderHistoryActivity.this));
        thread.start();

        // 레이아웃 요소 할당
        // 텍스트뷰
        // 일반 텍스트
        orderHistoryTitle = findViewById(R.id.orderHistoryTitle);
        orderHistoryMenuName = findViewById(R.id.orderHistoryMenuName);
        orderHistoryMenuCount = findViewById(R.id.orderHistoryMenuCount);
        orderHistoryMenuPrice = findViewById(R.id.orderHistoryMenuPrice);
        orderHistoryPayment = findViewById(R.id.orderHistoryPayment);
        orderHistoryPerPerson = findViewById(R.id.orderHistoryPerPerson);
        orderHistoryTotalString = findViewById(R.id.orderHistoryTotalString);

        // 값이 있는 텍스트
        orderHistoryPerson = findViewById(R.id.orderHistoryPerson);
        orderHistoryNewTotal = findViewById(R.id.orderHistoryNewTotal);
        orderHistoryRest = findViewById(R.id.orderHistoryRest);
        orderHistoryOldTotal = findViewById(R.id.orderHistoryOldTotal);

        //버튼
        orderHistoryMinus = findViewById(R.id.orderHistoryMinus);
        orderHistoryPlus = findViewById(R.id.orderHistoryPlus);
        orderHistoryClose = findViewById(R.id.orderHistoryClose);
        // 리스트뷰
        orderHistoryListView = findViewById(R.id.orderHistoryListView);
        orderHistoryAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this, R.layout.order_history_item);
        orderHistoryListView.setAdapter(orderHistoryAdapter);

        // 언어별 텍스트 주입
        orderHistoryTitle.setText(orderHistoryTitleArray[languageNumber]);
        orderHistoryMenuName.setText(orderHistoryMenuNameArray[languageNumber]);
        orderHistoryMenuCount.setText(orderHistoryMenuCountArray[languageNumber]);
        orderHistoryMenuPrice.setText(orderHistoryMenuPriceArray[languageNumber]);
        orderHistoryPayment.setText(orderHistoryPaymentArray[languageNumber]);
        orderHistoryTotalString.setText(orderHistoryTotalArray[languageNumber]);
        orderHistoryPerPerson.setText(orderHistoryPerPersonArray[languageNumber]);
        orderHistoryClose.setText(orderHistoryCloseArray[languageNumber]);

        //버튼 기능
        orderHistoryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                returnIntent.putExtra("languageNumber", languageNumber);
                startActivity(returnIntent);
                overridePendingTransition(0,0);
            }
        });

        orderHistoryMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int person = Integer.parseInt(orderHistoryPerson.getText().toString());
                int total = totalValue;
                person--;
                if(person<1){
                    person = 1;
                }
                int personPayment = (total/(person*100))*100;
                int rest = total - (personPayment*person);
                orderHistoryPerson.setText(String.valueOf(person));
                orderHistoryRest.setText(decimalFormat.format(rest));
                orderHistoryNewTotal.setText(decimalFormat.format(personPayment));
            }
        });

        orderHistoryPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int person = Integer.parseInt(orderHistoryPerson.getText().toString());
                int total = totalValue;
                person++;
                if(person>30){
                    person = 30;
                }
                int personPayment = (total/(person*100))*100;
                int rest = total - (personPayment*person);
                orderHistoryPerson.setText(String.valueOf(person));
                orderHistoryRest.setText(decimalFormat.format(rest));
                orderHistoryNewTotal.setText(decimalFormat.format(personPayment));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderHistoryAdapter.clear();
        setOrderHistoryList();
    }

    public void setOrderHistoryList(){
        totalValue = 0;
        List<OrderInfo> orderInfoList = dbHelper.getAllOrderInfo();
        if(orderInfoList!=null&&orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                if(orderInfo.getOrderTotal()>0){
                    orderHistoryAdapter.add(orderInfo);
                    totalValue += (orderInfo.getOrderTotal()*orderInfo.getOrderPrice());
                }
            }
        }
        orderHistoryPerson.setText("1");
        orderHistoryRest.setText("0");
        orderHistoryNewTotal.setText(decimalFormat.format(totalValue));
        orderHistoryOldTotal.setText(decimalFormat.format(totalValue));
    };

    class OrderHistoryAdapter extends BaseAdapter<OrderInfo, OrderHistoryHolder>{

        public OrderHistoryAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        /**
         * 자식클래스가 Override해야하는 추상 메서드 정의 1
         * 자식 클래스는 파라미터로 전달받은 holder에 선언된 컴포넌트의 객체에
         * LayoutInflater에 의해서 생성된 view에서 컴포넌트를 추출하여 참조시켜야 한다.
         *
         * @param holder - Holder의 객체
         * @param view   -  LayoutInflater에 의해서 생성된 객체
         */
        @Override
        public void setListHolder(OrderHistoryHolder holder, View view) {
            holder.orderHistoryOneMenuCount = view.findViewById(R.id.orderHistoryOneMenuCount);
            holder.orderHistoryOneMenuName = view.findViewById(R.id.orderHistoryOneMenuName);
            holder.orderHistoryOneMenuPrice = view.findViewById(R.id.orderHistoryOneMenuPrice);
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
            holder.orderHistoryOneMenuCount.setText(String.valueOf(item.getOrderTotal()));
            holder.orderHistoryOneMenuPrice.setText(decimalFormat.format(item.getOrderPrice()*item.getOrderTotal()));
            switch (languageNumber){
                case 0:
                    holder.orderHistoryOneMenuName.setText(item.getOrderKor());
                    break;
                case 1:
                    holder.orderHistoryOneMenuName.setText(item.getOrderEng());
                    break;
                case 2:
                    holder.orderHistoryOneMenuName.setText(item.getOrderChn());
                    break;
                case 3:
                    holder.orderHistoryOneMenuName.setText(item.getOrderJpn());
                    break;
            }

        }
    }
}

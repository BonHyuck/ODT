package android.study.ordertok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.study.ordertok.Back.BaseAdapter;
import android.study.ordertok.Back.DBHelper;
import android.study.ordertok.Back.HttpConnect;
import android.study.ordertok.Back.SocketReceiveClass;
import android.study.ordertok.HolderPackage.OrderCheckHolder;
import android.study.ordertok.InfoPackage.OrderInfo;
import android.study.ordertok.InfoPackage.ServerInfo;
import android.study.ordertok.InfoPackage.StoreInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderCheckActivity extends AppCompatActivity {

    // 필수 객체 선언
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    int languageNumber;
    OrderCheckAdapter orderCheckAdapter;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    // 레이아웃 요소
    TextView orderCheckTitle, orderCheckMenuName, orderCheckMenuCount, orderCheckMenuPrice, orderTotal, orderCheckMenuTotal;
    ListView orderCheckListView;
    Button orderCheckCancelButton, orderCheckDoneButton;

    // 필요 배열
    String[] orderCheckTitleArray = {"주문 확인", "Order Check", "指令检查", "注文確認"};
    String[] orderCheckMenuNameArray = {"메뉴 이름", "Menu Name", "菜单名", "メニュー名"};
    String[] orderCheckMenuCountArray = {"수량", "Count", "伯爵", "カウント"};
    String[] orderCheckMenuPriceArray = {"가격", "Price", "普赖斯", "価格"};
    String[] orderTotalText = {"합계","TOTAL","求和","合計"};
    String[] orderCheckCancelButtonText = {"닫기","Close","关门","閉じる"};
    String[] orderCheckDoneButtonText = {"주문하기","Place an order","下订单","オーダ"};

    String menuIdArray,menuCountArray,orderKorArray,orderEngArray,orderChnArray,orderJpnArray, orderPriceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check);

        // 필수객체 할당
        dbHelper = new DBHelper(OrderCheckActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        Intent intent = getIntent();
        languageNumber = intent.getIntExtra("languageNumber", 0);
        if(languageNumber<0||languageNumber>3){
            Intent returnIntent = new Intent(OrderCheckActivity.this, LanguageActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }

        Thread thread = new Thread(new SocketReceiveClass(OrderCheckActivity.this));
        thread.start();

//일반 텍스트
        orderCheckTitle = findViewById(R.id.orderCheckTitle);
        orderCheckMenuName = findViewById(R.id.orderCheckMenuName);
        orderCheckMenuCount = findViewById(R.id.orderCheckMenuCount);
        orderCheckMenuPrice = findViewById(R.id.orderCheckMenuPrice);
        orderTotal = findViewById(R.id.orderTotal);
        // 값 변동하는 텍스트
        orderCheckMenuTotal = findViewById(R.id.orderCheckMenuTotal);
        // 버튼
        orderCheckCancelButton = findViewById(R.id.orderCheckCancelButton);
        orderCheckDoneButton = findViewById(R.id.orderCheckDoneButton);

        // 언어별 설정
        orderCheckTitle.setText(orderCheckTitleArray[languageNumber]);
        orderCheckMenuName.setText(orderCheckMenuNameArray[languageNumber]);
        orderCheckMenuCount.setText(orderCheckMenuCountArray[languageNumber]);
        orderCheckMenuPrice.setText(orderCheckMenuPriceArray[languageNumber]);
        orderTotal.setText(orderTotalText[languageNumber]);
        orderCheckCancelButton.setText(orderCheckCancelButtonText[languageNumber]);
        orderCheckDoneButton.setText(orderCheckDoneButtonText[languageNumber]);

        // 리스트 뷰
        orderCheckListView = findViewById(R.id.orderCheckListView);
        orderCheckAdapter = new OrderCheckAdapter(OrderCheckActivity.this, R.layout.order_check_item);
        orderCheckListView.setAdapter(orderCheckAdapter);

        // 버튼 기능 추가
        orderCheckCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelIntent = new Intent(OrderCheckActivity.this, MainActivity.class);
                cancelIntent.putExtra("languageNumber", languageNumber);
                startActivity(cancelIntent);
                overridePendingTransition(0,0);
            }
        });

        orderCheckDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!orderCheckAdapter.isEmpty()&&orderCheckAdapter.getCount()>0){
                    menuIdArray = "";
                    menuCountArray = "";
                    orderKorArray = "";
                    orderEngArray = "";
                    orderChnArray = "";
                    orderJpnArray = "";
                    orderPriceArray = "";
                    for(int i =0; i<orderCheckAdapter.getCount(); i++){
                        OrderInfo orderInfo = orderCheckAdapter.getItem(i);

                        if(i==0){
                            menuIdArray = menuIdArray+orderInfo.getMenuId();
                            menuCountArray = menuCountArray+orderInfo.getOrderCount();
                            orderKorArray = orderKorArray+orderInfo.getOrderKor();
                            orderEngArray = orderEngArray+orderInfo.getOrderEng();
                            orderChnArray = orderChnArray+orderInfo.getOrderChn();
                            orderJpnArray = orderJpnArray+orderInfo.getOrderJpn();
                            orderPriceArray = orderPriceArray+orderInfo.getOrderPrice();
                        }else{
                            menuIdArray = menuIdArray+","+orderInfo.getMenuId();
                            menuCountArray = menuCountArray+","+orderInfo.getOrderCount();
                            orderKorArray = orderKorArray+","+orderInfo.getOrderKor();
                            orderEngArray = orderEngArray+","+orderInfo.getOrderEng();
                            orderChnArray = orderChnArray+","+orderInfo.getOrderChn();
                            orderJpnArray = orderJpnArray+","+orderInfo.getOrderJpn();
                            orderPriceArray = orderPriceArray+","+orderInfo.getOrderPrice();
                        }
                        int newTotal = orderInfo.getOrderTotal()+orderInfo.getOrderCount();
                        dbHelper.updateOrderInfoTotal(orderInfo, newTotal);
                        dbHelper.updateOrderInfoCount(orderInfo, 0);
                    }

                    InsertData();
                }
                Intent doneIntent = new Intent(OrderCheckActivity.this, MainActivity.class);
                doneIntent.putExtra("languageNumber", languageNumber);
                startActivity(doneIntent);
                overridePendingTransition(0,0);
            }
        });
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        orderCheckAdapter.clear();
        setOrderCheck();
        setNewTotal();
    }

    public void setOrderCheck(){
        List<OrderInfo> orderInfoList = dbHelper.getAllOrderInfo();
        if(orderInfoList!=null && orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                if(orderInfo.getOrderCount()>0){
                    orderCheckAdapter.add(orderInfo);
                }
            }
        }
    }

    class OrderCheckAdapter extends BaseAdapter<OrderInfo, OrderCheckHolder>{

        public OrderCheckAdapter(@NonNull Activity activity, int resource) {
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
        public void setListHolder(OrderCheckHolder holder, View view) {
            // 내부 버튼
            holder.orderCheckMinus = view.findViewById(R.id.orderCheckMinus);
            holder.orderCheckPlus = view.findViewById(R.id.orderCheckPlus);
            holder.orderCheckOneCancel = view.findViewById(R.id.orderCheckOneCancel);
            // 텍스트
            holder.orderCheckOneCount = view.findViewById(R.id.orderCheckOneCount);
            holder.orderCheckOneName = view.findViewById(R.id.orderCheckOneName);
            holder.orderCheckOnePrice = view.findViewById(R.id.orderCheckOnePrice);
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
        public void setListItem(final OrderCheckHolder holder, final OrderInfo item) {
            String[] oneCancelButton = {"취소", "Cancel", "取消", "X"};

            switch(languageNumber){
                case 0:
                    holder.orderCheckOneName.setText(item.getOrderKor());
                    break;
                case 1:
                    holder.orderCheckOneName.setText(item.getOrderEng());
                    break;
                case 2:
                    holder.orderCheckOneName.setText(item.getOrderChn());
                    break;
                case 3:
                    holder.orderCheckOneName.setText(item.getOrderJpn());
                    break;
            }

            holder.orderCheckOnePrice.setText(decimalFormat.format(item.getOrderPrice()*item.getOrderCount()));
            holder.orderCheckOneCount.setText(String.valueOf(item.getOrderCount()));
            holder.orderCheckOneCancel.setText(oneCancelButton[languageNumber]);

            // 버튼 기능 추가
            holder.orderCheckMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.orderCheckOneCount.getText().toString());
                    count--;
                    if(count<1){
                        count = 1;
                    }
                    item.setOrderCount(count);
                    orderCheckAdapter.notifyDataSetChanged();
                    setNewTotal();
                }
            });
            holder.orderCheckPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.orderCheckOneCount.getText().toString());
                    count++;
                    if(count>30){
                        count = 30;
                    }
                    item.setOrderCount(count);
                    orderCheckAdapter.notifyDataSetChanged();
                    setNewTotal();
                        /*finalOrderCheckHolder.orderCheckOneCount.setText(String.valueOf(count));
                        finalOrderCheckHolder.orderCheckOnePrice.setText(myFormatter.format(orderInfo.getOrderPrice()*count));*/
                }
            });
            holder.orderCheckOneCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                        orderInfo.setOrderCount(0);
//                        dbHelper.updateOrder(orderInfo);
                    dbHelper.updateOrderInfoCount(item, 0);

                    remove(item);
                    orderCheckAdapter.notifyDataSetChanged();
                    setNewTotal();
                }
            });

        }
    }

    public void setNewTotal(){
        int total = 0;
        if(!orderCheckAdapter.isEmpty()&&orderCheckAdapter.getCount()>0){
            for(int i=0; i<orderCheckAdapter.getCount(); i++){
                OrderInfo orderInfo = orderCheckAdapter.getItem(i);
                total += (orderInfo.getOrderCount()*orderInfo.getOrderPrice());
            }
        }
        orderCheckMenuTotal.setText(decimalFormat.format(total));
    }

    @SuppressLint("StaticFieldLeak")
    public void InsertData(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                Log.d("CONSOLE", "INSERT RESULT "+result);
                String[] resultArray = result.split(",,,");
                // result = POS IP 주소
                SendData sendData = new SendData();
                SendEmployeeData sendEmployeeData = new SendEmployeeData();
                if(resultArray.length>1){
                    if(resultArray[0].contains(".")){
                        sendData.execute(resultArray[0], "dish,,,"+storeInfo.getTableNumber());
                        dbHelper.updatePosIp(resultArray[0]);
                    }else{
                        sendData.execute(storeInfo.getPosIpAddress(), "dish,,,"+storeInfo.getTableNumber());
                    }
                    if(resultArray[1].contains(".")){
                        sendEmployeeData.execute(resultArray[1], String.valueOf(0));
                        dbHelper.updateEmployeeIp(resultArray[1]);
                    }else{
                        sendEmployeeData.execute(storeInfo.getEmployeeIpAddress(), String.valueOf(0));
                    }
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTok/setInfo.php", "storeId="+storeInfo.getStoreId()+"&menuId="+menuIdArray+"&menuCount="+menuCountArray+"&orderKor="+orderKorArray+"&orderEng="+orderEngArray+"&orderChn="+orderChnArray+"&orderJpn="+orderJpnArray+"&tableNumber="+storeInfo.getTableNumber()+"&orderPrice="+orderPriceArray+"&newIpAddress="+serverInfo.getLocalIpAddress());
    }

    //데이터 보낼때 쓰는것
    class SendEmployeeData extends AsyncTask<String, Void, String> {

        Socket socket;
        DataOutputStream dos;
        String ip, message;

        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            message = params[1];

            try{
                socket = new Socket(ip, 9700);
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(message);

                dos.close();
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }

    //데이터 보낼때 쓰는것
    static class SendData extends AsyncTask<String, Void, String> {

        Socket socket;
        DataOutputStream dos;
        String ip, message;

        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            message = params[1];

            try{
                socket = new Socket(ip, 9750);
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(message);

                dos.close();
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }
}

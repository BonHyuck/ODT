package android.study.ordertokmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.study.ordertokmenu.Back.BaseAdapter;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.Back.SocketReceiveClass;
import android.study.ordertokmenu.HolderPackage.CategoryHolder;
import android.study.ordertokmenu.HolderPackage.MenuHolder;
import android.study.ordertokmenu.HolderPackage.OrderHolder;
import android.study.ordertokmenu.InfoPackage.CategoryInfo;
import android.study.ordertokmenu.InfoPackage.MenuInfo;
import android.study.ordertokmenu.InfoPackage.OrderInfo;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 필요 객체 선언
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    DBHelper dbHelper;
    int languageNumber;
    int total;
    int selectedCategoryId;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    // 레이아웃 구성요소
    ImageButton homeButton;
    Button orderHistoryButton, eventButton, callButton, deleteAllButton, orderButton, tableNoButton;
    TextView orderCheckText, storeNameText, totalText, totalPrice;
    // 리스트뷰 및 그리드뷰 요소
    GridView menuGridView;
    ListView categoryList, orderList;
    CategoryAdapter categoryAdapter;
    MenuAdapter menuAdapter;
    OrderAdapter orderAdapter;
    // 언어에 따른 텍스트
    String[] eventArray = {"이벤트/게임","Event/Game","事件/游戏", "イベント/ゲーム"};
    String[] orderHistoryArray = {"주문서","OrderSheet","订单","注文シート"};
    String[] orderCheckButtonArray = {"주문하기","Order","订购","ご注文"};
    String[] orderDeleteArray = {"주문 전체삭제","Delete All","删除所有","すべて削除"};
    String[] callArray = {"직원호출","Call","呼叫","コール"};
    String[] orderCheckArray={"주문확인","Order confirmation","注文確認","订单确认"};
    String[] totalTextArray = {"합계 : ", "Total : ", "总计 : ", "合計 : "};
    String[] oneOrderButtonArray = {"담기","Order","咒语","注文"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 내부 객체 할당
        dbHelper = new DBHelper(MainActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread thread = new Thread(new SocketReceiveClass(MainActivity.this));
        thread.start();

        //필요 객체 할당
        Intent intent = getIntent();
        languageNumber = intent.getIntExtra("languageNumber", 0);
        if(languageNumber<0||languageNumber>3){
            Intent returnIntent = new Intent(MainActivity.this, LanguageActivity.class);
            startActivity(returnIntent);
        }

        // 레이아웃 구성요소 할당
        homeButton = findViewById(R.id.homeButton);
        orderHistoryButton = findViewById(R.id.orderHistoryButton);
        deleteAllButton = findViewById(R.id.deleteAllButton);
        orderButton = findViewById(R.id.orderButton);
        orderCheckText = findViewById(R.id.orderCheckText);
        storeNameText = findViewById(R.id.storeNameText);
        totalText = findViewById(R.id.totalText);
        totalPrice = findViewById(R.id.totalPrice);
        tableNoButton = findViewById(R.id.tableNoButton);

        storeNameText.setText(storeInfo.getStoreName());

        // 언어에 따른 버튼 변경
        orderHistoryButton.setText(orderHistoryArray[languageNumber]);
        orderCheckText.setText(orderCheckArray[languageNumber]);
        orderButton.setText(orderCheckButtonArray[languageNumber]);
        deleteAllButton.setText(orderDeleteArray[languageNumber]);
        totalText.setText(totalTextArray[languageNumber]);

        selectedCategoryId = dbHelper.getAllCategoryInfo().get(0).getCategoryId();

        // 카테고리 리스트뷰
        categoryList = findViewById(R.id.categoryList);
        categoryAdapter = new CategoryAdapter(MainActivity.this, R.layout.category_item);
        categoryList.setAdapter(categoryAdapter);
        // 카테고리 클릭
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategoryId = categoryAdapter.getItem(i).getCategoryId();
                menuAdapter.clear();
                menuAdapter.notifyDataSetChanged();
                setMenuGridView();
            }
        });

        //메뉴 리스트
        menuGridView = findViewById(R.id.menuGridView);
        menuAdapter = new MenuAdapter(MainActivity.this, R.layout.menu_item);
        menuGridView.setAdapter(menuAdapter);
        //메뉴 클릭
        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newIntent;
                MenuInfo menuInfo = menuAdapter.getItem(i);
                if(dbHelper.getOneCategoryInfo(selectedCategoryId).getCategorySet()>0){
                    Log.d("Console", "SET");
                    newIntent = new Intent(MainActivity.this, SetDetailActivity.class);
                } else if(menuInfo.getMenuOption()>0){
                    Log.d("Console", "OPTION");
                    newIntent = new Intent(MainActivity.this, OptionDetailActivity.class);
                }else{
                    Log.d("Console", "MENU");
                    newIntent = new Intent(MainActivity.this, MenuDetailActivity.class);
                }
                newIntent.putExtra("menuId", menuInfo.getMenuId());
                newIntent.putExtra("languageNumber", languageNumber);
                startActivity(newIntent);
                overridePendingTransition(0, 0);
            }
        });

        //주문 리스트
        orderList = findViewById(R.id.orderList);
        orderAdapter = new OrderAdapter(MainActivity.this, R.layout.order_item);
        orderList.setAdapter(orderAdapter);

        // MainActivity 내 버튼 기능
        // 버튼에 테이블 번호 넣기
        tableNoButton.setText(storeInfo.getTableNumber()+"");

        // 테이블 초기화
        tableNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5  = new Intent(MainActivity.this, tableNoPopup.class);
                startActivity(intent5);

                overridePendingTransition(0,0);

            }
        });
        // 홈버튼
        Glide.with(MainActivity.this).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+storeInfo.getLogoImgPath()).centerInside().into(homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(MainActivity.this, LanguageActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(0, 0);
            }
        });
        // 주문 전체 삭제
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterLength = orderAdapter.getCount();
                if(adapterLength>0){
                    for(int k = 0; k<adapterLength; k++){
                        OrderInfo newOrderInfo = orderAdapter.getItem(k);
                        dbHelper.updateOrderInfoCount(newOrderInfo,0);
                    }
                }
                orderAdapter.clear();
                orderAdapter.notifyDataSetChanged();
                totalPrice.setText("0");
            }
        });
        // 주문서 버튼
        orderHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(MainActivity.this, OrderHistoryActivity.class);
                historyIntent.putExtra("languageNumber", languageNumber);
                startActivity(historyIntent);
                overridePendingTransition(0,0);
            }
        });
        // 주문하기 버튼
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderIntent = new Intent(MainActivity.this, OrderCheckActivity.class);
                orderIntent.putExtra("languageNumber", languageNumber);
                startActivity(orderIntent);
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
        categoryAdapter.clear();
        setCategoryListView();
        menuAdapter.clear();
        setMenuGridView();
        orderAdapter.clear();
        setOrderListView();
        setNewTotal();
    }

    // 카테고리 세팅
    public void setCategoryListView(){
        List<CategoryInfo> categoryInfoList = dbHelper.getAllCategoryInfo();
        for(int i=0; i<categoryInfoList.size(); i++){
            CategoryInfo categoryInfo = categoryInfoList.get(i);
            categoryAdapter.add(categoryInfo);
        }
    }

    // 카테고리에 맞는 메뉴 세팅
    public void setMenuGridView(){
        List<MenuInfo> menuInfoList = dbHelper.getAllMenuInfo(selectedCategoryId);
        if(menuInfoList!=null&&menuInfoList.size()>0){
            for(int j=0; j<menuInfoList.size(); j++){
                MenuInfo menuInfo = menuInfoList.get(j);
                menuAdapter.add(menuInfo);
            }
        }
    }

    // 주문 내역 세팅
    public void setOrderListView(){
        List<OrderInfo> orderInfoList = dbHelper.getAllOrderInfo();
        if(orderInfoList != null && orderInfoList.size()>0){
            for(int k=0; k<orderInfoList.size(); k++){
                OrderInfo orderInfo = orderInfoList.get(k);
                if(orderInfo.getOrderCount()>0){
                    orderAdapter.add(orderInfo);
                }
            }
        }
    }

    // 합계 계산
    public void setNewTotal(){
        total = 0;
        List<OrderInfo> orderInfoList = dbHelper.getAllOrderInfo();
        if(orderInfoList != null && orderInfoList.size()>0){
            for(int k=0; k<orderInfoList.size(); k++){
                OrderInfo orderInfo = orderInfoList.get(k);
                if(orderInfo.getOrderCount()>0){
                    total += (orderInfo.getOrderCount()*orderInfo.getOrderPrice());
                }
            }
        }
        totalPrice.setText(decimalFormat.format(total));
    }

    class CategoryAdapter extends BaseAdapter<CategoryInfo, CategoryHolder>{

        public CategoryAdapter(@NonNull Activity activity, int resource) {
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
        public void setListHolder(CategoryHolder holder, View view) {
            holder.categoryText = view.findViewById(R.id.categoryText);
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
        public void setListItem(CategoryHolder holder, CategoryInfo item) {
            switch (languageNumber){
                case 0:
                    holder.categoryText.setText(item.getCategoryKor());
                    break;
                case 1:
                    holder.categoryText.setText(item.getCategoryEng());
                    break;
                case 2:
                    holder.categoryText.setText(item.getCategoryChn());
                    break;
                case 3:
                    holder.categoryText.setText(item.getCategoryJpn());
                    break;
            }

        }
    }

    class MenuAdapter extends BaseAdapter<MenuInfo, MenuHolder>{

        public MenuAdapter(@NonNull Activity activity, int resource) {
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
        public void setListHolder(MenuHolder holder, View view) {
            holder.menuImgView = view.findViewById(R.id.menuImgView);
            holder.menuButton = view.findViewById(R.id.menuButton);
            holder.menuName = view.findViewById(R.id.menuName);
            holder.menuPrice = view.findViewById(R.id.menuPrice);
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
        public void setListItem(MenuHolder holder, final MenuInfo item) {
            Glide.with(MainActivity.this).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+item.getImgPath()).centerInside().into(holder.menuImgView);
            holder.menuPrice.setText(decimalFormat.format(item.getMenuPrice()));
            holder.menuButton.setText(oneOrderButtonArray[languageNumber]);
            switch (languageNumber){
                case 0:
                    holder.menuName.setText(item.getMenuKor());
                    break;
                case 1:
                    holder.menuName.setText(item.getMenuEng());
                    break;
                case 2:
                    holder.menuName.setText(item.getMenuChn());
                    break;
                case 3:
                    holder.menuName.setText(item.getMenuJpn());
                    break;
            }
            if(dbHelper.getOneCategoryInfo(selectedCategoryId).getCategorySet()>0){
                holder.menuButton.setVisibility(View.INVISIBLE);
            }else{
                holder.menuButton.setVisibility(View.VISIBLE);
            }

            holder.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 이미 주문이 있다면
                    if(dbHelper.checkOrderInfo(item.getMenuId(), item.getMenuKor())){
                        OrderInfo newOrderInfo = dbHelper.getOneOrderInfo(item.getMenuId(), item.getMenuKor());
                        int newCount = newOrderInfo.getOrderCount()+1;
                        dbHelper.updateOrderInfoCount(newOrderInfo, newCount);
                    } else{ // 새로운 주문 추가
                        OrderInfo newOrderInfo = new OrderInfo();
                        newOrderInfo.setMenuId(item.getMenuId());
                        newOrderInfo.setOrderKor(item.getMenuKor());
                        newOrderInfo.setOrderEng(item.getMenuEng());
                        newOrderInfo.setOrderChn(item.getMenuChn());
                        newOrderInfo.setOrderJpn(item.getMenuJpn());
                        newOrderInfo.setOrderCount(1);
                        newOrderInfo.setOrderTotal(0);
                        newOrderInfo.setOrderPrice(item.getMenuPrice());
                        dbHelper.insertOrder(newOrderInfo);
                    }
                    orderAdapter.clear();
                    orderAdapter.notifyDataSetChanged();
                    setOrderListView();
                    setNewTotal();
                }
            });
        }
    }

    class OrderAdapter extends BaseAdapter<OrderInfo, OrderHolder>{
        public OrderAdapter(@NonNull Activity activity, int resource) {
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
        public void setListHolder(OrderHolder holder, View view) {
            holder.price = view.findViewById(R.id.price_text);
            holder.cancelButton = view.findViewById(R.id.cancelButton);
            holder.count = view.findViewById(R.id.count);
            holder.menuName = view.findViewById(R.id.menuName);
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
        public void setListItem(OrderHolder holder, final OrderInfo item) {
            holder.price.setText(decimalFormat.format(item.getOrderPrice()*item.getOrderCount()));
            holder.count.setText(String.valueOf(item.getOrderCount()));
            switch (languageNumber){
                case 0:
                    holder.menuName.setText(item.getOrderKor());
                    break;
                case 1:
                    holder.menuName.setText(item.getOrderEng());
                    break;
                case 2:
                    holder.menuName.setText(item.getOrderChn());
                    break;
                case 3:
                    holder.menuName.setText(item.getOrderJpn());
                    break;
            }
            holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbHelper.updateOrderInfoCount(item, 0);
                    remove(item);
                    orderAdapter.notifyDataSetChanged();
                    setNewTotal();
                }
            });
        }
    }

    //데이터 보낼때 쓰는것
    class SendData extends AsyncTask<String, Void, String>{

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


}

package android.study.ordertokmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.study.ordertokmenu.Back.BaseAdapter;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.Back.SocketReceiveClass;
import android.study.ordertokmenu.HolderPackage.OptionHolder;
import android.study.ordertokmenu.InfoPackage.MenuInfo;
import android.study.ordertokmenu.InfoPackage.OptionInfo;
import android.study.ordertokmenu.InfoPackage.OrderInfo;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class OptionDetailActivity extends AppCompatActivity {

    // 필요 객체 선언
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    MenuInfo menuInfo;
    int selectedMenuId;
    int languageNumber;
    OptionInfo selectedOption;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    // 언어에 따른 배열 선언 및 할당
    String[] optionCancelArray = {"취소", "Close", "克洛克", "閉じる"};
    String[] optionAddArray = {"담기","Order","订购","ご注文"};

    // 레이아웃 객체 선언
    ImageView optionDetailImgView;
    TextView optionDetailExp, optionDetailTitle, optionDetailCount, optionDetailPriceText;
    Button optionDetailMinus, optionDetailPlus, optionDetailCancelButton, optionDetailAddButton;
    ListView optionDetailListView;
    OptionDetailAdapter optionDetailAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_detail);

        dbHelper = new DBHelper(OptionDetailActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        selectedOption = null;

        Thread thread = new Thread(new SocketReceiveClass(OptionDetailActivity.this));
        thread.start();

        // 메인 정보 받기
        Intent intent = getIntent();
        selectedMenuId = intent.getIntExtra("menuId", 0);
        languageNumber = intent.getIntExtra("languageNumber", 0);
        if(languageNumber<0||languageNumber>3){
            Intent returnIntent = new Intent(OptionDetailActivity.this, LanguageActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }
        if(selectedMenuId<1){
            Intent returnIntent = new Intent(OptionDetailActivity.this, MainActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }

        // 받은 메뉴 아이디 정보 할당
        menuInfo = dbHelper.getOneMenuInfo(selectedMenuId);

        // 레이아웃 객체 할당
        optionDetailImgView = findViewById(R.id.optionDetailImgView);
        // 텍스트 모음
        optionDetailExp = findViewById(R.id.optionDetailExp);
        optionDetailTitle = findViewById(R.id.optionDetailTitle);
        optionDetailCount = findViewById(R.id.optionDetailCount);
        optionDetailPriceText = findViewById(R.id.optionDetailPriceText);
        //버튼 모음
        optionDetailMinus = findViewById(R.id.optionDetailMinus);
        optionDetailPlus = findViewById(R.id.optionDetailPlus);
        optionDetailCancelButton = findViewById(R.id.optionDetailCancelButton);
        optionDetailAddButton = findViewById(R.id.optionDetailAddButton);

        optionDetailCount.setText("1");
        optionDetailPriceText.setText(decimalFormat.format(menuInfo.getMenuPrice()));

        optionDetailListView = findViewById(R.id.optionDetailListView);
        optionDetailAdapter = new OptionDetailAdapter(OptionDetailActivity.this, R.layout.option_item);
        optionDetailListView.setAdapter(optionDetailAdapter);
        optionDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectedOption == null){ // 선택된 항목 없음
                    optionDetailListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.btn_bg));
                    selectedOption = optionDetailAdapter.getItem(i);
                    optionDetailPriceText.setText(decimalFormat.format(selectedOption.getOptionPrice()*Integer.parseInt(optionDetailCount.getText().toString())));
                } else if(selectedOption == optionDetailAdapter.getItem(i)){ // 선택항목이 기존의 것과 같음
                    optionDetailListView.getChildAt(i).setBackgroundColor(Color.WHITE);
                    selectedOption = null;
                    optionDetailPriceText.setText(decimalFormat.format(menuInfo.getMenuPrice()*Integer.parseInt(optionDetailCount.getText().toString())));
                } else{ // 선택항목이 기존의 것과 다름
                    // 기존 항목 없애기
                    optionDetailListView.getChildAt(optionDetailAdapter.getPosition(selectedOption)).setBackgroundColor(Color.WHITE);
                    optionDetailListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.btn_bg));
                    selectedOption = optionDetailAdapter.getItem(i);
                    optionDetailPriceText.setText(decimalFormat.format(selectedOption.getOptionPrice()*Integer.parseInt(optionDetailCount.getText().toString())));
                }
            }
        });

        // 초기 화면 세팅
        optionDetailCancelButton.setText(optionCancelArray[languageNumber]);
        optionDetailAddButton.setText(optionAddArray[languageNumber]);
        Glide.with(OptionDetailActivity.this).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+menuInfo.getImgPath()).centerInside().into(optionDetailImgView);

        // 언어 변경
        if(languageNumber==0){
            optionDetailExp.setText(menuInfo.getMenuKorExp());
            optionDetailTitle.setText(menuInfo.getMenuKor());
        }else if(languageNumber==1){
            optionDetailExp.setText(menuInfo.getMenuEngExp());
            optionDetailTitle.setText(menuInfo.getMenuEng());
        }else if(languageNumber==2){
            optionDetailExp.setText(menuInfo.getMenuChnExp());
            optionDetailTitle.setText(menuInfo.getMenuChn());
        }else if(languageNumber==3){
            optionDetailExp.setText(menuInfo.getMenuJpnExp());
            optionDetailTitle.setText(menuInfo.getMenuJpn());
        }

        // 버튼 기능
        // 마이너스
        optionDetailMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(optionDetailCount.getText().toString());
                int menuPrice = Integer.parseInt(optionDetailPriceText.getText().toString().replace(",",""))/menuCount;
                int newMenuCount = menuCount-1;
                if(newMenuCount<1){
                    newMenuCount = 1;
                }
                int newMenuTotal = newMenuCount*menuPrice;
                optionDetailCount.setText(String.valueOf(newMenuCount));
                optionDetailPriceText.setText(decimalFormat.format(newMenuTotal));
            }
        });
        //플러스
        optionDetailPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(optionDetailCount.getText().toString());
                int menuPrice = Integer.parseInt(optionDetailPriceText.getText().toString().replace(",",""))/menuCount;
                int newMenuCount = menuCount+1;
                if(newMenuCount>30){
                    newMenuCount = 30;
                }
                int newMenuTotal = newMenuCount*menuPrice;
                optionDetailCount.setText(String.valueOf(newMenuCount));
                optionDetailPriceText.setText(decimalFormat.format(newMenuTotal));
            }
        });
        //취소 버튼
        optionDetailCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent menuIntent = new Intent(OptionDetailActivity.this, MainActivity.class);
                menuIntent.putExtra("languageNumber", languageNumber);
                startActivity(menuIntent);
                overridePendingTransition(0,0);*/
                finish();
            }
        });

        optionDetailAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(optionDetailCount.getText().toString());
                if(selectedOption == null) { // 선택된 옵션 없음\
                    if(dbHelper.checkOrderInfo(menuInfo.getMenuId(), menuInfo.getMenuKor())){ // 이미 있는 메뉴
                        OrderInfo orderInfo = dbHelper.getOneOrderInfo(menuInfo.getMenuId(), menuInfo.getMenuKor());
                        int oldCount = orderInfo.getOrderCount();
                        int newCount = menuCount + oldCount;
                        dbHelper.updateOrderInfoCount(orderInfo, newCount);
                    } else { //메뉴 새로 추가
                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setOrderJpn(menuInfo.getMenuJpn());
                        orderInfo.setOrderChn(menuInfo.getMenuChn());
                        orderInfo.setOrderEng(menuInfo.getMenuEng());
                        orderInfo.setOrderKor(menuInfo.getMenuKor());
                        orderInfo.setOrderPrice(menuInfo.getMenuPrice());
                        orderInfo.setOrderTotal(0);
                        orderInfo.setOrderCount(menuCount);
                        orderInfo.setMenuId(menuInfo.getMenuId());
                        dbHelper.insertOrder(orderInfo);
                    }
                }else{ // 선택된 옵션있음
                    if(dbHelper.checkOrderInfo(menuInfo.getMenuId(), menuInfo.getMenuKor()+" - "+selectedOption.getOptionKor())){ // 이미 있는 메뉴
                        OrderInfo orderInfo = dbHelper.getOneOrderInfo(menuInfo.getMenuId(), menuInfo.getMenuKor()+" - "+selectedOption.getOptionKor());
                        int oldCount = orderInfo.getOrderCount();
                        int newCount = menuCount + oldCount;
                        dbHelper.updateOrderInfoCount(orderInfo, newCount);
                    } else { //메뉴 새로 추가
                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setOrderJpn(menuInfo.getMenuJpn()+" - "+selectedOption.getOptionJpn());
                        orderInfo.setOrderChn(menuInfo.getMenuChn()+" - "+selectedOption.getOptionChn());
                        orderInfo.setOrderEng(menuInfo.getMenuEng()+" - "+selectedOption.getOptionEng());
                        orderInfo.setOrderKor(menuInfo.getMenuKor()+" - "+selectedOption.getOptionKor());
                        orderInfo.setOrderPrice(selectedOption.getOptionPrice());
                        orderInfo.setOrderTotal(0);
                        orderInfo.setOrderCount(menuCount);
                        orderInfo.setMenuId(menuInfo.getMenuId());
                        dbHelper.insertOrder(orderInfo);
                    }
                }

                Intent doneIntent = new Intent(OptionDetailActivity.this, MainActivity.class);
                doneIntent.putExtra("languageNumber", languageNumber);
                startActivity(doneIntent);
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOptionDetailListView();
    }

    public void setOptionDetailListView(){
        List<OptionInfo> optionInfoList = dbHelper.getAllOptionInfo(selectedMenuId);
        if(optionInfoList!=null && optionInfoList.size()>0){
            for(int i=0; i<optionInfoList.size(); i++){
                OptionInfo optionInfo = optionInfoList.get(i);
                Log.d("Console", optionInfo.getOptionKor());
                optionDetailAdapter.add(optionInfo);
            }
        }
    }

    class OptionDetailAdapter extends BaseAdapter<OptionInfo, OptionHolder>{
        OptionDetailAdapter(@NonNull Activity activity, int resource) {
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
        public void setListHolder(OptionHolder holder, View view) {
            holder.oneOptionTextView = view.findViewById(R.id.oneOptionTextView);
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
        public void setListItem(OptionHolder holder, OptionInfo item) {
            switch (languageNumber){
                case 0:
                    Log.d("Console", "holder : "+item.getOptionKor());
                    holder.oneOptionTextView.setText(item.getOptionKor());
                    break;
                case 1:
                    holder.oneOptionTextView.setText(item.getOptionEng());
                    break;
                case 2:
                    holder.oneOptionTextView.setText(item.getOptionChn());
                    break;
                case 3:
                    holder.oneOptionTextView.setText(item.getOptionJpn());
                    break;
            }
        }
    }
}

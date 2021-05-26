package android.study.ordertok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.study.ordertok.Back.DBHelper;
import android.study.ordertok.Back.SocketReceiveClass;
import android.study.ordertok.InfoPackage.MenuInfo;
import android.study.ordertok.InfoPackage.OrderInfo;
import android.study.ordertok.InfoPackage.ServerInfo;
import android.study.ordertok.InfoPackage.StoreInfo;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

public class MenuDetailActivity extends AppCompatActivity {

    // 필요 객체 선언
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    MenuInfo menuInfo;
    int selectedMenuId;
    int languageNumber;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    // 언어에 따른 배열 선언 및 할당
    String[] menuCancelArray = {"취소", "Close", "克洛克", "閉じる"};
    String[] menuAddArray = {"담기","Order","订购","ご注文"};

    // 레이아웃 객체 선언
    ImageView menuDetailImgView;
    TextView menuDetailExp, menuDetailTitle, menuDetailCount, menuDetailPriceText;
    Button menuDetailMinus, menuDetailPlus, menuDetailCancelButton, menuDetailAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        dbHelper = new DBHelper(MenuDetailActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread thread = new Thread(new SocketReceiveClass(MenuDetailActivity.this));
        thread.start();

        // 메인 정보 받기
        Intent intent = getIntent();
        selectedMenuId = intent.getIntExtra("menuId", 0);
        languageNumber = intent.getIntExtra("languageNumber", 0);
        if(languageNumber<0||languageNumber>3){
            Intent returnIntent = new Intent(MenuDetailActivity.this, LanguageActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }
        if(selectedMenuId<1){
            Intent returnIntent = new Intent(MenuDetailActivity.this, MainActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }

        // 받은 메뉴 아이디 정보 할당
        menuInfo = dbHelper.getOneMenuInfo(selectedMenuId);

        // 레이아웃 객체 할당
        menuDetailImgView = findViewById(R.id.menuDetailImgView);
        // 텍스트 모음
        menuDetailExp = findViewById(R.id.menuDetailExp);
        menuDetailTitle = findViewById(R.id.menuDetailTitle);
        menuDetailCount = findViewById(R.id.menuDetailCount);
        menuDetailPriceText = findViewById(R.id.menuDetailPriceText);
        //버튼 모음
        menuDetailMinus = findViewById(R.id.menuDetailMinus);
        menuDetailPlus = findViewById(R.id.menuDetailPlus);
        menuDetailCancelButton = findViewById(R.id.menuDetailCancelButton);
        menuDetailAddButton = findViewById(R.id.menuDetailAddButton);

        // 초기 화면 세팅
        menuDetailCount.setText("1");
        menuDetailPriceText.setText(decimalFormat.format(menuInfo.getMenuPrice()));
        menuDetailCancelButton.setText(menuCancelArray[languageNumber]);
        menuDetailAddButton.setText(menuAddArray[languageNumber]);
        Glide.with(MenuDetailActivity.this).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+menuInfo.getImgPath()).centerInside().into(menuDetailImgView);

        // 언어 변경
        if(languageNumber==0){
            menuDetailExp.setText(menuInfo.getMenuKorExp());
            menuDetailTitle.setText(menuInfo.getMenuKor());
        }else if(languageNumber==1){
            menuDetailExp.setText(menuInfo.getMenuEngExp());
            menuDetailTitle.setText(menuInfo.getMenuEng());
        }else if(languageNumber==2){
            menuDetailExp.setText(menuInfo.getMenuChnExp());
            menuDetailTitle.setText(menuInfo.getMenuChn());
        }else if(languageNumber==3){
            menuDetailExp.setText(menuInfo.getMenuJpnExp());
            menuDetailTitle.setText(menuInfo.getMenuJpn());
        }

        // 버튼 기능
        // 마이너스
        menuDetailMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(menuDetailCount.getText().toString());
                int menuPrice = menuInfo.getMenuPrice();
                int newMenuCount = menuCount-1;
                if(newMenuCount<1){
                    newMenuCount = 1;
                }
                int newMenuTotal = newMenuCount*menuPrice;
                menuDetailCount.setText(String.valueOf(newMenuCount));
                menuDetailPriceText.setText(decimalFormat.format(newMenuTotal));
            }
        });
        //플러스
        menuDetailPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(menuDetailCount.getText().toString());
                int menuPrice = menuInfo.getMenuPrice();
                int newMenuCount = menuCount+1;
                if(newMenuCount>30){
                    newMenuCount = 30;
                }
                int newMenuTotal = newMenuCount*menuPrice;
                menuDetailCount.setText(String.valueOf(newMenuCount));
                menuDetailPriceText.setText(decimalFormat.format(newMenuTotal));
            }
        });
        //취소 버튼
        menuDetailCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent menuIntent = new Intent(MenuDetailActivity.this, MainActivity.class);
                menuIntent.putExtra("languageNumber", languageNumber);
                startActivity(menuIntent);
                overridePendingTransition(0,0);*/
                finish();
            }
        });

        // 추가 버튼
        menuDetailAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(menuDetailCount.getText().toString());
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
                Intent doneIntent = new Intent(MenuDetailActivity.this, MainActivity.class);
                doneIntent.putExtra("languageNumber", languageNumber);
                startActivity(doneIntent);
                overridePendingTransition(0,0);
            }
        });
    }
}

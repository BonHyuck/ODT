package android.study.ordertokmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.study.ordertokmenu.Back.BaseAdapter;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.Back.SocketReceiveClass;
import android.study.ordertokmenu.HolderPackage.SetHolder;
import android.study.ordertokmenu.HolderPackage.SetItemHolder;
import android.study.ordertokmenu.InfoPackage.MenuInfo;
import android.study.ordertokmenu.InfoPackage.OrderInfo;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.SetInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SetDetailActivity extends AppCompatActivity {

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
    ImageView setDetailImgView;
    TextView setDetailExp, setDetailTitle, setDetailCount, setDetailPriceText;
    Button setDetailMinus, setDetailPlus, setDetailCancelButton, setDetailAddButton;
    ListView setDetailListView;
    SetAdapter setAdapter;
    SetItemAdapter setItemAdapter;

    String[] subMenuArray, engArray, chnArray, jpnArray;
    String newText, engName, chnName, jpnName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_detail);

        dbHelper = new DBHelper(SetDetailActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread thread = new Thread(new SocketReceiveClass(SetDetailActivity.this));
        thread.start();

        // 메인 정보 받기
        Intent intent = getIntent();
        selectedMenuId = intent.getIntExtra("menuId", 0);
        languageNumber = intent.getIntExtra("languageNumber", 0);
        if(languageNumber<0||languageNumber>3){
            Intent returnIntent = new Intent(SetDetailActivity.this, LanguageActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }
        if(selectedMenuId<1){
            Intent returnIntent = new Intent(SetDetailActivity.this, MainActivity.class);
            startActivity(returnIntent);
            overridePendingTransition(0,0);
        }

        // 받은 메뉴 아이디 정보 할당
        menuInfo = dbHelper.getOneMenuInfo(selectedMenuId);
        subMenuArray = new String[dbHelper.getSetOptionCount(selectedMenuId)+1];
        engArray = new String[dbHelper.getSetOptionCount(selectedMenuId)+1];
        chnArray = new String[dbHelper.getSetOptionCount(selectedMenuId)+1];
        jpnArray = new String[dbHelper.getSetOptionCount(selectedMenuId)+1];

        // 레이아웃 객체 할당
        setDetailImgView = findViewById(R.id.setDetailImgView);
        // 텍스트 모음
        setDetailExp = findViewById(R.id.setDetailExp);
        setDetailTitle = findViewById(R.id.setDetailTitle);
        setDetailCount = findViewById(R.id.setDetailCount);
        setDetailPriceText = findViewById(R.id.setDetailPriceText);
        //버튼 모음
        setDetailMinus = findViewById(R.id.setDetailMinus);
        setDetailPlus = findViewById(R.id.setDetailPlus);
        setDetailCancelButton = findViewById(R.id.setDetailCancelButton);
        setDetailAddButton = findViewById(R.id.setDetailAddButton);
        setDetailListView = findViewById(R.id.setDetailListView);
        setAdapter = new SetAdapter(SetDetailActivity.this, R.layout.set_item);
        setDetailListView.setAdapter(setAdapter);

        // 초기 화면 세팅
        setDetailCount.setText("1");
        setDetailPriceText.setText(decimalFormat.format(menuInfo.getMenuPrice()));
        setDetailCancelButton.setText(menuCancelArray[languageNumber]);
        setDetailAddButton.setText(menuAddArray[languageNumber]);
        Glide.with(SetDetailActivity.this).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+menuInfo.getImgPath()).centerCrop().into(setDetailImgView);

        // 언어 변경
        if(languageNumber==0){
            setDetailExp.setText(menuInfo.getMenuKorExp());
            setDetailTitle.setText(menuInfo.getMenuKor());
        }else if(languageNumber==1){
            setDetailExp.setText(menuInfo.getMenuEngExp());
            setDetailTitle.setText(menuInfo.getMenuEng());
        }else if(languageNumber==2){
            setDetailExp.setText(menuInfo.getMenuChnExp());
            setDetailTitle.setText(menuInfo.getMenuChn());
        }else if(languageNumber==3){
            setDetailExp.setText(menuInfo.getMenuJpnExp());
            setDetailTitle.setText(menuInfo.getMenuJpn());
        }

        // 버튼 기능
        // 마이너스
        setDetailMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(setDetailCount.getText().toString());
                int menuPrice = Integer.parseInt(setDetailPriceText.getText().toString().replace(",",""))/menuCount;
                int newMenuCount = menuCount-1;
                if(newMenuCount<1){
                    newMenuCount = 1;
                }
                int newMenuTotal = newMenuCount*menuPrice;
                setDetailCount.setText(String.valueOf(newMenuCount));
                setDetailPriceText.setText(decimalFormat.format(newMenuTotal));
            }
        });
        //플러스
        setDetailPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(setDetailCount.getText().toString());
                int menuPrice = Integer.parseInt(setDetailPriceText.getText().toString().replace(",",""))/menuCount;
                int newMenuCount = menuCount+1;
                if(newMenuCount>30){
                    newMenuCount = 30;
                }
                int newMenuTotal = newMenuCount*menuPrice;
                setDetailCount.setText(String.valueOf(newMenuCount));
                setDetailPriceText.setText(decimalFormat.format(newMenuTotal));
            }
        });
        //취소 버튼
        setDetailCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(SetDetailActivity.this, MainActivity.class);
                menuIntent.putExtra("languageNumber", languageNumber);
                startActivity(menuIntent);
                overridePendingTransition(0,0);
            }
        });
        setDetailAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Arrays.asList(subMenuArray).contains("")||Arrays.asList(subMenuArray).contains(null) ){
                    Toast.makeText(SetDetailActivity.this, "옵션을 선택해주세요.", Toast.LENGTH_LONG).show();
                }else{
                int menuCount = Integer.parseInt(setDetailCount.getText().toString());
                    if(dbHelper.checkOrderInfo(menuInfo.getMenuId(), menuInfo.getMenuKor()+" - "+newText)){ // 이미 있는 메뉴
                        OrderInfo orderInfo = dbHelper.getOneOrderInfo(menuInfo.getMenuId(), menuInfo.getMenuKor()+" - "+newText);
                        int oldCount = orderInfo.getOrderCount();
                        int newCount = menuCount + oldCount;
                        dbHelper.updateOrderInfoCount(orderInfo, newCount);
                    } else { //메뉴 새로 추가
                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setOrderJpn(menuInfo.getMenuJpn()+" - "+jpnName);
                        orderInfo.setOrderChn(menuInfo.getMenuChn()+" - "+chnName);
                        orderInfo.setOrderEng(menuInfo.getMenuEng()+" - "+engName);
                        orderInfo.setOrderKor(menuInfo.getMenuKor()+" - "+newText);
                        orderInfo.setOrderPrice(menuInfo.getMenuPrice());
                        orderInfo.setOrderTotal(0);
                        orderInfo.setOrderCount(menuCount);
                        orderInfo.setMenuId(menuInfo.getMenuId());
                        dbHelper.insertOrder(orderInfo);
                    }

                Intent doneIntent = new Intent(SetDetailActivity.this, MainActivity.class);
                doneIntent.putExtra("languageNumber", languageNumber);
                startActivity(doneIntent);
                overridePendingTransition(0,0);
                }
            }
        });

    }

    public void setSetEditListView(){
        int setOptionCount = dbHelper.getSetOptionCount(selectedMenuId);
        Log.d("COnsole", "Number : "+setOptionCount);
        if(setOptionCount>0){
            for(int i=0; i<setOptionCount+1; i++){
                setAdapter.add(i);
            }
        }
    }

    class SetAdapter extends BaseAdapter<Integer, SetHolder>{

        public SetAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }


        @Override
        public void setListHolder(SetHolder holder, View view) {
            holder.oneSetListView = view.findViewById(R.id.oneSetListView);
        }

        @Override
        public void setListItem(SetHolder holder, Integer item) {
            setItemAdapter = new SetItemAdapter(SetDetailActivity.this, R.layout.set_detail_item);
            holder.oneSetListView.setAdapter(setItemAdapter);
            setSetItemAdapter(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSetEditListView();
    }

    public void setSetItemAdapter(int position){
        List<SetInfo> setInfoList = dbHelper.getPositionSet(selectedMenuId, position);
        if(setInfoList != null && setInfoList.size()>0){
            for(int i=0; i<setInfoList.size(); i++){
                SetInfo setInfo = setInfoList.get(i);
                setItemAdapter.add(setInfo);
            }
        }
    }

    class SetItemAdapter extends BaseAdapter<SetInfo, SetItemHolder>{
        public SetItemAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(SetItemHolder holder, View view) {
            holder.setItemTextView = view.findViewById(R.id.setItemTextView);
        }

        @Override
        public void setListItem(SetItemHolder holder, final SetInfo item) {
            switch (languageNumber){
                case 0:
                    holder.setItemTextView.setText(item.getSetKor());
                    break;
                case 1:
                    holder.setItemTextView.setText(item.getSetEng());
                    break;
                case 2:
                    holder.setItemTextView.setText(item.getSetChn());
                    break;
                case 3:
                    holder.setItemTextView.setText(item.getSetJpn());
                    break;
            }
            holder.setItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subMenuArray[item.getSetNumber()] = item.getSetKor();
                    engArray[item.getSetNumber()] = item.getSetEng();
                    chnArray[item.getSetNumber()] = item.getSetChn();
                    jpnArray[item.getSetNumber()] = item.getSetJpn();
                    newText = "";
                    for(int p=0; p<subMenuArray.length; p++){
                        if(subMenuArray[p]==null){
                            subMenuArray[p]="";
                        }
                        if(p==0){
                            newText += subMenuArray[p];
                            engName = menuInfo.getMenuEng()+" - "+engArray[p];
                            chnName = menuInfo.getMenuChn()+" - "+chnArray[p];
                            jpnName = menuInfo.getMenuJpn()+" - "+jpnArray[p];
                        }else{
                            newText += ","+subMenuArray[p];
                            engName += engArray[p];
                            chnName += chnArray[p];
                            jpnName += jpnArray[p];
                        }
                    }
                    switch (languageNumber){
                        case 0:
                            setDetailTitle.setText(menuInfo.getMenuKor()+" - "+newText);
                            break;
                        case 1:
                            setDetailTitle.setText(menuInfo.getMenuEng()+" - "+engName);
                            break;
                        case 2:
                            setDetailTitle.setText(menuInfo.getMenuChn()+" - "+chnName);
                            break;
                        case 3:
                            setDetailTitle.setText(menuInfo.getMenuJpn()+" - "+jpnName);
                            break;
                    }
                }
            });
        }
    }
}

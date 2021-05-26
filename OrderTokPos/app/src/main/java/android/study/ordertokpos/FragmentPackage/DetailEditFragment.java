package android.study.ordertokpos.FragmentPackage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.InfoPackage.MenuInfo;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.study.ordertokpos.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

public class DetailEditFragment extends Fragment {
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    MainActivity mainActivity;
    DetailFragment detailFragment;
    int detailTableNumber, detailMenuTotal, detailMenuId, detailMenuPrice;
    String detailMenuName;
    TextView detailOneMenuName, detailOneMenuCount, detailOneMenuPrice;
    Button detailOneMenuMinus, detailOneMenuPlus, detailOneMenuCancel, detailOneMenuConfirm;
    ImageView detailOneImageView;

    OrderInfo orderInfo;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
        detailFragment = new DetailFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        mainActivity = null;
        dbHelper.close();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail_edit, container, false);

        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        orderInfo = null;

        detailOneMenuName = rootView.findViewById(R.id.detailOneMenuName);
        detailOneMenuCount = rootView.findViewById(R.id.detailOneMenuCount);
        detailOneMenuPrice = rootView.findViewById(R.id.detailOneMenuPrice);
        detailOneMenuMinus = rootView.findViewById(R.id.detailOneMenuMinus);
        detailOneMenuPlus = rootView.findViewById(R.id.detailOneMenuPlus);
        detailOneMenuCancel = rootView.findViewById(R.id.detailOneMenuCancel);
        detailOneMenuConfirm = rootView.findViewById(R.id.detailOneMenuConfirm);
        detailOneImageView = rootView.findViewById(R.id.detailOneImageView);

        detailTableNumber = getArguments().getInt("tableNumber");
        detailMenuId = getArguments().getInt("menuId");
        detailMenuName = getArguments().getString("orderKor");
        detailMenuTotal = getArguments().getInt("orderTotal");
        detailMenuPrice = getArguments().getInt("orderPrice");

        if(dbHelper.getOneOrderInfo(detailMenuId, detailTableNumber, detailMenuName) != null){
            orderInfo = dbHelper.getOneOrderInfo(detailMenuId, detailTableNumber, detailMenuName);
            detailMenuName = orderInfo.getOrderKor();
            detailMenuTotal = orderInfo.getOrderTotal();
            detailMenuPrice = orderInfo.getOrderPrice();
        }

        MenuInfo menuInfo = dbHelper.getOneMenuInfo(detailMenuId);
        detailOneMenuName.setText(detailMenuName);
        detailOneMenuCount.setText(String.valueOf(detailMenuTotal));
        detailOneMenuPrice.setText(decimalFormat.format(detailMenuPrice*detailMenuTotal));
        Glide.with(mainActivity).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+menuInfo.getImgPath()).centerCrop().into(detailOneImageView);

        // 취소 버튼
        detailOneMenuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailCategoryFragment detailCategoryFragment = new DetailCategoryFragment();
                getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailCategoryFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", detailTableNumber);
                detailCategoryFragment.setArguments(bundle);
            }
        });

        // 마이너스 버튼 기능
        detailOneMenuMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(detailOneMenuCount.getText().toString());
                int menuPrice;
                if(detailOneMenuPrice.getText().toString().contains(",")){
                    menuPrice = Integer.parseInt(detailOneMenuPrice.getText().toString().replace(",", ""));
                    Log.d("CONSOLE", "MINUS : "+menuPrice);
                }else{
                    menuPrice = Integer.parseInt(detailOneMenuPrice.getText().toString());
                }
                // 개당 가격
                int oneMenuPrice;
                if(menuCount<=0){
                    oneMenuPrice = menuPrice;
                }else{
                    oneMenuPrice = menuPrice/menuCount;
                }
                menuCount--;
                int realMenuPrice;
                if(menuCount<=0){
                    menuCount = 0;
                    realMenuPrice = oneMenuPrice;
                }else{
                    realMenuPrice =  oneMenuPrice*menuCount;
                }

                //최종 가격
                detailOneMenuCount.setText(String.valueOf(menuCount));
                detailOneMenuPrice.setText(decimalFormat.format(realMenuPrice));
            }
        });

        // 플러스 버튼
        detailOneMenuPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(detailOneMenuCount.getText().toString());
                int menuPrice;
                if(detailOneMenuPrice.getText().toString().contains(",")){
                    menuPrice = Integer.parseInt(detailOneMenuPrice.getText().toString().replace(",", ""));
                    Log.d("CONSOLE", "MINUS : "+menuPrice);
                }else{
                    menuPrice = Integer.parseInt(detailOneMenuPrice.getText().toString());
                }
                // 개당 가격
                int oneMenuPrice;
                if(menuCount<=0){
                    oneMenuPrice = menuPrice;
                }else{
                    oneMenuPrice = menuPrice/menuCount;
                }

                menuCount++;
                if(menuCount>=30){
                    menuCount = 30;
                }

                //최종 가격
                int realMenuPrice = oneMenuPrice*menuCount;
                detailOneMenuCount.setText(String.valueOf(menuCount));
                detailOneMenuPrice.setText(decimalFormat.format(realMenuPrice));
            }
        });

        // 주문 완료
        detailOneMenuConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택 된 메뉴 정보 가져오기
                MenuInfo menuInfo = dbHelper.getOneMenuInfo(detailMenuId);
                // OrderDB에 있는지 확인
                // 있음
                if(dbHelper.getOneOrderInfo(detailMenuId, detailTableNumber, detailMenuName) != null){
                    OrderInfo orderInfo = dbHelper.getOneOrderInfo(detailMenuId, detailTableNumber, detailMenuName);
                    int menuCount = Integer.parseInt(detailOneMenuCount.getText().toString());
                    int prevCount = orderInfo.getOrderTotal();
                    if(menuCount<=0){
                        menuCount = 0;
                    }
                    if(prevCount>=menuCount){
                        orderInfo.setOrderPrinted(0);
                    }else{
                        orderInfo.setOrderPrinted(menuCount-prevCount);
                    }
                    dbHelper.updateOneOrderTotal(orderInfo, menuCount);
                }else{
                    OrderInfo orderInfo = new OrderInfo();
                    int menuCount = Integer.parseInt(detailOneMenuCount.getText().toString());
                    orderInfo.setTableNumber(detailTableNumber);
                    orderInfo.setMenuId(detailMenuId);
                    orderInfo.setOrderTotal(menuCount);
                    orderInfo.setOrderPrice(menuInfo.getMenuPrice());
                    orderInfo.setOrderKor(menuInfo.getMenuKor());
                    orderInfo.setOrderEng(menuInfo.getMenuEng());
                    orderInfo.setOrderChn(menuInfo.getMenuChn());
                    orderInfo.setOrderJpn(menuInfo.getMenuJpn());
                    orderInfo.setOrderPrinted(menuCount);
                    orderInfo.setOrderDate(0);
                    dbHelper.insertOrderInfo(orderInfo);
                }
                // 작업 이후 카테고리 화면으로 돌아가기
                mainActivity.reloadMain();

                DetailCategoryFragment detailCategoryFragment = new DetailCategoryFragment();
                getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailCategoryFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", detailTableNumber);
                detailCategoryFragment.setArguments(bundle);
            }
        });

        return rootView;
    }
}

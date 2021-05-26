package android.study.ordertokpos.FragmentPackage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.BaseAdapter;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.HolderPackage.OptionHolder;
import android.study.ordertokpos.InfoPackage.MenuInfo;
import android.study.ordertokpos.InfoPackage.OptionInfo;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.study.ordertokpos.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class OptionEditFragment extends Fragment {
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    MainActivity mainActivity;
    DetailFragment detailFragment;
    int optionTableNumber, optionMenuTotal, optionMenuId, optionMenuPrice;
    String optionMenuName;
    TextView optionOneMenuName, optionOneMenuCount, optionOneMenuPrice;
    Button optionOneMenuMinus, optionOneMenuPlus, optionOneMenuCancel, optionOneMenuConfirm;
    ListView optionEditListView;
    OptionEditAdapter optionEditAdapter;

    OptionInfo selectedOptionInfo;

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
        optionEditAdapter.clear();
        setOptionEditListView();
    }

    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_option_edit, container, false);

        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        orderInfo = null;
        selectedOptionInfo = null;

        optionOneMenuName = rootView.findViewById(R.id.optionOneMenuName);
        optionOneMenuCount = rootView.findViewById(R.id.optionOneMenuCount);
        optionOneMenuPrice = rootView.findViewById(R.id.optionOneMenuPrice);
        optionOneMenuMinus = rootView.findViewById(R.id.optionOneMenuMinus);
        optionOneMenuPlus = rootView.findViewById(R.id.optionOneMenuPlus);
        optionOneMenuCancel = rootView.findViewById(R.id.optionOneMenuCancel);
        optionOneMenuConfirm = rootView.findViewById(R.id.optionOneMenuConfirm);
        optionEditListView = rootView.findViewById(R.id.optionEditListView);
        optionEditAdapter = new OptionEditAdapter(mainActivity, R.layout.one_option_item);
        optionEditListView.setAdapter(optionEditAdapter);

        optionTableNumber = getArguments().getInt("tableNumber");
        optionMenuId = getArguments().getInt("menuId");
        optionMenuName = getArguments().getString("orderKor");
        optionMenuTotal = getArguments().getInt("orderTotal");
        optionMenuPrice = getArguments().getInt("orderPrice");

        final MenuInfo menuInfo = dbHelper.getOneMenuInfo(optionMenuId);

        if(dbHelper.getOneOrderInfo(optionMenuId, optionTableNumber, optionMenuName) != null){
            orderInfo = dbHelper.getOneOrderInfo(optionMenuId, optionTableNumber, optionMenuName);
            optionMenuName = orderInfo.getOrderKor();
            optionMenuTotal = orderInfo.getOrderTotal();
            optionMenuPrice = orderInfo.getOrderPrice();
        }

        optionOneMenuName.setText(optionMenuName);
        optionOneMenuCount.setText(String.valueOf(optionMenuTotal));
        optionOneMenuPrice.setText(decimalFormat.format(optionMenuPrice*optionMenuTotal));

        optionEditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OptionInfo optionInfo = optionEditAdapter.getItem(i);
                if(selectedOptionInfo == null){ // 선택된 항목 없음
                    optionEditListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.btn_bg));
                    selectedOptionInfo = optionInfo;
                    optionOneMenuName.setText(menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor());
                    if(dbHelper.getOneOrderInfo(menuInfo.getMenuId(), optionTableNumber, menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor()) != null){ // 해당 옵션의 주문 있음
                        OrderInfo orderInfo = dbHelper.getOneOrderInfo(menuInfo.getMenuId(), optionTableNumber, menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor());
                        optionOneMenuCount.setText(String.valueOf(orderInfo.getOrderTotal()));
                        optionOneMenuPrice.setText(decimalFormat.format(orderInfo.getOrderPrice()*orderInfo.getOrderTotal()));
                    }else{
                        optionOneMenuCount.setText("1");
                        optionOneMenuPrice.setText(decimalFormat.format(selectedOptionInfo.getOptionPrice()));
                    }
                } else if(selectedOptionInfo == optionInfo){ // 선택항목이 기존의 것과 같음
                    optionEditListView.getChildAt(i).setBackgroundColor(Color.WHITE);
                    selectedOptionInfo = null;
                    optionOneMenuName.setText(optionMenuName);
                    optionOneMenuCount.setText(String.valueOf(optionMenuTotal));
                    optionOneMenuPrice.setText(decimalFormat.format(optionMenuPrice*optionMenuTotal));
                } else{ // 선택항목이 기존의 것과 다름
                    // 기존 항목 없애기
                    optionEditListView.getChildAt(optionEditAdapter.getPosition(selectedOptionInfo)).setBackgroundColor(Color.WHITE);
                    optionEditListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.btn_bg));
                    selectedOptionInfo = optionInfo;
                    optionOneMenuName.setText(menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor());
                    if(dbHelper.getOneOrderInfo(menuInfo.getMenuId(), optionTableNumber, menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor()) != null){ // 해당 옵션의 주문 있음
                        OrderInfo orderInfo = dbHelper.getOneOrderInfo(menuInfo.getMenuId(), optionTableNumber, menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor());
                        optionOneMenuCount.setText(String.valueOf(orderInfo.getOrderTotal()));
                        optionOneMenuPrice.setText(decimalFormat.format(orderInfo.getOrderPrice()*orderInfo.getOrderTotal()));
                    }else{
                        optionOneMenuCount.setText("1");
                        optionOneMenuPrice.setText(decimalFormat.format(selectedOptionInfo.getOptionPrice()));
                    }
                }
            }
        });

        // 취소 버튼
        optionOneMenuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailCategoryFragment detailCategoryFragment = new DetailCategoryFragment();
                getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailCategoryFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", optionTableNumber);
                detailCategoryFragment.setArguments(bundle);
            }
        });

        // 마이너스 버튼 기능
        optionOneMenuMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(optionOneMenuCount.getText().toString());
                int menuPrice;
                if(optionOneMenuPrice.getText().toString().contains(",")){
                    menuPrice = Integer.parseInt(optionOneMenuPrice.getText().toString().replace(",", ""));
                    Log.d("CONSOLE", "MINUS : "+menuPrice);
                }else{
                    menuPrice = Integer.parseInt(optionOneMenuPrice.getText().toString());
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
                optionOneMenuCount.setText(String.valueOf(menuCount));
                optionOneMenuPrice.setText(decimalFormat.format(realMenuPrice));
            }
        });

        // 플러스 버튼
        optionOneMenuPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(optionOneMenuCount.getText().toString());
                int menuPrice;
                if(optionOneMenuPrice.getText().toString().contains(",")){
                    menuPrice = Integer.parseInt(optionOneMenuPrice.getText().toString().replace(",", ""));
                    Log.d("CONSOLE", "MINUS : "+menuPrice);
                }else{
                    menuPrice = Integer.parseInt(optionOneMenuPrice.getText().toString());
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
                optionOneMenuCount.setText(String.valueOf(menuCount));
                optionOneMenuPrice.setText(decimalFormat.format(realMenuPrice));
            }
        });

        // 주문 완료
        optionOneMenuConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택 된 메뉴 정보 가져오기
                MenuInfo menuInfo = dbHelper.getOneMenuInfo(optionMenuId);
                String newName = optionOneMenuName.getText().toString();
                // OrderDB에 있는지 확인
                // 있음
                if(dbHelper.getOneOrderInfo(optionMenuId, optionTableNumber, newName) != null){
                    OrderInfo orderInfo = dbHelper.getOneOrderInfo(optionMenuId, optionTableNumber, newName);
                    int menuCount = Integer.parseInt(optionOneMenuCount.getText().toString());
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
                    int menuCount = Integer.parseInt(optionOneMenuCount.getText().toString());
                    orderInfo.setTableNumber(optionTableNumber);
                    orderInfo.setMenuId(optionMenuId);
                    orderInfo.setOrderTotal(menuCount);
                    if(selectedOptionInfo != null){ // 선택한 옵션 있음
                        orderInfo.setOrderPrice(selectedOptionInfo.getOptionPrice());
                        orderInfo.setOrderKor(menuInfo.getMenuKor()+" - "+selectedOptionInfo.getOptionKor());
                        orderInfo.setOrderEng(menuInfo.getMenuEng()+" - "+selectedOptionInfo.getOptionEng());
                        orderInfo.setOrderChn(menuInfo.getMenuChn()+" - "+selectedOptionInfo.getOptionChn());
                        orderInfo.setOrderJpn(menuInfo.getMenuJpn()+" - "+selectedOptionInfo.getOptionJpn());
                    } else { // 선택 없음
                        orderInfo.setOrderPrice(menuInfo.getMenuPrice());
                        orderInfo.setOrderKor(menuInfo.getMenuKor());
                        orderInfo.setOrderEng(menuInfo.getMenuEng());
                        orderInfo.setOrderChn(menuInfo.getMenuChn());
                        orderInfo.setOrderJpn(menuInfo.getMenuJpn());
                    }
                    orderInfo.setOrderPrinted(menuCount);
                    orderInfo.setOrderDate(0);
                    dbHelper.insertOrderInfo(orderInfo);
                }
                // 작업 이후 카테고리 화면으로 돌아가기
                mainActivity.reloadMain();

                DetailCategoryFragment detailCategoryFragment = new DetailCategoryFragment();
                getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailCategoryFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", optionTableNumber);
                detailCategoryFragment.setArguments(bundle);
            }
        });

        return rootView;
    }

    public void setOptionEditListView(){
        List<OptionInfo> optionInfoList = dbHelper.getAllOptionInfo(optionMenuId);
        if(optionInfoList != null && optionInfoList.size()>0){
            for(int i=0; i<optionInfoList.size(); i++){
                OptionInfo optionInfo = optionInfoList.get(i);
                optionEditAdapter.add(optionInfo);
            }
        }
    }

    class OptionEditAdapter extends BaseAdapter<OptionInfo, OptionHolder>{

        public OptionEditAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(OptionHolder holder, View view) {
            holder.oneOptionTextView = view.findViewById(R.id.oneOptionTextView);
        }

        @Override
        public void setListItem(OptionHolder holder, OptionInfo item) {
            holder.oneOptionTextView.setText(item.getOptionKor());
        }
    }
}

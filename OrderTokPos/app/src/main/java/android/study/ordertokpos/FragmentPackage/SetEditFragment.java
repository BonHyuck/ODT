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
import android.study.ordertokpos.HolderPackage.OrderHolder;
import android.study.ordertokpos.HolderPackage.SetHolder;
import android.study.ordertokpos.HolderPackage.SetOneHolder;
import android.study.ordertokpos.InfoPackage.MenuInfo;
import android.study.ordertokpos.InfoPackage.OptionInfo;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.SetInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.study.ordertokpos.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SetEditFragment extends Fragment {
    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    MainActivity mainActivity;
    DetailFragment detailFragment;
    int setTableNumber, setMenuTotal, setMenuId, setMenuPrice;
    String setMenuName;
    TextView setOneMenuName, setOneMenuCount, setOneMenuPrice;
    Button setOneMenuMinus, setOneMenuPlus, setOneMenuCancel, setOneMenuConfirm;
    ListView setEditListView;
    SetEditAdapter setEditAdapter;
    SetOneItemAdapter setOneItemAdapter;

    OrderInfo orderInfo;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    MenuInfo menuInfo;

    String[] subMenuArray, engArray, chnArray, jpnArray;
    String engName, chnName, jpnName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
        detailFragment = new DetailFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //?????? ????????? ???????????? ???????????????
        mainActivity = null;
        dbHelper.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        setEditAdapter.clear();
        setSetEditListView();
    }

    //Fragment ??????
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_set_edit, container, false);

        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        setOneMenuName = rootView.findViewById(R.id.setOneMenuName);
        setOneMenuCount = rootView.findViewById(R.id.setOneMenuCount);
        setOneMenuPrice = rootView.findViewById(R.id.setOneMenuPrice);
        setOneMenuMinus = rootView.findViewById(R.id.setOneMenuMinus);
        setOneMenuPlus = rootView.findViewById(R.id.setOneMenuPlus);
        setOneMenuCancel = rootView.findViewById(R.id.setOneMenuCancel);
        setOneMenuConfirm = rootView.findViewById(R.id.setOneMenuConfirm);
        setEditListView = rootView.findViewById(R.id.setEditListView);
        setEditAdapter = new SetEditAdapter(mainActivity, R.layout.set_item);
        setEditListView.setAdapter(setEditAdapter);

        setTableNumber = getArguments().getInt("tableNumber");
        setMenuId = getArguments().getInt("menuId");
        setMenuName = getArguments().getString("orderKor");
        setMenuTotal = getArguments().getInt("orderTotal");
        setMenuPrice = getArguments().getInt("orderPrice");

        menuInfo = dbHelper.getOneMenuInfo(setMenuId);
        subMenuArray = new String[dbHelper.getSetOptionCount(setMenuId)+1];
        engArray = new String[dbHelper.getSetOptionCount(setMenuId)+1];
        chnArray = new String[dbHelper.getSetOptionCount(setMenuId)+1];
        jpnArray = new String[dbHelper.getSetOptionCount(setMenuId)+1];
        setOneMenuName.setText(setMenuName);
        setOneMenuCount.setText(String.valueOf(setMenuTotal));
        setOneMenuPrice.setText(decimalFormat.format(setMenuPrice*setMenuTotal));

        // ?????? ??????
        setOneMenuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailCategoryFragment detailCategoryFragment = new DetailCategoryFragment();
                getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailCategoryFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", setTableNumber);
                detailCategoryFragment.setArguments(bundle);
            }
        });

        // ???????????? ?????? ??????
        setOneMenuMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(setOneMenuCount.getText().toString());
                int menuPrice;
                if(setOneMenuPrice.getText().toString().contains(",")){
                    menuPrice = Integer.parseInt(setOneMenuPrice.getText().toString().replace(",", ""));
                    Log.d("CONSOLE", "MINUS : "+menuPrice);
                }else{
                    menuPrice = Integer.parseInt(setOneMenuPrice.getText().toString());
                }
                // ?????? ??????
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

                //?????? ??????
                setOneMenuCount.setText(String.valueOf(menuCount));
                setOneMenuPrice.setText(decimalFormat.format(realMenuPrice));
            }
        });

        // ????????? ??????
        setOneMenuPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuCount = Integer.parseInt(setOneMenuCount.getText().toString());
                int menuPrice;
                if(setOneMenuPrice.getText().toString().contains(",")){
                    menuPrice = Integer.parseInt(setOneMenuPrice.getText().toString().replace(",", ""));
                    Log.d("CONSOLE", "MINUS : "+menuPrice);
                }else{
                    menuPrice = Integer.parseInt(setOneMenuPrice.getText().toString());
                }
                // ?????? ??????
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

                //?????? ??????
                int realMenuPrice = oneMenuPrice*menuCount;
                setOneMenuCount.setText(String.valueOf(menuCount));
                setOneMenuPrice.setText(decimalFormat.format(realMenuPrice));
            }
        });

        // ?????? ??????
        setOneMenuConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ?????? ??? ?????? ?????? ????????????
                MenuInfo menuInfo = dbHelper.getOneMenuInfo(setMenuId);
                String newName = setOneMenuName.getText().toString();
                // OrderDB??? ????????? ??????
                // ??????
                if(dbHelper.getOneOrderInfo(setMenuId, setTableNumber, newName) != null){
                    OrderInfo orderInfo = dbHelper.getOneOrderInfo(setMenuId, setTableNumber, newName);
                    int menuCount = Integer.parseInt(setOneMenuCount.getText().toString());
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
                    int menuCount = Integer.parseInt(setOneMenuCount.getText().toString());
                    orderInfo.setTableNumber(setTableNumber);
                    orderInfo.setMenuId(setMenuId);
                    orderInfo.setOrderTotal(menuCount);
                    orderInfo.setOrderPrice(menuInfo.getMenuPrice());
                    orderInfo.setOrderKor(newName);
                    orderInfo.setOrderEng(engName);
                    orderInfo.setOrderChn(chnName);
                    orderInfo.setOrderJpn(jpnName);
                    orderInfo.setOrderPrinted(menuCount);
                    orderInfo.setOrderDate(0);
                    dbHelper.insertOrderInfo(orderInfo);
                }
                // ?????? ?????? ???????????? ???????????? ????????????
                mainActivity.reloadMain();

                DetailCategoryFragment detailCategoryFragment = new DetailCategoryFragment();
                getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailCategoryFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", setTableNumber);
                detailCategoryFragment.setArguments(bundle);
            }
        });

        return rootView;
    }

    public void setSetEditListView(){
        int setOptionCount = dbHelper.getSetOptionCount(setMenuId);
        Log.d("COnsole", "Number : "+setOptionCount);
        if(setOptionCount>0){
            for(int i=0; i<setOptionCount+1; i++){
                setEditAdapter.add(i);
            }
        }
    }

    class SetEditAdapter extends BaseAdapter<Integer, SetHolder>{

        public SetEditAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        /**
         * ?????????????????? Override???????????? ?????? ????????? ?????? 1
         * ?????? ???????????? ??????????????? ???????????? holder??? ????????? ??????????????? ?????????
         * LayoutInflater??? ????????? ????????? view?????? ??????????????? ???????????? ??????????????? ??????.
         *
         * @param holder - Holder??? ??????
         * @param view   -  LayoutInflater??? ????????? ????????? ??????
         */
        @Override
        public void setListHolder(SetHolder holder, View view) {
            holder.oneSetListView = view.findViewById(R.id.oneSetListView);
        }

        /**
         * ?????????????????? Override???????????? ?????? ????????? ?????? 2
         * ???????????? ????????? ????????? holder??? ???????????? ??? ???????????? ????????? item ????????? ?????? ?????????
         * ???????????? ??????????????? ???????????? ????????? ??????????????????.
         *
         * @param holder - Holder??? ??????
         * @param item   - Holder??? ????????? ????????? ???????????? ?????? ????????? ??????
         */
        @Override
        public void setListItem(final SetHolder holder, final Integer item) {
            setOneItemAdapter = new SetOneItemAdapter(mainActivity, R.layout.set_one_item);
            holder.oneSetListView.setAdapter(setOneItemAdapter);
            setSetOneItemAdapter(item);
        }
    }

    public void setSetOneItemAdapter(int position){
        List<SetInfo> setInfoList = dbHelper.getPositionSet(setMenuId, position);
        if(setInfoList != null && setInfoList.size()>0){
            for(int i=0; i<setInfoList.size(); i++){
                SetInfo setInfo = setInfoList.get(i);
                setOneItemAdapter.add(setInfo);
            }
        }
    }

    class SetOneItemAdapter extends BaseAdapter<SetInfo, SetOneHolder>{

        public SetOneItemAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(SetOneHolder holder, View view) {
            holder.setOneItemTextView = view.findViewById(R.id.setOneItemTextView);
        }

        @Override
        public void setListItem(SetOneHolder holder, final SetInfo item) {
            holder.setOneItemTextView.setText(item.getSetKor());
            holder.setOneItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subMenuArray[item.getSetNumber()] = item.getSetKor();
                    engArray[item.getSetNumber()] = item.getSetEng();
                    chnArray[item.getSetNumber()] = item.getSetChn();
                    jpnArray[item.getSetNumber()] = item.getSetJpn();
                    String newText = "";
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
                    setOneMenuName.setText(setMenuName+" - "+newText);
                }
            });
        }
    }
}

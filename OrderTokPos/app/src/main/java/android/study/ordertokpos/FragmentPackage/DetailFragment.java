package android.study.ordertokpos.FragmentPackage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.Back.HttpConnect;
import android.study.ordertokpos.HolderPackage.OrderHolder;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.study.ordertokpos.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private MainActivity mainActivity;
    private TextView detailOrderTitle, detailOrderTotal;
    private Button detailCancelButton, detailPayButton, detailPrintButton, detailEditTableButton;
    private ListView detailOrderListView;
    private DBHelper dbHelper;
    private ServerInfo serverInfo;
    private StoreInfo storeInfo;
    private OrderAdapter orderAdapter;
    FrameLayout detailLayout;
    private DetailCategoryFragment detailCategoryFragment;
    private DetailEditFragment detailEditFragment;
    private DetailCalculatorFragment detailCalculatorFragment;
    private OptionEditFragment optionEditFragment;
    private SetEditFragment setEditFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    int tableNumber;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참조 안됨
        mainActivity = null;
        dbHelper.close();
    }

    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail , container, false);
        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        // 우측 화면 Fragment
        detailLayout = rootView.findViewById(R.id.detailLayout);
        detailCategoryFragment = new DetailCategoryFragment();
        detailEditFragment = new DetailEditFragment();
        detailCalculatorFragment = new DetailCalculatorFragment();
        optionEditFragment = new OptionEditFragment();
        setEditFragment = new SetEditFragment();
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.detailLayout, detailCategoryFragment).commitAllowingStateLoss();

        // 필요 객체 할당 (타이틀 + 합계)
        detailOrderTitle = rootView.findViewById(R.id.detailOrderTitle);
        detailOrderTotal = rootView.findViewById(R.id.detailOrderTotal);
        // 뒤로가기 버튼 + 결제하기 버튼
        detailCancelButton = rootView.findViewById(R.id.detailCancelButton);
        detailPayButton = rootView.findViewById(R.id.detailPayButton);
        detailPrintButton = rootView.findViewById(R.id.detailPrintButton);
        detailEditTableButton = rootView.findViewById(R.id.detailEditTableButton);

        // 주문 리스트
        detailOrderListView = rootView.findViewById(R.id.detailOrderListView);
        orderAdapter = new OrderAdapter(mainActivity, R.layout.one_order_item, new ArrayList<OrderInfo>());
        detailOrderListView.setAdapter(orderAdapter);

        // MainActivity에서 테이블 번호 받아서 출력
        tableNumber = getArguments().getInt("tableNumber");
        detailOrderTitle.setText(tableNumber + "번 테이블");

        // 우측에도 저장
        Bundle bundle = new Bundle();
        bundle.putInt("tableNumber", tableNumber);
        detailCategoryFragment.setArguments(bundle);

        // 자리 수정
        detailEditTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(tableNumber);
            }
        });

        // 전체 테이블 보기로 돌아가기
        detailCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<OrderInfo> orderPrintList = dbHelper.getTableOrderNotPrinted(tableNumber);
                if(orderPrintList!=null && orderPrintList.size()>0){
                    // mainActivity.startPrint(tableNumber, orderPrintList);
                    mainActivity.kitchenPrint(tableNumber, orderPrintList);
                    for(int i=0; i<orderPrintList.size(); i++){
                        OrderInfo orderInfo = orderPrintList.get(i);
                        addThisOrder(orderInfo);
                        if(i==orderPrintList.size()-1){
                            UpdateSocket updateSocket = new UpdateSocket();
                            updateSocket.execute("192.168.0.106", String.valueOf(0));
                        }
                    }
                }
                mainActivity.onFragmentChange(0);
            }
        });

        // 결제하기 버튼 기능 추가
        detailPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().detach(detailCategoryFragment).detach(detailCalculatorFragment).detach(detailCalculatorFragment).attach(detailCalculatorFragment).replace(R.id.detailLayout, detailCalculatorFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putInt("tableNumber", tableNumber);
                detailCalculatorFragment.setArguments(bundle);
            }
        });

        // 중간 프린트 버튼
        detailPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<OrderInfo> orderInfos = dbHelper.getTableOrderInfo(tableNumber);
                if(orderInfos.size()>0){
                    mainActivity.posMidPrint(tableNumber, orderInfos);
                    //mainActivity.midPrint(tableNumber, orderInfos);
                }else{
                    Toast.makeText(mainActivity, "주문이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    // 해당 테이블 업데이트 소켓
    class UpdateSocket extends AsyncTask<String, Void, String> {

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

    @Override
    public void onResume() {
        super.onResume();
        reloadDetailFragment();
    }

    public void reloadDetailFragment(){
        if(orderAdapter!=null){
            orderAdapter.clear();
        }
        setOrderListView(tableNumber);
    }

    // 수정 팝업 DIALOG
    public void openDialog(final int tableNumber){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        LayoutInflater inflater = getLayoutInflater();
        final View bodyView = inflater.inflate(R.layout.edit_body, null);
        builder.setView(bodyView);
        TextView editTitle = bodyView.findViewById(R.id.editTitle);
        TextView editOldTableText = bodyView.findViewById(R.id.editOldTableText);
        final EditText editNewTableText = bodyView.findViewById(R.id.editNewTableText);
        Button editCancelButton = bodyView.findViewById(R.id.editCancelButton);
        Button editConfirmButton = bodyView.findViewById(R.id.editConfirmButton);

        editTitle.setText(tableNumber+"번 테이블 변경");
        editOldTableText.setText(String.valueOf(tableNumber));

        final Dialog dialog = builder.create();


        editCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        editConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newTableNumber = 0;
                try{
                    newTableNumber=Integer.parseInt(editNewTableText.getText().toString());
                    if(newTableNumber>storeInfo.getTableNumber() || newTableNumber<1){
                        Toast.makeText(mainActivity, "입력값을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(mainActivity, newTableNumber+"로 변경합니다.", Toast.LENGTH_SHORT).show();

                        List<OrderInfo> orderInfoList = dbHelper.getTableOrderInfo(tableNumber);
                        if(orderInfoList != null && orderInfoList.size()>0){
                            for(int i=0; i<orderInfoList.size(); i++){
                                OrderInfo orderInfo = orderInfoList.get(i);
                                cancelThisOrder(orderInfo);
                                dbHelper.changeTableNumber(orderInfo, newTableNumber);
                                orderInfo.setTableNumber(newTableNumber);
                                addThisOrder(orderInfo);
                            }
                        }
                        mainActivity.onFragmentChange(0);
                        dialog.dismiss();
                    }
                }catch (Exception e){
                    e.getLocalizedMessage();
                }
            }
        });

        dialog.show();
    }

    // 추가시 업데이트
    @SuppressLint("StaticFieldLeak")
    public void addThisOrder(OrderInfo orderInfo){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    String receivedIpAddress = resultObject.getString("ipAddress");
                    int receivedTableNumber = resultObject.getInt("tableNumber");
                    int receivedMenuId = resultObject.getInt("menuId");
                    String receivedOrderKor = resultObject.getString("orderKor");
                    String receivedOrderEng = resultObject.getString("orderEng");
                    String receivedOrderChn = resultObject.getString("orderChn");
                    String receivedOrderJpn = resultObject.getString("orderJpn");
                    int receivedOrderTotal = resultObject.getInt("orderTotal");
                    int receivedOrderPrice = resultObject.getInt("orderPrice");

                    if(!receivedIpAddress.equals("")){
                        PushData addData = new PushData();
                        addData.execute(receivedIpAddress, "add,,,"+receivedTableNumber+",,,"+receivedMenuId+",,,"+receivedOrderKor+",,,"+receivedOrderEng+",,,"+receivedOrderChn+",,,"+receivedOrderJpn+",,,"+receivedOrderTotal+",,,"+receivedOrderPrice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/addThisOrder.php", "storeId="+storeInfo.getStoreId()+"&tableNumber="+orderInfo.getTableNumber()+"&menuId="+orderInfo.getMenuId()+"&orderKor="+orderInfo.getOrderKor()+"&orderEng="+orderInfo.getOrderEng()+"&orderChn="+orderInfo.getOrderChn()+"&orderJpn="+orderInfo.getOrderJpn()+"&orderCount="+orderInfo.getOrderPrinted()+"&orderPrice="+orderInfo.getOrderPrice());
    }

    // 취소시 업데이트
    @SuppressLint("StaticFieldLeak")
    public void cancelThisOrder(OrderInfo orderInfo){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    String receivedIpAddress = resultObject.getString("ipAddress");
                    int receivedTableNumber = resultObject.getInt("tableNumber");
                    int receivedMenuId = resultObject.getInt("menuId");
                    String receivedOrderKor = resultObject.getString("orderKor");

                    if(!receivedIpAddress.equals("")){
                        PushData deleteData = new PushData();
                        deleteData.execute(receivedIpAddress, "delete,,,"+receivedTableNumber+",,,"+receivedMenuId+",,,"+receivedOrderKor);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/cancelThisOrder.php", "storeId="+storeInfo.getStoreId()+"&tableNumber="+orderInfo.getTableNumber()+"&menuId="+orderInfo.getMenuId()+"&orderKor="+orderInfo.getOrderKor()+"&orderCount="+orderInfo.getOrderTotal());
    }

    public void setOrderListView(int tableNumber){
        List<OrderInfo> orderInfoList = dbHelper.getTableOrderInfo(tableNumber);

        int totalValue = 0;
        if(orderInfoList!=null&&orderInfoList.size()>0){
            Log.d("Console", "Order List : "+orderInfoList.size());
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                totalValue += (orderInfo.getOrderTotal()*orderInfo.getOrderPrice());
                orderAdapter.add(orderInfo);
            }
        }
        detailOrderTotal.setText("합계 : "+decimalFormat.format(totalValue));
    }



    class OrderAdapter extends ArrayAdapter<OrderInfo> {
        int resource;

        OrderAdapter(Context context, int resource, List<OrderInfo> objects){
            super(context, resource, objects);
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            OrderHolder orderHolder;

            if (view == null){
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(this.resource, null);

                orderHolder = new OrderHolder();
                orderHolder.oneOrderNumber = view.findViewById(R.id.oneOrderNumber);
                orderHolder.oneOrderName = view.findViewById(R.id.oneOrderName);
                orderHolder.oneOrderTotal = view.findViewById(R.id.oneOrderTotal);
                orderHolder.oneOrderPrice = view.findViewById(R.id.oneOrderPrice);
                orderHolder.oneOrderCancel = view.findViewById(R.id.oneOrderCancel);

                view.setTag(orderHolder);
            }

            final OrderInfo orderInfo = getItem(position);

            if(orderInfo != null){
                orderHolder = (OrderHolder) view.getTag();
                orderHolder.oneOrderNumber.setText(String.valueOf(position+1));
                orderHolder.oneOrderName.setText(orderInfo.getOrderKor());
                orderHolder.oneOrderTotal.setText(String.valueOf(orderInfo.getOrderTotal()));
                orderHolder.oneOrderPrice.setText(decimalFormat.format(orderInfo.getOrderPrice()*orderInfo.getOrderTotal()));
                if(orderInfo.getOrderPrinted()==0){
                    view.setBackgroundColor(Color.rgb(200,200,200));
                }else{
                    view.setBackgroundColor(Color.WHITE);
                }

                orderHolder.oneOrderCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbHelper.deleteOneOrderInfo(orderInfo);
                        // 서버와 통신
                        cancelThisOrder(orderInfo);
                        remove(orderInfo);
                        if(orderAdapter!=null){
                            orderAdapter.clear();
                        }
                        setOrderListView(tableNumber);
                    }
                });
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dbHelper.getOneMenuInfo(orderInfo.getMenuId()).getMenuOption()>0){ // 옵션 있음
                        getFragmentManager().beginTransaction().detach(detailCategoryFragment).detach(detailEditFragment).detach(optionEditFragment).detach(setEditFragment).attach(optionEditFragment).replace(R.id.detailLayout, optionEditFragment).commit();
                        Bundle bundle = new Bundle();
                        bundle.putInt("tableNumber", orderInfo.getTableNumber());
                        bundle.putInt("menuId", orderInfo.getMenuId());
                        bundle.putString("orderKor", orderInfo.getOrderKor());
                        bundle.putInt("orderPrice", orderInfo.getOrderPrice());
                        bundle.putInt("orderTotal", orderInfo.getOrderTotal());
                        optionEditFragment.setArguments(bundle);
                    }else if(dbHelper.getOneCategoryInfo(dbHelper.getOneMenuInfo(orderInfo.getMenuId()).getCategoryId()).getCategorySet()>0){ // 세트메뉴
                        Toast.makeText(mainActivity, "세트 메뉴는 삭제 후 추가해주세요.", Toast.LENGTH_LONG).show();
                    }else{
                        getFragmentManager().beginTransaction().detach(detailCategoryFragment).detach(detailEditFragment).detach(optionEditFragment).detach(setEditFragment).attach(detailEditFragment).replace(R.id.detailLayout, detailEditFragment).commit();
                        Bundle bundle = new Bundle();
                        bundle.putInt("tableNumber", orderInfo.getTableNumber());
                        bundle.putInt("menuId", orderInfo.getMenuId());
                        bundle.putString("orderKor", orderInfo.getOrderKor());
                        bundle.putInt("orderPrice", orderInfo.getOrderPrice());
                        bundle.putInt("orderTotal", orderInfo.getOrderTotal());
                        detailEditFragment.setArguments(bundle);
                    }
                }
            });

            return view;
        }
    }

    // 해당 테이블 초기화 소켓
    class PushData extends AsyncTask<String, Void, String> {
        Socket socket;
        DataOutputStream dos;
        String ip, message;

        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            message = params[1];

            try{
                socket = new Socket(ip, 9800);
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

package android.study.ordertokpos.FragmentPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.BaseAdapter;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.Back.HttpConnect;
import android.study.ordertokpos.Back.MakePrint;
import android.study.ordertokpos.HolderPackage.PaymentHolder;
import android.study.ordertokpos.InfoPackage.PaymentInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentCancelFragment extends Fragment {
    private MainActivity mainActivity;
    private DBHelper dbHelper;
    private ServerInfo serverInfo;
    private StoreInfo storeInfo;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");

    private Button everyButton, cardButton, cashButton, taxButton, paymentCancelButton;
    private TextView pageCountTextView;
    private ListView paymentListView;
    private PaymentAdapter paymentAdapter;
    private ImageView paymentMinusButton, paymentPlusButton;
    private String method;
    private int checkedItem;

    private List<PaymentInfo> paymentInfoList;
    private PaymentInfo selectedPaymentInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        mainActivity = null;
    }

    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_payment_cancel , container, false);

        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        paymentInfoList = new ArrayList<>();
        method = "every";
        selectedPaymentInfo = new PaymentInfo();
        checkedItem = -1;

        PaymentDownload();

        everyButton = rootView.findViewById(R.id.everyButton);
        cardButton = rootView.findViewById(R.id.cardButton);
        cashButton = rootView.findViewById(R.id.cashButton);
        taxButton = rootView.findViewById(R.id.taxButton);
        paymentMinusButton = rootView.findViewById(R.id.paymentMinusButton);
        paymentPlusButton = rootView.findViewById(R.id.paymentPlusButton);
        pageCountTextView = rootView.findViewById(R.id.pageCountTextView);
        paymentListView = rootView.findViewById(R.id.paymentListView);
        paymentCancelButton = rootView.findViewById(R.id.paymentCancelButton);
        paymentAdapter = new PaymentAdapter(mainActivity, R.layout.payment_item);
        paymentListView.setAdapter(paymentAdapter);

        paymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(checkedItem == i){
                    paymentListView.getChildAt(checkedItem).setBackgroundColor(Color.WHITE);
                    selectedPaymentInfo = new PaymentInfo();
                    checkedItem = -1;
                } else if(checkedItem > -1){
                    paymentListView.getChildAt(checkedItem).setBackgroundColor(Color.WHITE);
                    checkedItem = i;
                    paymentListView.getChildAt(i).setBackgroundColor(Color.RED);
                    PaymentInfo paymentInfo = paymentAdapter.getItem(i);
                    Log.d("Console", "AMOUNT : "+paymentInfo.getPaymentAmount());
                    selectedPaymentInfo.setPaymentAcquirerName(paymentInfo.getPaymentAcquirerName());
                    selectedPaymentInfo.setPaymentAcquirerCode(paymentInfo.getPaymentAcquirerCode());
                    selectedPaymentInfo.setPaymentAmount(paymentInfo.getPaymentAmount());
                    selectedPaymentInfo.setPaymentDate(paymentInfo.getPaymentDate());
                    selectedPaymentInfo.setPaymentMethod(paymentInfo.getPaymentMethod());
                    selectedPaymentInfo.setPaymentTime(paymentInfo.getPaymentTime());
                    selectedPaymentInfo.setPaymentNumber(paymentInfo.getPaymentNumber());
                    selectedPaymentInfo.setPaymentTableNumber(paymentInfo.getPaymentTableNumber());
                } else{
                    checkedItem = i;
                    paymentListView.getChildAt(i).setBackgroundColor(Color.RED);
                    PaymentInfo paymentInfo = paymentAdapter.getItem(i);
                    Log.d("Console", "AMOUNT : "+paymentInfo.getPaymentAmount());
                    selectedPaymentInfo.setPaymentAcquirerName(paymentInfo.getPaymentAcquirerName());
                    selectedPaymentInfo.setPaymentAcquirerCode(paymentInfo.getPaymentAcquirerCode());
                    selectedPaymentInfo.setPaymentAmount(paymentInfo.getPaymentAmount());
                    selectedPaymentInfo.setPaymentDate(paymentInfo.getPaymentDate());
                    selectedPaymentInfo.setPaymentMethod(paymentInfo.getPaymentMethod());
                    selectedPaymentInfo.setPaymentTime(paymentInfo.getPaymentTime());
                    selectedPaymentInfo.setPaymentNumber(paymentInfo.getPaymentNumber());
                    selectedPaymentInfo.setPaymentTableNumber(paymentInfo.getPaymentTableNumber());
                }
            }
        });



        everyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentAdapter.clear();
                method = "every";
                setPaymentList(method);
            }
        });
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentAdapter.clear();
                method = "card";
                setPaymentList(method);
            }
        });
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentAdapter.clear();
                method = "cash";
                setPaymentList(method);
            }
        });
        taxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentAdapter.clear();
                method = "tax";
                setPaymentList(method);
            }
        });

        paymentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedPaymentInfo!=null && selectedPaymentInfo.getPaymentAmount()>0){
                    try {
                        showDialog(selectedPaymentInfo);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        paymentPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pageNumber = Integer.parseInt(pageCountTextView.getText().toString());
                if(pageNumber*15<dbHelper.getPaymentNumber()){
                    pageNumber++;
                    if(checkedItem>-1){
                        paymentListView.getChildAt(checkedItem).setBackgroundColor(Color.WHITE);
                        checkedItem = -1;
                    }
                    pageCountTextView.setText(String.valueOf(pageNumber));
                    paymentAdapter.clear();
                    setPaymentList(method);
                    selectedPaymentInfo = new PaymentInfo();
                }else{
                    Toast.makeText(mainActivity, "마지막 페이지 입니다.", Toast.LENGTH_LONG).show();
                }


            }
        });
        paymentMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pageNumber = Integer.parseInt(pageCountTextView.getText().toString());
                if(pageNumber>1){
                    pageNumber--;
                    if(checkedItem>-1){
                        paymentListView.getChildAt(checkedItem).setBackgroundColor(Color.WHITE);
                        checkedItem = -1;
                    }
                    pageCountTextView.setText(String.valueOf(pageNumber));
                    paymentAdapter.clear();
                    setPaymentList(method);
                    selectedPaymentInfo = new PaymentInfo();
                }else{
                    Toast.makeText(mainActivity, "첫 페이지 입니다.", Toast.LENGTH_LONG).show();
                }

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        paymentAdapter.clear();
        setPaymentList(method);
    }

    public void setPaymentList(String method){
        int pageNumber = Integer.parseInt(pageCountTextView.getText().toString());
        paymentInfoList = dbHelper.getPaymentInfoList(pageNumber);
        if(paymentInfoList!= null && paymentInfoList.size()>0){
            for(int j=0; j<paymentInfoList.size(); j++){
                PaymentInfo newPaymentInfo = paymentInfoList.get(j);
                if(method.equals("every")){
                    paymentAdapter.add(newPaymentInfo);
                }else{
                    if(newPaymentInfo.getPaymentMethod().equals(method)){
                        paymentAdapter.add(newPaymentInfo);
                    }
                }

            }
        }
    }

    class PaymentAdapter extends BaseAdapter<PaymentInfo, PaymentHolder>{

        public PaymentAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(PaymentHolder holder, View view) {
            holder.paymentAmountText = view.findViewById(R.id.paymentAmountText);
            holder.paymentCodeText = view.findViewById(R.id.paymentCodeText);
            holder.paymentDateText = view.findViewById(R.id.paymentDateText);
            holder.paymentMethodText = view.findViewById(R.id.paymentMethodText);
            holder.paymentNameText = view.findViewById(R.id.paymentNameText);
            holder.paymentTimeText = view.findViewById(R.id.paymentTimeText);
            holder.paymentNumberText = view.findViewById(R.id.paymentNumberText);
        }

        @Override
        public void setListItem(PaymentHolder holder, PaymentInfo item) {
            holder.paymentAmountText.setText(decimalFormat.format(item.getPaymentAmount()));

            holder.paymentDateText.setText(item.getPaymentDate());

            holder.paymentTimeText.setText(item.getPaymentTime());
            holder.paymentNumberText.setText(item.getPaymentNumber());
            if(item.getPaymentMethod().equals("card")){
                holder.paymentMethodText.setText("카드");
            } else if(item.getPaymentMethod().equals("cash")){
                holder.paymentMethodText.setText("현금");
            }else if(item.getPaymentMethod().equals("tax")){
                holder.paymentMethodText.setText("현금영수증");
            }
            if(item.getPaymentAcquirerCode().equals("null")||item.getPaymentAcquirerCode() == null){
                holder.paymentCodeText.setText("-");
            }else{
                holder.paymentCodeText.setText(item.getPaymentAcquirerCode());
            }
            if(item.getPaymentAcquirerName().equals("null")||item.getPaymentAcquirerName() == null){
                holder.paymentNameText.setText("-");
            }else{
                holder.paymentNameText.setText(item.getPaymentAcquirerName());
            }
        }
    }

    public void showDialog(final PaymentInfo paymentInfo) throws ParseException {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        builder.setTitle("취소하시겠습니까?");
        builder.setMessage(paymentInfo.getPaymentDate()+" "+paymentInfo.getPaymentTime()+" / "+paymentInfo.getPaymentAmount()+"원 취소");
        builder.setCancelable(false);

        final String newDateString = new SimpleDateFormat("yyMMdd", Locale.KOREA).format(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(paymentInfo.getPaymentDate()));

        // 버튼 만들기
        builder.setPositiveButton("결제 취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 리더기에 취소 요청
                if(paymentInfo.getPaymentMethod().equals("card")){
                    startCancel("D4", String.valueOf(paymentInfo.getPaymentAmount()), paymentInfo.getPaymentNumber(), newDateString); //yyMMdd
                }else if(paymentInfo.getPaymentMethod().equals("cash")){
                    startCancel("B4", String.valueOf(paymentInfo.getPaymentAmount()), paymentInfo.getPaymentNumber(), newDateString); //yyMMdd
                }else if(paymentInfo.getPaymentMethod().equals("tax")){
                    startCancel("B2", String.valueOf(paymentInfo.getPaymentAmount()), paymentInfo.getPaymentNumber(), newDateString); //yyMMdd
                }

            }
        });

        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("Console", "NEW DATE : "+newDateString);
            }
        });


        builder.create();
        builder.show();
    }

    // 결제 취소 요청
    public void startCancel(String tran_types, String totalAmount, String appNum, String appDate){

        // 외부 어플(EASYCARD-A) 불러오기
        ComponentName compName = new ComponentName("kr.co.kicc.easycarda","kr.co.kicc.easycarda.CallPopup");

        // 데이터 보내기용 INTENT
        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.putExtra("TRAN_TYPE", tran_types);
        intent.putExtra("TERMINAL_TYPE", "40");
        intent.putExtra("TOTAL_AMOUNT", totalAmount);
        intent.putExtra("TAX", "0");
        intent.putExtra("TAX_OPTION", "A");
        intent.putExtra("TIP", "0");
        intent.putExtra("APPROVAL_NUM", appNum);
        intent.putExtra("APPROVAL_DATE", appDate);
        intent.putExtra("INSTALLMENT", "0");
        // 취소 요청
        if(tran_types.equals("B4")||tran_types.equals("B2")){ // 현금 취소
            intent.putExtra("CASHAMOUNT", "00");
        }

        intent.setComponent(compName);
        startActivityForResult(intent, 1);
    }

    // 결제 요청 이후 받는 함수
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK = -1
        if(resultCode == -1){
            // 승인 요청때 받은 코드
            if(requestCode == 1){
                // 0000 : 정상 완료 => 정상 완료가 아니라면 그냥 메시지 띄우기
                if(!"0000".equals(data.getStringExtra("RESULT_CODE"))){
                    Toast.makeText(mainActivity, "RESULT CODE : "+data.getStringExtra("RESULT_CODE")+", RESULT MSG : "+data.getStringExtra("RESULT_MSG"), Toast.LENGTH_LONG ).show();
                } else{ // 정상완료 후
                    // 현금 계산이라면 => 팝업 띄우고 거스름돈
                    switch (data.getStringExtra("TRAN_TYPE")) {
                        case "B4":
                            // 아래 함수 처리
                            dbHelper.deleteOnePaymentInfo(selectedPaymentInfo);
                            // 외부 통신으로 외부 DB 내 정보 삭제
                            PaymentDelete();
                            break;
                        case "B2":
                            // 아래 함수 처리
                            dbHelper.deleteOnePaymentInfo(selectedPaymentInfo);
                            // 외부 통신으로 외부 DB 내 정보 삭제
                            PaymentDelete();
                            break;
                        case "D4":  // 카드계산이면 => 액수 비교 후 처리
                            // 아래 함수 처리
                            dbHelper.deleteOnePaymentInfo(selectedPaymentInfo);
                            // 외부 통신으로 외부 DB 내 정보 삭제
                            PaymentDelete();
                            break;
                    }


                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void PaymentDelete(){
        new HttpConnect(){

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                selectedPaymentInfo = new PaymentInfo();
            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/paymentCancel.php", "storeId="+storeInfo.getStoreId()+"&paymentAmount="+selectedPaymentInfo.getPaymentAmount()+"&paymentNumber="+selectedPaymentInfo.getPaymentNumber()+"&paymentCode="+selectedPaymentInfo.getPaymentAcquirerCode());
    }

    @SuppressLint("StaticFieldLeak")
    public void PaymentDownload(){
        new HttpConnect(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dbHelper.deleteAllPaymentInfo();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Log.d("Console", "결제 취소 : "+result);
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dateAndTime = jsonObject.getString("paymentDate");
                        String paymentDate = dateAndTime.substring(0, dateAndTime.indexOf(" "));
                        String paymentTime = dateAndTime.substring(dateAndTime.indexOf(" "));
                        PaymentInfo paymentInfo =new PaymentInfo();
                        paymentInfo.setPaymentNumber(jsonObject.getString("paymentNumber"));
                        paymentInfo.setPaymentTime(paymentTime);
                        paymentInfo.setPaymentMethod(jsonObject.getString("paymentMethod"));
                        paymentInfo.setPaymentDate(paymentDate);
                        paymentInfo.setPaymentAmount(jsonObject.getInt("paymentAmount"));
                        paymentInfo.setPaymentAcquirerCode(jsonObject.getString("paymentCode"));
                        paymentInfo.setPaymentAcquirerName(jsonObject.getString("paymentName"));

                        dbHelper.insertPaymentInfo(paymentInfo);
                    }
                    setPaymentList("every");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/paymentDownload.php", "storeId="+storeInfo.getStoreId());
    }
}

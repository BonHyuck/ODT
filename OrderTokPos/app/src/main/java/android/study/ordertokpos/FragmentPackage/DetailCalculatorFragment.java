package android.study.ordertokpos.FragmentPackage;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.Back.HttpConnect;
import android.study.ordertokpos.Back.MakePrint;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.study.ordertokpos.MainActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.List;

public class DetailCalculatorFragment extends Fragment {
    private MainActivity mainActivity;
    // 숫자 형식 정하기
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    // 데이터베이스 설정
    private DBHelper dbHelper;
    // 서버 정보
    private ServerInfo serverInfo;
    // 업체 정보
    private StoreInfo storeInfo;
    // 해당 테이블의 주문 목록 저장
    private List<OrderInfo> orderInfoList;
    // 밖에서 받아오는 테이블 번호
    private int tableNumber;

    // 결제 남은 금액, 결제 할 금액
    private TextView calculatorPayFuture, calculatorPayNow;
    // 남은 금액 : or 거스름돈 :
    private TextView changeTextView;

    // 계산기 버튼 => 하단의 TextView 와 작동 + 뒤에 하나 지우기, 전체 삭제
    private Button oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton, zeroButton, eraseOneButton, eraseAllButton;

    // 하단 결제 버튼
    private Button calculatorPayWithCard, calculatorPayWithCash;

    // 중단 +, - 버튼과 상호작용하는 텍스트뷰
    private Button minusButton, plusButton;
    private TextView calculatorPersonCount;
    private TextView separateCountPerson, separateTotalPerson;

    // 주문의 합계, 이미 지불한 돈, 남은 돈, 뿜빠이
    private int oldTotal, paidTotal, newTotal, dividedTotal;

    // 카운터 설정
    private int personCounter, paymentCounter;

    // 프린트용 값
    String shopName, shopTel, shopAddress, cardName, cardNum, appDate, merchNum, totalAmount, acqName, appNum, payMethod, acqCode, tranType, shopOwner, shopBizNum;
    boolean ifCash, ifCancel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
        mainActivity.usbConnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        mainActivity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail_calculator, container, false);

        // 데이터베이스 및 받아오는 값 불러오기
        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        tableNumber = getArguments().getInt("tableNumber");

        // 초기값으로 전부 0
        oldTotal = 0;
        paidTotal = 0;
        newTotal = 0;
        // 카운터는 1
        /*personCounter = 1;
        paymentCounter = 1;*/

        // 프린트용 값 초기화
        shopName=shopTel=shopAddress=cardName=cardNum=appDate=merchNum=totalAmount=acqName=appNum=payMethod=acqCode=tranType=shopOwner=shopBizNum="";
        ifCancel=ifCash=false;

        // 결제 남은 금액 텍스트뷰 할당
        calculatorPayFuture = rootView.findViewById(R.id.calculatorPayFuture);
        // 결제 금액 텍스트뷰 할당
        calculatorPayNow = rootView.findViewById(R.id.calculatorPayNow);
        changeTextView = rootView.findViewById(R.id.changeTextView);

        // 계산기 버튼 할당
        oneButton = rootView.findViewById(R.id.oneButton);
        twoButton = rootView.findViewById(R.id.twoButton);
        threeButton = rootView.findViewById(R.id.threeButton);
        fourButton = rootView.findViewById(R.id.fourButton);
        fiveButton = rootView.findViewById(R.id.fiveButton);
        sixButton = rootView.findViewById(R.id.sixButton);
        sevenButton = rootView.findViewById(R.id.sevenButton);
        eightButton = rootView.findViewById(R.id.eightButton);
        nineButton = rootView.findViewById(R.id.nineButton);
        zeroButton = rootView.findViewById(R.id.zeroButton);
        // 뒤에 1개 지우기
        eraseOneButton = rootView.findViewById(R.id.eraseOneButton);
        // 전체 삭제 후 0으로 만들기
        eraseAllButton = rootView.findViewById(R.id.eraseAllButton);

        // 현금 및 카드 결제 버튼 할당
        calculatorPayWithCard = rootView.findViewById(R.id.calculatorPayWithCard);
        calculatorPayWithCash = rootView.findViewById(R.id.calculatorPayWithCash);

        // 중단 버튼 할당
        minusButton = rootView.findViewById(R.id.minusButton);
        plusButton = rootView.findViewById(R.id.plusButton);
        // 중단 텍스트뷰 할당
        calculatorPersonCount = rootView.findViewById(R.id.calculatorPersonCount);
        separateCountPerson = rootView.findViewById(R.id.separateCountPerson);
        separateTotalPerson = rootView.findViewById(R.id.separateTotalPerson);

        // 초기 설정
        changeTextView.setText("남은 금액 : ");

        // DB를 조회하여 가격 입력
        orderInfoList = dbHelper.getTableOrderInfo(tableNumber);
        if(orderInfoList!=null&&orderInfoList.size()>0){
            for(int i=0; i<orderInfoList.size(); i++){
                OrderInfo orderInfo = orderInfoList.get(i);
                oldTotal += (orderInfo.getOrderTotal()*orderInfo.getOrderPrice());
            }
            calculatorPayFuture.setText(decimalFormat.format(oldTotal));
            dividedTotal = oldTotal;
            calculatorPayNow.setText(decimalFormat.format(dividedTotal));

        } else{
            Toast.makeText(mainActivity, "주문 내역이 없습니다.", Toast.LENGTH_LONG).show();
            mainActivity.onFragmentChange(0);
        }

        /*calculatorPersonCount.setText(String.valueOf(personCounter));
        separateTotalPerson.setText(String.valueOf(personCounter));
        separateCountPerson.setText(String.valueOf(paymentCounter));*/
        // 초기설정 완료

        // 계산기 버튼 기능 설정
        oneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(1);
            }
        });
        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(2);
            }
        });
        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(3);
            }
        });
        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(4);
            }
        });
        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(5);
            }
        });
        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(6);
            }
        });
        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(7);
            }
        });
        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(8);
            }
        });
        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(9);
            }
        });
        zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber(0);
            }
        });
        // 지우기 및 전체삭제 기능 설정
        eraseOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentText = calculatorPayNow.getText().toString().replace(",", "");
                String paymentNewText = "";
                // 한자리 수면 그대로 두기
                if(paymentText.length()<=1){
                    paymentNewText = "0";
                }else{
                    paymentNewText = paymentText.substring(0, paymentText.length()-1);
                }
                int newTotal = Integer.parseInt(paymentNewText);
                calculatorPayNow.setText(decimalFormat.format(newTotal));
            }
        });
        eraseAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorPayNow.setText("0");
            }
        });
        // 계산기 버튼 끝

        // 플러스, 마이너스 버튼
        /*minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paymentCounter<2){
                    personCounter--;
                    if(personCounter<=0){
                        personCounter = 1;
                    }
                    separateTotalPerson.setText(String.valueOf(personCounter));
                    calculatorPersonCount.setText(String.valueOf(personCounter));
                    paymentCounter = 1;
                    separateCountPerson.setText(String.valueOf(paymentCounter));
                    dividedTotal = (oldTotal/(personCounter*100))*100;
                    calculatorPayNow.setText(decimalFormat.format(dividedTotal));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paymentCounter<2){
                    personCounter++;
                    if(personCounter>=30){
                        personCounter = 30;
                    }
                    separateTotalPerson.setText(String.valueOf(personCounter));
                    calculatorPersonCount.setText(String.valueOf(personCounter));
                    paymentCounter = 1;
                    separateCountPerson.setText(String.valueOf(paymentCounter));
                    dividedTotal = (oldTotal/(personCounter*100))*100;
                    calculatorPayNow.setText(decimalFormat.format(dividedTotal));
                }
            }
        });
*/
        // 현금 결제 버튼
        // 팝업 띄우기
        calculatorPayWithCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 계산기 하단 텍스트뷰에 찍혀있는 입력값 받기
                String currentAmount = calculatorPayNow.getText().toString().replace(",", "");

                /*if(Integer.parseInt(currentAmount)<newTotal){
                    currentAmount = String.valueOf(newTotal);
                }*/

                // 일단 팝업
                cashDialog(currentAmount);

                // 카드 리더기와 통신 (B3 : 현금계산)
                //startPayment("B3", currentAmount);
            }
        });

        // 카드 결제 버튼
        calculatorPayWithCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 계산기 하단 텍스트뷰에 찍혀있는 입력값 받기
                int currentAmount = Integer.parseInt(calculatorPayNow.getText().toString().replace(",", ""));

                /*if(currentAmount<newTotal){
                    currentAmount = newTotal;
                    calculatorPayNow.setText(decimalFormat.format(currentAmount));
                }
*/
                // 카드 리더기와 통신 (D1 : 카드 승인 요청)
                startPayment("D1", String.valueOf(currentAmount));
            }
        });

        return rootView;
    }

    // 현금 결제 팝업
    private void cashDialog(final String paymentAmount){
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("현금 결제");

        LayoutInflater inflater = getLayoutInflater();
        final View bodyView = inflater.inflate(R.layout.dialog_body, null);
        builder.setView(bodyView);

        Button cashAmountButton = bodyView.findViewById(R.id.cashAmountButton);
        final EditText cashAmountText = bodyView.findViewById(R.id.cashAmountText);
        final TextView cashChangeAmountText = bodyView.findViewById(R.id.cashChangeAmountText);
        final TextView cashPaymentTextView = bodyView.findViewById(R.id.cashPaymentTextView);
        final CheckBox cashCheckBox = bodyView.findViewById(R.id.cashCheckBox);

        cashPaymentTextView.setText(decimalFormat.format(Integer.parseInt(paymentAmount)));

        cashAmountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int cashInputAmount = 0;
                if(!editable.toString().equals("")){
                    cashInputAmount = Integer.parseInt(editable.toString());
                }
                int changeAmount = cashInputAmount - Integer.parseInt(paymentAmount);
                if(changeAmount<0){
                    cashChangeAmountText.setText("금액이 부족합니다.");
                }else{
                    cashChangeAmountText.setText(decimalFormat.format(changeAmount));
                }

            }
        });

        cashAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cashInputAmount = Integer.parseInt(cashAmountText.getText().toString());
                int changeAmount = cashInputAmount - Integer.parseInt(paymentAmount);
                if(changeAmount<0){
                    cashChangeAmountText.setText("금액이 부족합니다.");
                }else{
                    cashChangeAmountText.setText(decimalFormat.format(changeAmount));
                }
            }
        });

        // 확인 버튼
        builder.setPositiveButton("결제 확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int cashInputAmount = Integer.parseInt(cashAmountText.getText().toString());
                int changeAmount = cashInputAmount - Integer.parseInt(paymentAmount);
                // 거스름돈이 0 보다 작음 ==> 현금 결제 노노해
                if(changeAmount<0){
                    Toast.makeText(mainActivity, "금액이 부족합니다.", Toast.LENGTH_SHORT).show();
                } else { // 금액이 딱 맞거나 거스름돈이 있음
                    Toast.makeText(mainActivity, "거스름돈 : "+decimalFormat.format(changeAmount), Toast.LENGTH_SHORT).show();
                    if(cashCheckBox.isChecked()){ // 현금 영수증
                        startPayment("B1", paymentAmount);
                    } else { // 그냥 현금
                        startPayment("B3", paymentAmount);
                    }

                }
            }
        });

        builder.setNegativeButton("결제 취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mainActivity, "현금 결제 취소", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create();
        builder.show();
    }

    // 결제 요청
    public void startPayment(String tran_types, String totalAmount){
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
        intent.putExtra("INSTALLMENT", "0");
        if(tran_types.equals("B3") || tran_types.equals("B1")){
            intent.putExtra("CASHAMOUNT", "00");
        }
        if(tran_types.equals("PT")){
            String printMessage = MakePrint.receiptPrint(appDate, tranType, ifCash, ifCancel, Integer.parseInt(totalAmount), Integer.parseInt(totalAmount)*10/100, 0, 0, cardNum, shopName, "1234567890", shopOwner, shopTel, merchNum, shopAddress, cardName, merchNum, "", "", appNum, acqName, "", "", true);
            intent.putExtra("PRINT_PORT", "UCOM3");
            try {
                intent.putExtra("PRINT_DATA", printMessage.getBytes("EUC-KR"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        intent.setComponent(compName);
        // 카드 결제면서 결제 금액이 크면
        if((paidTotal+Integer.parseInt(totalAmount))>oldTotal && !tran_types.equals("PT")){
            Toast.makeText(mainActivity, "금액을 확인해주세요.", Toast.LENGTH_LONG).show();
        }else {
            startActivityForResult(intent, 1);
        }
    }

    // 결제 요청 이후 받는 함수
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainActivity.usbConnect();
        // RESULT_OK = -1
        if(resultCode == -1){
            // 승인 요청때 받은 코드
            if(requestCode == 1){
                // 0000 : 정상 완료 => 정상 완료가 아니라면 그냥 메시지 띄우기
                if(!"0000".equals(data.getStringExtra("RESULT_CODE"))){
                    Toast.makeText(mainActivity, "RESULT CODE : "+data.getStringExtra("RESULT_CODE")+", RESULT MSG : "+data.getStringExtra("RESULT_MSG"), Toast.LENGTH_LONG ).show();
                } else{ // 정상완료 후
                    Toast.makeText(mainActivity, "결제 성공", Toast.LENGTH_LONG ).show();
                    shopName=data.getStringExtra("SHOP_NAME");
                    shopTel=data.getStringExtra("SHOP_TEL");
                    shopAddress=data.getStringExtra("SHOP_ADDRESS");
                    shopOwner = data.getStringExtra("SHOP_OWNER");
                    shopBizNum = data.getStringExtra("SHOP_BIZ_NUM");
                    cardName=data.getStringExtra("CARD_NAME");
                    cardNum=data.getStringExtra("CARD_NUM");
                    appDate=data.getStringExtra("APPROVAL_DATE");
                    merchNum=data.getStringExtra("MERCHANT_NUM");
                    totalAmount=data.getStringExtra("TOTAL_AMOUNT");
                    acqName=data.getStringExtra("ACQUIRER_NAME");
                    appNum=data.getStringExtra("APPROVAL_NUM");
                    acqCode = data.getStringExtra("ACQUIRER_CODE");
                    // 현금 계산이라면 => 팝업 띄우고 거스름돈
                    switch (data.getStringExtra("TRAN_TYPE")) {
                        case "B3":
                            // 아래 함수 처리
                            payMethod = "cash";
                            tranType = "현금(자가발급)";
                            ifCash = true;
                            afterPayment(appNum, totalAmount, payMethod, acqCode, acqName);
                            break;
                        case "B1":
                            // 아래 함수 처리
                            payMethod = "tax";
                            tranType = "현금(현금영수증)";
                            ifCash = true;
                            afterPayment(appNum, totalAmount, payMethod, acqCode, acqName);
                            break;
                        case "D1":  // 카드계산이면 => 액수 비교 후 처리
                            // 아래 함수 처리
                            payMethod = "card";
                            tranType = "IC신용구매";
                            ifCash = true;
                            afterPayment(appNum, totalAmount, payMethod, acqCode, acqName);
                            break;
                    }

                }
            }
        }
    }

    // EasyCard-A 와 통신 이후 처리
    private void afterPayment(String approvalNumber, String totalAmount, String paymentMethod, String acquirerCode, String acquirerName){
        mainActivity.usbConnect();
        /*if(personCounter>0&&personCounter>paymentCounter){
            paymentCounter++;
            separateCountPerson.setText(String.valueOf(paymentCounter));
        }*/
        int currentPaymentAmount = Integer.parseInt(totalAmount);
        // 이미 결제된 금액에 추가
        paidTotal += currentPaymentAmount;
        // 지불한 금액과 지불해야할 금액이 같으면
        final boolean checkMethod = paymentMethod.equals("card") || paymentMethod.equals("cash") || paymentMethod.equals("tax");
        if(paidTotal == oldTotal){
            //startPayment("PT", totalAmount);
            if(checkMethod){
                List<OrderInfo> orderInfos = dbHelper.getTableOrderInfo(tableNumber);
                if(orderInfos.size()>0){
                    mainActivity.posMidPrint(tableNumber, orderInfos);
                    //mainActivity.midPrint(tableNumber, orderInfos);
                }
                // 서버와 통신하여 통계 기록

                PaymentDone(approvalNumber, totalAmount, paymentMethod, acquirerCode, acquirerName);

                // 통계 DB 저장을 위한 작업
                if(orderInfoList!=null&&orderInfoList.size()>0){
                    // 필요 요소의 문자화
                    String menuIdArray = "";
                    String totalArray = "";
                    String priceArray = "";
                    for(int i=0; i<orderInfoList.size(); i++){
                        OrderInfo orderInfo = orderInfoList.get(i);
                        if(i==0){
                            menuIdArray = menuIdArray+orderInfo.getMenuId();
                            totalArray = totalArray+orderInfo.getOrderTotal();
                            priceArray = priceArray+orderInfo.getOrderPrice();
                        }else{
                            menuIdArray = menuIdArray+","+orderInfo.getMenuId();
                            totalArray = totalArray+","+orderInfo.getOrderTotal();
                            priceArray = priceArray+","+orderInfo.getOrderPrice();
                        }
                    }
                    Log.d("Console", "MENUIDARRAY : "+menuIdArray);
                    Log.d("Console", "TOTALARRAY : "+totalArray);
                    // 서버와 통신하여 통계 기록
                    SavePayment(menuIdArray, totalArray, priceArray);
                }
            } else { // 예외처리
                Toast.makeText(mainActivity, "잘 못된 접근입니다.", Toast.LENGTH_LONG).show();
                mainActivity.onFragmentChange(0);
            }

        } else if(paidTotal < oldTotal){ // 지불해야할 금액이 남았다면
            //startPayment("PT", totalAmount);
            // 남은 금액 계산
            newTotal = oldTotal - paidTotal;

            if(checkMethod){
                // 서버와 통신하여 통계 기록
                PaymentDone(approvalNumber, totalAmount, paymentMethod, acquirerCode, acquirerName);

                calculatorPayNow.setText(decimalFormat.format(newTotal));
                calculatorPayFuture.setText(decimalFormat.format(newTotal));

                /*if(personCounter>paymentCounter){
                    if(dividedTotal<=newTotal){
                        calculatorPayNow.setText(decimalFormat.format(dividedTotal));
                    }else{
                        calculatorPayNow.setText(decimalFormat.format(newTotal));
                    }
                }else{
                    calculatorPayNow.setText(decimalFormat.format(newTotal));
                }*/
            } else { // 예외처리
                Toast.makeText(mainActivity, "잘 못된 접근입니다.", Toast.LENGTH_LONG).show();
                mainActivity.onFragmentChange(0);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void PaymentDone(String approvalNumber, final String totalAmount, String paymentMethod, String acquirerCode, String acquirerName){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                mainActivity.usbConnect();
                startPayment("PT", totalAmount);
                /*mainActivity.posPrint(shopName, shopTel, shopAddress, cardName, cardNum, appDate, merchNum, String.valueOf(tableNumber), totalAmount, acqName, appNum);
                mainActivity.posPrint(shopName, shopTel, shopAddress, cardName, cardNum, appDate, merchNum, String.valueOf(tableNumber), totalAmount, acqName, appNum);*/
            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/paymentDone.php", "storeId="+storeInfo.getStoreId()+"&paymentAmount="+totalAmount+"&paymentMethod="+paymentMethod+"&paymentNumber="+approvalNumber+"&tableNumber="+tableNumber+"&acquirerCode="+acquirerCode+"&acquirerName="+acquirerName);
    }

    @SuppressLint("StaticFieldLeak")
    public void SavePayment(String menuIdArray, String totalArray, String priceArray){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dbHelper.deleteTableOrderInfo(tableNumber);
                SendData sendData = new SendData();
                sendData.execute(result, "allClear");
                mainActivity.onFragmentChange(0);
            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/finalPayment.php", "storeId="+storeInfo.getStoreId()+"&menuId="+menuIdArray+"&menuTotal="+totalArray+"&tableNumber="+tableNumber+"&menuPrice="+priceArray);
    }

    // 해당 테이블 초기화 소켓
    class SendData extends AsyncTask<String, Void, String> {

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

    // 계산기 버튼 클릭시 하단 텍스트뷰에 입력
    public void addNumber(int clickedNumber){
        // 결제 금액에 있는 숫자 받아오기
        String paymentText = calculatorPayNow.getText().toString().replace(",", "");
        // 새로 입력될 숫자 변수 선언
        String paymentNewText = "";
        // 0 처리하기
        // 현재 숫자가 0 이하
        if(Integer.parseInt(paymentText)<=0){
            if(clickedNumber != 0){
                paymentNewText += String.valueOf(clickedNumber);
            }else{
                paymentNewText = "0";
            }
        }else{ // 현재 숫자가 0 초과 (뒤에 숫자 붙음)
            paymentNewText = paymentText+clickedNumber;
        }

        // 숫자가 길어지면 그대로 두기
        if(paymentNewText.length()>9){
            calculatorPayNow.setText(decimalFormat.format(Integer.parseInt(paymentText)));
        } else {
            calculatorPayNow.setText(decimalFormat.format(Integer.parseInt(paymentNewText)));
        }
    }



}

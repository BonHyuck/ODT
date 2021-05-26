package android.study.ordertokpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.Back.HttpConnect;
import android.study.ordertokpos.FragmentPackage.CalendarFragment;
import android.study.ordertokpos.FragmentPackage.DetailCalculatorFragment;
import android.study.ordertokpos.FragmentPackage.DetailFragment;
import android.study.ordertokpos.FragmentPackage.PaymentCancelFragment;
import android.study.ordertokpos.FragmentPackage.StatFragment;
import android.study.ordertokpos.FragmentPackage.TableFragment;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    static ServerInfo serverInfo;
    StoreInfo storeInfo;

    // 레이아웃 객체 선언
    Button tableButton, statButton, logoutButton, paymentCancelButton, calendarButton;
    Button updateButton;
    //TextView textView1, textView2, textView3, textView4, textView5;
    FrameLayout mainLayout;

    // Fragment 선언
    TableFragment tableFragment;
    StatFragment statFragment;
    DetailFragment detailFragment;
    PaymentCancelFragment paymentCancelFragment;
    DetailCalculatorFragment detailCalculatorFragment;
    CalendarFragment calendarFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    // USB 연결에 필요한 객체들
    private UsbManager mUsbManager;
    private UsbDevice kitchenDevice;
    private UsbDevice posDevice;
    private UsbDeviceConnection kitchenConnection;
    private UsbDeviceConnection posConnection;
    private UsbInterface kitchenInterface;
    private UsbInterface posInterface;
    private UsbEndpoint kitchenEndPoint;
    private UsbEndpoint posEndPoint;
    private PendingIntent kitchenPermissionIntent;
    private PendingIntent posPermissionIntent;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static Boolean forceCLaim = true;

    // 프린터용 변수
    // 주방용
    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(MainActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread thread = new Thread(new SocketReceiveClass());
        thread.start();

        //레이아웃 객체 할당
        tableButton = findViewById(R.id.tableButton);
        statButton = findViewById(R.id.statButton);
        paymentCancelButton = findViewById(R.id.paymentCancelButton);
        logoutButton = findViewById(R.id.logoutButton);
        mainLayout = findViewById(R.id.mainLayout);
        updateButton = findViewById(R.id.updateButton);
        calendarButton = findViewById(R.id.calendarButton);

        // Fragment 할당
        tableFragment = new TableFragment();
        detailFragment = new DetailFragment();
        statFragment = new StatFragment();
        paymentCancelFragment = new PaymentCancelFragment();
        detailCalculatorFragment = new DetailCalculatorFragment();
        calendarFragment = new CalendarFragment();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainLayout, tableFragment).commitAllowingStateLoss();

        usbConnect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                orderCheck(storeInfo.getStoreId());
            }
        }, 5000);

        //메인 화면의 버튼 기능 추가
        tableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChange(0);
            }
        });

        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChange(100000);
            }
        });

        paymentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChange(150000);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChange(300000);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteAll();
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIpToUpdate();
            }
        });
    }

    // 화면 클릭시 페이지 전환을 위한 함수
    public void onFragmentChange(int index){
        if(index == 0 ){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, tableFragment).commit();
        } else if(index == 100000){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, statFragment).commit();
        } else if(index == 150000){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, paymentCancelFragment).commit();
        } else if(index ==300000){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, calendarFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, detailFragment).commit();
            Bundle bundle = new Bundle();
            bundle.putInt("tableNumber", index);
            detailFragment.setArguments(bundle);
        }
    }

    // 업데이트 통신
    @SuppressLint("StaticFieldLeak")
    void getIpToUpdate(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONArray resultArray = new JSONArray(result);

                    for(int i=0; i<resultArray.length(); i++){
                        String ipAddress = resultArray.getJSONObject(i).getString("ipAddress");
                        // 소켓 전송 (9800)
                        UpdateSocket updateSocket = new UpdateSocket();
                        updateSocket.execute(ipAddress, "update");
                    }
                    reloadMain();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        }.execute(serverInfo.getServerAddress()+"orderTokPos/updateAll.php", "storeId="+storeInfo.getStoreId());
    }

    // USB 연결
    public void usbConnect(){
        // USB 관련 작업
        // USB 관리자 소환
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // 해당 기기에 연결 돼있는 USB 장치 리스트
        mDeviceList = mUsbManager.getDeviceList();
        // USB 장치가 1개 이상이면
        if (mDeviceList.size() > 0) {
            mDeviceIterator = mDeviceList.values().iterator();
            // 연결 확인 완료
            //Toast.makeText(MainActivity.this, "ManuName : "+kitchenInfo.getManufacturerName()+", ProdId : "+kitchenInfo.getProductId(), Toast.LENGTH_LONG).show();

            while (mDeviceIterator.hasNext()) {
                UsbDevice usbDevice1 = mDeviceIterator.next();

                if (usbDevice1.getManufacturerName().equals("BIXOLON") || usbDevice1.getVendorId()==5380){
                    kitchenDevice = usbDevice1;

                    kitchenPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    registerReceiver(kitchenUsbReceiver, filter);

                    mUsbManager.requestPermission(kitchenDevice, kitchenPermissionIntent);
                    Toast.makeText(MainActivity.this, "주방 프린터 연결", Toast.LENGTH_LONG).show();

                    posDevice = usbDevice1;

                    posPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter1 = new IntentFilter(ACTION_USB_PERMISSION);
                    registerReceiver(posUsbReceiver, filter1);

                    mUsbManager.requestPermission(posDevice, posPermissionIntent);
                    Toast.makeText(MainActivity.this, "포스 프린터 연결", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void orderCheck(int storeId){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONArray resultArray = new JSONArray(result);
                    List<OrderInfo> orderInfoList = new ArrayList<>();
                    for(int k=0; k<resultArray.length(); k++){
                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setMenuId(resultArray.getJSONObject(k).getInt("menuId"));
                        orderInfo.setOrderDate(resultArray.getJSONObject(k).getInt("orderTime"));
                        orderInfo.setOrderKor(resultArray.getJSONObject(k).getString("orderKor"));
                        orderInfo.setOrderEng(resultArray.getJSONObject(k).getString("orderEng"));
                        orderInfo.setOrderChn(resultArray.getJSONObject(k).getString("orderChn"));
                        orderInfo.setOrderJpn(resultArray.getJSONObject(k).getString("orderJpn"));
                        orderInfo.setTableNumber(resultArray.getJSONObject(k).getInt("tableNumber"));
                        orderInfo.setOrderTotal(resultArray.getJSONObject(k).getInt("orderTotal"));
                        orderInfo.setOrderPrice(resultArray.getJSONObject(k).getInt("orderPrice"));
                        orderInfo.setOrderPrinted(resultArray.getJSONObject(k).getInt("orderTotal"));

                        orderInfoList.add(orderInfo);

                        if(dbHelper.getOneOrderInfo(resultArray.getJSONObject(k).getInt("menuId"), resultArray.getJSONObject(k).getInt("tableNumber"), resultArray.getJSONObject(k).getString("orderKor"))!= null){
                            OrderInfo prevOrderInfo = dbHelper.getOneOrderInfo(resultArray.getJSONObject(k).getInt("menuId"), resultArray.getJSONObject(k).getInt("tableNumber"), resultArray.getJSONObject(k).getString("orderKor"));
                            dbHelper.updateOneOrderTotal(orderInfo, orderInfo.getOrderTotal()+prevOrderInfo.getOrderTotal());
                        }else{
                            dbHelper.insertOrderInfo(orderInfo);
                        }

                        if(k==resultArray.length()-1){
                            reloadMain();
                        }
                    }

                    String orderStringList = "";
                    if(orderInfoList.size()>0){
                        int tablePrint = 0;
                        List<Integer> selectedCategoryList = dbHelper.getSelectedCategoryInfo();
                        if(selectedCategoryList != null && selectedCategoryList.size()>0){
                            for(int k=0; k<orderInfoList.size(); k++){
                                OrderInfo newOrderInfo = orderInfoList.get(k);
                                dbHelper.printDone(newOrderInfo);
                                if(selectedCategoryList.contains(dbHelper.getOneMenuInfo(newOrderInfo.getMenuId()).getCategoryId())){
                                    if(tablePrint==0){
                                        tablePrint = newOrderInfo.getTableNumber();
                                        orderStringList = "\n\n"+newOrderInfo.getOrderKor()+"            "+newOrderInfo.getOrderPrinted();
                                    }else{
                                        orderStringList = orderStringList+"\n\n"+newOrderInfo.getOrderKor()+"     "+newOrderInfo.getOrderPrinted();
                                    }
                                }
                            }
                            try {
                                Toast.makeText(MainActivity.this, orderStringList, Toast.LENGTH_LONG).show();
                                if(!orderStringList.equals("")){
                                    //startPrint("PT", tablePrint, orderStringList+"\n\n");
                                    if(kitchenConnection!=null){
                                        kitchenPrint(kitchenConnection, kitchenInterface, tablePrint, orderStringList+"\n\n");
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/checkOrder.php", "storeId="+storeId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        usbConnect();
    }

    public void reloadMain(){
        if(tableFragment.isVisible()){
            tableFragment.reloadTableFragment();
        }else if(detailFragment.isVisible()){
            detailFragment.reloadDetailFragment();
        }else if(statFragment.isVisible()){
            statFragment.reloadStatFragment();
        }
    }

    /*public void startPrint(String tran_types, final int tablePrint, final String stringArray) throws UnsupportedEncodingException {
        // 외부 어플(EASYCARD-A) 불러오기
        ComponentName compName = new ComponentName("kr.co.kicc.easycarda","kr.co.kicc.easycarda.CallPopup");

        String tableString = String.valueOf(tablePrint);



        // 종이 절단 명령
        byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
        // 글자 크기
        byte[] contentByte = new byte[]{0x1B,0x21,0x10};
        byte[] titleByte = {0x1D, 0x21, 77};
        //byte[] upside_down = {0x1B, 0x7B, 0};

        String totalString = new String(titleByte)+tableString+new String(contentByte)+"\n ____________________ \n"+stringArray+new String(cut_paper);

        // 데이터 보내기용 INTENT
        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.putExtra("TRAN_TYPE", tran_types);
        intent.putExtra("TERMINAL_TYPE", "40");
        intent.putExtra("TOTAL_AMOUNT", "0");
        intent.putExtra("TAX", "0");
        intent.putExtra("TAX_OPTION", "A");
        intent.putExtra("TIP", "0");
        intent.putExtra("INSTALLMENT", "0");
        intent.putExtra("PRINT_PORT", "COM3");
        intent.putExtra("PRINT_DATA", totalString.getBytes("EUC-KR"));

        intent.setComponent(compName);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPrint(int tablePrint, List<OrderInfo> orderInfoList){
        String orderStringList = "";
        if(orderInfoList != null && orderInfoList.size()>0){
            for(int k=0; k<orderInfoList.size(); k++){
                OrderInfo orderInfo = orderInfoList.get(k);
                orderStringList += "\n\n"+orderInfo.getOrderKor()+"            "+orderInfo.getOrderPrinted();
                dbHelper.printDone(orderInfo);
            }
        }
        try {
            startPrint("PT", tablePrint, orderStringList+"\n\n");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }*/

    public void kitchenPrint(final UsbDeviceConnection connection, final UsbInterface usbInterface, final int tablePrint, final String stringArray) throws UnsupportedEncodingException {
            String tableString = String.valueOf(tablePrint);

            final byte[] tableByte = tableString.getBytes("euc-kr");
            final byte[] printByte = stringArray.getBytes("euc-kr");
            final byte[] separatorByte = "\n ____________________ \n".getBytes();

            connection.claimInterface(usbInterface, forceCLaim);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 종이 절단 명령
                    byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
                    // 글자 크기
                    byte[] contentByte = new byte[]{0x1B,0x21,0x10};
                    byte[] titleByte = {0x1D, 0x21, 77};
                    //byte[] upside_down = {0x1B, 0x7B, 0};

                    //connection.bulkTransfer(kitchenEndPoint, upside_down, upside_down.length, 0);
                    connection.bulkTransfer(kitchenEndPoint, titleByte, titleByte.length, 0);
                    connection.bulkTransfer(kitchenEndPoint, tableByte, tableByte.length, 0);
                    connection.bulkTransfer(kitchenEndPoint, contentByte, contentByte.length, 0);
                    connection.bulkTransfer(kitchenEndPoint, separatorByte, separatorByte.length, 0);

                    connection.bulkTransfer(kitchenEndPoint, printByte, printByte.length, 0);
                    connection.bulkTransfer(kitchenEndPoint, cut_paper, cut_paper.length, 0);
                }
            });
            thread.run();
    }

    public void kitchenPrint(int tablePrint, List<OrderInfo> orderInfoList){
        String orderStringList = "";
        if(orderInfoList != null && orderInfoList.size()>0){
            for(int k=0; k<orderInfoList.size(); k++){
                OrderInfo orderInfo = orderInfoList.get(k);
                List<Integer> selectedCategoryList = dbHelper.getSelectedCategoryInfo();
                if(selectedCategoryList != null && selectedCategoryList.size()>0){
                    dbHelper.printDone(orderInfo);
                    if(selectedCategoryList.contains(dbHelper.getOneMenuInfo(orderInfo.getMenuId()).getCategoryId())){
                        orderStringList += "\n\n"+orderInfo.getOrderKor()+"            "+orderInfo.getOrderPrinted();
                    }
                }
            }
        }
        if(!orderStringList.equals("")){
            try {
                kitchenPrint(kitchenConnection, kitchenInterface, tablePrint, orderStringList+"\n\n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    public void midPrint(String tran_types, final int tablePrint, final String stringArray, String totalValue) throws UnsupportedEncodingException {
        // 외부 어플(EASYCARD-A) 불러오기
        ComponentName compName = new ComponentName("kr.co.kicc.easycarda","kr.co.kicc.easycarda.CallPopup");

        String tableString = String.valueOf(tablePrint);
        String totalString = "\n합계 : "+totalValue+"\n";

        // 종이 절단 명령
        byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
        // 글자 크기
        byte[] contentByte = new byte[]{0x1B,0x21,0x10};
        byte[] titleByte = {0x1D, 0x21, 77};

        String finalString = new String(titleByte)+tableString+new String(contentByte)+"\n ____________________ \n"+stringArray+"\n ____________________ \n"+totalString+new String(cut_paper);

        // 데이터 보내기용 INTENT
        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.putExtra("TRAN_TYPE", tran_types);
        intent.putExtra("TERMINAL_TYPE", "40");
        intent.putExtra("TOTAL_AMOUNT", "0");
        intent.putExtra("TAX", "0");
        intent.putExtra("TAX_OPTION", "A");
        intent.putExtra("TIP", "0");
        intent.putExtra("INSTALLMENT", "0");
        intent.putExtra("PRINT_PORT", "COM3");
        intent.putExtra("PRINT_DATA", finalString.getBytes("EUC-KR"));

        intent.setComponent(compName);
        startActivityForResult(intent, 1);
    }

    public void midPrint(final int tablePrint, final List<OrderInfo> orderInfoList) throws UnsupportedEncodingException {
        String orderStringList = "";
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        int finalTotalValue = 0;
        if(orderInfoList != null && orderInfoList.size()>0){
            for(int k=0; k<orderInfoList.size(); k++){
                OrderInfo orderInfo = orderInfoList.get(k);
                int totalPrice = orderInfo.getOrderTotal()*orderInfo.getOrderPrice();
                orderStringList += "\n\n"+orderInfo.getOrderKor()+" x"+orderInfo.getOrderTotal()+"  "+decimalFormat.format(totalPrice);
                finalTotalValue+=totalPrice;
            }
        }
        midPrint("PT", tablePrint, orderStringList+"\n\n", decimalFormat.format(finalTotalValue));
    }

    public void posMidPrint(final UsbDeviceConnection connection, final UsbInterface usbInterface, final int tablePrint, final String stringArray, String totalValue) throws UnsupportedEncodingException {
        if (usbInterface == null) {
            Toast.makeText(this, "INTERFACE IS NULL", Toast.LENGTH_SHORT).show();
        } else if (connection == null) {
            Toast.makeText(this, "CONNECTION IS NULL", Toast.LENGTH_SHORT).show();
        } else if (forceCLaim == null) {
            Toast.makeText(this, "FORCE CLAIM IS NULL", Toast.LENGTH_SHORT).show();
        } else if(stringArray == null || stringArray.equals("")){
            Toast.makeText(this, "No Order", Toast.LENGTH_SHORT).show();
        } else {
            String tableString = String.valueOf(tablePrint);
            String totalString = "\n합계 : "+totalValue+"\n";

            final byte[] tableByte = tableString.getBytes("euc-kr");
            final byte[] printByte = stringArray.getBytes("euc-kr");
            final byte[] separatorByte = "\n ____________________ \n".getBytes();
            final byte[] totalByte = totalString.getBytes("euc-kr");

            connection.claimInterface(usbInterface, forceCLaim);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 종이 절단 명령
                    byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
                    // 글자 크기
                    byte[] contentByte = new byte[]{0x1B,0x21,0x10};
                    byte[] titleByte = {0x1D, 0x21, 77};
                    //byte[] upside_down = {0x1B, 0x7B, 0};

                    //connection.bulkTransfer(kitchenEndPoint, upside_down, upside_down.length, 0);
                    connection.bulkTransfer(posEndPoint, titleByte, titleByte.length, 0);
                    connection.bulkTransfer(posEndPoint, tableByte, tableByte.length, 0);
                    connection.bulkTransfer(posEndPoint, contentByte, contentByte.length, 0);
                    connection.bulkTransfer(posEndPoint, separatorByte, separatorByte.length, 0);

                    connection.bulkTransfer(posEndPoint, printByte, printByte.length, 0);
                    connection.bulkTransfer(posEndPoint, separatorByte, separatorByte.length, 0);
                    connection.bulkTransfer(posEndPoint, totalByte, totalByte.length, 0);
                    connection.bulkTransfer(posEndPoint, cut_paper, cut_paper.length, 0);
                }
            });
            thread.run();
        }
    }

    public void posMidPrint(final int tablePrint, final List<OrderInfo> orderInfoList){
        String orderStringList = "";
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        int finalTotalValue = 0;
        if(orderInfoList != null && orderInfoList.size()>0){
            for(int k=0; k<orderInfoList.size(); k++){
                OrderInfo orderInfo = orderInfoList.get(k);
                int totalPrice = orderInfo.getOrderTotal()*orderInfo.getOrderPrice();
                orderStringList += "\n\n"+orderInfo.getOrderKor()+" x"+orderInfo.getOrderTotal()+"  "+decimalFormat.format(totalPrice);
                finalTotalValue+=totalPrice;
            }
        }
        try {
            posMidPrint(posConnection, posInterface, tablePrint, orderStringList+"\n\n", decimalFormat.format(finalTotalValue));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void posPrint(final UsbDeviceConnection connection, final UsbInterface usbInterface, String shopName, String shopTel, String shopAddress, String cardName, String cardNumber, String approvalDate, String merchantNumber, String tableNumber, String totalAmount, String acquirerName, String approvalNumber) throws UnsupportedEncodingException {
        if (usbInterface == null) {
            Toast.makeText(this, "INTERFACE IS NULL", Toast.LENGTH_SHORT).show();
        } else if (connection == null) {
            Toast.makeText(this, "CONNECTION IS NULL", Toast.LENGTH_SHORT).show();
        } else if (forceCLaim == null) {
            Toast.makeText(this, "FORCE CLAIM IS NULL", Toast.LENGTH_SHORT).show();
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            final byte[] shopNameByte = (shopName+"\n").getBytes("euc-kr");
            shopTel = "TEL : "+shopTel+"\n";
            final byte[] shopTelByte = shopTel.getBytes();
            final byte[] shopAddressByte = (shopAddress+"\n").getBytes("euc-kr");
            final byte[] cardNameByte = (cardName+"\n").getBytes("euc-kr");
            final byte[] cardNumberByte = (cardNumber+"\n").getBytes("euc-kr");
            String appDate = "거래일자 : 20"+approvalDate.substring(0,2)+"년"+approvalDate.substring(2,4)+"월"+approvalDate.substring(4,6)+"일 "+approvalDate.substring(6,8)+":"+approvalDate.substring(8,10)+":"+approvalDate.substring(10,12);
            final byte[] dateByte = (appDate+"\n").getBytes("euc-kr");
            final byte[] merchantNumberByte = ("가맹번호 : "+merchantNumber+"\n").getBytes("euc-kr");
            final byte[] tableNumberByte = ("테이블명 : "+tableNumber+"\n").getBytes("euc-kr");
            int totalValue = Integer.parseInt(totalAmount);
            final byte[] priceByte = ("거래 금액 : "+decimalFormat.format(totalValue*90/100)+"원\n").getBytes("euc-kr");
            final byte[] taxByte = ("부가 세액 : "+decimalFormat.format(totalValue*10/100)+"원\n").getBytes("euc-kr");
            final byte[] totalByte = ("합 계 : "+decimalFormat.format(totalValue)+"원\n").getBytes("euc-kr");
            final byte[] acquirerByte = ("매입사 : "+acquirerName+"\n").getBytes("euc-kr");
            final byte[] appNumberByte = ("승인 번호 : "+approvalNumber+"\n").getBytes("euc-kr");

            connection.claimInterface(usbInterface, forceCLaim);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 종이 절단 명령
                    byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
                    // 글자 크기
                    //byte[] contentByte = new byte[]{0x1B,0x21,0x10};
                    //byte[] titleByte = {0x1D, 0x21, 77};
                    //byte[] upside_down = {0x1B, 0x7B, 0};

                    //connection.bulkTransfer(posEndPoint, contentByte, contentByte.length, 0);

                    connection.bulkTransfer(posEndPoint, shopNameByte, shopNameByte.length, 0);
                    connection.bulkTransfer(posEndPoint, shopTelByte, shopTelByte.length, 0);
                    connection.bulkTransfer(posEndPoint, shopAddressByte, shopAddressByte.length, 0);
                    connection.bulkTransfer(posEndPoint, cardNameByte, cardNameByte.length, 0);
                    connection.bulkTransfer(posEndPoint, cardNumberByte, cardNumberByte.length, 0);
                    connection.bulkTransfer(posEndPoint, dateByte, dateByte.length, 0);
                    connection.bulkTransfer(posEndPoint, merchantNumberByte, merchantNumberByte.length, 0);
                    connection.bulkTransfer(posEndPoint, tableNumberByte, tableNumberByte.length, 0);
                    connection.bulkTransfer(posEndPoint, priceByte, priceByte.length, 0);
                    connection.bulkTransfer(posEndPoint, taxByte, taxByte.length, 0);
                    connection.bulkTransfer(posEndPoint, totalByte, totalByte.length, 0);
                    connection.bulkTransfer(posEndPoint, acquirerByte, acquirerByte.length, 0);
                    connection.bulkTransfer(posEndPoint, appNumberByte, appNumberByte.length, 0);

                    connection.bulkTransfer(posEndPoint, cut_paper, cut_paper.length, 0);
                }
            });
            thread.run();
        }
    }

    public void posPrint(String shopName, String shopTel, String shopAddress, String cardName, String cardNumber, String approvalDate, String merchantNumber, String tableNumber, String totalAmount, String acquirerName, String approvalNumber){
        try {
            posPrint(posConnection, posInterface, shopName, shopTel, shopAddress, cardName, cardNumber, approvalDate, merchantNumber, tableNumber, totalAmount, acquirerName, approvalNumber);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    // USB 설정
    final BroadcastReceiver kitchenUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            kitchenInterface = device.getInterface(0);
                            kitchenEndPoint = kitchenInterface.getEndpoint(1);// 0 IN and  1 OUT to printer.
                            kitchenConnection = mUsbManager.openDevice(device);
                        }
                    } else {
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    // USB 설정
    final BroadcastReceiver posUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            posInterface = device.getInterface(0);
                            posEndPoint = posInterface.getEndpoint(1);// 0 IN and  1 OUT to printer.
                            posConnection = mUsbManager.openDevice(device);
                        }
                    } else {
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    // 소켓
    public class SocketReceiveClass implements Runnable{

        ServerSocket serverSocket;
        Socket s;
        DataInputStream dis;
        String received;
        Handler handler = new Handler();
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(9750);
                while(true){
                    s = serverSocket.accept();
                    dis = new DataInputStream(s.getInputStream());
                    // call, or dish,
                    received = dis.readUTF();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(received.contains(",")){
                                String[] receivedArray = received.split(",,,");
                                if(receivedArray[0].equals("dish")){
                                    orderCheck(storeInfo.getStoreId());
                                }else{
                                    orderCheck(storeInfo.getStoreId());
                                }
                            }else if(received.length()>0){
                                orderCheck(storeInfo.getStoreId());
                            }

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

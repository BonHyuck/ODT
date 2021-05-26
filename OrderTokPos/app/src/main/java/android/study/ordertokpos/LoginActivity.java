package android.study.ordertokpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.Back.HttpConnect;
import android.study.ordertokpos.InfoPackage.CategoryInfo;
import android.study.ordertokpos.InfoPackage.MenuInfo;
import android.study.ordertokpos.InfoPackage.OptionInfo;
import android.study.ordertokpos.InfoPackage.PrinterInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.SetInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    ServerInfo serverInfo;
    DBHelper dbHelper;

    EditText idText, pwText;
    Button loginButton;
    String userId, userPw;

    private UsbManager mUsbManager;
    String productNameArray, manufacturerNameArray, vendorIdArray, productIdArray, thisIpAddress;

    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serverInfo = new ServerInfo();
        dbHelper = new DBHelper(LoginActivity.this);

        // IP 받기
        thisIpAddress = serverInfo.getLocalIpAddress();

        productIdArray = "";
        productNameArray = "";
        vendorIdArray = "";
        manufacturerNameArray = "";

        idText = findViewById(R.id.idText);
        pwText = findViewById(R.id.pwText);
        loginButton = findViewById(R.id.loginButton);

        // USB 관련 작업
        // USB 관리자 소환
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // 해당 기기에 연결 돼있는 USB 장치 리스트
        mDeviceList = mUsbManager.getDeviceList();

        // USB 장치가 1개 이상이면
        if (mDeviceList.size() > 0) {
            mDeviceIterator = mDeviceList.values().iterator();
            while (mDeviceIterator.hasNext()) {
                // Device 할당
                UsbDevice usbDevice1 = mDeviceIterator.next();
                int interfaceCount = usbDevice1.getInterfaceCount();
                for(int i=0; i<interfaceCount; i++){
                    if(usbDevice1.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER){
                        if(!productNameArray.equals("")) productNameArray += ",";
                        if(!productIdArray.equals("")) productIdArray += ",";
                        if(!manufacturerNameArray.equals("")) manufacturerNameArray += ",";
                        if(!vendorIdArray.equals("")) vendorIdArray += ",";

                        productIdArray += usbDevice1.getProductId();
                        productNameArray += usbDevice1.getProductName();
                        vendorIdArray += usbDevice1.getVendorId();
                        manufacturerNameArray += usbDevice1.getManufacturerName();
                    }
                }
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId = idText.getText().toString();
                userPw = pwText.getText().toString();
                LoginCheck();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void LoginCheck(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try{
                    JSONObject jsonObject =new JSONObject(result);
                    JSONArray loginArray = jsonObject.getJSONArray("login");
                    JSONArray categoryArray = jsonObject.getJSONArray("category");
                    JSONArray menuArray = jsonObject.getJSONArray("menu");
                    JSONArray printerArray = jsonObject.getJSONArray("printer");
                    JSONArray setArray = jsonObject.getJSONArray("set_option");
                    JSONArray optionArray = jsonObject.getJSONArray("menu_option");

                    // 로그인 실패
                    if(!loginArray.getJSONObject(0).getString("rt").equals("ok")){
                        Toast.makeText(LoginActivity.this, "입력값을 확인해주세요", Toast.LENGTH_LONG).show();
                    }else{
                        dbHelper.deleteAll();

                        StoreInfo newStoreInfo = new StoreInfo();
                        newStoreInfo.setStoreId(loginArray.getJSONObject(0).getInt("store_id"));
                        newStoreInfo.setStoreName(loginArray.getJSONObject(0).getString("store_name"));
                        newStoreInfo.setStoreRandom(loginArray.getJSONObject(0).getString("store_random"));
                        newStoreInfo.setTableNumber(loginArray.getJSONObject(0).getInt("store_table_number"));
                        dbHelper.insertStoreInfo(newStoreInfo);

                        if(categoryArray.length()>0){
                            for(int i=0; i<categoryArray.length(); i++){
                                CategoryInfo categoryInfo = new CategoryInfo();
                                categoryInfo.setCategoryId(categoryArray.getJSONObject(i).getInt("categoryId"));
                                categoryInfo.setCategoryKor(categoryArray.getJSONObject(i).getString("categoryKor"));
                                categoryInfo.setCategoryEng(categoryArray.getJSONObject(i).getString("categoryEng"));
                                categoryInfo.setCategoryChn(categoryArray.getJSONObject(i).getString("categoryChn"));
                                categoryInfo.setCategoryJpn(categoryArray.getJSONObject(i).getString("categoryJpn"));
                                categoryInfo.setSelectedCategory(categoryArray.getJSONObject(i).getInt("selectedCategory"));
                                categoryInfo.setCategorySet(categoryArray.getJSONObject(i).getInt("categorySet"));
                                dbHelper.insertCategoryInfo(categoryInfo);
                            }
                        }

                        if(menuArray.length()>0){
                            for(int j=0; j<menuArray.length(); j++){
                                MenuInfo menuInfo = new MenuInfo();
                                menuInfo.setMenuId(menuArray.getJSONObject(j).getInt("menuId"));
                                menuInfo.setCategoryId(menuArray.getJSONObject(j).getInt("categoryId"));
                                menuInfo.setMenuPrice(menuArray.getJSONObject(j).getInt("menuPrice"));
                                menuInfo.setMenuKor(menuArray.getJSONObject(j).getString("menuKor"));
                                menuInfo.setMenuEng(menuArray.getJSONObject(j).getString("menuEng"));
                                menuInfo.setMenuChn(menuArray.getJSONObject(j).getString("menuChn"));
                                menuInfo.setMenuJpn(menuArray.getJSONObject(j).getString("menuJpn"));
                                menuInfo.setMenuOption(menuArray.getJSONObject(j).getInt("menuOption"));
                                menuInfo.setImgPath(menuArray.getJSONObject(j).getString("imgPath"));
                                dbHelper.insertMenuInfo(menuInfo);
                            }
                        }

                        if(setArray.length()>0){
                            for (int k = 0; k < setArray.length(); k++) {
                                SetInfo setInfo = new SetInfo();
                                setInfo.setCategoryId(setArray.getJSONObject(k).getInt("categoryId"));
                                setInfo.setMenuId(setArray.getJSONObject(k).getInt("menuId"));
                                setInfo.setSetNumber(setArray.getJSONObject(k).getInt("setNumber"));
                                setInfo.setSetPrice(setArray.getJSONObject(k).getInt("setPrice"));
                                setInfo.setSetKor(setArray.getJSONObject(k).getString("setKor"));
                                setInfo.setSetEng(setArray.getJSONObject(k).getString("setEng"));
                                setInfo.setSetChn(setArray.getJSONObject(k).getString("setChn"));
                                setInfo.setSetJpn(setArray.getJSONObject(k).getString("setJpn"));
                                dbHelper.insertSetInfo(setInfo);
                            }
                        }

                        if(optionArray.length()>0){
                            for (int m = 0; m < optionArray.length(); m++) {
                                OptionInfo optionInfo = new OptionInfo();
                                optionInfo.setCategoryId(optionArray.getJSONObject(m).getInt("categoryId"));
                                optionInfo.setMenuId(optionArray.getJSONObject(m).getInt("menuId"));
                                optionInfo.setOptionPrice(optionArray.getJSONObject(m).getInt("optionPrice"));
                                optionInfo.setOptionKor(optionArray.getJSONObject(m).getString("optionKor"));
                                optionInfo.setOptionEng(optionArray.getJSONObject(m).getString("optionEng"));
                                optionInfo.setOptionChn(optionArray.getJSONObject(m).getString("optionChn"));
                                optionInfo.setOptionJpn(optionArray.getJSONObject(m).getString("optionJpn"));
                                dbHelper.insertOptionInfo(optionInfo);
                            }
                        }

                        if(printerArray.length()>0){
                            for(int k=0; k<printerArray.length(); k++){
                                PrinterInfo printerInfo = new PrinterInfo();
                                printerInfo.setProductName(printerArray.getJSONObject(k).getString("product_name"));
                                printerInfo.setManufacturerName(printerArray.getJSONObject(k).getString("manufacturer_name"));
                                printerInfo.setVendorId(printerArray.getJSONObject(k).getString("vendor_id"));
                                printerInfo.setProductId(printerArray.getJSONObject(k).getString("product_id"));
                                printerInfo.setPrinterWhat(printerArray.getJSONObject(k).getString("printer_what"));
                                dbHelper.insertPrinterInfo(printerInfo);
                            }
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(serverInfo.getServerAddress()+"orderTokPos/loginOk.php", "userId=" + userId + "&userPw=" + userPw + "&ipAddress="+thisIpAddress+"&productNameArray="+productNameArray+"&manufacturerNameArray="+manufacturerNameArray+"&vendorIdArray="+vendorIdArray+"&productIdArray="+productIdArray);
    }
}

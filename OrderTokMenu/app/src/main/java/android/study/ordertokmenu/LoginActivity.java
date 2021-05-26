package android.study.ordertokmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.Back.HttpConnect;
import android.study.ordertokmenu.Back.SocketReceiveClass;
import android.study.ordertokmenu.InfoPackage.CategoryInfo;
import android.study.ordertokmenu.InfoPackage.MenuInfo;
import android.study.ordertokmenu.InfoPackage.OptionInfo;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.SetInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText loginId, loginPw, loginTableNumber, tablePw;
    Button loginButton;
    String userIdInput, userPwInput, userTableInput, thisIpAddress, tablePwInput;
    DBHelper dbHelper;
    ServerInfo serverInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 레이아웃 객체 선언
        loginId = findViewById(R.id.loginId);
        loginPw = findViewById(R.id.loginPw);
        loginTableNumber = findViewById(R.id.loginTableNumber);
        tablePw = findViewById(R.id.tablePw);
        loginButton = findViewById(R.id.loginButton);

        // 내부 DB 객체 선언
        dbHelper = new DBHelper(LoginActivity.this);
        serverInfo = new ServerInfo();

        Thread thread = new Thread(new SocketReceiveClass(LoginActivity.this));
        thread.start();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                thisIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                userIdInput = loginId.getText().toString();
                userPwInput = loginPw.getText().toString();
                userTableInput = loginTableNumber.getText().toString();
                tablePwInput = tablePw.getText().toString();
                insertData();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void insertData(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    Log.d("Console", result);
                    JSONObject jsonObject =new JSONObject(result);
                    String loginResult = jsonObject.getString("rt");
                    if(!loginResult.equals("ok")){
                        Toast.makeText(LoginActivity.this, "입력값을 확인해주세요", Toast.LENGTH_LONG).show();
                    }else{
                        if(Integer.parseInt(userTableInput)>jsonObject.getInt("store_table_number")||userTableInput.equals("")){
                            Toast.makeText(LoginActivity.this, "테이블 번호를 확인해주세요", Toast.LENGTH_LONG).show();
                        }else{
                            StoreInfo newStoreInfo = new StoreInfo();
                            newStoreInfo.setStoreId(jsonObject.getInt("store_id"));
                            newStoreInfo.setStoreName(jsonObject.getString("store_name"));
                            newStoreInfo.setStoreRandom(jsonObject.getString("store_random"));
                            newStoreInfo.setTableNumber(Integer.parseInt(userTableInput));
                            newStoreInfo.setUpdateTime(jsonObject.getInt("store_update"));
                            newStoreInfo.setPosIpAddress(jsonObject.getString("pos_ip_address"));
                            newStoreInfo.setEmployeeIpAddress(jsonObject.getString("employee_ip_address"));
                            newStoreInfo.setLogoImgPath(jsonObject.getString("logo_img_path"));
                            newStoreInfo.setBackImgPath(jsonObject.getString("back_img_path"));
                            newStoreInfo.setResetCode(tablePwInput);
                            dbHelper.insertStoreInfo(newStoreInfo);

                            downloadData(newStoreInfo.getStoreId());

                            Intent intent = new Intent(LoginActivity.this, LanguageActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTok/loginOk.php", "userId="+userIdInput+"&userPw="+userPwInput+"&tableNumber="+userTableInput+"&ipAddress="+thisIpAddress);
    }

    @SuppressLint("StaticFieldLeak")
    public void downloadData(int storeId) {
        new HttpConnect() {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    Log.d("Console", "RESULT : "+result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray categoryArray = jsonObject.getJSONArray("category");
                    JSONArray menuArray = jsonObject.getJSONArray("menu");
                    JSONArray setArray = jsonObject.getJSONArray("set");
                    JSONArray optionArray = jsonObject.getJSONArray("option");


                    for (int i = 0; i < categoryArray.length(); i++) {
                        CategoryInfo categoryInfo = new CategoryInfo();
                        categoryInfo.setCategoryId(categoryArray.getJSONObject(i).getInt("categoryId"));
                        categoryInfo.setCategoryKor(categoryArray.getJSONObject(i).getString("categoryKor"));
                        categoryInfo.setCategoryEng(categoryArray.getJSONObject(i).getString("categoryEng"));
                        categoryInfo.setCategoryChn(categoryArray.getJSONObject(i).getString("categoryChn"));
                        categoryInfo.setCategoryJpn(categoryArray.getJSONObject(i).getString("categoryJpn"));
                        categoryInfo.setCategorySet(categoryArray.getJSONObject(i).getInt("categorySet"));
                        dbHelper.insertCategoryInfo(categoryInfo);
                    }
                    for (int j = 0; j < menuArray.length(); j++) {
                        MenuInfo menuInfo = new MenuInfo();
                        menuInfo.setMenuId(menuArray.getJSONObject(j).getInt("menuId"));
                        menuInfo.setMenuKor(menuArray.getJSONObject(j).getString("menuKor"));
                        menuInfo.setMenuEng(menuArray.getJSONObject(j).getString("menuEng"));
                        menuInfo.setMenuChn(menuArray.getJSONObject(j).getString("menuChn"));
                        menuInfo.setMenuJpn(menuArray.getJSONObject(j).getString("menuJpn"));
                        menuInfo.setMenuPrice(menuArray.getJSONObject(j).getInt("menuPrice"));
                        menuInfo.setMenuKorExp(menuArray.getJSONObject(j).getString("menuKorExp"));
                        menuInfo.setMenuEngExp(menuArray.getJSONObject(j).getString("menuEngExp"));
                        menuInfo.setMenuChnExp(menuArray.getJSONObject(j).getString("menuChnExp"));
                        menuInfo.setMenuJpnExp(menuArray.getJSONObject(j).getString("menuJpnExp"));
                        menuInfo.setCategoryId(menuArray.getJSONObject(j).getInt("categoryId"));
                        menuInfo.setMenuOption(menuArray.getJSONObject(j).getInt("menuOption"));
                        menuInfo.setImgPath(menuArray.getJSONObject(j).getString("imgPath"));
                        dbHelper.insertMenuInfo(menuInfo);
                    }
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
                    // 다운로드 이후 Activity 이동
                    Intent intent = new Intent(LoginActivity.this, LanguageActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress() + "orderTok/downloadInfo.php", "storeId=" + storeId);
    }
}

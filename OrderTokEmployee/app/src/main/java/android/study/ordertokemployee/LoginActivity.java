package android.study.ordertokemployee;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.study.ordertokemployee.Back.DBHelper;
import android.study.ordertokemployee.Back.HttpConnect;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.study.ordertokemployee.InfoPackage.MenuInfo;
import android.study.ordertokemployee.InfoPackage.ServerInfo;
import android.study.ordertokemployee.InfoPackage.StoreInfo;
import android.text.format.Formatter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    ServerInfo serverInfo;
    DBHelper dbHelper;

    EditText idText, pwText;
    Button loginButton;
    String userId, userPw, thisIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        serverInfo = new ServerInfo();
        dbHelper = new DBHelper(LoginActivity.this);

        idText = findViewById(R.id.idText);
        pwText = findViewById(R.id.pwText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                thisIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
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
                try {
                    JSONObject jsonObject =new JSONObject(result);
                    JSONArray loginArray = jsonObject.getJSONArray("login");
                    JSONArray categoryArray = jsonObject.getJSONArray("category");
                    JSONArray menuArray = jsonObject.getJSONArray("menu");
                    // 로그인 실패
                    if(!loginArray.getJSONObject(0).getString("rt").equals("ok")){
                        Toast.makeText(LoginActivity.this, "입력값을 확인해주세요", Toast.LENGTH_LONG).show();
                    }else{
                        StoreInfo newStoreInfo = new StoreInfo();
                        newStoreInfo.setStoreId(loginArray.getJSONObject(0).getInt("store_id"));
                        newStoreInfo.setStoreName(loginArray.getJSONObject(0).getString("store_name"));
                        newStoreInfo.setStoreRandom(loginArray.getJSONObject(0).getString("store_random"));
                        newStoreInfo.setTableNumber(loginArray.getJSONObject(0).getInt("store_table_number"));
                        newStoreInfo.setUpdateTime(loginArray.getJSONObject(0).getInt("store_update_time"));
                        dbHelper.insertStoreInfo(newStoreInfo);

                        if(categoryArray.length()>0){
                            for(int i=0; i<categoryArray.length(); i++){
                                CategoryInfo categoryInfo = new CategoryInfo();
                                categoryInfo.setCategoryId(categoryArray.getJSONObject(i).getInt("categoryId"));
                                categoryInfo.setCategoryKor(categoryArray.getJSONObject(i).getString("categoryKor"));
                                categoryInfo.setCategoryEng(categoryArray.getJSONObject(i).getString("categoryEng"));
                                categoryInfo.setCategoryChn(categoryArray.getJSONObject(i).getString("categoryChn"));
                                categoryInfo.setCategoryJpn(categoryArray.getJSONObject(i).getString("categoryJpn"));
                                categoryInfo.setSelectedCategory(1);
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

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokEmployee/loginOk.php", "userId=" + userId + "&userPw=" + userPw +"&ipAddress="+thisIpAddress);
    }
}

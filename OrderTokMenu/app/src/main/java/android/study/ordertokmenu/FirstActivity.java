package android.study.ordertokmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.Back.HttpConnect;
import android.study.ordertokmenu.InfoPackage.CategoryInfo;
import android.study.ordertokmenu.InfoPackage.MenuInfo;
import android.study.ordertokmenu.InfoPackage.OptionInfo;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.SetInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity {

    StoreInfo storeInfo;
    ServerInfo serverInfo;
    DBHelper dbHelper;
    int update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        serverInfo = new ServerInfo();
        dbHelper = new DBHelper(FirstActivity.this);

        storeInfo = dbHelper.getStoreInfo();

        if(storeInfo == null){
            Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            checkUpdate();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void checkUpdate(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                update = Integer.parseInt(result);
                // 업데이트가 있는 경우
                if(dbHelper.checkUpdate(update)){
                    // 정보 다운로드 필요
                    dbHelper.deleteCategoryInfo();
                    dbHelper.deleteMenuInfo();
                    dbHelper.deleteAllOrderInfo();
                    dbHelper.deleteOptionInfo();
                    dbHelper.deleteSetInfo();

                    downloadData();
                }else{
                    //업데이트가 없는 경우
                    // 그냥 이동
                    Intent intent = new Intent(FirstActivity.this, LanguageActivity.class);
                    startActivity(intent);
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTok/checkUpdate.php", "storeId="+storeInfo.getStoreId());
    }

    @SuppressLint("StaticFieldLeak")
    public void downloadData(){
        new HttpConnect(){

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObject =new JSONObject(result);
                    JSONArray categoryArray = jsonObject.getJSONArray("category");
                    JSONArray menuArray = jsonObject.getJSONArray("menu");
                    JSONArray setArray = jsonObject.getJSONArray("set");
                    JSONArray optionArray = jsonObject.getJSONArray("option");
                    for(int i=0; i<categoryArray.length(); i++){
                        CategoryInfo categoryInfo = new CategoryInfo();
                        categoryInfo.setCategoryId(categoryArray.getJSONObject(i).getInt("categoryId"));
                        categoryInfo.setCategoryKor(categoryArray.getJSONObject(i).getString("categoryKor"));
                        categoryInfo.setCategoryEng(categoryArray.getJSONObject(i).getString("categoryEng"));
                        categoryInfo.setCategoryChn(categoryArray.getJSONObject(i).getString("categoryChn"));
                        categoryInfo.setCategoryJpn(categoryArray.getJSONObject(i).getString("categoryJpn"));
                        categoryInfo.setCategorySet(categoryArray.getJSONObject(i).getInt("categorySet"));
                        dbHelper.insertCategoryInfo(categoryInfo);
                    }
                    for(int j=0; j<menuArray.length(); j++){
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
                    for(int k=0; k<setArray.length(); k++){
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
                    for(int m=0; m<optionArray.length(); m++){
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
                    Intent intent = new Intent(FirstActivity.this, LanguageActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTok/downloadInfo.php", "storeId="+storeInfo.getStoreId());
    }
}

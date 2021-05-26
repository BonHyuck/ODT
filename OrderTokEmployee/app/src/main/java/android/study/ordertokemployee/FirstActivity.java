package android.study.ordertokemployee;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.study.ordertokemployee.Back.DBHelper;
import android.study.ordertokemployee.Back.HttpConnect;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.study.ordertokemployee.InfoPackage.MenuInfo;
import android.study.ordertokemployee.InfoPackage.ServerInfo;
import android.study.ordertokemployee.InfoPackage.StoreInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ServerInfo serverInfo;
    StoreInfo storeInfo;
    int update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        dbHelper = new DBHelper(FirstActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();
        if(storeInfo==null){
            Intent loginIntent = new Intent(FirstActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }else{
            CheckUpdate();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void CheckUpdate(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // 업데이트가 있는 경우
                if(dbHelper.checkUpdate(update)){
                    // 정보 다운로드 필요
                    dbHelper.deleteOrderInfo();
                    dbHelper.deleteMenuInfo();
                    dbHelper.deleteCategoryInfo();

                    DownloadData();

                    // 다운로드 이후 Activity 이동
                    Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    //업데이트가 없는 경우
                    // 그냥 이동
                    Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokEmployee/checkUpdate.php", "storeId="+storeInfo.getStoreId());
    }

    @SuppressLint("StaticFieldLeak")
    public void DownloadData(){
        new HttpConnect(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObject =new JSONObject(result);
                    JSONArray categoryArray = jsonObject.getJSONArray("category");
                    JSONArray menuArray = jsonObject.getJSONArray("menu");
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
                    for(int j=0; j<menuArray.length(); j++){
                        MenuInfo menuInfo = new MenuInfo();
                        menuInfo.setMenuId(menuArray.getJSONObject(j).getInt("menuId"));
                        menuInfo.setMenuKor(menuArray.getJSONObject(j).getString("menuKor"));
                        menuInfo.setMenuEng(menuArray.getJSONObject(j).getString("menuEng"));
                        menuInfo.setMenuChn(menuArray.getJSONObject(j).getString("menuChn"));
                        menuInfo.setMenuJpn(menuArray.getJSONObject(j).getString("menuJpn"));
                        menuInfo.setMenuPrice(menuArray.getJSONObject(j).getInt("menuPrice"));
                        menuInfo.setCategoryId(menuArray.getJSONObject(j).getInt("categoryId"));
                        menuInfo.setMenuOption(menuArray.getJSONObject(j).getInt("menuOption"));
                        menuInfo.setImgPath(menuArray.getJSONObject(j).getString("imgPath"));
                        dbHelper.insertMenuInfo(menuInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(serverInfo.getServerAddress()+"orderTokEmployee/downloadInfo.php", "storeId="+storeInfo.getStoreId());
    }
}

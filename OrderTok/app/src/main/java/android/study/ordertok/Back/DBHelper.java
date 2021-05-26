package android.study.ordertok.Back;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.study.ordertok.InfoPackage.CategoryInfo;
import android.study.ordertok.InfoPackage.MenuInfo;
import android.study.ordertok.InfoPackage.OptionInfo;
import android.study.ordertok.InfoPackage.OrderInfo;
import android.study.ordertok.InfoPackage.SetInfo;
import android.study.ordertok.InfoPackage.StoreInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DBHelper extends SQLiteOpenHelper {
    // 데이터베이스 파일 명
    private static final String DB_NAME = "TABLE_DATABASE";
    // 데이터베이스 버전
    private static final int DB_VERSION = 1;

    /**
     * 데이터베이스 제어 객체 생성 ->> DB파일 생성 혹은 오픈.
     * @param context - 컨텍스트
     * @param name - 시스템저장소에 저장되는 파일 이름
     * @param factory - null
     * @param version - 데이터베이스 파일의 버전넘버
     */
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 생성자 간소화
    public DBHelper(Context context){
        this(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 생성자에 의해서 파일 생성 후 첫 SQL 실행 전에 자동 호출
     * 생성하고자 하는 테이블에 대한 CREATE 구문 작성 후 실행
     * 외부 DB에 들어가 있는 user.id, store.id, 안드로이드에서 받는 테이블 번호까지 설정
     * @param sqLiteDatabase - SQL문 수행 기능을 갖는 객체
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("Console", "DB onCreate");
        // 업체 DB 생성
        String storeSql = "CREATE TABLE storeInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "storeId INTEGER NOT NULL, " +
                "storeName TEXT NOT NULL, " +
                "storeRandom TEXT NOT NULL, " +
                "tableNumber INTEGER NOT NULL, " +
                "updateTime INTEGER NOT NULL, " +
                "posIpAddress TEXT NOT NULL, " +
                "employeeIpAddress TEXT NOT NULL, " +
                "backImgPath TEXT NOT NULL, " +
                "logoImgPath TEXT NOT NULL)";
        // 카테고리 DB 생성
        String categorySql = "CREATE TABLE categoryInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryId INTEGER NOT NULL, " +
                "categoryKor TEXT NOT NULL, " +
                "categoryEng TEXT NOT NULL, " +
                "categoryChn TEXT NOT NULL," +
                "categoryJpn TEXT NOT NULL, " +
                "categorySet INTEGER NOT NULL)";
        // 메뉴 DB 생성
        String menuSql = "CREATE TABLE menuInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "menuId INTEGER NOT NULL, " +
                "menuKor TEXT NOT NULL, " +
                "menuEng TEXT NOT NULL, " +
                "menuChn TEXT NOT NULL, " +
                "menuJpn TEXT NOT NULL, " +
                "menuPrice INTEGER NOT NULL, " +
                "menuKorExp TEXT NOT NULL, " +
                "menuEngExp TEXT NOT NULL, " +
                "menuChnExp TEXT NOT NULL, " +
                "menuJpnExp TEXT NOT NULL, " +
                "categoryId INTEGER NOT NULL, " +
                "menuOption INTEGER NOT NULL, " +
                "imgPath TEXT NOT NULL)";
        // 세트 DB 생성
        String setSql = "CREATE TABLE setInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryId INTEGER NOT NULL, " +
                "menuId INTEGER NOT NULL, " +
                "setKor TEXT NOT NULL, " +
                "setEng TEXT NOT NULL, " +
                "setChn TEXT NOT NULL, " +
                "setJpn TEXT NOT NULL, " +
                "setNumber INTEGER NOT NULL, " +
                "setPrice INTEGER NOT NULL)";
        // 옵션 DB 생성
        String optionSql = "CREATE TABLE optionInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "optionPrice INTEGER NOT NULL, " +
                "optionKor TEXT NOT NULL, " +
                "optionEng TEXT NOT NULL, " +
                "optionChn TEXT NOT NULL, " +
                "optionJpn TEXT NOT NULL, " +
                "categoryId INTEGER NOT NULL, " +
                "menuId INTEGER NOT NULL)";
        // 주문 DB 생성
        String orderSql = "CREATE TABLE orderInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "menuId INTEGER NOT NULL, " +
                "orderKor TEXT NOT NULL, " +
                "orderEng TEXT NOT NULL, " +
                "orderChn TEXT NOT NULL, " +
                "orderJpn TEXT NOT NULL, " +
                "orderCount INTEGER NOT NULL, " +
                "orderTotal INTEGER NOT NULL, " +
                "orderPrice INTEGER NOT NULL, " +
                "orderDate INTEGER NOT NULL)";

        try{
            sqLiteDatabase.execSQL(storeSql);
            sqLiteDatabase.execSQL(categorySql);
            sqLiteDatabase.execSQL(menuSql);
            sqLiteDatabase.execSQL(setSql);
            sqLiteDatabase.execSQL(optionSql);
            sqLiteDatabase.execSQL(orderSql);
        }catch(SQLException e){
            Log.d("Console", e.getLocalizedMessage());
            e.printStackTrace();
        }

        Log.d("Console", "DB onCreate DONE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // INSERT
    // 업체 정보 넣기
    public void insertStoreInfo(StoreInfo storeInfo){
        // 이미 정보가 있는지 확인
        String checkSql = "SELECT id, storeId, storeName, storeRandom, tableNumber, updateTime, posIpAddress, employeeIpAddress, backImgPath, logoImgPath FROM storeInfo";
        SQLiteDatabase datas = getReadableDatabase();
        Cursor cursor = datas.rawQuery(checkSql, null);

        if(cursor!=null||cursor.getCount()>0){
            String deleteSql = "DROP TABLE storeInfo";
            SQLiteDatabase deleteDb = getWritableDatabase();
            deleteDb.execSQL(deleteSql);
            String createSql = "CREATE TABLE storeInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "storeId INTEGER NOT NULL, " +
                    "storeName TEXT NOT NULL, " +
                    "storeRandom TEXT NOT NULL, " +
                    "tableNumber INTEGER NOT NULL, " +
                    "updateTime INTEGER NOT NULL, " +
                    "posIpAddress TEXT NOT NULL, " +
                    "employeeIpAddress TEXT NOT NULL, " +
                    "backImgPath TEXT NOT NULL, " +
                    "logoImgPath TEXT NOT NULL)";
            try{
                deleteDb.execSQL(createSql);
            }catch(SQLException e){
                Log.d("Console", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        String sql = "INSERT INTO storeInfo (storeId, storeName, storeRandom, tableNumber, updateTime, posIpAddress, employeeIpAddress, backImgPath, logoImgPath) VALUES (?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {storeInfo.getStoreId(), storeInfo.getStoreName(), storeInfo.getStoreRandom(), storeInfo.getTableNumber(), storeInfo.getUpdateTime(), storeInfo.getPosIpAddress(), storeInfo.getEmployeeIpAddress(), storeInfo.getBackImgPath(), storeInfo.getLogoImgPath()};
        db.execSQL(sql, data);
    }
    // 카테고리 정보 넣기
    public void insertCategoryInfo(CategoryInfo categoryInfo){
        String sql = "INSERT INTO categoryInfo (categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, categorySet) VALUES (?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {categoryInfo.getCategoryId(), categoryInfo.getCategoryKor(), categoryInfo.getCategoryEng(), categoryInfo.getCategoryChn(), categoryInfo.getCategoryJpn(), categoryInfo.getCategorySet()};
        db.execSQL(sql, data);
    }
    // 메뉴 정보 넣기
    public void insertMenuInfo(MenuInfo menuInfo){
        String sql = "INSERT INTO menuInfo (menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, menuKorExp, menuEngExp, menuChnExp, menuJpnExp, categoryId, menuOption, imgPath)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {menuInfo.getMenuId(), menuInfo.getMenuKor(), menuInfo.getMenuEng(), menuInfo.getMenuChn(), menuInfo.getMenuJpn(), menuInfo.getMenuPrice(), menuInfo.getMenuKorExp(), menuInfo.getMenuEngExp(), menuInfo.getMenuChnExp(), menuInfo.getMenuJpnExp(), menuInfo.getCategoryId(), menuInfo.getMenuOption(), menuInfo.getImgPath()};
        db.execSQL(sql, data);
    }
    // 세트 정보 넣기
    public void insertSetInfo(SetInfo setInfo){
        String sql = "INSERT INTO setInfo (categoryId, menuId, setNumber, setKor, setEng, setChn, setJpn, setPrice) VALUES (?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {setInfo.getCategoryId(), setInfo.getMenuId(), setInfo.getSetNumber(), setInfo.getSetKor(), setInfo.getSetEng(), setInfo.getSetChn(), setInfo.getSetJpn(), setInfo.getSetPrice()};
        db.execSQL(sql, data);
    }
    // 옵션 정보 넣기
    public void insertOptionInfo(OptionInfo optionInfo){
        String sql = "INSERT INTO optionInfo (optionPrice, optionKor, optionEng, optionChn, optionJpn, categoryId, menuId) VALUES (?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {optionInfo.getOptionPrice(), optionInfo.getOptionKor(), optionInfo.getOptionEng(), optionInfo.getOptionChn(), optionInfo.getOptionJpn(), optionInfo.getCategoryId(), optionInfo.getMenuId()};
        db.execSQL(sql, data);
    }
    // 주문 정보 넣기
    public void insertOrder(OrderInfo orderInfo){
        String sql = "INSERT INTO orderInfo (menuId, orderKor, orderEng, orderChn, orderJpn, orderCount, orderTotal, orderPrice, orderDate) VALUES (?,?,?,?,?,?,?,?, date('now'))";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {orderInfo.getMenuId(), orderInfo.getOrderKor(), orderInfo.getOrderEng(), orderInfo.getOrderChn(), orderInfo.getOrderJpn(), orderInfo.getOrderCount(), orderInfo.getOrderTotal(), orderInfo.getOrderPrice()};
        db.execSQL(sql, data);
    }

    // SELECT
    // 업체 정보 가져오기
    public StoreInfo getStoreInfo(){
        StoreInfo storeInfo = new StoreInfo();
        String sql = "SELECT storeId, storeName, storeRandom, tableNumber, updateTime, posIpAddress, employeeIpAddress, backImgPath, logoImgPath FROM storeInfo";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int indexStoreId = cursor.getColumnIndex("storeId");
                int indexStoreName = cursor.getColumnIndex("storeName");
                int indexStoreRandom = cursor.getColumnIndex("storeRandom");
                int indexTableNumber = cursor.getColumnIndex("tableNumber");
                int indexUpdateTime = cursor.getColumnIndex("updateTime");
                int indexPosIpAddress = cursor.getColumnIndex("posIpAddress");
                int indexEmployeeIpAddress = cursor.getColumnIndex("employeeIpAddress");
                int indexBackImgPath = cursor.getColumnIndex("backImgPath");
                int indexLogoImgPath = cursor.getColumnIndex("logoImgPath");

                storeInfo.setStoreId(cursor.getInt(indexStoreId));
                storeInfo.setStoreName(cursor.getString(indexStoreName));
                storeInfo.setStoreRandom(cursor.getString(indexStoreRandom));
                storeInfo.setTableNumber(cursor.getInt(indexTableNumber));
                storeInfo.setUpdateTime(cursor.getInt(indexUpdateTime));
                storeInfo.setPosIpAddress(cursor.getString(indexPosIpAddress));
                storeInfo.setEmployeeIpAddress(cursor.getString(indexEmployeeIpAddress));
                storeInfo.setBackImgPath(cursor.getString(indexBackImgPath));
                storeInfo.setLogoImgPath(cursor.getString(indexLogoImgPath));
            }else{
                storeInfo = null;
            }
        }else{
            storeInfo = null;
        }

        return storeInfo;
    }
    // 카테고리 전부 가져오기
    public List<CategoryInfo> getAllCategoryInfo(){
        List<CategoryInfo> categoryInfoList = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, categorySet FROM categoryInfo ORDER BY categoryId ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                categoryInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    CategoryInfo categoryInfo = new CategoryInfo();
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexCategoryKor = cursor.getColumnIndex("categoryKor");
                    int indexCategoryEng = cursor.getColumnIndex("categoryEng");
                    int indexCategoryChn = cursor.getColumnIndex("categoryChn");
                    int indexCategoryJpn = cursor.getColumnIndex("categoryJpn");
                    int indexCategorySet = cursor.getColumnIndex("categorySet");

                    categoryInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    categoryInfo.setCategoryKor(cursor.getString(indexCategoryKor));
                    categoryInfo.setCategoryEng(cursor.getString(indexCategoryEng));
                    categoryInfo.setCategoryChn(cursor.getString(indexCategoryChn));
                    categoryInfo.setCategoryJpn(cursor.getString(indexCategoryJpn));
                    categoryInfo.setCategorySet(cursor.getInt(indexCategorySet));

                    categoryInfoList.add(categoryInfo);
                }while(cursor.moveToNext());
            }
        }
        return categoryInfoList;
    }
    public CategoryInfo getOneCategoryInfo(int categoryId){
        CategoryInfo categoryInfo = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, categorySet FROM categoryInfo WHERE categoryId=?";
        String[] data = {String.valueOf(categoryId)};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                cursor.moveToFirst();

                    categoryInfo = new CategoryInfo();
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexCategoryKor = cursor.getColumnIndex("categoryKor");
                    int indexCategoryEng = cursor.getColumnIndex("categoryEng");
                    int indexCategoryChn = cursor.getColumnIndex("categoryChn");
                    int indexCategoryJpn = cursor.getColumnIndex("categoryJpn");
                    int indexCategorySet = cursor.getColumnIndex("categorySet");

                    categoryInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    categoryInfo.setCategoryKor(cursor.getString(indexCategoryKor));
                    categoryInfo.setCategoryEng(cursor.getString(indexCategoryEng));
                    categoryInfo.setCategoryChn(cursor.getString(indexCategoryChn));
                    categoryInfo.setCategoryJpn(cursor.getString(indexCategoryJpn));
                    categoryInfo.setCategorySet(cursor.getInt(indexCategorySet));
            }
        }
        return categoryInfo;
    }
    // 카테고리에 맞는 메뉴 가져오기
    public List<MenuInfo> getAllMenuInfo(int categoryId){
        List<MenuInfo> menuInfoList = null;
        String sql = "SELECT id, menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, menuKorExp, menuEngExp, menuChnExp, menuJpnExp, menuOption, categoryId, imgPath FROM menuInfo WHERE categoryId=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(categoryId)};
        Cursor cursor = db.rawQuery(sql, data);
        if(cursor != null){
            if(cursor.getCount() > 0){
                menuInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    MenuInfo menuInfo = new MenuInfo();
                    int indexMenuId = cursor.getColumnIndex("menuId");
                    int indexMenuKor = cursor.getColumnIndex("menuKor");
                    int indexMenuEng = cursor.getColumnIndex("menuEng");
                    int indexMenuChn = cursor.getColumnIndex("menuChn");
                    int indexMenuJpn = cursor.getColumnIndex("menuJpn");
                    int indexMenuPrice = cursor.getColumnIndex("menuPrice");
                    int indexMenuKorExp = cursor.getColumnIndex("menuKorExp");
                    int indexMenuEngExp = cursor.getColumnIndex("menuEngExp");
                    int indexMenuChnExp = cursor.getColumnIndex("menuChnExp");
                    int indexMenuJpnExp = cursor.getColumnIndex("menuJpnExp");
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexMenuOption = cursor.getColumnIndex("menuOption");
                    int indexImgPath = cursor.getColumnIndex("imgPath");

                    menuInfo.setMenuId(cursor.getInt(indexMenuId));
                    menuInfo.setMenuKor(cursor.getString(indexMenuKor));
                    menuInfo.setMenuEng(cursor.getString(indexMenuEng));
                    menuInfo.setMenuChn(cursor.getString(indexMenuChn));
                    menuInfo.setMenuJpn(cursor.getString(indexMenuJpn));
                    menuInfo.setMenuPrice(cursor.getInt(indexMenuPrice));
                    menuInfo.setMenuKorExp(cursor.getString(indexMenuKorExp));
                    menuInfo.setMenuEngExp(cursor.getString(indexMenuEngExp));
                    menuInfo.setMenuChnExp(cursor.getString(indexMenuChnExp));
                    menuInfo.setMenuJpnExp(cursor.getString(indexMenuJpnExp));
                    menuInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    menuInfo.setMenuOption(cursor.getInt(indexMenuOption));
                    menuInfo.setImgPath(cursor.getString(indexImgPath));

                    menuInfoList.add(menuInfo);
                }while(cursor.moveToNext());
            }
        }
        return menuInfoList;
    }
    // 주문에 맞는 메뉴 정보 가져오기
    public MenuInfo getOneMenuInfo(int menuId){
        MenuInfo menuInfo = null;
        String sql = "SELECT id, menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, menuKorExp, menuEngExp, menuChnExp, menuJpnExp, categoryId, menuOption, imgPath FROM menuInfo WHERE menuId=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId)};
        Cursor cursor = db.rawQuery(sql, data);
        if(cursor != null){
            if(cursor.getCount() > 0){
                menuInfo = new MenuInfo();
                cursor.moveToFirst();
                int indexMenuId = cursor.getColumnIndex("menuId");
                int indexMenuKor = cursor.getColumnIndex("menuKor");
                int indexMenuEng = cursor.getColumnIndex("menuEng");
                int indexMenuChn = cursor.getColumnIndex("menuChn");
                int indexMenuJpn = cursor.getColumnIndex("menuJpn");
                int indexMenuPrice = cursor.getColumnIndex("menuPrice");
                int indexMenuKorExp = cursor.getColumnIndex("menuKorExp");
                int indexMenuEngExp = cursor.getColumnIndex("menuEngExp");
                int indexMenuChnExp = cursor.getColumnIndex("menuChnExp");
                int indexMenuJpnExp = cursor.getColumnIndex("menuJpnExp");
                int indexCategoryId = cursor.getColumnIndex("categoryId");
                int indexMenuOption = cursor.getColumnIndex("menuOption");
                int indexImgPath = cursor.getColumnIndex("imgPath");

                menuInfo.setMenuId(cursor.getInt(indexMenuId));
                menuInfo.setMenuKor(cursor.getString(indexMenuKor));
                menuInfo.setMenuEng(cursor.getString(indexMenuEng));
                menuInfo.setMenuChn(cursor.getString(indexMenuChn));
                menuInfo.setMenuJpn(cursor.getString(indexMenuJpn));
                menuInfo.setMenuPrice(cursor.getInt(indexMenuPrice));
                menuInfo.setMenuKorExp(cursor.getString(indexMenuKorExp));
                menuInfo.setMenuEngExp(cursor.getString(indexMenuEngExp));
                menuInfo.setMenuChnExp(cursor.getString(indexMenuChnExp));
                menuInfo.setMenuJpnExp(cursor.getString(indexMenuJpnExp));
                menuInfo.setCategoryId(cursor.getInt(indexCategoryId));
                menuInfo.setMenuOption(cursor.getInt(indexMenuOption));
                menuInfo.setImgPath(cursor.getString(indexImgPath));
            }
        }
        return menuInfo;
    }
    // 선택한 메뉴에 맞는 세트 정보 가져오기
    public List<SetInfo> getAllSetInfo(int menuId){
        List<SetInfo> setInfoList = null;
        String sql = "SELECT categoryId, menuId, setNumber, setPrice, setKor, setEng, setChn, setJpn FROM setInfo WHERE menuId=? ORDER BY setNumber ASC";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId)};
        Cursor cursor = db.rawQuery(sql, data);
        if(cursor != null){
            if(cursor.getCount()>0){
                setInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    SetInfo setInfo = new SetInfo();
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexMenuId = cursor.getColumnIndex("menuId");
                    int indexSetNumber = cursor.getColumnIndex("setNumber");
                    int indexSetPrice = cursor.getColumnIndex("setPrice");
                    int indexSetKor = cursor.getColumnIndex("setKor");
                    int indexSetEng = cursor.getColumnIndex("setEng");
                    int indexSetChn = cursor.getColumnIndex("setChn");
                    int indexSetJpn = cursor.getColumnIndex("setJpn");

                    setInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    setInfo.setMenuId(cursor.getInt(indexMenuId));
                    setInfo.setSetNumber(cursor.getInt(indexSetNumber));
                    setInfo.setSetPrice(cursor.getInt(indexSetPrice));
                    setInfo.setSetKor(cursor.getString(indexSetKor));
                    setInfo.setSetEng(cursor.getString(indexSetEng));
                    setInfo.setSetChn(cursor.getString(indexSetChn));
                    setInfo.setSetJpn(cursor.getString(indexSetJpn));

                    setInfoList.add(setInfo);
                }while(cursor.moveToNext());
            }
        }
        return setInfoList;
    }
    // 선택한 메뉴에 맞는 옵션 정보 가져오기
    public List<OptionInfo> getAllOptionInfo(int menuId){
        List<OptionInfo> optionInfoList = null;
        String sql = "SELECT optionPrice, optionKor, optionEng, optionChn, optionJpn, categoryId, menuId FROM optionInfo WHERE menuId=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount()>0){
                optionInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    OptionInfo optionInfo = new OptionInfo();
                    int indexOptionPrice = cursor.getColumnIndex("optionPrice");
                    int indexOptionKor = cursor.getColumnIndex("optionKor");
                    int indexOptionEng = cursor.getColumnIndex("optionEng");
                    int indexOptionChn = cursor.getColumnIndex("optionChn");
                    int indexOptionJpn = cursor.getColumnIndex("optionJpn");
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexMenuId = cursor.getColumnIndex("menuId");

                    optionInfo.setOptionPrice(cursor.getInt(indexOptionPrice));
                    optionInfo.setOptionKor(cursor.getString(indexOptionKor));
                    optionInfo.setOptionEng(cursor.getString(indexOptionEng));
                    optionInfo.setOptionChn(cursor.getString(indexOptionChn));
                    optionInfo.setOptionJpn(cursor.getString(indexOptionJpn));
                    optionInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    optionInfo.setMenuId(cursor.getInt(indexMenuId));

                    optionInfoList.add(optionInfo);
                }while(cursor.moveToNext());
            }
        }


        return optionInfoList;
    }
    // 포지션에 맞는 세트 가져오기
    public List<SetInfo> getPositionSet(int menuId, int position){
        List<SetInfo> setInfoList = null;
        String sql = "SELECT id, categoryId, menuId, setKor, setEng, setChn, setJpn, setNumber, setPrice FROM setInfo WHERE menuId = ? AND setNumber=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId), String.valueOf(position)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount()>0){
                setInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    SetInfo setInfo = new SetInfo();

                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexMenuId = cursor.getColumnIndex("menuId");
                    int indexSetKor = cursor.getColumnIndex("setKor");
                    int indexSetEng = cursor.getColumnIndex("setEng");
                    int indexSetChn = cursor.getColumnIndex("setChn");
                    int indexSetJpn = cursor.getColumnIndex("setJpn");
                    int indexSetNumber = cursor.getColumnIndex("setNumber");
                    int indexSetPrice = cursor.getColumnIndex("setPrice");

                    setInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    setInfo.setMenuId(cursor.getInt(indexMenuId));
                    setInfo.setSetKor(cursor.getString(indexSetKor));
                    setInfo.setSetEng(cursor.getString(indexSetEng));
                    setInfo.setSetChn(cursor.getString(indexSetChn));
                    setInfo.setSetJpn(cursor.getString(indexSetJpn));
                    setInfo.setSetNumber(cursor.getInt(indexSetNumber));
                    setInfo.setSetPrice(cursor.getInt(indexSetPrice));

                    setInfoList.add(setInfo);
                }while(cursor.moveToNext());
            }
        }

        return setInfoList;
    }
    public int getSetOptionCount(int menuId){
        int setCount = 0;
        String sql = "SELECT MAX(setNumber) as max FROM setInfo WHERE menuId = ?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                int indexMaxSetNumber = cursor.getColumnIndex("max");

                setCount = cursor.getInt(indexMaxSetNumber);
            }
        }
        return setCount;
    }
    // 주문 전부 가져오기
    public List<OrderInfo> getAllOrderInfo(){
        List<OrderInfo> orderInfoList = null;
        String sql = "SELECT id, menuId, orderKor, orderEng, orderChn, orderJpn, orderCount, orderTotal, orderPrice, orderDate FROM orderInfo";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                orderInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    OrderInfo orderInfo = new OrderInfo();
                    int indexMenuId = cursor.getColumnIndex("menuId");
                    int indexOrderKor = cursor.getColumnIndex("orderKor");
                    int indexOrderEng = cursor.getColumnIndex("orderEng");
                    int indexOrderChn = cursor.getColumnIndex("orderChn");
                    int indexOrderJpn = cursor.getColumnIndex("orderJpn");
                    int indexOrderCount = cursor.getColumnIndex("orderCount");
                    int indexOrderTotal = cursor.getColumnIndex("orderTotal");
                    int indexOrderPrice = cursor.getColumnIndex("orderPrice");
                    int indexOrderDate = cursor.getColumnIndex("orderDate");

                    orderInfo.setMenuId(cursor.getInt(indexMenuId));
                    orderInfo.setOrderKor(cursor.getString(indexOrderKor));
                    orderInfo.setOrderEng(cursor.getString(indexOrderEng));
                    orderInfo.setOrderChn(cursor.getString(indexOrderChn));
                    orderInfo.setOrderJpn(cursor.getString(indexOrderJpn));
                    orderInfo.setOrderCount(cursor.getInt(indexOrderCount));
                    orderInfo.setOrderTotal(cursor.getInt(indexOrderTotal));
                    orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
                    orderInfo.setOrderDate(cursor.getInt(indexOrderDate));

                    orderInfoList.add(orderInfo);
                }while(cursor.moveToNext());
            }
        }

        return  orderInfoList;
    }
    // 한가지 주문 가져오기
    public OrderInfo getOneOrderInfo(int menuId, String orderKor){
        OrderInfo orderInfo = null;
        String sql = "SELECT id, menuId, orderKor, orderEng, orderChn, orderJpn, orderCount, orderTotal, orderPrice, orderDate FROM orderInfo WHERE menuId=? AND orderKor = ? ";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId), orderKor};
        Cursor cursor = db.rawQuery(sql, data);
        if(cursor != null&&cursor.getCount() > 0){
            orderInfo = new OrderInfo();
            cursor.moveToFirst();
            int indexMenuId = cursor.getColumnIndex("menuId");
            int indexOrderKor = cursor.getColumnIndex("orderKor");
            int indexOrderEng = cursor.getColumnIndex("orderEng");
            int indexOrderChn = cursor.getColumnIndex("orderChn");
            int indexOrderJpn = cursor.getColumnIndex("orderJpn");
            int indexOrderCount = cursor.getColumnIndex("orderCount");
            int indexOrderTotal = cursor.getColumnIndex("orderTotal");
            int indexOrderPrice = cursor.getColumnIndex("orderPrice");
            int indexOrderDate = cursor.getColumnIndex("orderDate");

            orderInfo.setMenuId(cursor.getInt(indexMenuId));
            orderInfo.setOrderKor(cursor.getString(indexOrderKor));
            orderInfo.setOrderEng(cursor.getString(indexOrderEng));
            orderInfo.setOrderChn(cursor.getString(indexOrderChn));
            orderInfo.setOrderJpn(cursor.getString(indexOrderJpn));
            orderInfo.setOrderCount(cursor.getInt(indexOrderCount));
            orderInfo.setOrderTotal(cursor.getInt(indexOrderTotal));
            orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
            orderInfo.setOrderDate(cursor.getInt(indexOrderDate));
        }
        return orderInfo;
    }

    // DELETE
    // 업체 정보 삭제
    public void deleteStoreInfo(){
        String deleteSql = "DELETE FROM storeInfo";
        SQLiteDatabase deleteDb = getWritableDatabase();
        deleteDb.execSQL(deleteSql);
    }
    // 카테고리 삭제
    public void deleteCategoryInfo(){
        String deleteSql = "DELETE FROM categoryInfo";
        SQLiteDatabase deleteDb = getWritableDatabase();
        deleteDb.execSQL(deleteSql);
    }
    // 메뉴 전체 삭제
    public void deleteMenuInfo(){
        String deleteSql = "DELETE FROM menuInfo";
        SQLiteDatabase deleteDb = getWritableDatabase();
        deleteDb.execSQL(deleteSql);
    }
    // 세트 전체 삭제
    public void deleteSetInfo(){
        String deleteSql = "DELETE FROM setInfo";
        SQLiteDatabase deleteDb = getWritableDatabase();
        deleteDb.execSQL(deleteSql);
    }
    // 옵션 전체 삭제
    public void deleteOptionInfo(){
        String deleteSql = "DELETE FROM optionInfo";
        SQLiteDatabase deleteDb = getWritableDatabase();
        deleteDb.execSQL(deleteSql);
    }
    // 주문 전체 삭제
    public void deleteAllOrderInfo(){
        String deleteSql = "DELETE FROM orderInfo";
        SQLiteDatabase deleteDb = getWritableDatabase();
        deleteDb.execSQL(deleteSql);
    }
    // 주문 1개 삭제
    public void deleteOneOrderInfo(OrderInfo orderInfo){
        String deleteSql = "DELETE FROM orderInfo WHERE orderKor = ? AND menuId=?";
        SQLiteDatabase deleteDb = getWritableDatabase();
        Object[] data = {orderInfo.getOrderKor(), orderInfo.getMenuId()};
        deleteDb.execSQL(deleteSql, data);
    }

    //UPDATE
    // 업체, 카테고리, 메뉴, 세트, 옵션은 업데이트 없이 삭제와 생성으로 커버
    // 해당 주문의 COUNT 업데이트
    public void updateOrderInfoCount(OrderInfo orderInfo, int newCount){
        String sql = "UPDATE orderInfo SET orderCount = ? WHERE menuId=? AND orderKor=?";
        SQLiteDatabase db = getWritableDatabase();
        String[] data = {String.valueOf(newCount), String.valueOf(orderInfo.getMenuId()), orderInfo.getOrderKor()};
        db.execSQL(sql, data);
    }

    public void updateOrderInfoTotal(OrderInfo orderInfo, int newTotal){
        String sql = "UPDATE orderInfo SET orderTotal = ? WHERE menuId=? AND orderKor=?";
        SQLiteDatabase db = getWritableDatabase();
        String[] data = {String.valueOf(newTotal), String.valueOf(orderInfo.getMenuId()), orderInfo.getOrderKor()};
        db.execSQL(sql, data);
    }

    public void updatePosIp(String newPosIp){
        String sql = "UPDATE storeInfo SET posIpAddress=?";
        SQLiteDatabase db = getWritableDatabase();
        String[] data = {newPosIp};
        db.execSQL(sql, data);
    }

    public void updateEmployeeIp(String newEmployeeIp){
        String sql = "UPDATE storeInfo SET employeeIpAddress=?";
        SQLiteDatabase db = getWritableDatabase();
        String[] data = {newEmployeeIp};
        db.execSQL(sql, data);
    }

    // 해당 주문이 있는지 확인하기
    public boolean checkOrderInfo(int menuId, String orderKor){
        String sql = "SELECT id FROM orderInfo WHERE menuId=? AND orderKor = ?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId), orderKor};
        Cursor cursor = db.rawQuery(sql, data);
        if(cursor != null&&cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    // 서버DB의 시간과 비교해서 업데이트 필요 여부 확인
    public boolean checkUpdate(int serverUpdate){
        int updateTime = 0;
        String sql = "SELECT updateTime FROM storeInfo";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int indexUpdateTime = cursor.getColumnIndex("updateTime");

                updateTime = cursor.getInt(indexUpdateTime);
            }
        }
        Log.d("UPDATE", "ANDROID UPDATE : "+updateTime);
        Log.d("UPDATE", "SERVER UPDATE : "+serverUpdate);
        // 업데이트가 있음
        if(updateTime < serverUpdate){
            return true;
        }else{ //업데이트가 없음
            return false;
        }
    }
}

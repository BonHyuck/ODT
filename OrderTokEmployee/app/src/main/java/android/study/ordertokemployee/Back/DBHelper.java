package android.study.ordertokemployee.Back;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.study.ordertokemployee.InfoPackage.MenuInfo;
import android.study.ordertokemployee.InfoPackage.OrderInfo;
import android.study.ordertokemployee.InfoPackage.StoreInfo;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper{
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
// 업체 DB 생성
        String storeSql = "CREATE TABLE storeInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "storeId INTEGER NOT NULL, " +
                "storeName TEXT NOT NULL, " +
                "storeRandom TEXT NOT NULL, " +
                "tableNumber INTEGER NOT NULL, " +
                "updateTime INTEGER NOT NULL)";
        // 카테고리 DB 생성
        String categorySql = "CREATE TABLE categoryInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryId INTEGER NOT NULL, " +
                "categoryKor TEXT NOT NULL, " +
                "categoryEng TEXT NOT NULL, " +
                "categoryChn TEXT NOT NULL," +
                "categoryJpn TEXT NOT NULL, " +
                "selectedCategory INTEGER NOT NULL, " +
                "categorySet INTEGER NOT NULL)";
        // 메뉴 DB 생성
        String menuSql = "CREATE TABLE menuInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "menuId INTEGER NOT NULL, " +
                "menuKor TEXT NOT NULL, " +
                "menuEng TEXT NOT NULL, " +
                "menuChn TEXT NOT NULL, " +
                "menuJpn TEXT NOT NULL, " +
                "menuPrice INTEGER NOT NULL, " +
                "categoryId INTEGER NOT NULL, " +
                "menuOption INTEGER NOT NULL, " +
                "imgPath TEXT NOT NULL)";
        // 주문 DB 생성
        String orderSql = "CREATE TABLE orderInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "menuId INTEGER NOT NULL, " +
                "categoryId INTEGER NOT NULL, " +
                "orderKor TEXT NOT NULL," +
                "tableNumber INTEGER NOT NULL, " +
                "orderCount INTEGER NOT NULL, " +
                "orderPrice INTEGER NOT NULL, " +
                "orderDate INTEGER NOT NULL, " +
                "orderCheck INTEGER NOT NULL)";

        try{
            sqLiteDatabase.execSQL(storeSql);
            sqLiteDatabase.execSQL(categorySql);
            sqLiteDatabase.execSQL(menuSql);
            sqLiteDatabase.execSQL(orderSql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    //INSERT
    // 업체 정보 넣기
    // 업체 정보 입력
    public void insertStoreInfo(StoreInfo storeInfo){
        // 이미 정보가 있는지 확인
        String checkSql = "SELECT id, storeId, storeName, storeRandom, tableNumber, updateTime FROM storeInfo";
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
                    "updateTime INTEGER NOT NULL)";
            try{
                deleteDb.execSQL(createSql);
            }catch(SQLException e){
                Log.d("Console", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        String sql = "INSERT INTO storeInfo (storeId, storeName, storeRandom, tableNumber, updateTime) VALUES (?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {storeInfo.getStoreId(), storeInfo.getStoreName(), storeInfo.getStoreRandom(), storeInfo.getTableNumber(), storeInfo.getUpdateTime()};
        db.execSQL(sql, data);
    }

    // 카테고리 입력
    public void insertCategoryInfo(CategoryInfo categoryInfo){
        String insertSql = "INSERT INTO categoryInfo (categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory, categorySet) VALUES (?,?,?,?,?,?,?)";
        SQLiteDatabase insertDB = getWritableDatabase();
        Object[] insertData = {categoryInfo.getCategoryId(), categoryInfo.getCategoryKor(), categoryInfo.getCategoryEng(), categoryInfo.getCategoryChn(), categoryInfo.getCategoryJpn(), categoryInfo.getSelectedCategory(), categoryInfo.getCategorySet()};
        insertDB.execSQL(insertSql, insertData);
    }

    // 메뉴 정보 입력
    public void insertMenuInfo(MenuInfo menuInfo){
        String insertSql = "INSERT INTO menuInfo (menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, categoryId, menuOption, imgPath) VALUES (?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase insertDB = getWritableDatabase();
        Object[] insertData = {menuInfo.getMenuId(), menuInfo.getMenuKor(), menuInfo.getMenuEng(), menuInfo.getMenuChn(), menuInfo.getMenuJpn(), menuInfo.getMenuPrice(), menuInfo.getCategoryId(), menuInfo.getMenuOption(), menuInfo.getImgPath()};
        insertDB.execSQL(insertSql, insertData);
    }

    // 주문 입력, 호출시 값 지정
    public void insertOrderInfo(OrderInfo orderInfo){
        String sql = "INSERT INTO orderInfo (menuId, categoryId, orderKor, tableNumber, orderCount, orderPrice, orderDate, orderCheck) VALUES (?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {orderInfo.getMenuId(), orderInfo.getCategoryId(), orderInfo.getOrderKor(), orderInfo.getTableNumber(), orderInfo.getOrderCount(), orderInfo.getOrderPrice(),orderInfo.getOrderDate(), orderInfo.getOrderCheck()};
        db.execSQL(sql, data);
    }

    // SELECT
    // 업체 정보 가져오기
    public StoreInfo getStoreInfo(){
        StoreInfo storeInfo = new StoreInfo();
        String sql = "SELECT storeId, storeName, storeRandom, tableNumber, updateTime FROM storeInfo";
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

                storeInfo.setStoreId(cursor.getInt(indexStoreId));
                storeInfo.setStoreName(cursor.getString(indexStoreName));
                storeInfo.setStoreRandom(cursor.getString(indexStoreRandom));
                storeInfo.setTableNumber(cursor.getInt(indexTableNumber));
                storeInfo.setUpdateTime(cursor.getInt(indexUpdateTime));
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
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory, categorySet FROM categoryInfo ORDER BY categoryId ASC";
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
                    int indexSelectedCategory = cursor.getColumnIndex("selectedCategory");
                    int indexCategorySet = cursor.getColumnIndex("categorySet");

                    categoryInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    categoryInfo.setCategoryKor(cursor.getString(indexCategoryKor));
                    categoryInfo.setCategoryEng(cursor.getString(indexCategoryEng));
                    categoryInfo.setCategoryChn(cursor.getString(indexCategoryChn));
                    categoryInfo.setCategoryJpn(cursor.getString(indexCategoryJpn));
                    categoryInfo.setSelectedCategory(cursor.getInt(indexSelectedCategory));
                    categoryInfo.setCategorySet(cursor.getInt(indexCategorySet));

                    categoryInfoList.add(categoryInfo);
                }while(cursor.moveToNext());
            }
        }
        return categoryInfoList;
    }

    // 선택된 카테고리 가져오기
    public List<CategoryInfo> getSelectedCategoryInfo(){
        List<CategoryInfo> categoryInfoList = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory, categorySet FROM categoryInfo WHERE selectedCategory>0 ORDER BY categoryId ASC";
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
                    int indexSelectedCategory = cursor.getColumnIndex("selectedCategory");
                    int indexCategorySet = cursor.getColumnIndex("categorySet");

                    categoryInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    categoryInfo.setCategoryKor(cursor.getString(indexCategoryKor));
                    categoryInfo.setCategoryEng(cursor.getString(indexCategoryEng));
                    categoryInfo.setCategoryChn(cursor.getString(indexCategoryChn));
                    categoryInfo.setCategoryJpn(cursor.getString(indexCategoryJpn));
                    categoryInfo.setSelectedCategory(cursor.getInt(indexSelectedCategory));
                    categoryInfo.setCategorySet(cursor.getInt(indexCategorySet));

                    categoryInfoList.add(categoryInfo);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }

        return categoryInfoList;
    }

    // 카테고리 선택 여부 바꾸기
    public void changeCategoryInfo(int categoryId, int isChecked){
        if(isChecked>0){
            String sql = "UPDATE categoryInfo SET selectedCategory=1 WHERE categoryId=?";
            SQLiteDatabase db = getWritableDatabase();
            Object[] data = {String.valueOf(categoryId)};
            db.execSQL(sql, data);
        }else{
            String sql = "UPDATE categoryInfo SET selectedCategory=0 WHERE categoryId=?";
            SQLiteDatabase db = getWritableDatabase();
            Object[] data = {String.valueOf(categoryId)};
            db.execSQL(sql, data);
        }
    }

    public List<Integer> selectAllOrderDate(){
        List<Integer> orderInfoList = null;
        String sql = "SELECT orderInfo.orderDate FROM orderInfo, categoryInfo WHERE categoryInfo.categoryId=orderInfo.categoryId AND categoryInfo.selectedCategory>0 AND orderInfo.orderCheck=0 GROUP BY orderInfo.orderDate";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                orderInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    int indexOrderDate = cursor.getColumnIndex("orderDate");

                    orderInfoList.add(cursor.getInt(indexOrderDate));
                }while(cursor.moveToNext());
            }
        }
        return orderInfoList;
    }

    public List<OrderInfo> selectTableNumber(int orderDate){
        List<OrderInfo> tableNumberList = null;
        String sql = "SELECT orderInfo.tableNumber FROM orderInfo, categoryInfo WHERE categoryInfo.categoryId=orderInfo.categoryId AND categoryInfo.selectedCategory>0 AND orderInfo.orderCheck=0 AND orderInfo.orderDate=? GROUP BY orderInfo.tableNumber";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(orderDate)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                tableNumberList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    OrderInfo orderInfo = new OrderInfo();

                    int indexTableNumber = cursor.getColumnIndex("tableNumber");

                    orderInfo.setTableNumber(cursor.getInt(indexTableNumber));
                    orderInfo.setOrderDate(orderDate);

                    tableNumberList.add(orderInfo);
                }while(cursor.moveToNext());
            }
        }
        return tableNumberList;
    }

    public List<OrderInfo> selectTableOrder(int tableNumber, int orderDate){
        List<OrderInfo> orderInfoList = null;
        String sql = "SELECT id, menuId, categoryId, orderKor, tableNumber, orderCount, orderPrice, orderDate, orderCheck FROM orderInfo WHERE tableNumber = ? AND orderDate=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(tableNumber), String.valueOf(orderDate)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                orderInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    OrderInfo orderInfo = new OrderInfo();
                    int indexMenuId = cursor.getColumnIndex("menuId");
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexMenuName = cursor.getColumnIndex("orderKor");
                    int indexTableNumber = cursor.getColumnIndex("tableNumber");
                    int indexOrderCount = cursor.getColumnIndex("orderCount");
                    int indexOrderPrice = cursor.getColumnIndex("orderPrice");
                    int indexOrderDate = cursor.getColumnIndex("orderDate");
                    int indexOrderCheck = cursor.getColumnIndex("orderCheck");

                    orderInfo.setMenuId(cursor.getInt(indexMenuId));
                    orderInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    orderInfo.setOrderKor(cursor.getString(indexMenuName));
                    orderInfo.setTableNumber(cursor.getInt(indexTableNumber));
                    orderInfo.setOrderCount(cursor.getInt(indexOrderCount));
                    orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
                    orderInfo.setOrderDate(cursor.getInt(indexOrderDate));
                    orderInfo.setOrderCheck(cursor.getInt(indexOrderCheck));

                    orderInfoList.add(orderInfo);
                }while(cursor.moveToNext());
            }
        }
        return orderInfoList;
    }

    public List<OrderInfo> getCheckOrder(){
        List<OrderInfo> orderInfoList = null;
        String sql = "SELECT orderInfo.id, orderInfo.menuId, orderInfo.categoryId, orderInfo.orderKor, orderInfo.tableNumber, orderInfo.orderCount, orderInfo.orderPrice, orderInfo.orderDate, orderInfo.orderCheck FROM orderInfo, categoryInfo WHERE orderInfo.categoryId=categoryInfo.categoryId AND categoryInfo.selectedCategory>0 AND orderCheck>0 ORDER BY orderDate DESC LIMIT 0, 50";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                orderInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    OrderInfo orderInfo = new OrderInfo();
                    int indexMenuId = cursor.getColumnIndex("menuId");
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexMenuName = cursor.getColumnIndex("orderKor");
                    int indexTableNumber = cursor.getColumnIndex("tableNumber");
                    int indexOrderCount = cursor.getColumnIndex("orderCount");
                    int indexOrderPrice = cursor.getColumnIndex("orderPrice");
                    int indexOrderDate = cursor.getColumnIndex("orderDate");
                    int indexOrderCheck = cursor.getColumnIndex("orderCheck");

                    orderInfo.setMenuId(cursor.getInt(indexMenuId));
                    orderInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    orderInfo.setOrderKor(cursor.getString(indexMenuName));
                    orderInfo.setTableNumber(cursor.getInt(indexTableNumber));
                    orderInfo.setOrderCount(cursor.getInt(indexOrderCount));
                    orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
                    orderInfo.setOrderDate(cursor.getInt(indexOrderDate));
                    orderInfo.setOrderCheck(cursor.getInt(indexOrderCheck));

                    orderInfoList.add(orderInfo);
                }while(cursor.moveToNext());
            }
        }
        return orderInfoList;
    }

    // 업데이트 확인
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
        // 업데이트가 있음
        if(updateTime < serverUpdate){
            return true;
        }else{ //업데이트가 없음
            return false;
        }
    }

    //UPDATE
    public void allCheck(){
        String sql = "UPDATE orderInfo SET orderCheck=1 WHERE orderCheck=0";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    public void allOrderCheck(int tableNumber, int orderDate){
        String sql = "UPDATE orderInfo SET orderCheck=1 WHERE tableNumber=? AND orderDate=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {tableNumber, orderDate};
        db.execSQL(sql, data);
    }
    public void thisOrderCheck(OrderInfo orderInfo){
        String sql = "UPDATE orderInfo SET orderCheck=1 WHERE tableNumber=? AND orderKor=? AND orderDate=?";
        SQLiteDatabase db= getWritableDatabase();
        Object[] data = {orderInfo.getTableNumber(), orderInfo.getOrderKor(), orderInfo.getOrderDate()};
        db.execSQL(sql, data);
    }

    // DELETE
    public void deleteOrderInfo(){
        String sql = "DELETE FROM orderInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public void deleteCategoryInfo(){
        String sql = "DELETE FROM categoryInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public void deleteMenuInfo(){
        String sql = "DELETE FROM menuInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }


}

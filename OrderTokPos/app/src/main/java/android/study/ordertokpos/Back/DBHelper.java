package android.study.ordertokpos.Back;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.study.ordertokpos.InfoPackage.CalendarInfo;
import android.study.ordertokpos.InfoPackage.CashInfo;
import android.study.ordertokpos.InfoPackage.CategoryInfo;
import android.study.ordertokpos.InfoPackage.MenuInfo;
import android.study.ordertokpos.InfoPackage.OptionInfo;
import android.study.ordertokpos.InfoPackage.OrderInfo;
import android.study.ordertokpos.InfoPackage.PaymentInfo;
import android.study.ordertokpos.InfoPackage.PrinterInfo;
import android.study.ordertokpos.InfoPackage.SetInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        // 업체 DB 생성
        String storeSql = "CREATE TABLE storeInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "storeId INTEGER NOT NULL, " +
                "storeName TEXT NOT NULL, " +
                "storeRandom TEXT NOT NULL, " +
                "tableNumber INTEGER NOT NULL)";
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
                "menuOption INTEGER NOT NULL, " +
                "categoryId INTEGER NOT NULL, " +
                "imgPath TEXT NOT NULL)";
        // 주문 DB 생성
        String orderSql = "CREATE TABLE orderInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "menuId INTEGER NOT NULL, " +
                "orderKor TEXT NOT NULL, " +
                "orderEng TEXT NOT NULL, " +
                "orderChn TEXT NOT NULL, " +
                "orderJpn TEXT NOT NULL, " +
                "tableNumber INTEGER NOT NULL, " +
                "orderTotal INTEGER NOT NULL, " +
                "orderPrice INTEGER NOT NULL, " +
                "orderDate INTEGER NOT NULL, " +
                "orderPrinted INTEGER NOT NULL)";
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
        // 결제 DB 생성
        String paymentSql = "CREATE TABLE paymentInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "paymentAmount INTEGER NOT NULL, " +
                "paymentMethod TEXT NOT NULL, " +
                "paymentDate TEXT NOT NULL, " +
                "paymentTime TEXT NOT NULL, " +
                "paymentNumber TEXT NOT NULL, " +
                "paymentTableNumber INTEGER NOT NULL, " +
                "paymentAcquirerCode TEXT NOT NULL, " +
                "paymentAcquirerName TEXT NOT NULL)";
        // 프린터 DB 생성
        String printerSql = "CREATE TABLE printerInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productName TEXT NOT NULL, " +
                "manufacturerName TEXT NOT NULL," +
                "vendorId TEXT NOT NULL, " +
                "productId TEXT NOT NULL, " +
                "printerWhat TEXT NOT NULL)";
        // 달력 DB 생성
        String calendarSql = "CREATE TABLE calendarInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "calendarDate TEXT NOT NULL, " +
                "openCash INTEGER NOT NULL, " +
                "closeCash INTEGER NOT NULL)";

        // 현금 DB 생성
        String cashSql = "CREATE TABLE cashInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fiftyThousandCount INTEGER NOT NULL, " +
                "tenThousandCount INTEGER NOT NULL, " +
                "fiveThousandCount INTEGER NOT NULL, " +
                "oneThousandCount INTEGER NOT NULL, " +
                "fiveHundredCount INTEGER NOT NULL, " +
                "oneHundredCount INTEGER NOT NULL, " +
                "fiftyCount INTEGER NOT NULL, " +
                "tenCount INTEGER NOT NULL, " +
                "cashDate TEXT NOT NULL, " +
                "cashSave TEXT NOT NULL)";

        try{
            sqLiteDatabase.execSQL(storeSql);
            sqLiteDatabase.execSQL(categorySql);
            sqLiteDatabase.execSQL(menuSql);
            sqLiteDatabase.execSQL(setSql);
            sqLiteDatabase.execSQL(optionSql);
            sqLiteDatabase.execSQL(orderSql);
            sqLiteDatabase.execSQL(paymentSql);
            sqLiteDatabase.execSQL(printerSql);
            sqLiteDatabase.execSQL(calendarSql);
            sqLiteDatabase.execSQL(cashSql);
        }catch(SQLException e){
            Log.d("Console", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // INSERT
    // 업체 정보 입력
    public void insertStoreInfo(StoreInfo storeInfo){
        Log.d("DBHELPER", "Insert Information to DB");
        // 이미 정보가 있는지 확인
        String checkSql = "SELECT id, storeId, storeName, storeRandom, tableNumber FROM storeInfo";
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
                    "tableNumber INTEGER NOT NULL)";
            try{
                deleteDb.execSQL(createSql);
            }catch(SQLException e){
                Log.d("Console", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        String sql = "INSERT INTO storeInfo (storeId, storeName, storeRandom, tableNumber) VALUES (?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {storeInfo.getStoreId(), storeInfo.getStoreName(), storeInfo.getStoreRandom(), storeInfo.getTableNumber()};
        db.execSQL(sql, data);
    }
    //카테고리 정보 넣기
    public void insertCategoryInfo(CategoryInfo categoryInfo){
        Log.d("DBHELPER", "Insert Category Information to DB");
        String sql = "INSERT INTO categoryInfo (categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory, categorySet) VALUES (?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {categoryInfo.getCategoryId(), categoryInfo.getCategoryKor(), categoryInfo.getCategoryEng(), categoryInfo.getCategoryChn(), categoryInfo.getCategoryJpn(), categoryInfo.getSelectedCategory(), categoryInfo.getCategorySet()};
        db.execSQL(sql, data);
    }
    // 메뉴 정보 넣기
    public void insertMenuInfo(MenuInfo menuInfo){
        Log.d("DBHELPER", "Insert Menu Information to DB");
        String sql = "INSERT INTO menuInfo (menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, menuOption, categoryId, imgPath) VALUES (?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {menuInfo.getMenuId(), menuInfo.getMenuKor(), menuInfo.getMenuEng(), menuInfo.getMenuChn(), menuInfo.getMenuJpn(), menuInfo.getMenuPrice(), menuInfo.getMenuOption(), menuInfo.getCategoryId(), menuInfo.getImgPath()};
        db.execSQL(sql, data);
    }
    // 주문 넣기
    public void insertOrderInfo(OrderInfo orderInfo){
        Log.d("DBHELPER", "Insert Order Information to DB");
        String sql = "INSERT INTO orderInfo (menuId, orderKor, orderEng, orderChn, orderJpn, tableNumber, orderTotal, orderPrice, orderDate, orderPrinted) VALUES (?,?,?,?,?,?,?,?,date('now'),?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {orderInfo.getMenuId(), orderInfo.getOrderKor(), orderInfo.getOrderEng(), orderInfo.getOrderChn(), orderInfo.getOrderJpn(), orderInfo.getTableNumber(), orderInfo.getOrderTotal(), orderInfo.getOrderPrice(), orderInfo.getOrderPrinted()};
        db.execSQL(sql, data);
    }
    //세트 넣기
    public void insertSetInfo(SetInfo setInfo){
        Log.d("DBHELPER", "Insert Order Information to DB");
        String sql = "INSERT INTO setInfo (categoryId, menuId, setKor, setEng, setChn, setJpn, setNumber, setPrice) VALUES (?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {setInfo.getCategoryId(), setInfo.getMenuId(), setInfo.getSetKor(), setInfo.getSetEng(), setInfo.getSetChn(), setInfo.getSetJpn(), setInfo.getSetNumber(), setInfo.getSetPrice()};
        db.execSQL(sql, data);
    }
    // 옵션 넣기
    public void insertOptionInfo(OptionInfo optionInfo){
        String sql = "INSERT INTO optionInfo (optionPrice, optionKor, optionEng, optionChn, optionJpn, categoryId, menuId) VALUES (?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {optionInfo.getOptionPrice(), optionInfo.getOptionKor(), optionInfo.getOptionEng(), optionInfo.getOptionChn(), optionInfo.getOptionJpn(), optionInfo.getCategoryId(), optionInfo.getMenuId()};
        db.execSQL(sql, data);
    }
    // 결제 넣기
    public void insertPaymentInfo(PaymentInfo paymentInfo){
        String sql = "INSERT INTO paymentInfo (paymentAmount, paymentMethod, paymentDate, paymentTime, paymentNumber, paymentTableNumber, paymentAcquirerCode, paymentAcquirerName) VALUES (?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {paymentInfo.getPaymentAmount(), paymentInfo.getPaymentMethod(), paymentInfo.getPaymentDate(), paymentInfo.getPaymentTime(), paymentInfo.getPaymentNumber(), paymentInfo.getPaymentTableNumber(), paymentInfo.getPaymentAcquirerCode(), paymentInfo.getPaymentAcquirerName()};
        db.execSQL(sql, data);
    }
    // 프린터 넣기
    public void insertPrinterInfo(PrinterInfo printerInfo){
        String sql = "INSERT INTO printerInfo (productName, manufacturerName, vendorId, productId, printerWhat) VALUES (?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {printerInfo.getProductName(), printerInfo.getManufacturerName(), printerInfo.getVendorId(), printerInfo.getProductId(), printerInfo.getPrinterWhat()};
        db.execSQL(sql, data);
    }



    //DELETE
    // 업체 삭제
    public void deleteStoreInfo(){
        String sql = "DELETE FROM storeInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    // 카테고리 삭제
    public void deleteCategoryInfo(){
        String sql = "DELETE FROM categoryInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    // 메뉴 삭제
    public void deleteMenuInfo(){
        String sql = "DELETE FROM menuInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    // 1 테이블 주문 삭제
    public void deleteTableOrderInfo(int tableNumber){
        String sql = "DELETE FROM orderInfo WHERE tableNumber=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {tableNumber};
        db.execSQL(sql, data);
    }
    // 1개 주문 삭제
    public void deleteOneOrderInfo(OrderInfo orderInfo){
        String sql = "DELETE FROM orderInfo WHERE orderKor=? AND tableNumber=? AND menuId=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {orderInfo.getOrderKor(), orderInfo.getTableNumber(), orderInfo.getMenuId()};
        db.execSQL(sql, data);
    }
    // 세트 삭제
    public void deleteSetInfo(){
        String sql = "DELETE FROM setInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    public void deleteOptionInfo(){
        String sql = "DELETE FROM optionInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    public void deleteAllPaymentInfo(){
        String sql = "DELETE FROM paymentInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    public void deleteOnePaymentInfo(PaymentInfo paymentInfo){
        String sql = "DELETE FROM paymentInfo WHERE paymentNumber=? AND paymentAmount=? AND paymentMethod=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {paymentInfo.getPaymentNumber(), paymentInfo.getPaymentAmount(), paymentInfo.getPaymentMethod()};
        db.execSQL(sql, data);
    }
    public void deletePrinterInfo(){
        String sql = "DELETE FROM printerInfo";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    // 전체 삭제
    public void deleteAll(){
        deleteStoreInfo();
        deleteCategoryInfo();
        deleteMenuInfo();
        deleteSetInfo();
        deleteOptionInfo();
        deletePrinterInfo();
    }

    // UPDATE
    // 업체, 카테고리, 메뉴, 세트, 옵션, 프린터, 결제는 삭제 후 재생성으로 해결
    // 이미 있는 주문 수정
    public void updateOneOrderTotal(OrderInfo orderInfo, int newTotal){
        Log.d("DBHELPER", "Update Order Information to DB");
        String sql = "UPDATE orderInfo SET orderTotal=?, orderDate=date('now'), orderPrinted=? WHERE menuId=? AND tableNumber=? AND orderKor=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {newTotal, orderInfo.getOrderPrinted(), orderInfo.getMenuId(), orderInfo.getTableNumber(), orderInfo.getOrderKor()};
        db.execSQL(sql, data);
    }
    // 프린트 완료
    public void printDone(OrderInfo orderInfo){
        String sql = "UPDATE orderInfo SET orderPrinted=0 WHERE menuId=? AND tableNumber=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {orderInfo.getMenuId(), orderInfo.getTableNumber()};
        db.execSQL(sql, data);
    }
    public void changeTableNumber(OrderInfo orderInfo, int newTableNumber){
        String sql = "UPDATE orderInfo SET tableNumber=? WHERE tableNumber=? AND menuId=? AND orderKor=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {newTableNumber, orderInfo.getTableNumber(), orderInfo.getMenuId(), orderInfo.getOrderKor()};
        db.execSQL(sql, data);
    }


    //SELECT
    // 업체 정보
    // 업체 정보 가져오기
    public StoreInfo getStoreInfo(){
        StoreInfo storeInfo = new StoreInfo();
        String sql = "SELECT storeId, storeName, storeRandom, tableNumber FROM storeInfo";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int indexStoreId = cursor.getColumnIndex("storeId");
                int indexStoreName = cursor.getColumnIndex("storeName");
                int indexStoreRandom = cursor.getColumnIndex("storeRandom");
                int indexTableNumber = cursor.getColumnIndex("tableNumber");

                storeInfo.setStoreId(cursor.getInt(indexStoreId));
                storeInfo.setStoreName(cursor.getString(indexStoreName));
                storeInfo.setStoreRandom(cursor.getString(indexStoreRandom));
                storeInfo.setTableNumber(cursor.getInt(indexTableNumber));
            }else{
                storeInfo = null;
            }
            cursor.close();
        }else{
            storeInfo = null;
        }

        return storeInfo;
    }
    // 카테고리 리스트 가져오기
    public List<CategoryInfo> getCategoryInfo(){
        List<CategoryInfo> categoryInfoList = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory, categorySet FROM categoryInfo";
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
    public CategoryInfo getOneCategoryInfo(int categoryId){
        CategoryInfo categoryInfo = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory, categorySet FROM categoryInfo WHERE categoryId=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(categoryId)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                categoryInfo = new CategoryInfo();
                cursor.moveToFirst();
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
            }
            cursor.close();
        }

        return categoryInfo;
    }
    // 프린트 될 카테고리 아이디
    public List<Integer> getSelectedCategoryInfo(){
        List<Integer> selectedList = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory FROM categoryInfo WHERE selectedCategory=1";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                selectedList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    int indexCategoryId = cursor.getColumnIndex("categoryId");

                    selectedList.add(cursor.getInt(indexCategoryId));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return selectedList;
    }
    // 세트 카테고리 아이디
    public List<Integer> getSetCategoryInfo(){
        List<Integer> selectedList = null;
        String sql = "SELECT id, categoryId, categoryKor, categoryEng, categoryChn, categoryJpn, selectedCategory FROM categoryInfo WHERE categorySet=1";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                selectedList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    int indexCategoryId = cursor.getColumnIndex("categoryId");

                    selectedList.add(cursor.getInt(indexCategoryId));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return selectedList;
    }
    // 카테고리 ID에 맞는 메뉴 리스트 가져오기
    public List<MenuInfo> getMenuInfo(int categoryId){
        List<MenuInfo> menuInfoList = null;
        String sql = "SELECT id, menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, menuOption, categoryId, imgPath FROM menuInfo WHERE categoryId=?";
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
                    int indexMenuOption = cursor.getColumnIndex("menuOption");
                    int indexCategoryId = cursor.getColumnIndex("categoryId");
                    int indexImgPath = cursor.getColumnIndex("imgPath");

                    menuInfo.setMenuId(cursor.getInt(indexMenuId));
                    menuInfo.setMenuKor(cursor.getString(indexMenuKor));
                    menuInfo.setMenuEng(cursor.getString(indexMenuEng));
                    menuInfo.setMenuChn(cursor.getString(indexMenuChn));
                    menuInfo.setMenuJpn(cursor.getString(indexMenuJpn));
                    menuInfo.setMenuPrice(cursor.getInt(indexMenuPrice));
                    menuInfo.setMenuOption(cursor.getInt(indexMenuOption));
                    menuInfo.setCategoryId(cursor.getInt(indexCategoryId));
                    menuInfo.setImgPath(cursor.getString(indexImgPath));

                    menuInfoList.add(menuInfo);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return menuInfoList;
    }
    // 메뉴 1개 가져오기
    public MenuInfo getOneMenuInfo(int menuId){
        MenuInfo menuInfo = null;
        String sql = "SELECT id, menuId, menuKor, menuEng, menuChn, menuJpn, menuPrice, menuOption, categoryId, imgPath FROM menuInfo WHERE menuId=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor!=null){
            if(cursor.getCount()>0){
                menuInfo = new MenuInfo();
                cursor.moveToFirst();

                int indexMenuId = cursor.getColumnIndex("menuId");
                int indexMenuKor = cursor.getColumnIndex("menuKor");
                int indexMenuEng = cursor.getColumnIndex("menuEng");
                int indexMenuChn = cursor.getColumnIndex("menuChn");
                int indexMenuJpn = cursor.getColumnIndex("menuJpn");
                int indexMenuPrice = cursor.getColumnIndex("menuPrice");
                int indexMenuOption = cursor.getColumnIndex("menuOption");
                int indexCategoryId = cursor.getColumnIndex("categoryId");
                int indexImgPath = cursor.getColumnIndex("imgPath");

                menuInfo.setMenuId(cursor.getInt(indexMenuId));
                menuInfo.setMenuKor(cursor.getString(indexMenuKor));
                menuInfo.setMenuEng(cursor.getString(indexMenuEng));
                menuInfo.setMenuChn(cursor.getString(indexMenuChn));
                menuInfo.setMenuJpn(cursor.getString(indexMenuJpn));
                menuInfo.setMenuPrice(cursor.getInt(indexMenuPrice));
                menuInfo.setMenuOption(cursor.getInt(indexMenuOption));
                menuInfo.setCategoryId(cursor.getInt(indexCategoryId));
                menuInfo.setImgPath(cursor.getString(indexImgPath));
            }
            cursor.close();
        }
        return menuInfo;
    }
    // 테이블 별 주문 가져오기
    public List<OrderInfo> getTableOrderInfo(int tableNumber){
        List<OrderInfo> orderInfoList = null;
        String sql = "SELECT id, menuId, orderKor, orderEng, orderChn, orderJpn, tableNumber, orderTotal, orderPrice, orderDate, orderPrinted FROM orderInfo WHERE tableNumber=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(tableNumber)};
        Cursor cursor = db.rawQuery(sql, data);

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
                    int indexTableNumber = cursor.getColumnIndex("tableNumber");
                    int indexOrderTotal = cursor.getColumnIndex("orderTotal");
                    int indexOrderPrice = cursor.getColumnIndex("orderPrice");
                    int indexOrderDate = cursor.getColumnIndex("orderDate");
                    int indexOrderPrinted = cursor.getColumnIndex("orderPrinted");

                    orderInfo.setMenuId(cursor.getInt(indexMenuId));
                    orderInfo.setOrderKor(cursor.getString(indexOrderKor));
                    orderInfo.setOrderEng(cursor.getString(indexOrderEng));
                    orderInfo.setOrderChn(cursor.getString(indexOrderChn));
                    orderInfo.setOrderJpn(cursor.getString(indexOrderJpn));
                    orderInfo.setTableNumber(cursor.getInt(indexTableNumber));
                    orderInfo.setOrderTotal(cursor.getInt(indexOrderTotal));
                    orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
                    orderInfo.setOrderDate(cursor.getInt(indexOrderDate));
                    orderInfo.setOrderPrinted(cursor.getInt(indexOrderPrinted));

                    orderInfoList.add(orderInfo);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return orderInfoList;
    }
    // 프린트 안된 주문 처리
    public List<OrderInfo> getTableOrderNotPrinted(int tableNumber){
        List<OrderInfo> orderInfoList = null;
        String sql = "SELECT id, menuId, orderKor, orderEng, orderChn, orderJpn, tableNumber, orderTotal, orderPrice, orderDate, orderPrinted FROM orderInfo WHERE tableNumber=? AND orderPrinted>0";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(tableNumber)};
        Cursor cursor = db.rawQuery(sql, data);

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
                    int indexTableNumber = cursor.getColumnIndex("tableNumber");
                    int indexOrderTotal = cursor.getColumnIndex("orderTotal");
                    int indexOrderPrice = cursor.getColumnIndex("orderPrice");
                    int indexOrderDate = cursor.getColumnIndex("orderDate");
                    int indexOrderPrinted = cursor.getColumnIndex("orderPrinted");

                    orderInfo.setMenuId(cursor.getInt(indexMenuId));
                    orderInfo.setOrderKor(cursor.getString(indexOrderKor));
                    orderInfo.setOrderEng(cursor.getString(indexOrderEng));
                    orderInfo.setOrderChn(cursor.getString(indexOrderChn));
                    orderInfo.setOrderJpn(cursor.getString(indexOrderJpn));
                    orderInfo.setTableNumber(cursor.getInt(indexTableNumber));
                    orderInfo.setOrderTotal(cursor.getInt(indexOrderTotal));
                    orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
                    orderInfo.setOrderDate(cursor.getInt(indexOrderDate));
                    orderInfo.setOrderPrinted(cursor.getInt(indexOrderPrinted));

                    orderInfoList.add(orderInfo);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return orderInfoList;
    }

    // 합계 가져오기
    public int getTableOrderTotal(int tableNumber){
        int tableTotal = 0;
        String sql = "SELECT id, orderTotal, orderPrice FROM orderInfo WHERE tableNumber=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(tableNumber)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                do{
                    int indexOrderTotal = cursor.getColumnIndex("orderTotal");
                    int indexOrderPrice = cursor.getColumnIndex("orderPrice");

                    tableTotal += cursor.getInt(indexOrderTotal)*cursor.getInt(indexOrderPrice);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return tableTotal;
    }
    // 해당 메뉴 아이디에 맞는 주문 내역 찾기
    public OrderInfo getOneOrderInfo(int menuId, int tableNumber, String orderKor){
        OrderInfo orderInfo = null;
        String sql = "SELECT id, menuId, orderKor, orderEng, orderChn, orderJpn, tableNumber, orderTotal, orderPrice, orderDate, orderPrinted FROM orderInfo WHERE menuId=? AND tableNumber = ? AND orderKor=?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(menuId), String.valueOf(tableNumber), orderKor};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor!=null){
            if(cursor.getCount()>0){
                orderInfo = new OrderInfo();
                cursor.moveToFirst();

                int indexMenuId = cursor.getColumnIndex("menuId");
                int indexOrderKor = cursor.getColumnIndex("orderKor");
                int indexOrderEng = cursor.getColumnIndex("orderEng");
                int indexOrderChn = cursor.getColumnIndex("orderChn");
                int indexOrderJpn = cursor.getColumnIndex("orderJpn");
                int indexTableNumber = cursor.getColumnIndex("tableNumber");
                int indexOrderTotal = cursor.getColumnIndex("orderTotal");
                int indexOrderPrice = cursor.getColumnIndex("orderPrice");
                int indexOrderDate = cursor.getColumnIndex("orderDate");
                int indexOrderPrinted = cursor.getColumnIndex("orderPrinted");

                orderInfo.setMenuId(cursor.getInt(indexMenuId));
                orderInfo.setOrderKor(cursor.getString(indexOrderKor));
                orderInfo.setOrderEng(cursor.getString(indexOrderEng));
                orderInfo.setOrderChn(cursor.getString(indexOrderChn));
                orderInfo.setOrderJpn(cursor.getString(indexOrderJpn));
                orderInfo.setTableNumber(cursor.getInt(indexTableNumber));
                orderInfo.setOrderTotal(cursor.getInt(indexOrderTotal));
                orderInfo.setOrderPrice(cursor.getInt(indexOrderPrice));
                orderInfo.setOrderDate(cursor.getInt(indexOrderDate));
                orderInfo.setOrderPrinted(cursor.getInt(indexOrderPrinted));
            }
            cursor.close();
        }
        return orderInfo;
    }
    // 해당 메뉴의 세트 리스트 확인
    public List<SetInfo> getAllSetInfo(int menuId){
        List<SetInfo> setInfoList = null;
        String sql = "SELECT id, categoryId, menuId, setKor, setEng, setChn, setJpn, setNumber, setPrice FROM setInfo WHERE menuId = ?";
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
    // 결제 정보 전체 보기
    public List<PaymentInfo> getPaymentInfoList(int index){
        List<PaymentInfo> paymentInfoList = null;

        int startNumber = (index-1)*15;

        String sql = "SELECT id, paymentAmount, paymentMethod, paymentDate, paymentTime, paymentNumber, paymentTableNumber, paymentAcquirerCode, paymentAcquirerName FROM paymentInfo LIMIT ?, 15";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {String.valueOf(startNumber)};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                paymentInfoList = new ArrayList<>();
                cursor.moveToFirst();
                do{
                    PaymentInfo paymentInfo = new PaymentInfo();
                    int indexPaymentAmount = cursor.getColumnIndex("paymentAmount");
                    int indexPaymentMethod = cursor.getColumnIndex("paymentMethod");
                    int indexPaymentDate = cursor.getColumnIndex("paymentDate");
                    int indexPaymentTime = cursor.getColumnIndex("paymentTime");
                    int indexPaymentNumber = cursor.getColumnIndex("paymentNumber");
                    int indexPaymentTableNumber = cursor.getColumnIndex("paymentTableNumber");
                    int indexPaymentAcquirerCode = cursor.getColumnIndex("paymentAcquirerCode");
                    int indexPaymentAcquirerName = cursor.getColumnIndex("paymentAcquirerName");

                    paymentInfo.setPaymentAcquirerCode(cursor.getString(indexPaymentAcquirerCode));
                    paymentInfo.setPaymentAcquirerName(cursor.getString(indexPaymentAcquirerName));
                    paymentInfo.setPaymentAmount(cursor.getInt(indexPaymentAmount));
                    paymentInfo.setPaymentDate(cursor.getString(indexPaymentDate));
                    paymentInfo.setPaymentMethod(cursor.getString(indexPaymentMethod));
                    paymentInfo.setPaymentTableNumber(cursor.getInt(indexPaymentTableNumber));
                    paymentInfo.setPaymentTime(cursor.getString(indexPaymentTime));
                    paymentInfo.setPaymentNumber(cursor.getString(indexPaymentNumber));

                    paymentInfoList.add(paymentInfo);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }

        return paymentInfoList;
    }
    // 결제 정보 리스트 숫자
    public int getPaymentNumber(){
        int paymentCount = 0;
        String sql = "SELECT COUNT(id) as count FROM paymentInfo";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex("count");
                paymentCount = cursor.getInt(index);
            }
        }

        return paymentCount;
    }
    // 해당 용도에 맞는 프린터 가져오기
    public PrinterInfo getPrinterInfo(String printerWhat){
        PrinterInfo printerInfo = null;
        String sql = "SELECT id, productName, manufacturerName, vendorId, productId, printerWhat FROM printerInfo WHERE printerWhat = ?";
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {printerWhat};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                printerInfo = new PrinterInfo();
                cursor.moveToFirst();

                int indexProductName = cursor.getColumnIndex("productName");
                int indexManufacturerName = cursor.getColumnIndex("manufacturerName");
                int indexVendorId = cursor.getColumnIndex("vendorId");
                int indexProductId = cursor.getColumnIndex("productId");
                int indexPrinterWhat = cursor.getColumnIndex("printerWhat");

                printerInfo.setProductName(cursor.getString(indexProductName));
                printerInfo.setManufacturerName(cursor.getString(indexManufacturerName));
                printerInfo.setVendorId(cursor.getString(indexVendorId));
                printerInfo.setProductId(cursor.getString(indexProductId));
                printerInfo.setPrinterWhat(cursor.getString(indexPrinterWhat));
            }
            cursor.close();
        }

        return printerInfo;
    }

    //// 달력 DB 생성
    //        String calendarSql = "CREATE TABLE calendarInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    //                "calendarDate TEXT NOT NULL, " +
    //                "openCash INTEGER NOT NULL, " +
    //                "closeCash INTEGER NOT NULL)";
    //
    //        // 현금 DB 생성
    //        String cashSql = "CREATE TABLE cashInfo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    //                "fiftyThousandCount INTEGER NOT NULL, " +
    //                "tenThousandCount INTEGER NOT NULL, " +
    //                "fiveThousandCount INTEGER NOT NULL, " +
    //                "oneThousandCount INTEGER NOT NULL, " +
    //                "fiveHundredCount INTEGER NOT NULL, " +
    //                "oneHundredCount INTEGER NOT NULL, " +
//    "fiftyCount INTEGER NOT NULL, " +
//            "tenCount INTEGER NOT NULL, " +
    //                "cashDate TEXT NOT NULL)";

    public void insertCalendarInfo(CalendarInfo calendarInfo){
        String sql = "INSERT INTO calendarInfo (calendarDate, openCash, closeCash) VALUES (?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {calendarInfo.getCalendarDate(), calendarInfo.getOpenCash(), calendarInfo.getCloseCash()};
        db.execSQL(sql, data);
    }

    public void updateCalendarInfo(CalendarInfo calendarInfo){
        String sql = "UPDATE calendarInfo SET openCash=?, closeCash=? WHERE calendarDate=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {calendarInfo.getOpenCash(), calendarInfo.getCloseCash(), calendarInfo.getCalendarDate()};
        db.execSQL(sql, data);
    }

    public void insertCashInfo(CashInfo cashInfo){
        String sql = "INSERT INTO cashInfo (fiftyThousandCount, tenThousandCount, fiveThousandCount, oneThousandCount, fiveHundredCount, oneHundredCount, fiftyCount, tenCount, cashDate, cashSave) VALUES (?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {cashInfo.getFiftyThousandCount(), cashInfo.getTenThousandCount(), cashInfo.getFiveThousandCount(), cashInfo.getOneThousandCount(), cashInfo.getFiveHundredCount(), cashInfo.getOneHundredCount(), cashInfo.getFiftyCount(), cashInfo.getTenCount(), cashInfo.getCashDate(), cashInfo.getCashSave()};
        db.execSQL(sql, data);
    }

    public void updateCashInfo(CashInfo cashInfo){
        String sql ="UPDATE cashInfo SET fiftyThousandCount=?,tenThousandCount=?, fiveThousandCount=?, oneThousandCount=?, fiveHundredCount=?, oneHundredCount=?, fiftyCount=?, tenCount=? WHERE cashDate=? AND cashSave=?";
        SQLiteDatabase db = getWritableDatabase();
        Object[] data = {cashInfo.getFiftyThousandCount(), cashInfo.getTenThousandCount(), cashInfo.getFiveThousandCount(), cashInfo.getOneThousandCount(), cashInfo.getFiveHundredCount(), cashInfo.getOneHundredCount(), cashInfo.getFiftyCount(), cashInfo.getTenCount(), cashInfo.getCashDate(), cashInfo.getCashSave()};
        db.execSQL(sql, data);
    }

    public CashInfo getCashInfo(String year, String month, String day, String cashSave){
        CashInfo cashInfo = null;
        String sql = "SELECT id, fiftyThousandCount, tenThousandCount, fiveThousandCount, oneThousandCount, fiveHundredCount, oneHundredCount, fiftyCount, tenCount, cashDate, cashSave FROM cashInfo WHERE cashDate = ? AND cashSave=?";
        String newString = year+"-"+month+"-"+day;
        SQLiteDatabase db = getReadableDatabase();
        String[] data = {newString, cashSave};
        Cursor cursor = db.rawQuery(sql, data);

        if(cursor != null){
            if(cursor.getCount() > 0){
                cashInfo = new CashInfo();
                cursor.moveToFirst();

                int fiftyThousandCount = cursor.getColumnIndex("fiftyThousandCount");
                int tenThousandCount = cursor.getColumnIndex("tenThousandCount");
                int fiveThousandCount = cursor.getColumnIndex("fiveThousandCount");
                int oneThousandCount = cursor.getColumnIndex("oneThousandCount");
                int fiveHundredCount = cursor.getColumnIndex("fiveHundredCount");
                int oneHundredCount = cursor.getColumnIndex("oneHundredCount");
                int fiftyCount = cursor.getColumnIndex("fiftyCount");
                int tenCount = cursor.getColumnIndex("tenCount");
                int cashDate = cursor.getColumnIndex("cashDate");
                int indexCashSave = cursor.getColumnIndex("cashSave");

                cashInfo.setCashDate(cursor.getString(cashDate));
                cashInfo.setFiftyThousandCount(cursor.getInt(fiftyThousandCount));
                cashInfo.setTenThousandCount(cursor.getInt(tenThousandCount));
                cashInfo.setFiveThousandCount(cursor.getInt(fiveThousandCount));
                cashInfo.setOneThousandCount(cursor.getInt(oneThousandCount));
                cashInfo.setFiveHundredCount(cursor.getInt(fiveHundredCount));
                cashInfo.setOneHundredCount(cursor.getInt(oneHundredCount));
                cashInfo.setFiftyCount(cursor.getInt(fiftyCount));
                cashInfo.setTenCount(cursor.getInt(tenCount));
                cashInfo.setCashSave(cursor.getString(indexCashSave));
            }
        }

        return cashInfo;
    }

    public CalendarInfo getCalendarInfo(String year, String month, String day){
        CalendarInfo calendarInfoList = null;
        String sql = "SELECT id, calendarDate, openCash, closeCash FROM calendarInfo WHERE calendarDate = ?";
        SQLiteDatabase db = getReadableDatabase();
        String newString = year+"-"+month+"-"+day;
        String[] data = {newString};
        Cursor cursor = db.rawQuery(sql, data);
        if(cursor != null){
            if(cursor.getCount() > 0){
                calendarInfoList = new CalendarInfo();
                cursor.moveToFirst();

                int indexCalendarDate = cursor.getColumnIndex("calendarDate");
                int indexOpenCash = cursor.getColumnIndex("openCash");
                int indexCloseCash = cursor.getColumnIndex("closeCash");

                calendarInfoList.setCalendarDate(cursor.getString(indexCalendarDate));
                calendarInfoList.setOpenCash(cursor.getInt(indexOpenCash));
                calendarInfoList.setCloseCash(cursor.getInt(indexCloseCash));
            }
        }


        return calendarInfoList;
    }




}

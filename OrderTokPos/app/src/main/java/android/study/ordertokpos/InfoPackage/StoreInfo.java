package android.study.ordertokpos.InfoPackage;

public class StoreInfo {
    private int storeId;
    private String storeName;
    private String storeRandom;
    private int tableNumber;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreRandom() {
        return storeRandom;
    }

    public void setStoreRandom(String storeRandom) {
        this.storeRandom = storeRandom;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}

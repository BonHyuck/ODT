package android.study.ordertokmenu.InfoPackage;

public class StoreInfo {
    private int storeId;
    private String storeName;
    private String storeRandom;
    private int tableNumber;
    private int updateTime;
    private String posIpAddress;
    private String employeeIpAddress;
    private String backImgPath;
    private String logoImgPath;
    private String resetCode;

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public String getBackImgPath() {
        return backImgPath;
    }

    public void setBackImgPath(String backImgPath) {
        this.backImgPath = backImgPath;
    }

    public String getLogoImgPath() {
        return logoImgPath;
    }

    public void setLogoImgPath(String logoImgPath) {
        this.logoImgPath = logoImgPath;
    }

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

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public String getPosIpAddress() {
        return posIpAddress;
    }

    public void setPosIpAddress(String posIpAddress) {
        this.posIpAddress = posIpAddress;
    }

    public String getEmployeeIpAddress() {
        return employeeIpAddress;
    }

    public void setEmployeeIpAddress(String employeeIpAddress) {
        this.employeeIpAddress = employeeIpAddress;
    }
}

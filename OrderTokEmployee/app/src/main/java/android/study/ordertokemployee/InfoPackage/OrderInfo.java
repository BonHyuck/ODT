package android.study.ordertokemployee.InfoPackage;

public class OrderInfo {
    private int menuId;
    private int categoryId;
    private String orderKor;
    private int tableNumber;
    private int orderCount;
    private int orderPrice;
    private int orderDate;
    private int orderCheck;

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getOrderKor() {
        return orderKor;
    }

    public void setOrderKor(String orderKor) {
        this.orderKor = orderKor;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(int orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderCheck() {
        return orderCheck;
    }

    public void setOrderCheck(int orderCheck) {
        this.orderCheck = orderCheck;
    }
}

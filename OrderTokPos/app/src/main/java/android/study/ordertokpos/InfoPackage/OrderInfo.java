package android.study.ordertokpos.InfoPackage;

public class OrderInfo {
    private int menuId;
    private String orderKor;
    private String orderEng;
    private String orderChn;
    private String orderJpn;
    private int tableNumber;
    private int orderTotal;
    private int orderPrice;
    private int orderDate;
    private int orderPrinted;

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getOrderKor() {
        return orderKor;
    }

    public void setOrderKor(String orderKor) {
        this.orderKor = orderKor;
    }

    public String getOrderEng() {
        return orderEng;
    }

    public void setOrderEng(String orderEng) {
        this.orderEng = orderEng;
    }

    public String getOrderChn() {
        return orderChn;
    }

    public void setOrderChn(String orderChn) {
        this.orderChn = orderChn;
    }

    public String getOrderJpn() {
        return orderJpn;
    }

    public void setOrderJpn(String orderJpn) {
        this.orderJpn = orderJpn;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(int orderTotal) {
        this.orderTotal = orderTotal;
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

    public int getOrderPrinted() {
        return orderPrinted;
    }

    public void setOrderPrinted(int orderPrinted) {
        this.orderPrinted = orderPrinted;
    }
}

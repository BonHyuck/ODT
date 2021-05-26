package android.study.ordertokpos.InfoPackage;

public class PaymentInfo {
    private int paymentAmount;
    private String paymentMethod;
    private String paymentDate;
    private String paymentTime;
    private String paymentNumber;
    private int paymentTableNumber;
    private String paymentAcquirerCode;
    private String paymentAcquirerName;

    public String getPaymentAcquirerCode() {
        return paymentAcquirerCode;
    }

    public void setPaymentAcquirerCode(String paymentAcquirerCode) {
        this.paymentAcquirerCode = paymentAcquirerCode;
    }

    public String getPaymentAcquirerName() {
        return paymentAcquirerName;
    }

    public void setPaymentAcquirerName(String paymentAcquirerName) {
        this.paymentAcquirerName = paymentAcquirerName;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public int getPaymentTableNumber() {
        return paymentTableNumber;
    }

    public void setPaymentTableNumber(int paymentTableNumber) {
        this.paymentTableNumber = paymentTableNumber;
    }
}

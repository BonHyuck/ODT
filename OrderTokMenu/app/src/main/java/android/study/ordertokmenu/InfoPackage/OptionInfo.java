package android.study.ordertokmenu.InfoPackage;

public class OptionInfo {
    private int optionPrice;
    private String optionKor;
    private String optionEng;
    private String optionChn;
    private String optionJpn;
    private int categoryId;
    private int menuId;

    public int getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(int optionPrice) {
        this.optionPrice = optionPrice;
    }

    public String getOptionKor() {
        return optionKor;
    }

    public void setOptionKor(String optionKor) {
        this.optionKor = optionKor;
    }

    public String getOptionEng() {
        return optionEng;
    }

    public void setOptionEng(String optionEng) {
        this.optionEng = optionEng;
    }

    public String getOptionChn() {
        return optionChn;
    }

    public void setOptionChn(String optionChn) {
        this.optionChn = optionChn;
    }

    public String getOptionJpn() {
        return optionJpn;
    }

    public void setOptionJpn(String optionJpn) {
        this.optionJpn = optionJpn;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}

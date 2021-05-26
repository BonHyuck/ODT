package android.study.ordertokemployee.InfoPackage;

public class CategoryInfo {
    private int categoryId;
    private String categoryKor;
    private String categoryEng;
    private String categoryChn;
    private String categoryJpn;
    private int selectedCategory;
    private int categorySet;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryKor() {
        return categoryKor;
    }

    public void setCategoryKor(String categoryKor) {
        this.categoryKor = categoryKor;
    }

    public String getCategoryEng() {
        return categoryEng;
    }

    public void setCategoryEng(String categoryEng) {
        this.categoryEng = categoryEng;
    }

    public String getCategoryChn() {
        return categoryChn;
    }

    public void setCategoryChn(String categoryChn) {
        this.categoryChn = categoryChn;
    }

    public String getCategoryJpn() {
        return categoryJpn;
    }

    public void setCategoryJpn(String categoryJpn) {
        this.categoryJpn = categoryJpn;
    }

    public int getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(int selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public int getCategorySet() {
        return categorySet;
    }

    public void setCategorySet(int categorySet) {
        this.categorySet = categorySet;
    }
}

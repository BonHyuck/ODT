package android.study.ordertokemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.study.ordertokemployee.Back.BaseAdapter;
import android.study.ordertokemployee.Back.DBHelper;
import android.study.ordertokemployee.Back.SocketReceiveClass;
import android.study.ordertokemployee.HolderPackage.CategoryHolder;
import android.study.ordertokemployee.InfoPackage.CategoryInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    ListView categorySelectListView;
    CategoryAdapter categoryAdapter;
    DBHelper dbHelper;
    Button categorySelectedDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dbHelper = new DBHelper(SelectActivity.this);

        Thread thread = new Thread(new SocketReceiveClass(SelectActivity.this));
        thread.start();

        categorySelectedDoneButton = findViewById(R.id.categorySelectedDoneButton);
        categorySelectListView = findViewById(R.id.categorySelectListView);
        categoryAdapter = new CategoryAdapter(SelectActivity.this, R.layout.category_item);
        categorySelectListView.setAdapter(categoryAdapter);

        categorySelectedDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent proceedIntent = new Intent(SelectActivity.this, MainActivity.class);
                startActivity(proceedIntent);
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryAdapter.clear();
        setCategorySelect();
    }

    public void setCategorySelect(){
        List<CategoryInfo> categoryInfoList = dbHelper.getAllCategoryInfo();
        if(categoryInfoList!=null&&categoryInfoList.size()>0){
            for(int i=0; i<categoryInfoList.size(); i++){
                CategoryInfo categoryInfo = categoryInfoList.get(i);
                categoryAdapter.add(categoryInfo);
            }
        }
    }

    class CategoryAdapter extends BaseAdapter<CategoryInfo, CategoryHolder>{

        public CategoryAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(CategoryHolder holder, View view) {
            holder.categoryTextView = view.findViewById(R.id.categoryTextView);
            holder.categorySwitch = view.findViewById(R.id.categorySwitch);
        }

        @Override
        public void setListItem(CategoryHolder holder, final CategoryInfo item) {
            holder.categoryTextView.setText(item.getCategoryKor());
            if(item.getSelectedCategory()>0){
                holder.categorySwitch.setChecked(true);
            }else{
                holder.categorySwitch.setChecked(false);
            }
            holder.categorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        dbHelper.changeCategoryInfo(item.getCategoryId(), 1);
                    }else{
                        dbHelper.changeCategoryInfo(item.getCategoryId(), 0);
                    }
                }
            });
        }
    }
}

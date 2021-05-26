package android.study.ordertokpos.FragmentPackage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.BaseAdapter;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.HolderPackage.CategoryHolder;
import android.study.ordertokpos.InfoPackage.CategoryInfo;
import android.study.ordertokpos.InfoPackage.MenuInfo;
import android.study.ordertokpos.MainActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailCategoryFragment extends Fragment {
    private MainActivity mainActivity;
    private GridView categoryGridView, menuGridView;
    private CategoryAdapter categoryAdapter;
    private NewMenuAdapter newMenuAdapter;
    private DBHelper dbHelper;
    private int tableNumber;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        mainActivity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryAdapter.clear();
        setCategoryGridView();
    }

    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail_category, container, false);

        dbHelper = new DBHelper(mainActivity);
        categoryGridView = rootView.findViewById(R.id.categoryGridView);
        menuGridView = rootView.findViewById(R.id.menuGridView);
        categoryAdapter = new CategoryAdapter(mainActivity, R.layout.one_category_item);
        newMenuAdapter = new NewMenuAdapter(mainActivity, R.layout.one_category_item);
        categoryGridView.setAdapter(categoryAdapter);
        menuGridView.setAdapter(newMenuAdapter);

        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CategoryInfo categoryInfo = categoryAdapter.getItem(i);
                newMenuAdapter.clear();
                setNewMenuAdapter(categoryInfo.getCategoryId());
            }
        });

        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuInfo menuInfo = newMenuAdapter.getItem(i);
                if(dbHelper.getOneMenuInfo(menuInfo.getMenuId()).getMenuOption()>0){ // 옵션 있음
                    OptionEditFragment optionEditFragment = new OptionEditFragment();
                    getFragmentManager().beginTransaction().replace(R.id.detailLayout, optionEditFragment).commit();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tableNumber", tableNumber);
                    bundle.putInt("menuId", menuInfo.getMenuId());
                    bundle.putString("orderKor", menuInfo.getMenuKor());
                    bundle.putInt("orderPrice", menuInfo.getMenuPrice());
                    bundle.putInt("orderTotal", 1);
                    optionEditFragment.setArguments(bundle);
                }else if(dbHelper.getOneCategoryInfo(dbHelper.getOneMenuInfo(menuInfo.getMenuId()).getCategoryId()).getCategorySet()>0){ // 세트메뉴
                    SetEditFragment setEditFragment = new SetEditFragment();
                    getFragmentManager().beginTransaction().replace(R.id.detailLayout, setEditFragment).commit();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tableNumber", tableNumber);
                    bundle.putInt("menuId", menuInfo.getMenuId());
                    bundle.putString("orderKor", menuInfo.getMenuKor());
                    bundle.putInt("orderPrice", menuInfo.getMenuPrice());
                    bundle.putInt("orderTotal", 1);
                    setEditFragment.setArguments(bundle);
                }else{
                    DetailEditFragment detailEditFragment = new DetailEditFragment();
                    getFragmentManager().beginTransaction().replace(R.id.detailLayout, detailEditFragment).commit();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tableNumber", tableNumber);
                    bundle.putInt("menuId", menuInfo.getMenuId());
                    bundle.putString("orderKor", menuInfo.getMenuKor());
                    bundle.putInt("orderPrice", menuInfo.getMenuPrice());
                    bundle.putInt("orderTotal", 1);
                    detailEditFragment.setArguments(bundle);
                }
            }
        });

        tableNumber = getArguments().getInt("tableNumber");

        return rootView;
    }

    public void setCategoryGridView() {
        List<CategoryInfo> categoryInfoList = dbHelper.getCategoryInfo();
        if(categoryInfoList!=null&&categoryInfoList.size()>0){
            for(int j=0; j<categoryInfoList.size(); j++){
                CategoryInfo categoryInfo = categoryInfoList.get(j);
                categoryAdapter.add(categoryInfo);
            }
        }
    }

    public void setNewMenuAdapter(int categoryId) {
        List<MenuInfo> menuInfoList = dbHelper.getMenuInfo(categoryId);
        if(menuInfoList!=null && menuInfoList.size()>0){
            for(int i=0; i<menuInfoList.size(); i++){
                MenuInfo menuInfo = menuInfoList.get(i);
                newMenuAdapter.add(menuInfo);
            }
        }
    }

    class CategoryAdapter extends BaseAdapter<CategoryInfo, CategoryHolder>{

        public CategoryAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }


        @Override
        public void setListHolder(CategoryHolder holder, View view) {
            holder.detailName = view.findViewById(R.id.detailName);
            holder.detailPrice = view.findViewById(R.id.detailPrice);
        }

        @Override
        public void setListItem(CategoryHolder holder, CategoryInfo item) {
            holder.detailName.setText(item.getCategoryKor());
            holder.detailPrice.setText("");
        }
    }

    class NewMenuAdapter extends BaseAdapter<MenuInfo, CategoryHolder>{

        public NewMenuAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(CategoryHolder holder, View view) {
            holder.detailName = view.findViewById(R.id.detailName);
            holder.detailPrice = view.findViewById(R.id.detailPrice);
        }

        @Override
        public void setListItem(CategoryHolder holder, MenuInfo item) {
            holder.detailName.setText(item.getMenuKor());
            if(item.getMenuPrice()!=0){
                holder.detailPrice.setText(decimalFormat.format(item.getMenuPrice()));
            }else{
                holder.detailPrice.setText("");
            }
        }
    }
}

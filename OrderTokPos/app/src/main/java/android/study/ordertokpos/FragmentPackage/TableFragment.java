package android.study.ordertokpos.FragmentPackage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.BaseAdapter;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.HolderPackage.TableHolder;
import android.study.ordertokpos.InfoPackage.StoreInfo;
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

public class TableFragment extends Fragment {
    private MainActivity mainActivity;
    private DBHelper dbHelper;
    private GridView gridView;
    private TableAdapter tableAdapter;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");

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
        dbHelper.close();
    }


    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_table , container, false);
        dbHelper = new DBHelper(mainActivity);
        gridView = rootView.findViewById(R.id.tableGridView);
        tableAdapter = new TableAdapter(mainActivity, R.layout.one_table_item, new ArrayList<StoreInfo>());
        gridView.setAdapter(tableAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int tableNumber = i+1;
                mainActivity.onFragmentChange(tableNumber);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadTableFragment();
    }

    public void reloadTableFragment(){
        tableAdapter.clear();
        setTableList();
    }

    private void setTableList(){
        StoreInfo storeInfo = dbHelper.getStoreInfo();
        for(int j=0; j<storeInfo.getTableNumber(); j++){
            tableAdapter.add(storeInfo);
        }
    }

    class TableAdapter extends ArrayAdapter<StoreInfo> {
        int resource;

        TableAdapter(Context context, int resource, List<StoreInfo> objects){
            super(context, resource, objects);
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            TableHolder tableHolder;

            if (view == null){
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(this.resource, null);

                tableHolder = new TableHolder();
                tableHolder.oneTableTitle = view.findViewById(R.id.oneTableTitle);
                tableHolder.oneTableTotal = view.findViewById(R.id.oneTableTotal);

                view.setTag(tableHolder);
            }

            final StoreInfo storeInfo = getItem(position);
            final int tableNumber = position+1;
            final int tableTotal = dbHelper.getTableOrderTotal(tableNumber);

            if(storeInfo != null && storeInfo.getTableNumber()>position){
                tableHolder = (TableHolder) view.getTag();
                tableHolder.oneTableTitle.setText(String.valueOf(tableNumber));

                if(tableTotal<=0){
                    tableHolder.oneTableTotal.setText("");
                    view.setBackgroundColor(Color.rgb(200,200,200));
                    tableHolder.oneTableTitle.setBackgroundColor(Color.rgb(200,200,200));
                    tableHolder.oneTableTitle.setTextColor(Color.rgb(229,229,229));
                }else{
                    tableHolder.oneTableTotal.setText(decimalFormat.format(tableTotal));
                    view.setBackgroundColor(Color.WHITE);
                    tableHolder.oneTableTitle.setBackground(getResources().getDrawable(R.drawable.border_top_left));
                    tableHolder.oneTableTitle.setTextColor(getResources().getColor(R.color.btn_bg));
                }
            }

            return view;
        }
    }
}

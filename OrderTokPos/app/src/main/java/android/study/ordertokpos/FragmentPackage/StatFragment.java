package android.study.ordertokpos.FragmentPackage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.study.ordertokpos.Back.BaseAdapter;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.HolderPackage.StatCategoryHolder;
import android.study.ordertokpos.InfoPackage.CategoryInfo;
import android.study.ordertokpos.InfoPackage.ServerInfo;
import android.study.ordertokpos.InfoPackage.StoreInfo;
import android.study.ordertokpos.MainActivity;
import android.study.ordertokpos.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;

public class StatFragment extends Fragment {
    private MainActivity mainActivity;
    private DBHelper dbHelper;
    private ServerInfo serverInfo;
    private StoreInfo storeInfo;
    private ListView statCategoryListView;
    private StatCategoryAdapter statCategoryAdapter;
    private Button todayButton, dailyButton, weeklyButton, monthlyButton;
    private AdvancedWebView statWebView;

    private int selectedCategoryId;
    private double statProfit;
    private String selectedDate;

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
        statWebView.onDestroy();
        dbHelper.close();
    }


    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stat , container, false);
        dbHelper = new DBHelper(mainActivity);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        // 기본 값 세팅
        selectedCategoryId = 0;
        selectedDate ="today";

        statCategoryListView = rootView.findViewById(R.id.statCategoryListView);
        todayButton = rootView.findViewById(R.id.todayButton);
        dailyButton = rootView.findViewById(R.id.dailyButton);
        weeklyButton = rootView.findViewById(R.id.weeklyButton);
        monthlyButton = rootView.findViewById(R.id.monthlyButton);
        statWebView = rootView.findViewById(R.id.statWebView);

        statCategoryAdapter = new StatCategoryAdapter(mainActivity, R.layout.stat_category_item);
        statCategoryListView.setAdapter(statCategoryAdapter);
        statCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CategoryInfo categoryInfo = statCategoryAdapter.getItem(i);
                        selectedCategoryId = categoryInfo.getCategoryId();
                        Log.d("STAT", "SELECTED CATEGORY ID : "+selectedCategoryId);
                        statWebView.reload();
            }
        });

        // 웹 뷰 세팅
        WebSettings webSettings = statWebView.getSettings();
        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        statWebView.addJavascriptInterface(new WebAppInterface(mainActivity), "Android");
        statWebView.loadUrl("file:///android_asset/statGraph.html");

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = "today";
                statWebView.reload();
            }
        });
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = "daily";
                statWebView.reload();
            }
        });
        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = "weekly";
                statWebView.reload();
            }
        });
        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = "monthly";
                statWebView.reload();
            }
        });

        return rootView;
    }

    // 웹뷰에서 사용하는 함수들
    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }

        // 서버 정보 입력
        @JavascriptInterface
        public String getServerInfo(){
            return serverInfo.getServerAddress();
        }

        // 업체 아이디 입력
        @JavascriptInterface
        public int getStoreId(){
            return storeInfo.getStoreId();
        }

        // 선택된 날짜 입력
        @JavascriptInterface
        public String getSelectedDate(){
            return selectedDate;
        }

        // 선택된 카테고리 입력
        @JavascriptInterface
        public int getSelectedCategory(){
            return selectedCategoryId;
        }

        @JavascriptInterface
        public int getTableNumber(){
            return storeInfo.getTableNumber();
        }

        // 재시작
        public void reload(){
            statWebView.reload();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        reloadStatFragment();
//        setStatMenuListView(selectedCategoryId);
    }

    public void reloadStatFragment(){
        if(statCategoryAdapter!=null){
            statCategoryAdapter.clear();
        }
        setStatCategoryListView();
    }

    // 좌측 카테고리 리스트뷰
    public void setStatCategoryListView(){
        List<CategoryInfo> categoryInfoList = dbHelper.getCategoryInfo();
        CategoryInfo wholeCategoryInfo = new CategoryInfo();
        wholeCategoryInfo.setCategoryId(0);
        wholeCategoryInfo.setCategoryKor("전체");
        wholeCategoryInfo.setCategoryEng("전체");
        wholeCategoryInfo.setCategoryChn("전체");
        wholeCategoryInfo.setCategoryJpn("전체");
        statCategoryAdapter.add(wholeCategoryInfo);
        if(categoryInfoList!=null && categoryInfoList.size()>0){
            for(int i=0; i<categoryInfoList.size(); i++){
                CategoryInfo categoryInfo = categoryInfoList.get(i);
                statCategoryAdapter.add(categoryInfo);
            }
        }
    }

    class StatCategoryAdapter extends BaseAdapter<CategoryInfo, StatCategoryHolder>{

        public StatCategoryAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(StatCategoryHolder holder, View view) {
            holder.statCategoryText = view.findViewById(R.id.statCategoryText);

        }

        @Override
        public void setListItem(StatCategoryHolder holder, CategoryInfo item) {
            holder.statCategoryText.setText(item.getCategoryKor());
        }
    }
}

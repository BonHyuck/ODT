package android.study.ordertokpos.FragmentPackage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.study.ordertokpos.Back.BaseAdapter;
import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.HolderPackage.CalendarHolder;
import android.study.ordertokpos.InfoPackage.CalendarInfo;
import android.study.ordertokpos.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment {
    private MainActivity mainActivity;
    private DBHelper dbHelper;
    private ImageButton minusCalendarButton, plusCalendarButton;
    private TextView calendarTitle;
    private GridView calendarGridView;
    private CalendarAdapter calendarAdapter;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private String thisYear, thisMonth;
    private CashFragment cashFragment;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar , container, false);

        dbHelper = new DBHelper(mainActivity);
        cashFragment = new CashFragment();

        minusCalendarButton = rootView.findViewById(R.id.minusCalendarButton);
        plusCalendarButton = rootView.findViewById(R.id.plusCalendarButton);
        calendarTitle = rootView.findViewById(R.id.calendarTitle);

        calendarGridView = rootView.findViewById(R.id.calendarGridView);
        calendarAdapter = new CalendarAdapter(mainActivity, R.layout.calendar_item);
        calendarGridView.setAdapter(calendarAdapter);

        thisYear = new SimpleDateFormat("yyyy", Locale.KOREA).format(new Date());
        thisMonth = new SimpleDateFormat("MM", Locale.KOREA).format(new Date());

        calendarTitle.setText(thisYear+"년 "+thisMonth+"월");

        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Console", "CLICKED INTEGER : "+calendarAdapter.getItem(i));
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, cashFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putString("thisYear", thisYear);
                bundle.putString("thisMonth", thisMonth);
                bundle.putString("thisDay", String.valueOf(calendarAdapter.getItem(i)));
                cashFragment.setArguments(bundle);
            }
        });

        minusCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(thisYear));
                calendar.set(Calendar.MONTH, Integer.parseInt(thisMonth)-1);
                calendar.add(Calendar.MONTH, -1);

                thisYear = new SimpleDateFormat("yyyy", Locale.KOREA).format(calendar.getTime());
                thisMonth = new SimpleDateFormat("MM", Locale.KOREA).format(calendar.getTime());

                calendarTitle.setText(thisYear+"년 "+thisMonth+"월");

                calendarAdapter.clear();
                setCalendarAdapter(thisYear, thisMonth);
            }
        });

        plusCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(thisYear));
                calendar.set(Calendar.MONTH, Integer.parseInt(thisMonth)-1);
                calendar.add(Calendar.MONTH, 1);

                thisYear = new SimpleDateFormat("yyyy", Locale.KOREA).format(calendar.getTime());
                thisMonth = new SimpleDateFormat("MM", Locale.KOREA).format(calendar.getTime());

                calendarTitle.setText(thisYear+"년 "+thisMonth+"월");

                calendarAdapter.clear();
                setCalendarAdapter(thisYear, thisMonth);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        calendarAdapter.clear();
        setCalendarAdapter(thisYear, thisMonth);
    }

    public void setCalendarAdapter(String year, String month){
        int weekDay = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month)-1, 1).get(Calendar.DAY_OF_WEEK);
        int monthDate = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month)-1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("CONSOLE", "WEEKDAY = "+weekDay);
        if(weekDay>0){
            for(int i=0; i<weekDay-1; i++){
                calendarAdapter.add(0);
            }
        }
        for(int k=0; k<monthDate; k++){
            calendarAdapter.add(k+1);
        }

    }

    class CalendarAdapter extends BaseAdapter<Integer, CalendarHolder>{

        public CalendarAdapter(@NonNull Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        public void setListHolder(CalendarHolder holder, View view) {
            holder.oneCalendarTitle = view.findViewById(R.id.oneCalendarTitle);
            holder.openCalendarAmount = view.findViewById(R.id.openCalendarAmount);
            holder.differenceCalendarAmount = view.findViewById(R.id.differenceCalendarAmount);
            holder.closeCalendarAmount = view.findViewById(R.id.closeCalendarAmount);
        }

        @Override
        public void setListItem(CalendarHolder holder, Integer integer) {
            if(integer>0){
                holder.oneCalendarTitle.setText(String.valueOf(integer));
            }else{
                holder.oneCalendarTitle.setVisibility(View.INVISIBLE);
            }

            CalendarInfo item = dbHelper.getCalendarInfo(thisYear, thisMonth, String.valueOf(integer));

            if(item==null){
                holder.openCalendarAmount.setVisibility(View.INVISIBLE);
                holder.closeCalendarAmount.setVisibility(View.INVISIBLE);
                holder.differenceCalendarAmount.setVisibility(View.INVISIBLE);
            }else{
                if(item.getOpenCash()>0){
                    holder.openCalendarAmount.setText("오픈 : "+decimalFormat.format(item.getOpenCash()));
                }else{
                    holder.openCalendarAmount.setVisibility(View.INVISIBLE);
                }

                if(item.getCloseCash()>0){
                    holder.closeCalendarAmount.setText("마감 : "+decimalFormat.format(item.getCloseCash()));
                } else{
                    holder.closeCalendarAmount.setVisibility(View.INVISIBLE);
                }

                if(item.getCloseCash()<=0 && item.getOpenCash()<=0){
                    holder.differenceCalendarAmount.setVisibility(View.INVISIBLE);
                }else{
                    int difference = item.getCloseCash() - item.getOpenCash();

                    holder.differenceCalendarAmount.setText("차액 : "+decimalFormat.format(difference));
                    if(difference>=0){
                        holder.differenceCalendarAmount.setTextColor(Color.BLUE);
                    }else{
                        holder.differenceCalendarAmount.setTextColor(Color.RED);
                    }
                }
            }
        }
    }
}

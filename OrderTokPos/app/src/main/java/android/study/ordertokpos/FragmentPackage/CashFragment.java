package android.study.ordertokpos.FragmentPackage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.study.ordertokpos.Back.DBHelper;
import android.study.ordertokpos.InfoPackage.CalendarInfo;
import android.study.ordertokpos.InfoPackage.CashInfo;
import android.study.ordertokpos.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.study.ordertokpos.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CashFragment extends Fragment {
    private MainActivity mainActivity;
    private DBHelper dbHelper;
    private TextView cashDifferenceTextView, cashCloseTextView, cashOpenTextView, cashChangeTitle;
    private EditText fiftyThousandText, tenThousandText, fiveThousandText, oneThousandText, fiveHundredText, oneHundredText, fiftyText, tenText;
    private String thisYear, thisMonth, thisDay;
    private Button cashDoneButton, backCalendarButton;
    private CalendarInfo calendarInfo;
    private CashInfo cashInfo;
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
        dbHelper.close();
    }


    //Fragment 생성
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cash , container, false);
        dbHelper = new DBHelper(mainActivity);


        cashDifferenceTextView = rootView.findViewById(R.id.cashDifferenceTextView);
        cashCloseTextView = rootView.findViewById(R.id.cashCloseTextView);
        cashOpenTextView = rootView.findViewById(R.id.cashOpenTextView);
        cashChangeTitle = rootView.findViewById(R.id.cashChangeTitle);

        cashDoneButton = rootView.findViewById(R.id.cashDoneButton);
        backCalendarButton = rootView.findViewById(R.id.backCalendarButton);

        fiftyThousandText = rootView.findViewById(R.id.fiftyThousandText);
        tenThousandText = rootView.findViewById(R.id.tenThousandText);
        fiveThousandText = rootView.findViewById(R.id.fiveThousandText);
        oneThousandText = rootView.findViewById(R.id.oneThousandText);
        fiveHundredText = rootView.findViewById(R.id.fiveHundredText);
        oneHundredText = rootView.findViewById(R.id.oneHundredText);
        fiftyText = rootView.findViewById(R.id.fiftyText);
        tenText = rootView.findViewById(R.id.tenText);

        cashChangeTitle.setText("오픈 현금 입력");
        cashDoneButton.setText("오픈 입력");

        thisYear = getArguments().getString("thisYear");
        thisMonth = getArguments().getString("thisMonth");
        thisDay = getArguments().getString("thisDay");

        if(thisYear==null || thisMonth==null || thisDay==null){
            mainActivity.onFragmentChange(300000);
        }


        calendarInfo = dbHelper.getCalendarInfo(thisYear, thisMonth, thisDay);
        if(calendarInfo==null){
            cashDifferenceTextView.setText("차액 : 0");
            cashCloseTextView.setText("마감 : 0");
            cashOpenTextView.setText("오픈 : 0");
        } else {
            cashDifferenceTextView.setText("차액 : "+decimalFormat.format(calendarInfo.getCloseCash()-calendarInfo.getOpenCash()));
            cashCloseTextView.setText("마감 : "+decimalFormat.format(calendarInfo.getCloseCash()));
            cashOpenTextView.setText("오픈 : "+decimalFormat.format(calendarInfo.getOpenCash()));
            Log.d("CONSOLE", "DATE : "+calendarInfo.getCalendarDate());
        }

        cashInfo = dbHelper.getCashInfo(thisYear, thisMonth, thisDay, "open");
        if(cashInfo == null){
            fiftyThousandText.setText("0");
            tenThousandText.setText("0");
            fiveThousandText.setText("0");
            oneThousandText.setText("0");
            fiveHundredText.setText("0");
            oneHundredText.setText("0");
            fiftyText.setText("0");
            tenText.setText("0");
        }else{
            fiftyThousandText.setText(String.valueOf(cashInfo.getFiftyThousandCount()));
            tenThousandText.setText(String.valueOf(cashInfo.getTenThousandCount()));
            fiveThousandText.setText(String.valueOf(cashInfo.getFiveThousandCount()));
            oneThousandText.setText(String.valueOf(cashInfo.getOneThousandCount()));
            fiveHundredText.setText(String.valueOf(cashInfo.getFiveHundredCount()));
            oneHundredText.setText(String.valueOf(cashInfo.getOneHundredCount()));
            fiftyText.setText(String.valueOf(cashInfo.getFiftyCount()));
            tenText.setText(String.valueOf(cashInfo.getTenCount()));
        }

        cashCloseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashChangeTitle.setText("마감 현금 입력");
                cashDoneButton.setText("마감 입력");

                cashInfo = dbHelper.getCashInfo(thisYear, thisMonth, thisDay, "close");
                if(cashInfo == null){
                    fiftyThousandText.setText("0");
                    tenThousandText.setText("0");
                    fiveThousandText.setText("0");
                    oneThousandText.setText("0");
                    fiveHundredText.setText("0");
                    oneHundredText.setText("0");
                    fiftyText.setText("0");
                    tenText.setText("0");
                }else{
                    fiftyThousandText.setText(String.valueOf(cashInfo.getFiftyThousandCount()));
                    tenThousandText.setText(String.valueOf(cashInfo.getTenThousandCount()));
                    fiveThousandText.setText(String.valueOf(cashInfo.getFiveThousandCount()));
                    oneThousandText.setText(String.valueOf(cashInfo.getOneThousandCount()));
                    fiveHundredText.setText(String.valueOf(cashInfo.getFiveHundredCount()));
                    oneHundredText.setText(String.valueOf(cashInfo.getOneHundredCount()));
                    fiftyText.setText(String.valueOf(cashInfo.getFiftyCount()));
                    tenText.setText(String.valueOf(cashInfo.getTenCount()));
                }
            }
        });

        cashOpenTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashChangeTitle.setText("오픈 현금 입력");
                cashDoneButton.setText("오픈 입력");

                cashInfo = dbHelper.getCashInfo(thisYear, thisMonth, thisDay, "open");
                if(cashInfo == null){
                    fiftyThousandText.setText("0");
                    tenThousandText.setText("0");
                    fiveThousandText.setText("0");
                    oneThousandText.setText("0");
                    fiveHundredText.setText("0");
                    oneHundredText.setText("0");
                    fiftyText.setText("0");
                    tenText.setText("0");
                }else{
                    fiftyThousandText.setText(String.valueOf(cashInfo.getFiftyThousandCount()));
                    tenThousandText.setText(String.valueOf(cashInfo.getTenThousandCount()));
                    fiveThousandText.setText(String.valueOf(cashInfo.getFiveThousandCount()));
                    oneThousandText.setText(String.valueOf(cashInfo.getOneThousandCount()));
                    fiveHundredText.setText(String.valueOf(cashInfo.getFiveHundredCount()));
                    oneHundredText.setText(String.valueOf(cashInfo.getOneHundredCount()));
                    fiftyText.setText(String.valueOf(cashInfo.getFiftyCount()));
                    tenText.setText(String.valueOf(cashInfo.getTenCount()));
                }
            }
        });


        backCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onFragmentChange(300000);
            }
        });

        cashDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cashDone;
                if(cashDoneButton.getText().toString().contains("오픈")){
                    cashDone = "open";
                }else{
                    cashDone = "close";
                }

                cashInfo = dbHelper.getCashInfo(thisYear, thisMonth, thisDay, cashDone);

                CashInfo newCashInfo = new CashInfo();

                int fiftyThousand = Integer.parseInt(fiftyThousandText.getText().toString());
                int tenThousand = Integer.parseInt(tenThousandText.getText().toString());
                int fiveThousand = Integer.parseInt(fiveThousandText.getText().toString());
                int oneThousand = Integer.parseInt(oneThousandText.getText().toString());
                int fiveHundred = Integer.parseInt(fiveHundredText.getText().toString());
                int oneHundred = Integer.parseInt(oneHundredText.getText().toString());
                int fifty = Integer.parseInt(fiftyText.getText().toString());
                int ten = Integer.parseInt(tenText.getText().toString());

                newCashInfo.setFiftyThousandCount(fiftyThousand);
                newCashInfo.setTenThousandCount(tenThousand);
                newCashInfo.setFiveThousandCount(fiveThousand);
                newCashInfo.setOneThousandCount(oneThousand);
                newCashInfo.setFiveHundredCount(fiveHundred);
                newCashInfo.setOneHundredCount(oneHundred);
                newCashInfo.setFiftyCount(fifty);
                newCashInfo.setTenCount(ten);
                newCashInfo.setCashDate(thisYear+"-"+thisMonth+"-"+thisDay);
                newCashInfo.setCashSave(cashDone);
                if(cashInfo != null){
                    dbHelper.updateCashInfo(newCashInfo);
                }else{
                    dbHelper.insertCashInfo(newCashInfo);
                }

                int totalAmount = 50000*fiftyThousand + 10000*tenThousand + 5000*fiveThousand + 1000*oneThousand + 500*fiveHundred + 100*oneHundred + 50*fifty + 10*ten;

                if(calendarInfo != null){
                    if(cashDone.equals("open")){
                        calendarInfo.setOpenCash(totalAmount);
                        dbHelper.updateCalendarInfo(calendarInfo);
                    }else{
                        calendarInfo.setCloseCash(totalAmount);
                        dbHelper.updateCalendarInfo(calendarInfo);
                    }
                }else{
                    calendarInfo = new CalendarInfo();
                    calendarInfo.setCalendarDate(thisYear+"-"+thisMonth+"-"+thisDay);
                    if(cashDone.equals("open")){
                        calendarInfo.setOpenCash(totalAmount);
                        calendarInfo.setCloseCash(0);
                    }else{
                        calendarInfo.setCloseCash(totalAmount);
                        calendarInfo.setOpenCash(0);
                    }
                    dbHelper.insertCalendarInfo(calendarInfo);
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(CashFragment.this).attach(CashFragment.this).commit();
            }
        });

        return rootView;
    }
}

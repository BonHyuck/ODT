package android.study.ordertok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.study.ordertok.Back.DBHelper;
import android.study.ordertok.Back.SocketReceiveClass;
import android.study.ordertok.InfoPackage.ServerInfo;
import android.study.ordertok.InfoPackage.StoreInfo;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class LanguageActivity extends AppCompatActivity {

    StoreInfo storeInfo;
    ServerInfo serverInfo;
    DBHelper dbHelper;
    Button koreanButton, englishButton, chineseButton, japaneseButton;
    ImageView storeBackGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dbHelper = new DBHelper(LanguageActivity.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        Thread thread = new Thread(new SocketReceiveClass(LanguageActivity.this));
        thread.start();

        storeBackGround = findViewById(R.id.storeBackGround);

        koreanButton = findViewById(R.id.kor_btn);
        englishButton = findViewById(R.id.eng_btn);
        chineseButton = findViewById(R.id.chn_btn);
        japaneseButton = findViewById(R.id.jpn_btn);

        Glide.with(LanguageActivity.this).load(serverInfo.getServerAddress()+storeInfo.getStoreRandom()+"/"+storeInfo.getBackImgPath()).centerCrop().into(storeBackGround);

        koreanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                intent.putExtra("languageNumber",0);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                intent.putExtra("languageNumber",1);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        chineseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                intent.putExtra("languageNumber",2);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        japaneseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                intent.putExtra("languageNumber",3);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}

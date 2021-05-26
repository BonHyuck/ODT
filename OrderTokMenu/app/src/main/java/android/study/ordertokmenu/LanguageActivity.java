package android.study.ordertokmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.Back.SocketReceiveClass;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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

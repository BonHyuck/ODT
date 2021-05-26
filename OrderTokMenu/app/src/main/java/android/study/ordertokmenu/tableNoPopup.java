package android.study.ordertokmenu;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.study.ordertokmenu.Back.DBHelper;
import android.study.ordertokmenu.InfoPackage.ServerInfo;
import android.study.ordertokmenu.InfoPackage.StoreInfo;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class tableNoPopup extends AppCompatActivity {
    Button closeButton, loginButton;
    EditText passwordPopUpText, tableNumberPopUpText;

    StoreInfo storeInfo;
    ServerInfo serverInfo;
    DBHelper dbHelper;

    String receivedTableNumber, receivedCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_no_popup);

        dbHelper = new DBHelper(tableNoPopup.this);
        serverInfo = new ServerInfo();
        storeInfo = dbHelper.getStoreInfo();

        closeButton = findViewById(R.id.closeButton);
        loginButton = findViewById(R.id.loginButton);
        passwordPopUpText = findViewById(R.id.passwordPopUpText);
        tableNumberPopUpText = findViewById(R.id.tableNumberPopUpText);
        tableNumberPopUpText.setTransformationMethod(null);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(tableNoPopup.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String thisIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                receivedCode = passwordPopUpText.getText().toString();
                receivedTableNumber = tableNumberPopUpText.getText().toString();

                if(!storeInfo.getResetCode().equals(receivedCode)){
                    Toast.makeText(tableNoPopup.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if(receivedTableNumber.equals("") || Integer.parseInt(receivedTableNumber)<1){
                    Toast.makeText(tableNoPopup.this, "테이블 번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    ChangeIp changeIp = new ChangeIp();
                    changeIp.execute(serverInfo.getServerAddress()+"orderTokPos/changeIpAddress.php", "storeId="+storeInfo.getStoreId()+"&tableNumber="+storeInfo.getTableNumber()+"&newTableNumber="+receivedTableNumber+"&ipAddress="+thisIpAddress);
                }
            }
        });
    }

    public class ChangeIp extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("done")){
                dbHelper.changeTableNumber(Integer.parseInt(receivedTableNumber));
                dbHelper.deleteAllOrderInfo();
                Intent intent = new Intent(tableNoPopup.this, FirstActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                return new String("Error:" + e.getLocalizedMessage());
            }
        }
    }
}

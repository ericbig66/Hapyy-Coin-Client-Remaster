package com.greeting.happycoin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.hideKB;

public class send extends AppCompatActivity {

    ImageView qrview;//送出用QRcode顯示處
    EditText amount;//送出金額
    Button submit;//送出按鈕
    String code = null;//紅包金鑰
    String ip = null;//IP功能尚在開發階段***
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_send);
        //定義區
        qrview  = findViewById(R.id.QRcode);
        amount = findViewById(R.id.input);
        submit =  findViewById(R.id.Intoshop);
        //設定區
        //點擊確認按鈕動作
        submit.setOnClickListener(v -> {
            //執行資料庫指令
            Redbag redbag = new Redbag();
            redbag.execute();
            hideKB(this);
        });
    }

    //產生QRcode
    public void getCode() {
        BarcodeEncoder encoder = new BarcodeEncoder();//宣告QRcode產生器
//         Log.v("test",acc+"zpek," +amount .getText().toString());
        try{
            //產生QRcode
            Bitmap bit = encoder.encodeBitmap(getUUID(getApplicationContext())+"cj/61l,"+code+"cj/61l,"+ amount.getText().toString()+"cj/61l,"+ip +"cj/61l,"+"C", BarcodeFormat.QR_CODE,1000,1000);
            qrview.setImageBitmap(bit);//將產生的QRcode顯示在QRcode顯示區
        }catch (WriterException e){
            e.printStackTrace();
        }
    }
    //發送紅包資料庫連接
    private class Redbag extends AsyncTask<Void,Void,String> {
        int sum = Integer.parseInt(amount.getText().toString());//紅包金額
        public String uuid = getUUID(getApplicationContext());//取得手機UUID供交易使用
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //連接資料庫
        @Override
        protected String doInBackground(Void... voids) {
            String res = null;//裝載執行結果
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");//抓ip
                rs.next();
                ip = rs.getString(1);
                CallableStatement cstmt = con.prepareCall("{? = call redenvelope_manager(?,?,?,?,?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2,uuid);
                cstmt.setString(3,"C");
                cstmt.setString(4,null);
                cstmt.setString(5,null);
                cstmt.setInt(6,sum);
                cstmt.setString(7,"yee");
                cstmt.setString(8,null);
                cstmt.execute();
                res = cstmt.getString(1);
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            code = result;//接收回傳的紅包金鑰
            Log.v("test", "My ip is : "+ip);
            getCode();//產生QRcode
        }
    }
    //返回主畫面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(send.this,LoginAndRegister.class);
        startActivity(intent);
        finish();
    }

}

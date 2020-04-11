package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class send extends AppCompatActivity {

    ImageView qrview;
    EditText How_much;
    Button check;
    String code;
    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_send);
        qrview  = findViewById(R.id.QRcode);
        How_much = findViewById(R.id.input);
        check =  findViewById(R.id.Intoshop);
        code = null;
        ip= null;
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Redbag redbag = new Redbag();
                redbag.execute();
            }
        });

    }

    public void getCode() {
        BarcodeEncoder encoder = new BarcodeEncoder();
//         Log.v("test",acc+"zpek," +amount .getText().toString());
        try{
            Bitmap bit = encoder.encodeBitmap(getUUID(getApplicationContext())+"cj/61l,"+code+"cj/61l,"+How_much.getText().toString()+"cj/61l,"+ip , BarcodeFormat.QR_CODE,1000,1000);
            qrview.setImageBitmap(bit);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }

    private class Redbag extends AsyncTask<Void,Void,String> {
        String ip = null;
        int sum = Integer.parseInt(How_much.getText().toString());
        public String uuid = getUUID(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = null;
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
            code = result;
            Log.v("test", "My ip is : "+ip);

            getCode();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(send.this,LoginAndRegister.class);
        startActivity(intent);
        finish();
    }
}

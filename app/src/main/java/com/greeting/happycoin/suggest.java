package com.greeting.happycoin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.LoginAndRegister.ver;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.popup;

public class suggest extends AppCompatActivity {
    EditText text ;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suggest);
        text = findViewById(R.id.communication);
        send = findViewById(R.id.submit);
        send.setOnClickListener(v -> {
            Bag bag = new Bag();
            bag.execute();
        });
        SetFontSize();
    }

    private class Bag extends AsyncTask<Void,Void,String> {
        //        String ip = null;
//        int sum = Integer.parseInt(How_much.getText().toString());
//        public String uuid = getUUID(getApplicationContext());
        String Catch;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Catch = text.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = null;
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
//                String result ="";
//                Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");//抓ip
//                rs.next();
//                ip = rs.getString(1);
                CallableStatement cstmt = con.prepareCall("{? = call add_comment(?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, ver);
                cstmt.setString(3, Catch);
                cstmt.execute();
                res = cstmt.getString(1);
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            popup(getApplicationContext(), result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(suggest.this,LoginAndRegister.class);
        startActivity(intent);
        finish();
    }
    //字型大小設定
    private void SetFontSize(){
        TextView tv = findViewById(R.id.textView8);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView textView10 = findViewById(R.id.textView10);
        textView10.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView textView9 = findViewById(R.id.textView9);
        textView9.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        EditText communication = findViewById(R.id.communication);
        communication.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        Button submit = findViewById(R.id.submit);
        submit.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//每日遊戲
    }
}

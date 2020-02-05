package com.greeting.happycoin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.alertRS;
import static com.greeting.happycoin.LoginAndRegister.getPfr;
import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.setAlert;
import static com.greeting.happycoin.LoginAndRegister.setPfr;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;

public class AlterMember extends AppCompatActivity {

    Switch greet;
    Button CD, RD; // changeDevice, ReceiveData
    ImageView CDQR; // chamge Device QRcode
    SurfaceView RDCAM; // Receive Data Camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_member);

        greet = findViewById(R.id.greet);
        //restore the preference of greeting on login page
        greet.setChecked(getPfr("HCgreet",getApplicationContext()));
        //save preference when preference is changed
        greet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setPfr("HCgreet",isChecked, getApplicationContext());
            greet.setText(isChecked?"以暱稱稱呼您":"以姓名稱呼您");
        });

        CD.setOnClickListener(v -> {
            if((setAlert("換機確認", "您是否要將此帳號轉移至另一支手機?", "是", "否", getApplicationContext()))){
                if(alertRS){

                }
            }
        });

    }

    //update personal info and change device *********************
    private class SaveData extends AsyncTask<Void,Void,String> {
        String ip = null;
        public String uuid = getUUID(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"資料儲存中請稍後...",Toast.LENGTH_SHORT).show();
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
                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");
                rs.next();
                ip = rs.getString(1);
                CallableStatement cstmt = con.prepareCall("{? = call auto_login_register(?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2,uuid);
                cstmt.setString(3,ip);
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
            super.onPostExecute(result);
            Intent intent = new Intent(getApplicationContext(), LoginAndRegister.class);
            startActivity(intent);
            finish();
        }
    }


    //override onbackPressed method to reopen menu (Login) page
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SaveData saveData = new SaveData();
        saveData.execute();
    }
}

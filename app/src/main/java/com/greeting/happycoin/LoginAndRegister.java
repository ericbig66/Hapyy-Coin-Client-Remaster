package com.greeting.happycoin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.UUID;

import static com.greeting.happycoin.MainActivity.entryIsRecent;
import static com.greeting.happycoin.MainActivity.isBack;

public class LoginAndRegister extends AppCompatActivity {
//    public static final String url = "jdbc:mysql://140.135.113.196:3360/happycoin";
//    public static final String user = "currency";
//    public static final String pass = "@SAclass";
    //temp database for emergency will be switch back to normal when VM is ready to go (first one failed)
//    public static final String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12327183";
//    public static final String user = "sql12327183";
//    public static final String pass = "4xcYzbRmwN";
    ///////////////////////////////////////////////
//    public static final String url = "jdbc:mysql://65.19.141.67:3306/ericbig6_happycoin?noAccessToProcedureBodies=true";
//    public static final String user = "ericbig6";
//    public static final String pass = "Ericgood0";
    ///////////////////////////////////////////////218.161.48.27:3306
//    public static final String url = "jdbc:mysql://140.135.112.25:3360/happycoin";
//    public static final String url = "jdbc:mysql://218.161.48.27:3306/happycoin?noAccessToProcedureBodies=true";
//    public static final String user = "currency";
//    public static final String pass = "@SAclass";

    ///////////////////////////////////////////////218.161.48.27:3360
    public static final String url = "jdbc:mysql://218.161.48.27:3360/happycoin?noAccessToProcedureBodies=true";
    public static final String user = "currency";
    public static final String pass = "SEclassUmDb@outside";

    TextView wcm;
    CircularImageView profile;
    Intent intent ;
    public static String[] nm = new String[2];
    public static String[] inf = new String[9];
    public static Bitmap pf = null;



    public static final String ver = "0.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_and_register);
        wcm = findViewById(R.id.wcm);
        profile = findViewById(R.id.profile);
        //prevent error
//        Log.v("test", "Your greeting preference is :"+getPfr("HCgreet",getApplicationContext()));

        Login login = new Login();
        login.execute();
    }

    private class Login extends AsyncTask<Void,Void,String>{
        String ip = null;
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
//            Log.v("test","Your result:\n"+result);

            super.onPostExecute(result);
//            Log.v("test","info from mySQL ="+result);
            if(result.contains("sep,")){
                inf = result.split("sep,");
                nm = inf[1].split("nm,");
                nm[0] = nm[0].equals("null")?"":nm[0];
                nm[1] = nm[1].equals("null")?"":nm[1];
                //set greeting sentence
                wcm.setText((getPfr("HCgreet",getApplicationContext())?nm[1]:nm[0])+"您好目前您尚有$"+inf[2]);
                if(!inf[3].equals("null")){ConvertToBitmap();}
                else{profile.setImageResource(R.drawable.df_profile);}
                profile.setRotation(Float.parseFloat(inf[4]));
            }else if(result.equals("註冊成功")){recreate();}
            else{wcm.setText(result);}
        }
    }

//    public void member(View v){
//        Intent intent = new Intent(this,AlterMember.class);
//        startActivity(intent);
//        finish();
//    }
//
//    public void QQ(View v){//送紅包的Qrcode 顯示
//        Intent intent = new Intent(this,send.class);
//        startActivity(intent);
//        finish();
//    }

    public void execute(View v){
        switch (v.getId()){
            case R.id.contact:
                intent = new Intent(LoginAndRegister.this,suggest.class);
                break;
            case R.id.SendRedbag:
                intent = new Intent(LoginAndRegister.this,send.class);
                break;
            case R.id.AlterMember:
                intent = new Intent(LoginAndRegister.this,AlterMember.class);
                break;
            case R.id.GetRedbag:
                intent = new Intent(LoginAndRegister.this,scan.class);
                break;
            case R.id.Intoshop:
                intent = new Intent(LoginAndRegister.this,market.class);
                break;
            case R.id.Enroll:
                intent = new Intent(LoginAndRegister.this,event.class);
                break;
            case R.id.recent:
                entryIsRecent = true;
                intent = new Intent(LoginAndRegister.this,event.class);
                break;
            case R.id.Record:
                intent = new Intent(LoginAndRegister.this,record.class);
                break;
        }
        startActivity(intent);
        finish();
    }
    public void ConvertToBitmap(){
        try{
            byte[] imageBytes = Base64.decode(inf[3], Base64.DEFAULT);
            pf = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profile.setImageBitmap(pf);
        }catch (Exception e){
            //Log.v("test","error = "+e.toString());
        }

    }
//**************all public method are here**************
    //get uuid
    public static String getUUID(Context context) {
        UUID uuid = null;
        final String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidID)) {
                uuid = UUID.nameUUIDFromBytes(androidID.getBytes(StandardCharsets.UTF_8));
            } else {
                @SuppressLint("MissingPermission") final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes(StandardCharsets.UTF_8)) : UUID.randomUUID();
//                Log.v("test","info: UUID has been generated");
            }
        }catch (Exception e){
//            Log.v("test","Error when getting UUID:\n"+e.toString());
        }
        return uuid.toString();
    }

    //access preferences
    //set preference key and value
    public static void setPfr(String key, boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    //get preference key or set default value
    public static boolean getPfr(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }

    public static void popup(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public void closekeybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBack = true;
        finish();
    }
    //**************all public method are above**************
}

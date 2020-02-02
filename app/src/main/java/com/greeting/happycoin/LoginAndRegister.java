package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;

public class LoginAndRegister extends AppCompatActivity {
    public static final String url = "jdbc:mysql://140.135.113.196:3360/happycoin";
    public static final String user = "currency";
    public static final String pass = "@SAclass";
    TextView wcm;

//   public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_and_register);
        wcm = findViewById(R.id.wcm);

//        preferences = this.getPreferences(Context.MODE_PRIVATE);

        Login login = new Login();
        login.execute();
    }

    private class Login extends AsyncTask<Void,Void,String>{
        String ip = null;
        String uuid = getUUID(getApplicationContext());

//        boolean greeting = preferences.getBoolean(String.valueOf(R.bool.greet_pref),true);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get ip
//            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//            ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
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
            String[] inf = new String[4];
            String[] nm = new String[2];
            super.onPostExecute(result);
            if(result.contains("sep,")){
                inf = result.split("sep,");
                nm = inf[1].split("nm,");
                nm[0] = nm[0].equals("null")?"":nm[0];
                nm[1] = nm[1].equals("null")?"":nm[1];
//                Log.v("test",(getResources().getBoolean(R.bool.greet_pref))+"");
//                Log.v("test",greeting+"");

//                wcm.setText(((getResources().getBoolean(R.bool.greet_pref))?nm[0]:nm[1])+"您好目前您尚有$"+inf[2]);
//                wcm.setText((greeting?nm[0]:nm[1])+"您好目前您尚有$"+inf[2]);
                wcm.setText((getPfr("HCgreet",getApplicationContext())?nm[1]:nm[0])+"您好目前您尚有$"+inf[2]);
            }else{wcm.setText(result);}
//            wcm.setText(result);
        }
    }

    //get uuid
    public String getUUID(Context context) {
        UUID uuid = null;
        final String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidID)) {
                uuid = UUID.nameUUIDFromBytes(androidID.getBytes("utf8"));
            } else {
                @SuppressLint("MissingPermission") final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                Log.v("test","info: UUID has been generated");
            }
        }catch (Exception e){
            Log.v("test","Error when getting UUID:\n"+e.toString());
        }
        return uuid.toString();
    }

    public void member(View v){
        Intent intent = new Intent(this,AlterMember.class);
        startActivity(intent);
        finish();
    }

    //access preferences
    public static void setPfr(String key, boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPfr(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }
}

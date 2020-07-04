//package com.greeting.happycoin;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.telephony.TelephonyManager;
//import android.util.Base64;
//import android.view.View;
//import android.widget.GridLayout;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.mikhaellopez.circularimageview.CircularImageView;
//
//import java.nio.charset.StandardCharsets;
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.sql.Types;
//import java.util.UUID;
//
//import static com.greeting.happycoin.MainActivity.FONTsize;
//import static com.greeting.happycoin.MainActivity.LoggedIn;
//import static com.greeting.happycoin.MainActivity.entryIsRecent;
//import static com.greeting.happycoin.MainActivity.getPfr;
//import static com.greeting.happycoin.MainActivity.isBack;
//import static com.greeting.happycoin.MainActivity.lv;
//import static com.greeting.happycoin.MainActivity.popup;
//
////***表示待檢查
//public class LoginAndRegister extends AppCompatActivity {
//   //連線資料
//    public static final String url = "jdbc:mysql://140.135.113.36:6033/happycoin?noAccessToProcedureBodies=true&useUnicode=yes&characterEncoding=UTF-8";
//    public static final String user = "currency";
//    public static final String pass = "SEclassUmDb@outside";
//    LinearLayout canvas;//底層(連線出錯時當作重試按鈕)
//    boolean ConnectionError = false;
//    TextView wcm;//歡迎訊息
//    CircularImageView profile;//頭像容器
//    Intent intent ;//切換畫面使用的事件
//    public static String[] nm = new String[2];//姓名[0]暱稱[1]
//    //姓名+暱稱[0] 餘額[1] 頭像(base64)[2] 頭像角度[3] 生日[4] 性別[5] 送貨地址[6] 建議***[7]
//    public static String[] inf = new String[9];//登入成功時回傳的資料^^^
//    public static Bitmap pf = null;//頭像圖片
//
//    public static final String ver = "0.0.1";//版本號***
//    GridLayout menu_btn;//主功能區按鈕區
//    LinearLayout submenu;//子功能按鈕區
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_login_and_register);
//        //定義區(對應主畫面物件)
//        menu_btn = findViewById(R.id.menu_btn);
//        submenu = findViewById(R.id.submenu);
//        wcm = findViewById(R.id.wcm);
//        profile = findViewById(R.id.profile);
//        canvas = findViewById(R.id.canvas);
//        //設定區(登入成功前設定操作區為隱形)
//        menu_btn.setVisibility(View.INVISIBLE);
//        submenu.setVisibility(View.INVISIBLE);
//        canvas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ConnectionError){
//                    ConnectionError = false;
//                    popup(getApplicationContext(),"登入中，請稍後...");
//                    recreate();
//                }
//            }
//        });
//        //呼叫登入動作
//        Login login = new Login();
//        login.execute();
//    }
//    //登入
//    private class Login extends AsyncTask<Void,Void,String>{
//        String ip = null;//須查詢ip，目前正在開發
//        public String uuid = getUUID(getApplicationContext());//自動取得此手機之UUID
//        @Override
//        protected void onPreExecute() {//執行非同步(背景)工作前
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {//執行非同步工作(登入)
//            String res = null;//資料庫回傳結果容器
//            try{
//                //連接資料庫
//                Class.forName("com.mysql.jdbc.Driver");
//                Connection con = DriverManager.getConnection(url, user, pass);
//                //建立查詢
//                String result ="";
//                Statement st = con.createStatement();
//                //查詢連線IP***
//                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");
//                rs.next();
//                ip = rs.getString(1);//將ip回填到ip變數內
//                //執行MySQL function(自動註冊/登入)
//                CallableStatement cstmt = con.prepareCall("{? = call auto_login_register(?,?,?)}");
//                cstmt.registerOutParameter(1, Types.VARCHAR);
//                cstmt.setString(2,uuid);
//                cstmt.setString(3,ip);
//                cstmt.setInt(4,LoggedIn);
//                cstmt.execute();
//                res = cstmt.getString(1);//將結果裝入res
//            }catch (Exception e){//錯誤攔截
//                e.printStackTrace();
//                res = e.toString();
//            }
//            return res;//回傳給結果處理器
//        }
//
//        @Override
//        protected void onPostExecute(String result) {//結果處理器
////            Log.v("test","Your result:\n"+result);
//            super.onPostExecute(result);
////            Log.v("test","info from mySQL ="+result);
//            //處理資料庫回傳結果(將資料切分至陣列內)
//            if(result.contains("sep,")){
//                lv("sep detected!");
//                inf = result.split("sep,");
//                lv("LENTH of SEPERATER= "+inf.length);
//                nm = inf[1].split("nm,");
//                nm[0] = nm[0].equals("null")?"":nm[0];
//                nm[1] = nm[1].equals("null")?"":nm[1];
//                FONTsize = Integer.parseInt(inf[8]);
//                //設定歡迎訊息
//                wcm.setText((getPfr("HCgreet",getApplicationContext())?nm[1]:nm[0])+"您好\n目前您有$"+inf[2]);
//                if(!inf[3].equals("null")){ConvertToBitmap();}
//                else{profile.setImageResource(R.drawable.df_profile);}
//                profile.setRotation(Float.parseFloat(inf[4]));
//                menu_btn.setVisibility(View.VISIBLE);
//                submenu.setVisibility(View.VISIBLE);
//                if (inf[9].contains("簽到成功!")){popup(getApplicationContext(), "簽到成功!");}
//                else if (inf[9].contains("本日已完成簽到任務!")){popup(getApplicationContext(), "本日已完成簽到任務!");}
//                LoggedIn = 1;
//            }else if(result.equals("註冊成功")){//如註冊成功將自動重新登入
//                recreate();
//                menu_btn.setVisibility(View.VISIBLE);
//                submenu.setVisibility(View.VISIBLE);
//            }
//            else{//否則如果有列出帳戶金額則表示可用
//                lv("sep isn't present");
//                if (result.contains("$")){
//                    menu_btn.setVisibility(View.VISIBLE);
//                    submenu.setVisibility(View.VISIBLE);
//                    wcm.setText(result);
//                }else{
//                    lv("error found");
//                    menu_btn.setVisibility(View.INVISIBLE);
//                    submenu.setVisibility(View.INVISIBLE);
//                    wcm.setText("目前無法與伺服器連線\n請檢查您的網路連線!!\n或著\n您可以輕觸螢幕嘗試重新連線");
//                    ConnectionError = true;
//                }
//            }
//        }
//    }
//    //當主畫面之任意按鈕被點選時
//    public void execute(View v){//判斷所按之開關並重新引導到相對應頁面
//        switch (v.getId()){
//            case R.id.contact:
//                intent = new Intent(LoginAndRegister.this,suggest.class);
//                break;
//            case R.id.SendRedbag:
//                intent = new Intent(LoginAndRegister.this,send.class);
//                break;
//            case R.id.AlterMember:
//            case R.id.profile:
//                intent = new Intent(LoginAndRegister.this,AlterMember.class);
//                break;
//            case R.id.GetRedbag:
//                intent = new Intent(LoginAndRegister.this,scan.class);
//                break;
//            case R.id.Intoshop:
//                intent = new Intent(LoginAndRegister.this,market.class);
//                break;
//            case R.id.Enroll:
//                intent = new Intent(LoginAndRegister.this,event.class);
//                break;
//            case R.id.recent:
//                entryIsRecent = true;
//                intent = new Intent(LoginAndRegister.this,event.class);
//                break;
//            case R.id.Record:
//                intent = new Intent(LoginAndRegister.this,record.class);
//                break;
//            case R.id.game:
//                intent = new Intent(LoginAndRegister.this,game.class);
//                break;
//            case R.id.rate:
////                intent = new Intent(LoginAndRegister.this,product_rating.class);
//                intent = new Intent(LoginAndRegister.this,CommentCenter.class);
//                break;
//        }
//        startActivity(intent);
//        finish();
//    }
//    //將Base64還原為點陣圖***需修改
//    public void ConvertToBitmap(){
//        try{
//            byte[] imageBytes = Base64.decode(inf[3], Base64.DEFAULT);
//            pf = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            profile.setImageBitmap(pf);
//        }catch (Exception e){
//            //Log.v("test","error = "+e.toString());
//        }
//    }
////**************all public method are here**************全區待移動
//    //取得客戶端裝置之UUID***待移位至固定頁面
//    public static String getUUID(Context context) {
//        UUID uuid = null;
//        final String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        try {
//            if (!"9774d56d682e549c".equals(androidID)) {
//                uuid = UUID.nameUUIDFromBytes(androidID.getBytes(StandardCharsets.UTF_8));
//            } else {
//                @SuppressLint("MissingPermission") final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes(StandardCharsets.UTF_8)) : UUID.randomUUID();
////                Log.v("test","info: UUID has been generated");
//            }
//        }catch (Exception e){
////            Log.v("test","Error when getting UUID:\n"+e.toString());
//        }
//        return uuid.toString();
//    }
//
//    //設定按下返回鍵的動作
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        isBack = true;//手動按下返回==>設定手動返回變數為true
//        finish();
//    }
//    //**************all public method are above**************
//}

//newfile
package com.greeting.happycoin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;

import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.LoggedIn;
import static com.greeting.happycoin.MainActivity.entryIsRecent;
import static com.greeting.happycoin.MainActivity.getPfr;
import static com.greeting.happycoin.MainActivity.isBack;
import static com.greeting.happycoin.MainActivity.lv;
import static com.greeting.happycoin.MainActivity.popup;

//***表示待檢查
public class LoginAndRegister extends AppCompatActivity {
    //連線資料
    public static final String url = "jdbc:mysql://140.135.113.36:6033/happycoin?noAccessToProcedureBodies=true&useUnicode=yes&characterEncoding=UTF-8";
    public static final String user = "currency";
    public static final String pass = "SEclassUmDb@outside";
    LinearLayout canvas;//底層(連線出錯時當作重試按鈕)
    boolean ConnectionError = false;
    TextView wcm;//歡迎訊息
    CircularImageView profile;//頭像容器
    Intent intent ;//切換畫面使用的事件
    public static String[] nm = new String[2];//姓名[0]暱稱[1]
    //姓名+暱稱[0] 餘額[1] 頭像(base64)[2] 頭像角度[3] 生日[4] 性別[5] 送貨地址[6] 建議***[7]
    public static String[] inf = new String[9];//登入成功時回傳的資料^^^
    public static Bitmap pf = null;//頭像圖片

    public static final String ver = "0.0.1";//版本號***
    LinearLayout menu_btn;//主功能區按鈕區
    LinearLayout submenu;//子功能按鈕區
    LinearLayout profile_frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_and_register);
        //定義區(對應主畫面物件)
        menu_btn = findViewById(R.id.menu_btn);
        submenu = findViewById(R.id.submenu);
        wcm = findViewById(R.id.wcm);
        profile = findViewById(R.id.profile);
        canvas = findViewById(R.id.canvas);
        //設定區(登入成功前設定操作區為隱形)
        menu_btn.setVisibility(View.INVISIBLE);
        submenu.setVisibility(View.INVISIBLE);
        profile_frame = findViewById(R.id.profile_frame);
        canvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionError){
                    ConnectionError = false;
                    popup(getApplicationContext(),"登入中，請稍後...");
                    recreate();
                }
            }
        });
        //呼叫登入動作
        Login login = new Login();
        login.execute();
    }
    //登入
    private class Login extends AsyncTask<Void,Void,String>{
        String ip = null;//須查詢ip，目前正在開發
        public String uuid = getUUID(getApplicationContext());//自動取得此手機之UUID
        @Override
        protected void onPreExecute() {//執行非同步(背景)工作前
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {//執行非同步工作(登入)
            String res = null;//資料庫回傳結果容器
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                Statement st = con.createStatement();
                //查詢連線IP***
                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");
                rs.next();
                ip = rs.getString(1);//將ip回填到ip變數內
                //執行MySQL function(自動註冊/登入)
                CallableStatement cstmt = con.prepareCall("{? = call auto_login_register(?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2,uuid);
                cstmt.setString(3,ip);
                cstmt.setInt(4,LoggedIn);
                cstmt.execute();
                res = cstmt.getString(1);//將結果裝入res
            }catch (Exception e){//錯誤攔截
                e.printStackTrace();
                res = e.toString();
            }
            return res;//回傳給結果處理器
        }

        @Override
        protected void onPostExecute(String result) {//結果處理器
//            Log.v("test","Your result:\n"+result);
            super.onPostExecute(result);
//            Log.v("test","info from mySQL ="+result);
            //處理資料庫回傳結果(將資料切分至陣列內)
            if(result.contains("sep,")){
                lv("sep detected!");
                inf = result.split("sep,");
                lv("LENTH of SEPERATER= "+inf.length);
                nm = inf[1].split("nm,");
                nm[0] = nm[0].equals("null")?"":nm[0];
                nm[1] = nm[1].equals("null")?"":nm[1];
                FONTsize = Integer.parseInt(inf[8]);
                //設定歡迎訊息
                wcm.setText((getPfr("HCgreet",getApplicationContext())?nm[1]:nm[0])+"您好\n目前您有$"+inf[2]);
                if(!inf[3].equals("null")){ConvertToBitmap();}
                else{profile.setImageResource(R.drawable.df_profile);}
                profile.setRotation(Float.parseFloat(inf[4]));
                menu_btn.setVisibility(View.VISIBLE);
                submenu.setVisibility(View.VISIBLE);
                if (inf[9].contains("簽到成功!")){popup(getApplicationContext(), "簽到成功!");}
                else if (inf[9].contains("本日已完成簽到任務!")){popup(getApplicationContext(), "本日已完成簽到任務!");}
                LoggedIn = 1;
            }else if(result.equals("註冊成功")){//如註冊成功將自動重新登入
                recreate();
                menu_btn.setVisibility(View.VISIBLE);
                submenu.setVisibility(View.VISIBLE);
            }
            else{//否則如果有列出帳戶金額則表示可用
                lv("sep isn't present");
                if (result.contains("$")){
                    menu_btn.setVisibility(View.VISIBLE);
                    submenu.setVisibility(View.VISIBLE);
                    profile_frame.setVisibility(View.VISIBLE);
                    wcm.setText(result);
                }else{
                    lv("error found");
                    menu_btn.setVisibility(View.GONE);
                    submenu.setVisibility(View.GONE);
                    profile_frame.setVisibility(View.GONE);
                    wcm.setText("目前無法與伺服器連線\n請檢查您的網路連線!!\n或著您可以輕觸螢幕嘗試重新連線");
                    ConnectionError = true;
                }
            }
        }
    }
    //當主畫面之任意按鈕被點選時
    public void execute(View v){//判斷所按之開關並重新引導到相對應頁面
        switch (v.getId()){
            case R.id.contact:
                intent = new Intent(LoginAndRegister.this,suggest.class);
                break;
            case R.id.send_or_receive:
                intent = new Intent();
                intent.setAction("send_or_receive");
                intent = Intent.createChooser(intent, "請選擇您欲使用的項目");
                break;
            case R.id.profile:
                intent = new Intent(LoginAndRegister.this,AlterMember.class);
                break;
            case R.id.shop_or_activity:
                intent = new Intent();
                intent.setAction("shop_or_activity");
                intent = Intent.createChooser(intent, "請選擇您欲使用的項目");
                break;
            case R.id.record_or_rate:
                intent = new Intent();
                intent.setAction("record_or_rate");
                intent = Intent.createChooser(intent, "請選擇您欲使用的項目");
                break;
            case R.id.game:
                intent = new Intent(LoginAndRegister.this,game.class);
                break;
            case R.id.recent:
                entryIsRecent = true;
                intent = new Intent(LoginAndRegister.this,event.class);
                break;
        }
        startActivity(intent);
        finish();
    }
    //將Base64還原為點陣圖***需修改
    public void ConvertToBitmap(){
        try{
            byte[] imageBytes = Base64.decode(inf[3], Base64.DEFAULT);
            pf = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profile.setImageBitmap(pf);
        }catch (Exception e){
            //Log.v("test","error = "+e.toString());
        }
    }
    //**************all public method are here**************全區待移動
    //取得客戶端裝置之UUID***待移位至固定頁面
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

    //設定按下返回鍵的動作
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBack = true;//手動按下返回==>設定手動返回變數為true
        finish();
    }
    //**************all public method are above**************
}

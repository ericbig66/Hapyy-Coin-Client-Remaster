package com.greeting.happycoin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //賣場資料(market)
    public static ArrayList<String> PID = new ArrayList<>();      //商品編號
    public static ArrayList<String> Pname = new ArrayList<>();    //品名
    public static ArrayList<Integer> Pprice = new ArrayList<>();  //價格
    public static ArrayList<Integer> Pamount = new ArrayList<>(); //庫存數
    public static ArrayList<String> Vendor = new ArrayList<>();   //廠商
    public static ArrayList<String> PIMG = new ArrayList<>();     //商品圖
    public static ArrayList<String> Pdescribtion = new ArrayList<>();  //商品說明
    public static int BuyId = -1;                                 //被點選之商品位於清單之第幾項
    //活動資料
    public static ArrayList<String> Aid = new ArrayList<>();           //活動編號
    public static ArrayList<String> Avendor = new ArrayList<>();       //主辦場商
    public static ArrayList<String> Aname = new ArrayList<>();         //活動名稱
    public static ArrayList<String> Actpic = new ArrayList<>();        //活動封面照
    public static ArrayList<Date> AactDate = new ArrayList<>();        //活動開始日期
    public static ArrayList<Date> AactEnd = new ArrayList<>();         //活動結束時間
    public static ArrayList<Date> Astart_date = new ArrayList<>();     //開始報名
    public static ArrayList<Date> Adeadline_date = new ArrayList<>();  //報名截止
    public static ArrayList<Date> AsignStart = new ArrayList<>();      //開始簽到
    public static ArrayList<Date> AsignEnd = new ArrayList<>();        //簽到截止
    public static ArrayList<Integer> Aamount = new ArrayList<>();      //活動名額
    public static ArrayList<Integer> Areward = new ArrayList<>();      //回饋獎金
    public static ArrayList<Integer> AamountLeft = new ArrayList<>();  //剩餘名額
    public static ArrayList<String> Adesc = new ArrayList<>();         //活動說明
    public static ArrayList<String> attended = new ArrayList<>();      //該使用者已報名之活動
    public static int  EventId=-1;                                     //被點選之活動位於清單之第幾項

    public static ArrayList<Integer> ASerial = new ArrayList<>();//活動評價 交易序號
    public static ArrayList<Integer> PSerial = new ArrayList<>();// 產品評價 交易序號
    public static ArrayList<String> ARecDate = new ArrayList<>();// 簽到時間
    public static ArrayList<String> PRecDate = new ArrayList<>();// 購買日期
    public static ArrayList<Integer> ARating = new ArrayList<>();//活動評價
    public static ArrayList<String> AComment = new ArrayList<>();//活動評語
    public static ArrayList<Integer> PRating = new ArrayList<>();//商品評價
    public static ArrayList<String> PComment = new ArrayList<>();//商品評語

    public static ArrayList<String> HAid = new ArrayList<>();           //(歷史)活動編號
    public static ArrayList<String> HAname = new ArrayList<>();         //(歷史)活動名稱
    public static ArrayList<String> HActpic = new ArrayList<>();        //(歷史)活動封面照
    public static ArrayList<Date> HAactDate = new ArrayList<>();        //(歷史)活動開始日期
    public static ArrayList<Integer> HAreward = new ArrayList<>();      //(歷史)回饋獎金

    public static boolean entryIsRecent = false;//是否透過近期報名進入活動列表

    public static boolean isBack = false;//是否為手動返回

    public static int LoggedIn = 0;//是否已經登入

    public static int FONTsize =20;//字型大小

    public static int PreviousRated = 0;//待會用的到的頁面記憶變數***********************

    @Override
    //由其他頁面返回時
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,LoginAndRegister.class);//預備返回之通道
        if(!isBack)//自動返回
            startActivity(intent);//回到主畫面
        else//手動返回
            isBack = false;//重設為自動返回模式==>由最近應用可以快速重啟
            finish();//退出應用程式
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,LoginAndRegister.class);//預備返回之通道
        if(!isBack)//自動返回
            startActivity(intent);//回到主畫面
        else//手動返回
            isBack = false;//重設為自動返回模式==>由最近應用可以快速重啟
            finish();//退出應用程式

    }
    //簡化Log.v語法==>lv("訊息內容");
    public static void lv(String s){
        Log.v("test",s);
    }

    //新版隱藏鍵盤方式***舊版需被替換
    public static void hideKB(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //簡化的toast提示訊息==>popupL(getApplicationContext(),"訊息內容");==>長時間顯示
    public static void popupL(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    //公用函式

    //設定偏好開關值及其偏好項目名稱
    public static void setPfr(String key, boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    //設定偏好預設值或取得偏好設定編號
    public static boolean getPfr(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }
    //簡化的toast提示訊息==>popup(getApplicationContext(),"訊息內容");
    public static void popup(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static int DP(float dp){//寬度單位轉換器(設定值為DP)
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }
}

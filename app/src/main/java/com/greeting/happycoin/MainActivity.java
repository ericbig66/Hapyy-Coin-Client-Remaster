package com.greeting.happycoin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
    public static ArrayList<String> happypi = new ArrayList<>();  //商品說明
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

    public static boolean entryIsRecent = false;//是否透過近期報名進入活動列表

    public static boolean isBack = false;//是否為手動返回

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
}

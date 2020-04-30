package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<String> PID = new ArrayList<>();    //他是麻可特的東西
    public static ArrayList<String> Pname = new ArrayList<>();
    public static ArrayList<Integer> Pprice = new ArrayList<>();
    public static ArrayList<Integer> Pamount = new ArrayList<>();
    public static ArrayList<String> Vendor = new ArrayList<>();
    public static ArrayList<String> PIMG = new ArrayList<>();
    public static ArrayList<String> happypi = new ArrayList<>();
    public static int BuyId = -1;

    public static ArrayList<String> Aid = new ArrayList<>();  //event
    public static ArrayList<String> Avendor = new ArrayList<>();
    public static ArrayList<String> Aname = new ArrayList<>();
    public static ArrayList<String> Actpic = new ArrayList<>();
    public static ArrayList<Date> AactDate = new ArrayList<>();
    public static ArrayList<Date> AactEnd = new ArrayList<>();
    public static ArrayList<Date> Astart_date = new ArrayList<>();
    public static ArrayList<Date> Adeadline_date = new ArrayList<>();
    public static ArrayList<Date> AsignStart = new ArrayList<>();
    public static ArrayList<Date> AsignEnd = new ArrayList<>();
    public static ArrayList<Integer> Aamount = new ArrayList<>();
    public static ArrayList<Integer> Areward = new ArrayList<>();
    public static ArrayList<Integer> AamountLeft = new ArrayList<>();
    public static ArrayList<String> Adesc = new ArrayList<>(); //資料庫無
    public static ArrayList<String> attended = new ArrayList<>();
    public static int  EventId=-1;

    public static boolean entryIsRecent = false;

    public static boolean isBack = false;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,LoginAndRegister.class);
        if(!isBack)
            startActivity(intent);
        else
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,LoginAndRegister.class);
        if(!isBack)
            startActivity(intent);
        else
            finish();

    }
}

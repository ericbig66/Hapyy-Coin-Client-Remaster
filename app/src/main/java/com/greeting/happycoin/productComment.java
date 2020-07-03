package com.greeting.happycoin;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.BuyId;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.lv;
import static com.greeting.happycoin.MainActivity.popup;

public class productComment extends AppCompatActivity {
    LinearLayout ll;
    ArrayList<Date> RateDate = new ArrayList<>();
    ArrayList<Float> score = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_comment);
        ll = findViewById(R.id.ll);
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute();
    }

    public void render(int length){//傳入長度
        for (int i = 0 ; i<length ; i++){
            generate(i);
        }
    }

    public void generate(final int ID){
        //最外層卡片容器
        LinearLayout frame = new LinearLayout(this);
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DP(150)
        );
        frame.setPadding(DP(15),DP(15),DP(15),DP(15));
        framep.setMargins(0,0,0,DP(20));
        frame.setOrientation(LinearLayout.VERTICAL);
        frame.setLayoutParams(framep);
        frame.setBackgroundColor(Color.parseColor("#D1FFDE"));

        //評價星+購買日期容器
        LinearLayout star_container = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams star_container_pram = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                DP(20)
        );
        star_container.setLayoutParams(star_container_pram);

        //評價星
        RatingBar stars = new RatingBar(this,null,android.R.attr.ratingBarStyleSmall);
        stars.setIsIndicator(true);
        stars.setNumStars(5);
        stars.setStepSize(0.1f);
        stars.setRating(score.get(ID));


        //評價日期
        TextView rate_date = new TextView(getApplicationContext());
        rate_date.setText(RateDate.get(ID).toString());
        rate_date.setTextSize(16f);
        rate_date.setGravity(Gravity.CENTER_VERTICAL);

        //評價內容
        TextView rate_text = new TextView(getApplicationContext());
        rate_text.setText(content.get(ID));
        rate_text.setTextSize(18f);
        LinearLayout.LayoutParams rate_text_pram = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DP(100)
                );

        /**容器結構
         * ll
         *   frame
         *      star_container
         *          stars
         *          rate_date
         *      rate_text
         */

        star_container.addView(stars);
        star_container.addView(rate_date);
        frame.addView(star_container);
        frame.addView(rate_text);
        ll.addView(frame);
    }
    public static int DP(float dp){//寬度單位轉換器(設定值為DP)
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }
    //由資料庫取出交易資料
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
//            Toast.makeText(getActivity(),"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //開始取得資料
        @Override
        protected String doInBackground(String... strings) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                //String result = "對方帳戶\t交易\t金額\t餘額\n";
                Statement st = con.createStatement();
//                Log.v("test","select productName, price, amount, sellDate from sellhistory where client ='"+acc+"'");
                ResultSet rs = st.executeQuery("SELECT date, rating, msg from sell_record where rating>0 and LENGTH(msg)>0 and PID = '"+PID.get(BuyId)+"'");
                //將查詢結果裝入陣列
                while(rs.next()){
                    RateDate.add(rs.getDate(1));
                    score.add(rs.getFloat(2));
                    content.add(rs.getString(3).replace("-","/"));
                }
                return "完成";
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        //完成查詢後
        protected void onPostExecute(String result) {
            if (result.equals("完成")){
                render(RateDate.size());
            }else{
                popup(getApplicationContext(),"發生錯誤，請稍後重試");
                lv(result);
            }

        }

    }
}























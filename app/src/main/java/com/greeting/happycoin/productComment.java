package com.greeting.happycoin;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.lv;
import static com.greeting.happycoin.MainActivity.popup;


public class productComment extends AppCompatActivity {
    LinearLayout ll;
    ArrayList<Date> RateDate = new ArrayList<>();
    ArrayList<Float> score = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    ProgressBar one;
    ProgressBar two;
    ProgressBar three;
    ProgressBar four;
    ProgressBar five;
    TextView average_score;
    RatingBar average_star;
    TextView rated;
    float [] score_distribute = {0,0,0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_comment);
        ll = findViewById(R.id.ll);
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute();
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        average_score = findViewById(R.id.average_score);
        average_star = findViewById(R.id.average_star);
        rated = findViewById(R.id.rated);
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
        rate_date.setText(RateDate.get(ID).toString().replace("-","/"));
        rate_date.setTextSize(14f);
        rate_date.setGravity(Gravity.CENTER_VERTICAL);

        //評價內容
        TextView rate_text = new TextView(getApplicationContext());
        rate_text.setText(content.get(ID));
        rate_text.setTextSize(FONTsize);
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
                //將評價資料裝入陣列
                while(rs.next()){
                    RateDate.add(rs.getDate(1));
                    score.add(rs.getFloat(2));
                    content.add(rs.getString(3));
                }
                //取得計算平均資料
                for(int i = 0 ; i<5 ; i++){
                    rs = st.executeQuery("select count(rating) FROM sell_record WHERE rating = "+(i+1)+" AND PID = '"+PID.get(BuyId)+"' AND VID IN (select VID from vendor where name = '"+Vendor.get(BuyId)+"')");
                    rs.next();
                    score_distribute[i]=rs.getFloat(1);
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
            lv("★"+score_distribute[0]+"\n★★"+score_distribute[1]+"\n★★★"+score_distribute[2]+"\n★★★★"+score_distribute[3]+"\n★★★★★"+score_distribute[4]);
            if (result.equals("完成")){
                render(RateDate.size());
                int total_rated = (int)(score_distribute[0]+score_distribute[1]+score_distribute[2]+score_distribute[3]+score_distribute[4]);
                rated.setText(""+total_rated);
                String avsc = "";
                if(total_rated>0){
                    float average = ((score_distribute[0]+2*score_distribute[1]+3*score_distribute[2])+4*score_distribute[3]+5*score_distribute[4])/(score_distribute[0]+score_distribute[1]+score_distribute[2]+score_distribute[3]+score_distribute[4]);
                    if((""+average).length()>3){
                       avsc = (""+average).substring(0,3);
                    }else{avsc = ""+average;}

                    average_score.setText(""+avsc);
                    average_star.setRating(average);
                    one.setProgress((int)score_distribute[0]);
                    two.setProgress((int)score_distribute[1]);
                    three.setProgress((int)score_distribute[2]);
                    four.setProgress((int)score_distribute[3]);
                    five.setProgress((int)score_distribute[4]);
                }
            }else{
                popup(getApplicationContext(),"發生錯誤，請稍後重試");
                lv(result);
            }

        }

    }
}























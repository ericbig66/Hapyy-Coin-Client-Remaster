package com.greeting.happycoin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
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
import static com.greeting.happycoin.MainActivity.EventId;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.HAid;
import static com.greeting.happycoin.MainActivity.HAname;
import static com.greeting.happycoin.MainActivity.lv;
import static com.greeting.happycoin.MainActivity.popup;

public class activity_feedback_detail extends AppCompatActivity {
    LinearLayout ll;
    TextView activityName;
    ImageView actpic;
    TextView host;
    TextView activityDescription;
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
    String[] setUI ={"","",""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_feedback_detail);
        ll = findViewById(R.id.ll);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        average_score = findViewById(R.id.average_score);
        average_star = findViewById(R.id.average_star);
        rated = findViewById(R.id.rated);
        activityName = findViewById(R.id.activityName);
        actpic = findViewById(R.id.actpic);
        host = findViewById(R.id.host);
        activityDescription = findViewById(R.id.activityDescription);
        lv("HAname.length = "+HAname.size());
        activityName.setText(HAname.get(EventId));
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute();
        SetFontSize();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(activity_feedback_detail.this, CommentCenter.class);
        startActivity(intent);
        finish();
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
//        TextView rate_date = new TextView(getApplicationContext());
//        rate_date.setText(RateDate.get(ID).toString());
//        rate_date.setTextSize(16f);
//        rate_date.setGravity(Gravity.CENTER_VERTICAL);

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
//        star_container.addView(rate_date);
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
                ResultSet rs = st.executeQuery("SELECT rating, msg from application_form where rating>0 and LENGTH(msg)>0 and activityID = "+HAid.get(EventId));
                //將評價資料裝入陣列
                while(rs.next()){
//                    RateDate.add(rs.getDate(1));
                    score.add(rs.getFloat(1));
                    content.add(rs.getString(2));
                }
                //取得活動資料
                rs = st.executeQuery("SELECT vendor.name, activity.actPic, activity.actDesc FROM activity, vendor WHERE id ="+ HAid.get(EventId)+" and vendor.VID = activity.HostId");
                rs.next();
//                host.setText(rs.getString(1));
//                activityDescription.setText(rs.getString(3)==null?"":rs.getString(3));
//                tmpimg = rs.getString(2);
//                  SetUI(rs.getString(1),rs.getString(2),rs.getString(3));
                setUI[0]=rs.getString(1);
                setUI[1]=rs.getString(2);
                setUI[2]=rs.getString(3);
                //取得計算平均資料
                for(int i = 0 ; i<5 ; i++){
                    rs = st.executeQuery("select count(rating) FROM application_form WHERE rating = "+(i+1)+" AND activityID = '"+HAid.get(EventId)+"'");
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
            lv(result);
            if (result.equals("完成")){
                ConvertToBitmap(setUI[1]);
                host.setText(setUI[0]);
                activityDescription.setText(setUI[2]);
                render(score.size());
                int total_rated = (int)(score_distribute[0]+score_distribute[1]+score_distribute[2]+score_distribute[3]+score_distribute[4]);
                rated.setText(""+total_rated);
                String avsc = "";
                if(total_rated>0){
                    float average = ((score_distribute[0]+2*score_distribute[1]+3*score_distribute[2])+4*score_distribute[3]+5*score_distribute[4])/(score_distribute[0]+score_distribute[1]+score_distribute[2]+score_distribute[3]+score_distribute[4]);
                    if((""+average).length()>3){
                        avsc = (""+average).substring(0,3);
                    }else{avsc = ""+average;}
                    lv("counting score");
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

    //將Base64還原為點陣圖***需修改
    public void ConvertToBitmap(String b64){
        try{
            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            actpic.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        }catch (Exception e){
            lv("error = "+e.toString());
        }
    }
    //字型大小設定
    private void SetFontSize(){
        TextView activityName = findViewById(R.id.activityName);
        activityName.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//activityName
        TextView host_com = findViewById(R.id.host_com);
        host_com.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//place
        TextView host = findViewById(R.id.host);
        host.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//host
        TextView act_detail = findViewById(R.id.act_detail);
        act_detail.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//act_detail
        TextView activityDescription = findViewById(R.id.activityDescription);
        activityDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//activityDescription
    }
}
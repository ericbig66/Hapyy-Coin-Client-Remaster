package com.greeting.happycoin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.AactDate;
import static com.greeting.happycoin.MainActivity.AactEnd;
import static com.greeting.happycoin.MainActivity.Aamount;
import static com.greeting.happycoin.MainActivity.AamountLeft;
import static com.greeting.happycoin.MainActivity.Actpic;
import static com.greeting.happycoin.MainActivity.Adeadline_date;
import static com.greeting.happycoin.MainActivity.Adesc;
import static com.greeting.happycoin.MainActivity.Aid;
import static com.greeting.happycoin.MainActivity.Aname;
import static com.greeting.happycoin.MainActivity.Areward;
import static com.greeting.happycoin.MainActivity.AsignEnd;
import static com.greeting.happycoin.MainActivity.AsignStart;
import static com.greeting.happycoin.MainActivity.Astart_date;
import static com.greeting.happycoin.MainActivity.Avendor;
import static com.greeting.happycoin.MainActivity.EventId;
import static com.greeting.happycoin.MainActivity.attended;
import static com.greeting.happycoin.MainActivity.popup;

public class eventDetail extends AppCompatActivity {
    //將Base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID){
        try{
            Log.v("test",Actpic.get(ID));
            byte[] imageBytes = Base64.decode(Actpic.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            int w = proimg.getWidth();
            int h = proimg.getHeight();
            Log.v("test","pic"+ID+" original = "+w+"*"+h);
            int scale = 1;
            if(w>h && (w/DP(360))>1 || h==w && (w/DP(360))>1){
                scale = w/DP(360);
                w = w/scale;
                h = h/scale;
            }else if(h>w && (h/DP(360))>1){
                scale = h/DP(360);
                w = w/scale;
                h = h/scale;
            }
            Log.v("test","pic"+ID+" resized = "+w+"*"+h);
            proimg = Bitmap.createScaledBitmap(proimg, w, h, false);
            return proimg;
        }catch (Exception e){
            Log.v("test","error = "+e.toString());
            return null;
        }


    }
    int function = 0;
    float star = 0;
    Button btnBuy; //購買按鈕
    String acc;//UUID(購物用)
    RatingBar reputation;
    TextView ratingDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_event_detail);
        Log.v("test","eventid="+EventId);
        //定義區
        ImageView merPic = findViewById(R.id.merPic);
        TextView txtName=findViewById(R.id.txtName);
        TextView txtVdrName=findViewById(R.id.txtVdrName);
        btnBuy = findViewById(R.id.btnBuy);
        //設定區(初始化活動資訊)
        merPic.setImageBitmap(ConvertToBitmap(EventId));//設定活動圖片
        txtName.setText(Aname.get(EventId));//設定活動名稱
        txtVdrName.setText("主辦廠商: "+Avendor.get(EventId)+"\n活動名稱: "+Aname.get(EventId)+"\n剩餘名額: "+AamountLeft.get(EventId)+"人\n回饋金額: $"+Areward.get(EventId)+"\n活動日期: "+AactDate.get(EventId)+"\n活動時間: "+AsignStart.get(EventId)+"~"+AsignEnd.get(EventId)+"\n報名截止: "+Adeadline_date.get(EventId));//設定活動詳細資訊
        //設定報名按鈕文字，若已報名將顯示取消報名，否則顯示參加
        if (attended.contains(Aid.get(EventId))){btnBuy.setText("取消報名");}
        else{btnBuy.setText("參加");}
        btnBuy.setOnClickListener(v -> Buyer());//按下參加鈕所執行之動作
        reputation = findViewById(R.id.reputation);
        ratingDetail = findViewById(R.id.ratingDetail);
        Buyer();
    }
    //進行報名或取消報名
    void Buyer() {
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }


    //報名或取消報名
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            acc = getUUID(getApplicationContext());
            if (function==0) popup(getApplicationContext(),"處理中，請稍後...");
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.v("test","活動報名中");
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                if(function==0){
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(signTime), Round(AVG(rating),1) from application_form where rating>0 and activityID = '"+Aid.get(EventId)+"'");
                    rs.next();
                    if(rs.getInt(1)>0){
                        res = "目前有"+rs.getString(1)+"人評價過";
                        star = rs.getFloat(2);
                    }else{res ="目前沒有人評價過此活動";}
                    return res;
                }else if(function==1){
                    CallableStatement cstmt = con.prepareCall("{?=call activity_regist(?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2,acc);//設定輸出變數(參數位置,參數型別)
                    cstmt.setString(3,Aid.get(EventId));
                    cstmt.executeUpdate();
                    return cstmt.getString(1);
                }

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //報名/取消報名後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            if (function==0){
                reputation.setRating(star);
                ratingDetail.setText(result);
                function++;
            }else if (function==1){
                try{
                    if (result.equals("報名成功")){
                        btnBuy.setText("取消報名");
                    }else if(result.equals("已取消報名")){
                        btnBuy.setText("參加");
                    }
                    popup(getApplicationContext(),result);
                    //報名成功後將自動清空活動列表並轉跳回活動列表
                    if(result.equals("報名成功")||result.equals("已取消報名")){
                        clear();
                        Intent intent;
                        intent = new Intent(eventDetail.this, event.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (Exception e){
                    Log.v("test","錯誤: "+e.toString());
                }
            }

        }
    }
    //清空活動列表，避免重複堆疊活動資訊
    public void clear(){
        Aid.clear();
        Avendor.clear();
        Aname.clear();
        Actpic.clear();
        AactDate.clear();
        AactEnd.clear();
        Astart_date.clear();
        Adeadline_date.clear();
        AsignStart.clear();
        AsignEnd.clear();
        Aamount.clear();
        Areward.clear();
        AamountLeft.clear();
        Adesc.clear();
        attended.clear();
    }
    //單位轉換器(轉為DP)
    public static int DP(float dp){
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }
}

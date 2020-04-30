package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
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
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.attended;

public class eventDetail extends AppCompatActivity {
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

    EditText Qt;
    Button btnBuy;
    String acc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_event_detail);
        Log.v("test","eventid="+EventId);
        ImageView merPic = findViewById(R.id.merPic);
        merPic.setImageBitmap(ConvertToBitmap(EventId));

        TextView txtName=findViewById(R.id.txtName);
        txtName.setText(Aname.get(EventId));

        TextView txtVdrName=findViewById(R.id.txtVdrName);
        txtVdrName.setText("主辦廠商: "+Avendor.get(EventId)+"\n活動名稱: "+Aname.get(EventId)+"\n剩餘名額: "+AamountLeft.get(EventId)+"人\n回饋金額: $"+Areward.get(EventId)+"\n活動日期: "+AactDate.get(EventId)+"\n活動時間: "+AsignStart.get(EventId)+"~"+AsignEnd.get(EventId)+"\n報名截止: "+Adeadline_date.get(EventId));




        btnBuy = findViewById(R.id.btnBuy);
        if (attended.contains(Aid.get(EventId))){btnBuy.setText("取消報名");}
        else{btnBuy.setText("參加");}
        btnBuy.setOnClickListener(v -> Buyer());
    }
    void Buyer() {
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }


    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            acc = getUUID(getApplicationContext());
            Toast.makeText(eventDetail.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            ////////////////////////////////////////////
            try {
                Log.v("test","活動報名中");
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                CallableStatement cstmt = con.prepareCall("{?=call activity_regist(?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2,acc);//設定輸出變數(參數位置,參數型別)
                cstmt.setString(3,Aid.get(EventId));
                cstmt.executeUpdate();
                return cstmt.getString(1);
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            try{
                if (result.equals("報名成功")){
                    btnBuy.setText("取消報名");
                }else if(result.equals("已取消報名")){
                    btnBuy.setText("參加");
                }
                Toast.makeText(eventDetail.this, result, Toast.LENGTH_SHORT).show();
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

    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
            if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public static int DP(float dp){
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }
}

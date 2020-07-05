package com.greeting.happycoin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.BuyId;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.PRecDate;
import static com.greeting.happycoin.MainActivity.PSerial;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pdescribtion;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.hideKB;
import static com.greeting.happycoin.MainActivity.popup;
import static com.greeting.happycoin.market.DP;

public class eval_product extends AppCompatActivity {
    EditText message;
    String comment = null;//客戶輸入評價用的變數
    RatingBar ratingBar;
    float star = 0; //客戶的評分

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_eval_product);
        //定義區
        ImageView merPic = findViewById(R.id.merPic);
        TextView txtName=findViewById(R.id.txtName);
        TextView txtVdrName=findViewById(R.id.txtVdrName);
        TextView detail = findViewById(R.id.detail);
        message = findViewById(R.id.comment);
        Button btnRate = findViewById(R.id.btnBuy);
        ratingBar = findViewById(R.id.ratingBar);
        SetFontSize();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                star = rating;
                if(star<1){
                    btnRate.setEnabled(false);
                    message.setVisibility(View.GONE);
                }else{
                    btnRate.setEnabled(true);
                    message.setVisibility(View.VISIBLE);
                }
            }
        });

        //初始化設定
        merPic.setImageBitmap(ConvertToBitmap(BuyId));//設定商品圖
        txtName.setText(Pname.get(BuyId));//設定商品名稱
        //初始化商品詳細資料區域
        txtVdrName.setText("廠商名稱: "+Vendor.get(BuyId)+"\n商品編號: "+PID.get(BuyId)+"\n商品價格: $"+Pprice.get(BuyId)+"\n商品說明：\n"+Pdescribtion.get(BuyId));
        detail.setText("購買數量："+Pamount.get(BuyId)+"\n 交易金額："+Pamount.get(BuyId)*Pprice.get(BuyId)+"\n 交易日期："+PRecDate.get(BuyId).substring(0,11));
        btnRate.setOnClickListener(v -> Rater());//點選購買執行其相關動作
    }

    //將base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(PIMG.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);//裝入轉換後結果
            int w = proimg.getWidth();//取得圖片寬度
            int h = proimg.getHeight();//取得圖片高度
            Log.v("test","pic"+ID+" original = "+w+"*"+h);
            //壓縮圖片
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
            return proimg;//回傳壓縮後的圖片
        }catch (Exception e){
            Log.v("test","error = "+e.toString());
            return null;
        }
    }
    //購買程序
    void Rater() {
        hideKB(this);//隱藏鍵盤
        //數量驗證
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //連接資料庫以完成交易
    public class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        String uuid=getUUID(getApplicationContext());//取得裝置UUID供交易使用
        //準備購買
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            popup(getApplicationContext(),"評價中...");//顯示提示等待交易完成
            comment = message.getText().toString();
        }
        //購買
        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                CallableStatement cstmt = null;
                cstmt = con.prepareCall("{call product_rating(?,?,?,?,?,?)}");
                cstmt.setString(1,uuid);
                cstmt.setString(2,"set");
                cstmt.registerOutParameter(3,Types.LONGVARCHAR);
                cstmt.setInt(4,PSerial.get(BuyId));
                cstmt.setFloat(5,star);
                cstmt.setString(6,comment);
                cstmt.executeUpdate();
//                return cstmt.getString(1);
                return "評價成功";

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //交易結果
        @Override
        protected void onPostExecute(String result) {
            try{
                if (result.contains("評價成功")){onBackPressed();}
                else {
                    Log.v("test","評價結果"+result);
                }
                popup(getApplicationContext(),result);//提示交易結果
            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }
        }
    }
    @Override
    //返回商品列表
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(eval_product.this, CommentCenter.class);
        startActivity(intent);
        finish();
    }
    //字型大小設定
    private void SetFontSize(){
        TextView text1 = findViewById(R.id.text1);
        text1.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView txtVdrName = findViewById(R.id.txtVdrName);
        txtVdrName.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView text3 = findViewById(R.id.text3);
        text3.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView detail = findViewById(R.id.detail);
        detail.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView textView4 = findViewById(R.id.textView4);
        textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        Button btnBuy = findViewById(R.id.btnBuy);
        btnBuy.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//每日遊戲
    }
}
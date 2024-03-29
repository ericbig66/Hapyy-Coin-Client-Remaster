package com.greeting.happycoin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
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
import static com.greeting.happycoin.MainActivity.BuyId;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.hideKB;
import static com.greeting.happycoin.MainActivity.popup;
import static com.greeting.happycoin.market.Amount;
import static com.greeting.happycoin.market.DP;


public class productDetail extends AppCompatActivity {
    //將base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID){
        SetFontSize();
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
    int function = 0;
    EditText Qt; //購買數量
    RatingBar reputation;//星數
    TextView ratingDetail;
    Button show_comments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_detail);
        //定義區
        ImageView merPic = findViewById(R.id.merPic);
        TextView txtName=findViewById(R.id.txtName);
        TextView txtVdrName=findViewById(R.id.txtVdrName);
        Qt = findViewById(R.id.Qt);
        Button btnBuy = findViewById(R.id.btnBuy);
        reputation = findViewById(R.id.reputation);
        ratingDetail = findViewById(R.id.ratingDetail);
        //初始化設定
        merPic.setImageBitmap(ConvertToBitmap(BuyId));//設定商品圖
        txtName.setText(Pname.get(BuyId));//設定商品名稱
        //初始化商品詳細資料區域
        txtVdrName.setText("廠商名稱: "+Vendor.get(BuyId)+"\n商品編號: "+PID.get(BuyId)+"\n庫存數量: "+Pamount.get(BuyId)+"\n商品價格: $"+Pprice.get(BuyId));
        Qt.setText(Amount+"");//帶入前頁所輸入的數量
        show_comments = findViewById(R.id.show_comments);
        show_comments.setOnClickListener(v -> {
            Intent intent = new Intent(productDetail.this,productComment.class);
            startActivity(intent);
        });
        btnBuy.setOnClickListener(v -> Buyer());//點選購買執行其相關動作
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }
    //購買程序
    void Buyer() {
        if(Qt.getText().toString().trim().isEmpty()){Qt.setText("0");}//數量驗證
        final int quantity = Integer.parseInt(Qt.getText().toString());//鎖定購買數量
        hideKB(this);//隱藏鍵盤
        //數量驗證
        if(quantity>0){
            Amount = Integer.parseInt(Qt.getText().toString().trim());
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }else {
            popup(getApplicationContext(),"請至少購買一項商品");
        }
    }

    //連接資料庫以完成交易
    class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        float star =0;
        String uuid=getUUID(getApplicationContext());//取得裝置UUID供交易使用
        //準備購買
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            if (function==1) popup(getApplicationContext(),"購買中...");//顯示提示等待交易完成
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
                if(function == 0){
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(DISTINCT ID), COUNT(PID),ROUND(AVG(rating),1) FROM sell_record WHERE PID  ='"+PID.get(BuyId)+"' AND rating>0 AND VID IN (SELECT VID FROM vendor WHERE name = '"+Vendor.get(BuyId)+"')");
                    rs.next();
                    if(rs.getInt(2)>0){
                        star=rs.getFloat(3);
                        res = "目前已有"+rs.getString(1)+"人評價過，共計"+rs.getString(2)+"次";
                    }else{res = "目前還沒有人評價過此商品";}
                    return res;
                }else if(function == 1){
                    cstmt = con.prepareCall("{? = call purchase(?,?,?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2,PID.get(BuyId));
                    cstmt.setInt(3,Amount);
                    cstmt.setString(4,uuid);
                    cstmt.setString(5,Vendor.get(BuyId));
                    cstmt.setString(6,"N/A");
                    cstmt.executeUpdate();
                    return cstmt.getString(1);
                }


            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //交易結果
        @Override
        protected void onPostExecute(String result) {
            if(function == 0){
                reputation.setRating(star);
                ratingDetail.setText(result);
                function++;
            }else if(function == 1){
                try{
                    if (result.contains("交易成功")){onBackPressed();}
                    else {
                        Log.v("test","購買結果"+result);
                    }
                    popup(getApplicationContext(),result);//提示交易結果
                }catch (Exception e){
                    Log.v("test","錯誤: "+e.toString());
                }
            }



        }
    }



    @Override
    //返回商品列表
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(productDetail.this, market.class);
        startActivity(intent);
        finish();
    }

    //字型大小設定
    private void SetFontSize(){
        TextView ratingDetail = findViewById(R.id.ratingDetail);
        ratingDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView text1 = findViewById(R.id.text1);
        text1.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView txtVdrName = findViewById(R.id.txtVdrName);
        txtVdrName.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//一般
        TextView textView4 = findViewById(R.id.textView4);
        textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-5);//一般
        EditText Qt = findViewById(R.id.Qt);
        Qt.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-5);//一般
        Button btnBuy = findViewById(R.id.btnBuy);
        btnBuy.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//每日遊戲
    }

    //清除商品列表避免堆疊舊商品資訊***待修改
//    public void clear(){
//        PID.clear();
//        Pname.clear();
//        Pprice.clear();
//        Pamount.clear();
//        Vendor.clear();
//        PIMG.clear();
//    }
}

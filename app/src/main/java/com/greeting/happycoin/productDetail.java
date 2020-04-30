package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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
import static com.greeting.happycoin.LoginAndRegister.popup;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.MainActivity.BuyId;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.market.Amount;
import static com.greeting.happycoin.market.DP;

public class productDetail extends AppCompatActivity {

    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(PIMG.get(ID), Base64.DEFAULT);
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

    EditText Qt; //數量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_detail);

        ImageView merPic = findViewById(R.id.merPic);
        merPic.setImageBitmap(ConvertToBitmap(BuyId));

        TextView txtName=findViewById(R.id.txtName);
        txtName.setText(Pname.get(BuyId));

        TextView txtVdrName=findViewById(R.id.txtVdrName);
        txtVdrName.setText("廠商名稱: "+Vendor.get(BuyId)+"\n商品編號: "+PID.get(BuyId)+"\n庫存數量: "+Pamount.get(BuyId)+"\n商品價格: $"+Pprice.get(BuyId));

        Qt = findViewById(R.id.Qt);
        Qt.setText(Amount+"");

        Button btnBuy = findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(v -> Buyer());
    }

    void Buyer() {
        if(Qt.getText().toString().trim().isEmpty()){Qt.setText("0");}
        final int quantity = Integer.parseInt(Qt.getText().toString());
        closekeybord();

        if(quantity>0){
            Amount = Integer.parseInt(Qt.getText().toString().trim());
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }else {
            popup(getApplicationContext(),"請至少購買一項商品");
        }
    }
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        String uuid=getUUID(getApplicationContext());
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(productDetail.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            ////////////////////////////////////////////
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                CallableStatement cstmt = null;
                cstmt = con.prepareCall("{? = call purchase(?,?,?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2,PID.get(BuyId));
                cstmt.setInt(3,Amount);
                cstmt.setString(4,uuid);
                cstmt.setString(5,Vendor.get(BuyId));
                cstmt.setString(6,"N/A");
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
                if (result.contains("交易成功")){onBackPressed();}
                else {
                    Log.v("test","購買結果"+result);
                }
                Toast.makeText(productDetail.this, result, Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(productDetail.this, market.class);
        startActivity(intent);
        finish();
    }

    //下一版功能
//    public void clear(){
//        PID.clear();
//        Pname.clear();
//        Pprice.clear();
//        Pamount.clear();
//        Vendor.clear();
//        PIMG.clear();
//    }
}

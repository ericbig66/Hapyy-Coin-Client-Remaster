package com.greeting.happycoin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pdescribtion;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.hideKB;
import static com.greeting.happycoin.MainActivity.popup;


public class market extends AppCompatActivity {
    int function = 0;//功能執行，0表示從資料庫載入商品列表，1表示購買商品
    LinearLayout ll; //商品列表顯示區
    public static int cardCounter = 0;//商品數量計數器
    String ip = "111231123";//容納此裝置之外連IP***目前尚在開發中
    String ProductId, FirmId;//商品編號,公司id
    public static int Amount;//購買數量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_market);
        ll = findViewById(R.id.ll);//指定物件
        //連接資料庫取得商品資訊
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        String uuid;//裝載此裝置的UUID供交易使用
        //開始執行動作
        @Override
        protected void onPreExecute(){//執行前
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");//顯示提示等待下載資料
            uuid = getUUID(getApplicationContext());//取得裝置的UUID
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            //連接資料庫
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                CallableStatement cstmt = null;//因需多次使用故先設為null
            if(function == 0) {//把所有商品資訊抓回來
                try {
                    //建立查詢
                    String result = "";
                    ResultSet rs = st.executeQuery("select p.*, name from product p, vendor v where stock > 0 and vid = vendor");

                    while (rs.next()) {
                        PID.add(rs.getString("sku"));
                        Pname.add(rs.getString("productName"));
                        Pprice.add(rs.getInt("price"));
                        Pamount.add(rs.getInt("stock"));
                        Vendor.add(rs.getString("name"));
                        PIMG.add(rs.getString("picture"));
                        Pdescribtion.add(rs.getString("description"));
                    }

                    return PID.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)

                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                    Log.v("test","catch by query");
                }
                return res;
            }
            ////////////////////////////////////////////
            else if(function == 1){//購買商品
                try {
                    cstmt = con.prepareCall("{? = call purchase(?,?,?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2,ProductId);
                    cstmt.setInt(3,Amount);
                    cstmt.setString(4,uuid);
                    cstmt.setString(5,FirmId);
                    cstmt.setString(6,"N/A");
                    cstmt.executeUpdate();
                    Log.v("test","result= "+ProductId+" "+Amount+" "+uuid+" "+FirmId);
                    return cstmt.getString(1);
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                    Log.v("test","error found in function1");
                }
                return res;
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            Log.v("test","result returned = "+result);
            try{
                if(function == 0){//若取得商品列表
                    cardCounter = Integer.parseInt(result);//回報商品數量
                    cardRenderer();//繪製商品卡
                }
                else if(function == 1){//若購買商品
                    if(result.equals("交易成功!")){//回傳資料中包含"交易成功"
                        clear();
                        recreate();//重新繪製本頁面並更新商品資料
                    }
                   popup(getApplicationContext(),result);//提示交易結果
                }
                else{
                    popup(getApplicationContext(),result);//提示交易結果
                }
                function = -1;//表示程序執行完畢
            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }

        }
    }
    //商品卡產生器
    public void cardRenderer(){
        for(int i = 0 ; i < PID.size() ; i++){//以迴圈產生商品卡
            Log.v("test", "render card "+i);
            add(i);//增加商品卡
        }
    }


    //產生商品卡
    public void add(final int ID){
        //商品卡片
        LinearLayout frame = new LinearLayout(this);//新增LinearLayout(商品卡母容器)
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(//商品卡本身
                LinearLayout.LayoutParams.MATCH_PARENT, // 商品卡寬度
                DP(150) //設定商品卡高度

        );//LinearLayout 參數設定


        frame.setPadding(DP(15),DP(15),DP(15),DP(15));
        framep.setMargins(0,0,0,DP(20));
        frame.setOrientation(LinearLayout.HORIZONTAL);
        frame.setLayoutParams(framep);
        frame.setBackgroundColor(Color.parseColor("#D1FFDE"));

        //圖片&價格區
        LinearLayout picpri = new LinearLayout(this);
        LinearLayout.LayoutParams picprip = new LinearLayout.LayoutParams(DP(120),DP(120));
        picprip.setMargins(0,0,DP(5),0);
        picpri.setOrientation(LinearLayout.VERTICAL);
        picpri.setLayoutParams(picprip);

        //數量
        final EditText amount = new EditText(this);
        LinearLayout.LayoutParams amountp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount.setEms(3);
        amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        amount.setLayoutParams(amountp);
        amount.setId(5*ID+2);
        amount.setText("1");

        //商品圖片
        ImageView propic = new ImageView(this);
        LinearLayout.LayoutParams propicp = new LinearLayout.LayoutParams(DP(120),DP(90));
        //propic.setImageBitmap(Bitmap.createScaledBitmap(ConvertToBitmap(ID), 120, 90, false));
        propic.setImageBitmap(ConvertToBitmap(ID));
        propic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        propic.setLayoutParams(propicp);
        propic.setId(5*ID);
        propic.setOnClickListener(v -> {
            final int id = ID;
            if(amount.getText().toString().trim().isEmpty()){amount.setText("0");}
            final int quantity = Integer.parseInt(amount.getText().toString());
            hideKB(this);
            identifier("D",id,quantity);
        });

        //商品價格
        TextView price = new TextView(this);
        LinearLayout.LayoutParams pricep = new LinearLayout.LayoutParams(DP(120),DP(30));
        price.setText("價格: $"+Pprice.get(ID));
        price.setTextSize(18f);
        price.setLayoutParams(picprip);

        //商品訊息區
        LinearLayout proinf = new LinearLayout(this);
        LinearLayout.LayoutParams proinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1f
        );
        proinf.setOrientation(LinearLayout.VERTICAL);
        proinf.setLayoutParams(proinfp);

        //商品名稱
        TextView proname = new TextView(this);
        LinearLayout.LayoutParams pronamep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        proname.setText(Pname.get(ID));
        proname.setTextSize(18f);
        proname.setClickable(true);
        proname.setLayoutParams(pronamep);
        proname.setId(5*ID+1);

        //購買資訊
        LinearLayout buyinf = new LinearLayout(this);
        LinearLayout.LayoutParams buyinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        buyinf.setOrientation(LinearLayout.HORIZONTAL);
        buyinf.setLayoutParams(buyinfp);

        //數量:[標籤]
        TextView amount_label = new TextView(this);
        LinearLayout.LayoutParams amount_labelp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount_label.setText("數量：");
        amount_label.setTextSize(18f);
        amount_label.setLayoutParams(amount_labelp);

        //按鈕箱
        LinearLayout btnbox = new LinearLayout(this);
        LinearLayout.LayoutParams btnboxp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        btnboxp.setMargins(0,20,0,0);
        btnbox.setLayoutParams(btnboxp);

        //詳情按鈕
        Button detail = new Button(this);
        LinearLayout.LayoutParams detailp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,0.5f
        );
        detailp.setMarginEnd(20);
        detail.setText("詳情");
        detail.setTextSize(18f);
        detail.setLayoutParams(detailp);
        detail.setId(5*ID+3);
        detail.setTextColor(Color.parseColor("#FFFFFF"));
        detail.setBackgroundResource(R.drawable.rounded_button_green);
        detail.setOnClickListener(v -> {
            final int id = ID;
            if(amount.getText().toString().trim().isEmpty()){amount.setText("0");}
            final int quantity = Integer.parseInt(amount.getText().toString());
            hideKB(this);
            identifier("D",id,quantity);
        });

        //訂購按鈕
        Button buybtn = new Button(this);
        LinearLayout.LayoutParams buybtnp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,0.5f
        );
        buybtn.setBackgroundResource(R.drawable.rounded_button_green);
        buybtn.setTextColor(Color.parseColor("#FFFFFF"));
        buybtn.setText("訂購");
        buybtn.setTextSize(18f);
        buybtn.setLayoutParams(buybtnp);
        buybtn.setId(5*ID+4);
        buybtn.setOnClickListener(v -> {
            final int id = ID;
            if(amount.getText().toString().trim().isEmpty()){amount.setText("0");}
            final int quantity = Integer.parseInt(amount.getText().toString());
            hideKB(this);
            identifier("B",id,quantity);
        });

        //將內容填入frame
        /*
        frame
            propic
            proinf
                proname
                buyinf
                    amount_label
                    amount
                btnbox
                    dteail
                    buybtn
        */
        //將產生之物件放入卡片容器中
        proinf.addView(proname);
        buyinf.addView(amount_label);
        buyinf.addView(amount);
        proinf.addView(buyinf);
        btnbox.addView(detail);
        btnbox.addView(buybtn);
        proinf.addView(btnbox);
        picpri.addView(propic);
        picpri.addView(price);
        frame.addView(picpri);
        frame.addView(proinf);
        Log.v("test","still alive before last one");
        ll.addView(frame);
//        loading.setVisibility(View.GONE);
        Log.v("test","card"+ID+"rendered");
    }

    public static int DP(float dp){//寬度單位轉換器(設定值為DP)
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }

    public void identifier(String act, int ID,int quantity){ //動作判定器(判斷按下的按鈕式購買或詳情)
        Log.v("test","act= "+act+" ID= "+ID+" quantity= "+quantity);
        Amount = quantity;//購買數量
        ProductId=PID.get(ID);//商品ID
        FirmId=Vendor.get(ID);//廠商ID
        BuyId=ID; //商品列表中的第幾樣商品

        if(act.equals("D")){//若按下詳情
            Log.v("test","您正在檢視第"+Pname.get(ID)+"的詳細資料");
            Intent intent = new Intent(market.this, productDetail.class);//準備轉跳頁面
            startActivity(intent);//轉跳詳情頁面
            finish();//結束本頁面
        }else if(act.equals("B")){//若按下購買
//            Log.v("test","您購買了"+quantity+"個"+Pname.get(ID));
            function = 1;//設定功能變數為1表示購買
            if(quantity>0){//若數量正>0則會正常執行購買
                ConnectMySql connectMySql = new ConnectMySql();
                connectMySql.execute("");
            }else{//否則不執行任何功能並提示其錯誤
                function = -1;
                popup(getApplicationContext(),"請至少購買一項商品");
            }
        }
    }
public Bitmap ConvertToBitmap(int ID){ //將Base64轉換為點陣圖
    try{
//            Log.v("test",PIMG.get(ID));
        byte[] imageBytes = Base64.decode(PIMG.get(ID), Base64.DEFAULT);
        Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);//轉換後的圖片
        int w = proimg.getWidth();//取得寬度
        int h = proimg.getHeight();//取得高度
        Log.v("test","pic"+ID+" original = "+w+"*"+h);
        //圖片大小設定
        int scale = 1;
        if(w>h && (w/DP(120))>1 || h==w && (w/DP(120))>1){
            scale = w/DP(120);
            w = w/scale;
            h = h/scale;
        }else if(h>w && (h/DP(120))>1){
            scale = h/DP(120);
            w = w/scale;
            h = h/scale;
        }
        Log.v("test","pic"+ID+" resized = "+w+"*"+h);
        proimg = Bitmap.createScaledBitmap(proimg, w, h, false);//建立固定大小的圖片
        return proimg;//傳回轉換後的圖片
    }catch (Exception e){
        Log.v("test","error = "+e.toString());
        return null;
    }
}
    public void onBackPressed() {//返回鈕動作
        Intent intent = new Intent(market.this,LoginAndRegister.class);//準備轉跳回首頁
        startActivity(intent);//轉跳
        finish();//結束本頁面
    }

    //清空列表以確保活動資訊不會重複疊加
    public void clear(){
        PID.clear();
        Pname.clear();
        Pprice.clear();
        Pamount.clear();
        Vendor.clear();
        PIMG.clear();
        Pdescribtion.clear();
    }
}

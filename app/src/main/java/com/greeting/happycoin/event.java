package com.greeting.happycoin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
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
import static com.greeting.happycoin.LoginAndRegister.popup;
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
import static com.greeting.happycoin.MainActivity.entryIsRecent;
import static com.greeting.happycoin.MainActivity.hideKB;


public class event extends AppCompatActivity {
    int function = 0;//功能選擇器0= 自資料庫擷取活動資料集 1= 報名活動
    LinearLayout ll;//活動列表顯示處
    public static int cardCounter = 0;//活動數量
    String acc ;//使用者UUID供活動報名用
    String sql="";//當點選近期活動進來時給予SQL額外條件的指令存放處
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_event);
        //定義區
        ll = findViewById(R.id.ll);
        //初始化活動列表(資料擷取)
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");

    }

    //活動資料擷取與報名
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            acc= getUUID(getApplicationContext());
            if (function==0)
                popup(getApplicationContext(),"活動查詢中，請稍後...");
            else
                popup(getApplicationContext(),"報名中，請稍後...");
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            //連接資料庫
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result = "";
                //取得活動資料集
                if (function == 0) {
                    try {

                        Statement st = con.createStatement();
                        attended.clear();
                        ResultSet rs = st.executeQuery("select activityID from application_form where clientID in (select id from client where acc = '" + acc + "')");
                        //
                        String att = "";
                        while (rs.next()) {
                            attended.add(rs.getString("activityID"));
                            att += "'" + rs.getString("activityID") + "', ";
                        }
                        att = att.isEmpty() ? "null" : att.substring(0, att.length() - 2);
                        if (entryIsRecent)
                            sql = "and id in (" + att + ")";
                        else
                            sql = "";
//                    Log.v("test", "query = "+"select * from activity where activityNumber in("+att +") and actDate >= curdate() or endApply >= curdate()");
                        rs = st.executeQuery("select * from activity where actDate >= curdate() " + sql);
                        Log.v("test", sql);
                        //新增活動資料至陣列
                        while (rs.next()) {
                            Aid.add(rs.getString(1));
                            Avendor.add(rs.getString(2));
                            Aname.add(rs.getString(3));
                            Actpic.add(rs.getString(4));
                            AactDate.add(rs.getDate(5));
                            AactEnd.add(rs.getDate(6));
                            Astart_date.add(rs.getDate(7));
                            Adeadline_date.add(rs.getDate(8));
                            AsignStart.add(rs.getTime(9));
                            AsignEnd.add(rs.getTime(10));
                            Aamount.add(rs.getInt(11));
                            Areward.add(rs.getInt(12));
                            AamountLeft.add(rs.getInt(13));
                            Adesc.add(rs.getString(14)); //待檢查***相關功能

                        }

                        return Aname.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)
                    } catch (Exception e) {
                        e.printStackTrace();
                        res = e.toString();
                    }
                    return res;
                }
                //活動報名
                else if (function == 1) {
                    try {
                        Log.v("test", "活動報名中");
                        CallableStatement cstmt = con.prepareCall("{?=call activity_regist(?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2, acc);//設定輸出變數(參數位置,參數型別)
                        cstmt.setString(3, Aid.get(EventId));
                        cstmt.executeUpdate();
                        return cstmt.getString(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        res = e.toString();
                    }
                    return res;
                }
            }catch (Exception e){}
            return null;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            try{
                //活動列表取得後
                if(function == 0){
                    cardCounter = Integer.parseInt(result);//設定活動列表大小
                    cardRenderer();//產生列表
                }
                //活動報名
                else if(function == 1){
                    popup(getApplicationContext(),result);//顯示報名結果
                    //若成功將自動更新本頁資訊
                    if(result.contains("報名成功")||result.contains("已取消報名")){
                        clear();
                        recreate();
                    }
                }
                function = -1;
            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }

        }
    }

    //商品卡產生器
    public void cardRenderer(){
        for(int i = 0 ; i < Aname.size() ; i++){
            Log.v("test", "render card "+i);
            add(i);
        }
    }


    //產生活動卡
    public void add(final int ID){
        //活動卡片
        LinearLayout frame = new LinearLayout(this);
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DP(150)
        );

        frame.setBackgroundColor(Color.parseColor("#D1FFDE"));
        frame.setPadding(DP(15),DP(15),DP(15),DP(15));
        framep.setMargins(0,0,0,DP(20));
        frame.setOrientation(LinearLayout.HORIZONTAL);
        frame.setLayoutParams(framep);

        //圖片&價格區
        LinearLayout picpri = new LinearLayout(this);
        LinearLayout.LayoutParams picprip = new LinearLayout.LayoutParams(DP(120),DP(120));
        picprip.setMargins(0,0,DP(5),0);
        picpri.setOrientation(LinearLayout.VERTICAL);
        picpri.setLayoutParams(picprip);

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
            hideKB(this);
            identifier("D",id);
        });

        //獎勵金額
        TextView price = new TextView(this);
        LinearLayout.LayoutParams pricep = new LinearLayout.LayoutParams(DP(120),DP(30));
        price.setText("獎勵: $"+Areward.get(ID));
        price.setTextSize(18f);
        price.setLayoutParams(picprip);

        //活動訊息區
        LinearLayout proinf = new LinearLayout(this);
        LinearLayout.LayoutParams proinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1f
        );
        proinf.setOrientation(LinearLayout.VERTICAL);
        proinf.setLayoutParams(proinfp);

        //活動名稱
        TextView proname = new TextView(this);
        LinearLayout.LayoutParams pronamep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        proname.setText(Aname.get(ID));
        proname.setTextSize(18f);
        proname.setClickable(true);
        proname.setLayoutParams(pronamep);
        proname.setId(5*ID+1);

        //報名資訊
        LinearLayout buyinf = new LinearLayout(this);
        LinearLayout.LayoutParams buyinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        buyinf.setOrientation(LinearLayout.HORIZONTAL);
        buyinf.setLayoutParams(buyinfp);

        //剩餘名額
        TextView amount_label = new TextView(this);
        LinearLayout.LayoutParams amount_labelp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount_label.setText("剩餘名額："+AamountLeft.get(ID));
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
        detailp.setMarginEnd(10);
        detail.setBackgroundResource(R.drawable.rounded_button_green);
        detail.setTextColor(Color.parseColor("#FFFFFF"));
        detail.setText("詳情");
        detail.setTextSize(18f);
        detail.setLayoutParams(detailp);
        detail.setId(5*ID+3);
        detail.setOnClickListener(v -> {
            final int id = ID;
            hideKB(this);
            identifier("D",id);
        });

        //參加按鈕
        Button buybtn = new Button(this);
        LinearLayout.LayoutParams buybtnp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,0.5f
        );
        buybtn.setBackgroundResource(R.drawable.rounded_button_green);
        buybtn.setTextColor(Color.parseColor("#FFFFFF"));
        if(attended.contains(Aid.get(ID))){buybtn.setText("取消報名");}
        else{buybtn.setText("參加");}
        buybtn.setTextSize(18f);
        buybtn.setLayoutParams(buybtnp);
        buybtn.setId(5*ID+4);
        buybtn.setOnClickListener(v -> {
//            if(buybtn.getText().toString().equals("參加")){buybtn.setText("取消報名");}
//            else{buybtn.setText("參加");}
            final int id = ID;
            hideKB(this);
            identifier("B",id);
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

        proinf.addView(proname);
        buyinf.addView(amount_label);
        proinf.addView(buyinf);
        btnbox.addView(detail);
        btnbox.addView(buybtn);
        proinf.addView(btnbox);
        picpri.addView(propic);
        picpri.addView(price);
        frame.addView(picpri);
        frame.addView(proinf);
        ll.addView(frame);
        Log.v("test","card"+ID+"rendered");
    }
    //單位轉換器(轉換為DP)
    public static int DP(float dp){
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }
    //報名或詳細資料檢視判斷
    public void identifier(String act, int ID){
        EventId=ID;//目前檢是的活動位於列表中第幾個
        //檢視詳細資料
        if(act.equals("D")){
            Log.v("test","您正在檢視第"+Aname.get(ID)+"的詳細資料");
            //轉跳到詳細資訊頁
            Intent intent = new Intent(event.this, eventDetail.class);
            startActivity(intent);
        //進入報名環節
        }else if(act.equals("B")){
            Log.v("test","您報名了==>"+Aname.get(ID));
            function = 1;//設定功能為報名活動
            //報名活動階段
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }
    }
    //清空列表以確保活動資訊不會重複疊加
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


    //返回主頁並清除活動列表資料然後關閉此頁面
    public void onBackPressed(){
        entryIsRecent = false;
        Intent intent = new Intent(event.this, LoginAndRegister.class);
        startActivity(intent);
        clear();
        finish();
    }
    //Base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(Actpic.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            int w = proimg.getWidth();
            int h = proimg.getHeight();
            Log.v("test","pic"+ID+" original = "+w+"*"+h);
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
            proimg = Bitmap.createScaledBitmap(proimg, w, h, false);
            return proimg;
        }catch (Exception e){
            Log.v("test","error = "+e.toString());
            return null;
        }
    }
}

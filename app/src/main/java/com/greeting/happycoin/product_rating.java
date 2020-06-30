package com.greeting.happycoin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import static com.greeting.happycoin.MainActivity.Comment;
import static com.greeting.happycoin.MainActivity.DP;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Rating;
import static com.greeting.happycoin.MainActivity.RecDate;
import static com.greeting.happycoin.MainActivity.Serial;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.happypi;
import static com.greeting.happycoin.MainActivity.lv;

public class product_rating extends AppCompatActivity {
    ArrayAdapter adapter;
//    String comment = null;//客戶輸入評價用的變數
//    int star = 0; //客戶的評分
    private ListView listview;


    ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_rating);
        lv("loading success");
        setTitle("平價商品");
        clear();
        DataProcess getData = new DataProcess();
        getData.execute();
    }

    private class DataProcess extends AsyncTask<String, Void, Void>{
        String res;
        String uuid = getUUID(getApplicationContext());
        @Override
        protected Void doInBackground(String... strings) {
            try {//讀取未評價商品
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                CallableStatement cstmt = null;//因需多次使用故先設為null
                ResultSet rs;
                cstmt = con.prepareCall("{call rating(?,?,?,?,?,?)}");
                cstmt.registerOutParameter(3, Types.LONGVARCHAR);
                cstmt.setString(1,uuid);
                cstmt.setString(2,"get");
                cstmt.setInt(4,-1);
                cstmt.setInt(5,-1);
                cstmt.setString(6,"");
                rs = cstmt.executeQuery();
//                lv("result out put：");
                while(rs.next()){
//                    lv(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+rs.getString(7)+" "+rs.getString(8)+" "+rs.getString(9)+" "+rs.getString(10)+" "+rs.getString(11)); //測試輸出取得的資料
                    Serial.add(rs.getInt(1));
                    PID.add(rs.getString(2));
                    Pname.add(rs.getString(3));
                    happypi.add(rs.getString(4));
                    Pprice.add(rs.getInt(5));
                    Pamount.add(rs.getInt(6));
                    Vendor.add(rs.getString(7));
                    RecDate.add(rs.getString(8));
                    Rating.add(rs.getInt(9));
                    Comment.add(rs.getString(10));
                    PIMG.add(rs.getString(11));
                }
            }catch (Exception e){
                lv("ＥＲＲＯＲ：\n"+e.toString());
            }
            return null;
        }
    }
    //轉換為點陣圖(輸入值為PIMG中的陣列位置)
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
        Intent intent = new Intent(product_rating.this,LoginAndRegister.class);//準備轉跳回首頁
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
        happypi.clear();
    }
}

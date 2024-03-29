package com.greeting.happycoin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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
import static com.greeting.happycoin.MainActivity.DP;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.PComment;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.PRating;
import static com.greeting.happycoin.MainActivity.PRecDate;
import static com.greeting.happycoin.MainActivity.PSerial;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pdescribtion;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.hideKB;
import static com.greeting.happycoin.MainActivity.lv;
import static com.greeting.happycoin.MainActivity.popup;
import static com.greeting.happycoin.MainActivity.popupL;


public class ProductRating extends Fragment {
    LinearLayout ll;
    int[] FS ={136, 137, 138, 139, 140, 141, 143, 145, 147, 150, 155, 160, 165, 170, 175, 178, 180, 185, 190, 200, 210, 210, 215, 220};
    public ProductRating() {
        // Required empty public constructor
    }

    public static ProductRating newInstance(){
        return new ProductRating();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_center,container, false);
        // Inflate the layout for this fragment
        ll =view.findViewById(R.id.ll);
        lv("loading success");
        clear();
        DataProcess getData = new DataProcess();
        getData.execute();
        return view;
    }

    private class DataProcess extends AsyncTask<String, Void, String> {
        String res;
        String uuid = getUUID(getActivity().getApplicationContext());
        @Override
        protected String doInBackground(String... strings) {
            try {//讀取未評價商品
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                CallableStatement cstmt = null;//因需多次使用故先設為null
                ResultSet rs;
                cstmt = con.prepareCall("{call product_rating(?,?,?,?,?,?)}");
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
                    PSerial.add(rs.getInt(1));
                    PID.add(rs.getString(2));
                    Pname.add(rs.getString(3));
                    Pdescribtion.add(rs.getString(4));
                    Pprice.add(rs.getInt(5));
                    Pamount.add(rs.getInt(6));
                    Vendor.add(rs.getString(7));
                    PRecDate.add(rs.getString(8));
                    PRating.add(rs.getInt(9));
                    PComment.add(rs.getString(10));
                    PIMG.add(rs.getString(11));
                }
            }catch (Exception e){
                lv("ＥＲＲＯＲ：\n"+e.toString());
                return "發生錯誤";
            }
            return "載入成功";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("載入成功")){
                cardRenderer();
                popup(getActivity().getApplicationContext(),"請稍後...");
            }else{
                popupL(getActivity().getApplicationContext(),"發生錯誤，請聯絡客服中心協助處理");
            }
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
//    public void onBackPressed() {//返回鈕動作
//        Intent intent = new Intent(ActivityRating.this,LoginAndRegister.class);//準備轉跳回首頁
//        startActivity(intent);//轉跳
//        finish();//結束本頁面
//    }

    //清空列表以確保活動資訊不會重複疊加
    public void clear(){
        PSerial.clear();
        PID.clear();
        Pname.clear();
        Pdescribtion.clear();
        Pprice.clear();
        Pamount.clear();
        Vendor.clear();
        PRecDate.clear();
        PRating.clear();
        PIMG.clear();
        PComment.clear();
        PIMG.clear();
    }

    /**移植區(由market.java移植並修改)*/
    //頁面轉跳工具
    public void identifier(int ID){ //動作判定器(判斷按下的按鈕式購買或詳情)
        BuyId=ID; //商品列表中的第幾樣商品
        Log.v("test","您正在評價第"+Pname.get(ID)+"的詳細資料");
        Intent intent = new Intent(getActivity(), eval_product.class);//準備轉跳頁面
        startActivity(intent);//轉跳詳情頁面
        getActivity().finish();//結束本頁面
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
        LinearLayout frame = new LinearLayout(getActivity());//新增LinearLayout(商品卡母容器)
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(//商品卡本身
                LinearLayout.LayoutParams.MATCH_PARENT, // 商品卡寬度
//                DP((FONTsize*6)+15) //設定商品卡高度
//                DP(test)
                DP(FS[FONTsize-12])
        );//LinearLayout 參數設定
        frame.setPadding(DP(15),DP(15),DP(15),DP(15));
        framep.setMargins(0,0,0,DP(20));
        frame.setOrientation(LinearLayout.HORIZONTAL);
        frame.setLayoutParams(framep);
        frame.setBackgroundColor(Color.parseColor("#D1FFDE"));

        //圖片&價格區
        LinearLayout picpri = new LinearLayout(getActivity());
        LinearLayout.LayoutParams picprip = new LinearLayout.LayoutParams(DP(120),DP(200));
        picprip.setMargins(0,0,DP(5),0);
        picpri.setOrientation(LinearLayout.VERTICAL);
        picpri.setLayoutParams(picprip);
/**
 //數量
 final EditText amount = new EditText(getActivity());
 LinearLayout.LayoutParams amountp = new LinearLayout.LayoutParams(
 LinearLayout.LayoutParams.WRAP_CONTENT,
 LinearLayout.LayoutParams.WRAP_CONTENT
 );
 amount.setEms(3);
 amount.setInputType(InputType.TYPE_CLASS_NUMBER);
 amount.setLayoutParams(amountp);
 amount.setId(5*ID+2);
 amount.setText("1");
 */
        //商品圖片
        ImageView propic = new ImageView(getActivity());
        LinearLayout.LayoutParams propicp = new LinearLayout.LayoutParams(DP(120),DP(90));
        //propic.setImageBitmap(Bitmap.createScaledBitmap(ConvertToBitmap(ID), 120, 90, false));
        propic.setImageBitmap(ConvertToBitmap(ID));
        propic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        propic.setLayoutParams(propicp);
        propic.setId(5*ID);
        propic.setOnClickListener(v -> {
            final int id = ID;
            final int quantity = Pamount.get(ID);
            hideKB(getActivity());
            identifier(id);
        });

        //商品價格
        TextView price = new TextView(getActivity());
        LinearLayout.LayoutParams pricep = new LinearLayout.LayoutParams(DP(120),DP(30));
        price.setText("價格: $"+Pprice.get(ID));
        price.setTextSize(FONTsize-3);
        price.setLayoutParams(picprip);

        //商品訊息區
        LinearLayout proinf = new LinearLayout(getActivity());
        LinearLayout.LayoutParams proinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1f
        );
        proinf.setOrientation(LinearLayout.VERTICAL);
        proinf.setLayoutParams(proinfp);

        //商品名稱
        TextView proname = new TextView(getActivity());
        LinearLayout.LayoutParams pronamep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        proname.setText(Pname.get(ID));
        proname.setTextSize(FONTsize-3);
        proname.setClickable(true);
        proname.setLayoutParams(pronamep);
        proname.setId(5*ID+1);

        //購買歷史資訊
        LinearLayout buyinf = new LinearLayout(getActivity());
        LinearLayout.LayoutParams buyinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        buyinf.setOrientation(LinearLayout.HORIZONTAL);
        buyinf.setLayoutParams(buyinfp);

        //購買日期
        TextView amount_label = new TextView(getActivity());
        LinearLayout.LayoutParams amount_labelp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount_label.setText("購買日期："+PRecDate.get(ID).substring(0,11));
        amount_label.setTextSize(FONTsize-3);
        amount_label.setLayoutParams(amount_labelp);

        //按鈕箱
        LinearLayout btnbox = new LinearLayout(getActivity());
        LinearLayout.LayoutParams btnboxp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        btnboxp.setMargins(0,20,0,0);
        btnbox.setLayoutParams(btnboxp);

        //詳情按鈕
        Button detail = new Button(getActivity());
        LinearLayout.LayoutParams detailp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,0.5f
        );
        detailp.setMarginEnd(20);
        detail.setText("評價此商品");
        detail.setTextSize(FONTsize-6);
        detail.setLayoutParams(detailp);
        detail.setId(5*ID+3);
        detail.setTextColor(Color.parseColor("#FFFFFF"));
        detail.setBackgroundResource(R.drawable.rounded_button_green);
        detail.setOnClickListener(v -> {
            final int id = ID;
            final int quantity = Pamount.get(ID);
            hideKB(getActivity());
            identifier(id);
        });
/**
 //訂購按鈕
 Button buybtn = new Button(getActivity());
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
 hideKB(getActivity());
 identifier("B",id,quantity);
 });
 */
        //將內容填入frame
        /**
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
//        buyinf.addView(amount);
        proinf.addView(buyinf);
        btnbox.addView(detail);
//        btnbox.addView(buybtn);
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
}
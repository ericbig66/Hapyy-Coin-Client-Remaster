package com.greeting.happycoin;


import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static android.graphics.Color.parseColor;
import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;


/**
 * A simple {@link Fragment} subclass.
 * record.java的子檔案(子頁面)
 */
public class BuyDiary extends Fragment {
    int function = 0;//功能執行，0表示從資料庫載入商品列表，1表示購買商品
    private ArrayList<String> PID = new ArrayList<>();  //品名
    private ArrayList<String> pname = new ArrayList<>();  //品名
    private ArrayList<String> pprice = new ArrayList<>(); //單價
    private ArrayList<String> pamount = new ArrayList<>(); //購買數量
    private ArrayList<String> total = new ArrayList<>();   //總價
    private ArrayList<String> selldate = new ArrayList<>();//交易日期
    LinearLayout ll; //商品列表顯示區
    String acc;//用於裝載交易UUID

    public BuyDiary() {
        // Required empty public constructor
    }

    //此行需加在每個子頁面上(instance的名稱需與java檔相同)
    public static BuyDiary newInstance() {
        return new BuyDiary();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //註解以下三行
        //TextView textView = new TextView(getActivity());
        //textView.setText(R.string.hello_blank_fragment);
        //return textView;
        //新增下面一行

        View view = inflater.inflate(R.layout.fragment_record, container, false);
        //定義區
        ll = view.findViewById(R.id.tradeData);
        //設定區
        acc = getUUID(getActivity().getApplicationContext());//取得UUID以取得交易資料
        clear();//清除列表避免重複疊加
        //新增表頭至陣列

        //連接資料庫取得資料
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
        return view;//回傳繪製後畫面
    }

    //由資料庫取出交易資料
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
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
                ResultSet rs = st.executeQuery("SELECT productName, price, amount, price*amount, date from sell_record,client where client.id = sell_record.id and acc = '" + acc + "' order by date DESC;");
                //將查詢結果裝入陣列
                while (rs.next()) {
                    //result += rs.getString("paccount")+"\t"+rs.getString("state")+"\t$"+rs.getString("amount")+"\t$"+rs.getString("moneyLeft")+"\n";
                    pname.add(rs.getString(1));
                    pprice.add("$" + rs.getString(2));
                    pamount.add(rs.getString(3));
                    total.add("$" + Integer.parseInt(rs.getString(4)));
                    selldate.add(rs.getString(5).substring(0, 16));

                }
                return String.valueOf(pname.size());
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        //查詢後的結果將回傳於此
        @Override
        //完成查詢後
        protected void onPostExecute(String result) {
            Log.e("a", String.valueOf(pname.size()));
            //dt.setText(result);
            cardRenderer();//繪製商品卡
        }
    }

    //商品卡產生器
    public void cardRenderer() {
        for (int i = 0; i <pname.size(); i++) {//以迴圈產生商品卡
            Log.e("test", "render card " + i);
            add(i);//增加商品卡
        }
    }


    //產生商品卡
    public void add(final int ID) {
        //商品卡片
        LinearLayout frame = new LinearLayout(getActivity());//新增LinearLayout(商品卡母容器)
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(//商品卡本身
                LinearLayout.LayoutParams.MATCH_PARENT, // 商品卡寬度
                DP(110)//設定商品卡高度
        );//LinearLayout 參數設定
        frame.setPadding(DP(15), DP(15), DP(15), DP(15));
        framep.setMargins(0, 0, 0, DP(20));
        frame.setOrientation(LinearLayout.VERTICAL);
        frame.setLayoutParams(framep);
        frame.setBackgroundColor(parseColor("#D1FFDE"));

        //商品名稱
        TextView proname = new TextView(getActivity());
        LinearLayout.LayoutParams pronamep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        proname.setText(pname.get(ID));
        proname.setTextSize(18f);
        proname.setLayoutParams(pronamep);

        //商品計價區
        TextView procounter = new TextView(getActivity());
        procounter.setText("單價"+pprice.get(ID)+" × "+pamount.get(ID)+"件 = 總價"+total.get(ID));
        procounter.setTextSize(18f);
        procounter.setLayoutParams(pronamep);

        //購買時間
        TextView date = new TextView(getActivity());
        date.setText("日期: " + selldate.get(ID));
        date.setTextSize(18f);
        LinearLayout.LayoutParams date1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
                );
        date.setLayoutParams(date1);


        //將產生之物件放入卡片容器中
        frame.addView(proname);
        frame.addView(procounter);
        frame.addView(date);


        Log.v("test", "still alive before last one");
        ll.addView(frame);
//        loading.setVisibility(View.GONE);
        Log.v("test", "card" + ID + "rendered");
    }

    public int DP(float dp) {//寬度單位轉換器(設定值為DP)
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }





    //清除陣列資料避免重複疊加
    public void clear() {
        pname.clear();
        pprice.clear();
        pamount.clear();
        total.clear();
        selldate.clear();
    }


}


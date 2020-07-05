//package com.greeting.happycoin;
//
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//
//import static com.greeting.happycoin.LoginAndRegister.getUUID;
//import static com.greeting.happycoin.LoginAndRegister.pass;
//import static com.greeting.happycoin.LoginAndRegister.url;
//import static com.greeting.happycoin.LoginAndRegister.user;
//
///**
// * A simple {@link Fragment} subclass.
// * record.java的子檔案(子頁面)
// */
//public class EventAttendRecord extends Fragment {
//    private ArrayList<String> Aname  = new ArrayList<>(); //活動名稱
//    private ArrayList<String> Action  = new ArrayList<>();//操作(報名/取消報名)
//    private ArrayList<String> Atime  = new ArrayList<>(); //操作時間
//    private ArrayList<String> Asign  = new ArrayList<>(); //簽到時間
//    TableLayout tradeData;//表格繪製空間
//    String acc ;//用於裝載交易UUID
//    public EventAttendRecord() {
//        // Required empty public constructor
//    }
//    //此行需加在每個子頁面上(instance的名稱需與java檔相同)
//    public static EventAttendRecord newInstance() {
//        return new EventAttendRecord();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        //未簡化步驟以下兩行先行保留
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
////        return textView;  //此處將以下方的新return取代
//        //新增此行
//        View view = inflater.inflate(R.layout.fragment_record,container, false);
//        //定義區
//        tradeData = view.findViewById(R.id.tradeData);
//        //設定區
//       acc = getUUID(getActivity().getApplicationContext());//取得UUID以取得活動報名資料
//       clear();//清除列表避免重複疊加
//        //新增表頭至陣列
//        Aname.add("活動名稱　　");
//        Action.add("操作　　");
//        Atime.add("操作時間　　");
//        Asign.add("簽到時間　　");
//        Log.v("test","\nAsign[0] = "+Asign.get(0)+"\nAname[0] = "+Aname.get(0)+"\nAction[0]"+Action.get(0)+"\nAction[0] = "+Action.get(0)+"\nAtime[0] = "+Atime.get(0)+"\nAsign[0] = "+Asign.get(0));
//        //連接資料庫取得資料
//        ConnectMySql connectMySql = new ConnectMySql();
//        connectMySql.execute("");
//        return view;//回傳繪製後畫面
//    }
//
//    //由資料庫取出活動報名與簽到資料
//    private class ConnectMySql extends AsyncTask<String, Void, String> {
//        String res="";//錯誤信息儲存變數
//        //開始執行動作
//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
////            Toast.makeText(getActivity(),"請稍後...",Toast.LENGTH_SHORT).show();
//        }
//        //開始取得資料
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                //連接資料庫
//                Class.forName("com.mysql.jdbc.Driver");
//                Connection con = DriverManager.getConnection(url, user, pass);
//                //建立查詢
//                //String result = "對方帳戶\t交易\t金額\t餘額\n";
//                Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery("SELECT a.actName, rr.type, rr.time, af.signTime from regist_record rr \n" +
//                        "LEFT JOIN application_form af ON rr.clientID = af.clientID and rr.activityID = af.activityID and rr.time = af.registTime\n" +
//                        "LEFT JOIN activity a on a.id = rr.activityID\n" +
//                        "LEFT JOIN client c on rr.clientID = c.id\n" +
//                        "where c.acc = '"+acc+"'\n" +
//                        "and rr.activityID = a.id\n" +
//                        "ORDER by rr.time");
//                //將查詢結果裝入陣列
//                while(rs.next()){
//                    Aname.add(rs.getString(1));
//                    Action.add(rs.getString(2)+"　");
//                    Atime.add(rs.getString(3).substring(0,16)+"　");
//                    String trytry = "  ";
//                    if(rs.getString(4)!= null && rs.getString(4).trim().length()>0){
//                        trytry = rs.getString(4).substring(0,16);
//                    }
//                    Asign.add(trytry);
//                }
//
//                return "0";
//            }catch (Exception e){
//                e.printStackTrace();
//                res = e.toString();
//            }
//            return res;
//        }
//        //查詢後的結果將回傳於此
//        @Override
//        //完成查詢後
//        protected void onPostExecute(String result) {
//            Log.v("test","YOUR RESULT ="+result);
//            renderTable();//繪製表格
//        }
//
//        //繪製報名與簽到紀錄表格
//        private void renderTable(){
//            for(int row = 0 ; row < Aname.size() ; row++ ){
////                Toast.makeText(Diary.this,"第"+row+"列建構中",Toast.LENGTH_SHORT).show();
//                //新增一列
//                TableRow tr = new TableRow(getActivity());
//                //新增一個TextView
//                TextView t1 = new TextView(getActivity());
//                TextView t2 = new TextView(getActivity());
//                TextView t3 = new TextView(getActivity());
//                TextView t4 = new TextView(getActivity());
//                //設定TextView的文字
//                t1.setText(Aname.get(row));
//                t2.setText(Action.get(row));
////                Log.v("test",trade.get(row));
//                t3.setText(Atime.get(row));
//                t4.setText(Asign.get(row));
//                //將TextView放入列
//                tr.addView(t1);
//                tr.addView(t2);
//                tr.addView(t3);
//                tr.addView(t4);
//                //將整列加入預先建立的TableLayout中
//                tradeData.addView(tr,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            }
//        }
//    }
//
//    //清除陣列資料避免重複疊加
//    public void clear(){
//        Aname.clear();
//        Action.clear();
//        Atime.clear();
//        Asign.clear();
//    }
//}

//new file
package com.greeting.happycoin;


import android.content.res.Resources;
import android.graphics.Color;
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

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;

/**
 * A simple {@link Fragment} subclass.
 * record.java的子檔案(子頁面)
 */
public class EventAttendRecord extends Fragment {
    private ArrayList<String> Aname  = new ArrayList<>(); //活動名稱
    private ArrayList<String> Action  = new ArrayList<>();//操作(報名/取消報名)
    private ArrayList<String> Atime  = new ArrayList<>(); //操作時間
    private ArrayList<String> Asign  = new ArrayList<>(); //簽到時間
    LinearLayout ll; //商品列表顯示區
    String acc ;//用於裝載交易UUID
    public EventAttendRecord() {
        // Required empty public constructor
    }
    //此行需加在每個子頁面上(instance的名稱需與java檔相同)
    public static EventAttendRecord newInstance() {
        return new EventAttendRecord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //未簡化步驟以下兩行先行保留
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
//        return textView;  //此處將以下方的新return取代
        //新增此行
        View view = inflater.inflate(R.layout.fragment_record,container, false);
        //定義區
        ll = view.findViewById(R.id.tradeData);
        //設定區
        acc = getUUID(getActivity().getApplicationContext());//取得UUID以取得活動報名資料
        clear();//清除列表避免重複疊加
        //新增表頭至陣列
//        Aname.add("活動名稱　　");
//        Action.add("操作　　");
//        Atime.add("操作時間　　");
//        Asign.add("簽到時間　　");
//        Log.v("test","\nAsign[0] = "+Asign.get(0)+"\nAname[0] = "+Aname.get(0)+"\nAction[0]"+Action.get(0)+"\nAction[0] = "+Action.get(0)+"\nAtime[0] = "+Atime.get(0)+"\nAsign[0] = "+Asign.get(0));
        //連接資料庫取得資料
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
        return view;//回傳繪製後畫面
    }

    //由資料庫取出活動報名與簽到資料
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
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
                ResultSet rs = st.executeQuery("SELECT a.actName, rr.type, rr.time, af.signTime from regist_record rr LEFT JOIN application_form af ON rr.clientID = af.clientID and rr.activityID = af.activityID and rr.time = af.registTime LEFT JOIN activity a on a.id = rr.activityID LEFT JOIN client c on rr.clientID = c.id where c.acc = '"+acc+"' and rr.activityID = a.id ORDER by rr.time DESC");
                //將查詢結果裝入陣列
                while(rs.next()){
                    Aname.add(rs.getString(1));
                    Action.add(rs.getString(2));
                    Atime.add(rs.getString(3).substring(0,16));
                    String trytry = " -";
                    if(rs.getString(4)!= null && rs.getString(4).trim().length()>0){
                        trytry = rs.getString(4).substring(0,16);
                    }
                    Asign.add(trytry);
                }

                return "0";
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        //完成查詢後
        protected void onPostExecute(String result) {
            Log.v("test","YOUR RESULT ="+result);
            cardRenderer();//繪製商品卡
        }

//          Aname.add("活動名稱　　");
//        Action.add("操作　　");
//        Atime.add("操作時間　　");
//        Asign.add("簽到時間　　");

        //商品卡產生器
        public void cardRenderer() {
            for (int i = 0; i <Aname.size(); i++) {//以迴圈產生商品卡
                Log.e("test", "render card " + i);
                add(i);//增加商品卡
            }
        }


        //產生商品卡
        public void add(final int ID) {
            //資訊卡片
            LinearLayout frame = new LinearLayout(getActivity());//新增LinearLayout(商品卡母容器)
            LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(//商品卡本身
                    LinearLayout.LayoutParams.MATCH_PARENT, // 商品卡寬度
                    DP(125)//設定商品卡高度
            );//LinearLayout 參數設定
            frame.setPadding(DP(15), DP(15), DP(15), DP(15));
            framep.setMargins(0, 0, 0, DP(20));
            frame.setOrientation(LinearLayout.VERTICAL);
            frame.setLayoutParams(framep);
            frame.setBackgroundColor(Color.parseColor("#D1FFDE"));

            //活動名稱
            TextView actname = new TextView(getActivity());
            actname.setText("活動名稱："+Aname.get(ID));
            actname.setTextSize(18f);

            //操作項目及時間
            TextView operate = new TextView(getActivity());
            operate.setText("操作："+Action.get(ID)+"\n操作時間："+Atime.get(ID));
            operate.setTextSize(18f);

            //商品價格
            TextView date = new TextView(getActivity());
            date.setText("簽到時間：" + Asign.get(ID));
            date.setTextSize(18f);

            //將產生之物件放入卡片容器中
            frame.addView(actname);
            frame.addView(operate);
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
    }

    //清除陣列資料避免重複疊加
    public void clear(){
        Aname.clear();
        Action.clear();
        Atime.clear();
        Asign.clear();
    }
}

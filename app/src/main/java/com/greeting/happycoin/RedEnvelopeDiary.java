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
import static com.greeting.happycoin.MainActivity.lv;

/**
 * A simple {@link Fragment} subclass.
 * record.java的子檔案(子頁面)
 */
public class RedEnvelopeDiary extends Fragment {
    private ArrayList<String> ioacc  = new ArrayList<>();    //對方帳戶
    private ArrayList<String> trade  = new ArrayList<>();    //交易方向
    private ArrayList<String> amount = new ArrayList<>();    //交易金額
    private ArrayList<String> dealtTime = new ArrayList<>(); //交易時間
    LinearLayout ll; //商品列表顯示區
    String acc;//用於裝載交易UUID
    public RedEnvelopeDiary() {
        // Required empty public constructor
    }
    //此行需加在每個子頁面上(instance的名稱需與java檔相同)
    public static RedEnvelopeDiary newInstance() {
        return new RedEnvelopeDiary();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //新增下面一行
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        //定義區
        ll = view.findViewById(R.id.tradeData);
        //設定區
        acc = getUUID(getActivity().getApplicationContext());//取得UUID以取得交易資料
        clear();//清除列表避免重複疊加
        //新增表頭至陣列
//        ioacc.add("zzz　　");
//        trade.add("zzz　");
//        amount.add("zzz");
//        dealtTime.add("zz");
        //連接資料庫取得資料
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
        return view;//回傳繪製後畫面
    }

    //由資料庫取出交易資料
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
            lv("------------------------red-------------------");
            try {
                Log.v("test",acc);
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                //String result = "對方帳戶\t交易\t金額\t餘額\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select SndType, if(SndType = 'C',if( sender = (select id from client where acc='"+acc+"'),'"+acc+"',(select name from client where id = sender )), (select name from vendor where vid = sender )), if(SndType = 'C',(select CONCAT(SUBSTR(acc,1,3),SUBSTR(acc,-3)) from client where id = sender ),null), recType, if (recType = 'C',(select name from client where id = receiver),(select name from vendor where vid = receiver)), if(recType = 'C',(select CONCAT(SUBSTR(acc,1,3),SUBSTR(acc,-3)) from client where id = receiver),null), amount, receiveDate from redenvelope_record r, client c where (c.acc = '"+acc+"' and r.sender = c.ID and sndType = 'C') OR (c.acc = '"+acc+"' and r.receiver = c.ID and recType = 'C') ORDER BY receiveDate DESC");
//                lv("select\n" +
//                        "SndType,\n" +
//                        "if(SndType = 'C',(select name from client where id = sender),(select name from vendor where vid = sender)) sender, if(SndType = 'C',(select CONCAT(SUBSTR(acc,1,3),SUBSTR(acc,-3)) from client where id = sender),null) sender_cid,\n" +
//                        "                    \n" +
//                        "recType,\n" +
//                        "if (recType = 'C',(select name from client where id = receiver),(select name from vendor where vid = receiver))\n" +
//                        "receiver, if(recType = 'C',(select CONCAT(SUBSTR(acc,1,3),SUBSTR(acc,-3)) from client where id = receiver),null) receiver_cid,\n" +
//                        "amount, receiveDate \n" +
//                        "from redenvelope_record r, client c\n" +
//                        "where\n" +
//                        "(c.acc = '"+acc+"' and r.sender = c.ID and sndType = 'C')\n" +
//                        "OR (c.acc = '"+acc+"' and r.receiver = c.ID and recType = 'C')");
                //將查詢結果裝入陣列
                while(rs.next()){
                    lv("running");
                    //result += rs.getString("paccount")+"\t"+rs.getString("state")+"\t$"+rs.getString("amount")+"\t$"+rs.getString("moneyLeft")+"\n";
                    String snd, sndCid, sndType, rec, recCid, recType;
                    snd = rs.getString(2)==null?" ":rs.getString(2);
                    sndCid = rs.getString(3)==null?" ":rs.getString(3);
                    sndType = rs.getString(1)==null?" ":rs.getString(1);
                    rec = rs.getString(5)==null?" ":rs.getString(5);
                    recCid = rs.getString(6)==null?" ":rs.getString(6);
                    recType = rs.getString(4)==null?" ":rs.getString(4);
                    //交易方向判斷
                    if (sndType.equals("C")){//客戶送的
                        if (snd.equals(acc)){//我送人的
                            ioacc.add(rec.equals(" ")?recCid+" ":rec);
                            trade.add("送出");
                        }else{//別人送的(我收的)
                            ioacc.add(snd.equals(" ")?sndCid+" ":snd);
                            trade.add("接收");
                        }
                    }else if (sndType.equals("V")){//廠商送的
                        ioacc.add(snd+" ");
                        trade.add("接收");
                    }
                    //將結果裝入陣列
                    amount.add("$"+rs.getString(7));
                    dealtTime.add(rs.getString(8)==null?" ":rs.getString(8).substring(0,16));
//                    lv("sndType= "+sndType+"　sender= "+snd+"　sender_cid= "+sndCid+"　recType= "+recType+"　receiver= "+rec+"　receiver_cid= "+recCid+"　amount= $"+rs.getString("amount")+"　receiveDate= "+rs.getString("receiveDate"));
                }
                return ioacc.size()+"";//回傳陣列大小
            }catch (Exception e){
                e.printStackTrace();
                res = "ERROR:" + e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        //完成查詢後
        protected void onPostExecute(String result) {
            lv("onpsetExe");
            Log.v("test","size= "+result);
            //dt.setText(result);
            cardRenderer();//繪製商品卡
        }

        //        ioacc
//        trade
//        amount
//        dealtTime
//   ioacc.add("對方帳戶　　");
//        trade.add("交易方向　　");
//        amount.add("金額　　");
//        dealtTime.add("交易時間");
        //商品卡產生器
        public void cardRenderer() {
            for (int i = 0; i <ioacc.size(); i++) {//以迴圈產生商品卡
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

            //對方帳戶
            TextView account = new TextView(getActivity());
            LinearLayout.LayoutParams accountp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            account.setText("對方帳戶："+ioacc.get(ID));
            account.setTextSize(18f);
            account.setLayoutParams(accountp);

            //交易方向
            TextView direction = new TextView(getActivity());
            direction.setText("交易方向："+trade.get(ID));
            direction.setTextSize(18f);
            if(trade.get(ID).equals("送出")){direction.setTextColor(Color.parseColor("#ff0000"));}

            //交易金額
            TextView price = new TextView(getActivity());
            price.setText("金額: " + amount.get(ID));
            price.setTextSize(18f);
            if(trade.get(ID).equals("送出")){price.setTextColor(Color.parseColor("#ff0000"));}

            //商品價格
            TextView date = new TextView(getActivity());
            date.setText("交易時間: " + dealtTime.get(ID));
            date.setTextSize(18f);

            //將產生之物件放入卡片容器中
            frame.addView(account);
            frame.addView(direction);
            frame.addView(price);
            frame.addView(date);
            Log.v("test", "still alive before last one RED");
            ll.addView(frame);
//        loading.setVisibility(View.GONE);
            Log.v("test", "card" + ID + "rendered RED");
        }

        public int DP(float dp) {//寬度單位轉換器(設定值為DP)
            dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return (int) dp;
        }

    }
    //清除陣列資料避免重複疊加
    public void clear(){
        ioacc.clear();
        trade.clear();
        amount.clear();
        dealtTime.clear();
    }
}


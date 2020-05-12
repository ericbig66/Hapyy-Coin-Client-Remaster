package com.greeting.happycoin;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
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
public class RedEnvelopeDiary extends Fragment {
    private ArrayList<String> ioacc  = new ArrayList<>();    //對方帳戶
    private ArrayList<String> trade  = new ArrayList<>();    //交易方向
    private ArrayList<String> amount = new ArrayList<>();    //交易金額
    private ArrayList<String> dealtTime = new ArrayList<>(); //交易時間
    TableLayout tradeData;//表格繪製空間
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
        tradeData = view.findViewById(R.id.tradeData);
        //設定區
        acc = getUUID(getActivity().getApplicationContext());//取得UUID以取得交易資料
        clear();//清除列表避免重複疊加
        //新增表頭至陣列
        ioacc.add("對方帳戶　　");
        trade.add("交易方向　　");
        amount.add("金額　　");
        dealtTime.add("交易時間");
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
            try {
                Log.v("test",acc);
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                //String result = "對方帳戶\t交易\t金額\t餘額\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select\n" +
                        "SndType,\n" +
                        "if(SndType = 'C',(select name from client where id = sender),(select name from vendor where vid = sender)) sender, if(SndType = 'C',(select CONCAT(SUBSTR(acc,1,3),SUBSTR(acc,-3)) from client where id = sender),null) sender_cid,\n" +
                        "                    \n" +
                        "recType,\n" +
                        "if (recType = 'C',(select name from client where id = receiver),(select name from vendor where vid = receiver))\n" +
                        "receiver, if(recType = 'C',(select CONCAT(SUBSTR(acc,1,3),SUBSTR(acc,-3)) from client where id = receiver),null) receiver_cid,\n" +
                        "amount, receiveDate \n" +
                        "from redenvelope_record r, client c\n" +
                        "where\n" +
                        "(c.acc = '"+acc+"' and r.sender = c.ID and sndType = 'C')\n" +
                        "OR (c.acc = '"+acc+"' and r.receiver = c.ID and recType = 'V')");
                //將查詢結果裝入陣列
                while(rs.next()){
                    //result += rs.getString("paccount")+"\t"+rs.getString("state")+"\t$"+rs.getString("amount")+"\t$"+rs.getString("moneyLeft")+"\n";
                    String snd, sndCid, sndType, rec, recCid, recType;
                    snd = rs.getString("sender")==null?" ":rs.getString("sender");
                    sndCid = rs.getString("sender_cid")==null?" ":rs.getString("sender_cid");
                    sndType = rs.getString("sndType")==null?" ":rs.getString("sndType");
                    rec = rs.getString("receiver")==null?" ":rs.getString("receiver");
                    recCid = rs.getString("receiver_cid")==null?" ":rs.getString("receiver_cid");
                    recType = rs.getString("recType")==null?" ":rs.getString("recType");
                    //交易方向判斷
                    if (sndType.equals("C")){//客戶送的
                        if (snd.equals(acc)){//我送人的
                            ioacc.add(rec.equals(" ")?recCid+" ":rec);
                            trade.add("送出 ");
                        }else{//別人送的(我收的)
                            ioacc.add(snd.equals(" ")?sndCid+" ":snd);
                            trade.add("接收 ");
                        }
                    }else if (sndType.equals("V")){//廠商送的
                        ioacc.add(snd+" ");
                        trade.add("接收 ");
                    }
                    //將結果裝入陣列
                    amount.add("$"+rs.getString("amount")+"  ");
                    dealtTime.add(rs.getString("receiveDate")==null?" ":rs.getString("receiveDate").substring(0,16));
                }
                return ioacc.size()+"";//回傳陣列大小
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
            Log.v("test","size= "+result);
            //dt.setText(result);
            renderTable();//繪製表格
        }

        //繪製交易資料表格
        private void renderTable(){
            for(int row = 0 ; row < ioacc.size() ; row++ ){
//                Toast.makeText(Diary.this,"第"+row+"列建構中",Toast.LENGTH_SHORT).show();
                //新增一列
                TableRow tr = new TableRow(getActivity());
                //新增一個TextView
                TextView t1 = new TextView(getActivity());
                TextView t2 = new TextView(getActivity());
                TextView t3 = new TextView(getActivity());
                TextView t4 = new TextView(getActivity());
                //設定TextView的文字
                t1.setText(ioacc.get(row));
                t2.setText(trade.get(row));
//                Log.v("test",trade.get(row));
                t3.setText(amount.get(row));
                t4.setText(dealtTime.get(row));
                //將TextView放入列
                tr.addView(t1);
                tr.addView(t2);
                tr.addView(t3);
                tr.addView(t4);
                //將整列加入預先建立的TableLayout中
                tradeData.addView(tr,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
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

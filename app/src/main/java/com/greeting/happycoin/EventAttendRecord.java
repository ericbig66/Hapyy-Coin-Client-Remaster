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
 */
public class EventAttendRecord extends Fragment {

//    private ArrayList<String> Aid  = new ArrayList<>();
    private ArrayList<String> Aname  = new ArrayList<>();
    private ArrayList<String> Action  = new ArrayList<>();
    private ArrayList<String> Atime  = new ArrayList<>();

//    private ArrayList<String> Att  = new ArrayList<>();
    private ArrayList<String> Asign  = new ArrayList<>();

//    String sign[];

    TableLayout tradeData;

    String acc ;
    public EventAttendRecord() {
        // Required empty public constructor
    }

    public static EventAttendRecord newInstance() {
        return new EventAttendRecord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
//        return textView;

        View view = inflater.inflate(R.layout.fragment_record,container, false);
        clear();
       acc = getUUID(getActivity().getApplicationContext());
        tradeData = view.findViewById(R.id.tradeData);
//        Aid.add("empty");
//        Att.add("empty");
        Aname.add("活動名稱　　");
        Action.add("操作　　");
        Atime.add("操作時間　　");
        Asign.add("簽到時間　　");
        Log.v("test","\nAsign[0] = "+Asign.get(0)+"\nAname[0] = "+Aname.get(0)+"\nAction[0]"+Action.get(0)+"\nAction[0] = "+Action.get(0)+"\nAtime[0] = "+Atime.get(0)+"\nAsign[0] = "+Asign.get(0));
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
        return view;
    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
//            Toast.makeText(getActivity(),"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                //String result = "對方帳戶\t交易\t金額\t餘額\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT a.actName, rr.type, rr.time, af.signTime from regist_record rr \n" +
                        "LEFT JOIN application_form af ON rr.clientID = af.clientID and rr.activityID = af.activityID and rr.time = af.registTime\n" +
                        "LEFT JOIN activity a on a.id = rr.activityID\n" +
                        "LEFT JOIN client c on rr.clientID = c.id\n" +
                        "where c.acc = '"+acc+"'\n" +
                        "and rr.activityID = a.id\n" +
                        "ORDER by rr.time");
                //將查詢結果裝入陣列
                while(rs.next()){
                    Aname.add(rs.getString(1));
                    Action.add(rs.getString(2)+"　");
                    Atime.add(rs.getString(3).substring(0,16)+"　");
                    String trytry = "  ";
                    if(rs.getString(4)!= null && rs.getString(4).trim().length()>0){
                        trytry = rs.getString(4).substring(0,16);
                    }
                    Asign.add(trytry);
                }



//                Statement st2 = con.createStatement();
//                ResultSet rs2 = st2.executeQuery("select activity, signTime from attendlist where account = '"+acc+"'");
////                Log.v("test", "select activity, signTime from attendlist where account = '"+acc+"'");
//                while (rs2.next()){
//                    Att.add(rs2.getString("activity"));
//                    Asign.add(rs2.getString("signTime"));
////                    Log.v("test", "signTime[string] = "+rs2.getString("signTime"));
////                    Log.v("test", "signTime[time] = "+rs2.getTime("signTime"));
//                }
////                Log.v("test","Asign["+1+"] = "+Asign.get(1));
//
//                sign = new String[Aid.size()];
//                for(int i = 1 ; i< Aid.size() ; i++){sign[i] = "";}
//                sign[0] = "簽到時間";
//                Log.v("test", "Aid.size = "+Aid.size()+" / Att.size = "+Att.size()+" / sign.size = "+sign.length);
//                for(int i = 1 ; i<=Att.size() ; i++){
//
//                    Log.v("test","inside for...");
//                    Log.v("test","Asign["+i+"] = "+Asign.get(i));
//                    int ST = Aid.lastIndexOf(Att.get(i));
//                    Log.v("test","Last index of '"+Att.get(i)+"' is " + ST);
//                    sign[ST]= Asign.get(i);
//                    Log.v("test","sign["+ST+"] =  " + sign[ST]);
//                }

                return "0";
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            Log.v("test","YOUR RESULT ="+result);
            renderTable();
        }
        private void renderTable(){
            for(int row = 0 ; row < Aname.size() ; row++ ){
//                Toast.makeText(Diary.this,"第"+row+"列建構中",Toast.LENGTH_SHORT).show();
                //新增一列
                TableRow tr = new TableRow(getActivity());
                //新增一個TextView
                TextView t1 = new TextView(getActivity());
                TextView t2 = new TextView(getActivity());
                TextView t3 = new TextView(getActivity());
                TextView t4 = new TextView(getActivity());
                //設定TextView的文字
                t1.setText(Aname.get(row));
                t2.setText(Action.get(row));
//                Log.v("test",trade.get(row));
                t3.setText(Atime.get(row));
                t4.setText(Asign.get(row));
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

    public void clear(){
//        Aid.clear();
//        Att.clear();
        Aname.clear();
        Action.clear();
        Atime.clear();
        Asign.clear();
    }

}

package com.greeting.happycoin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.lv;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class fontsize extends AppCompatActivity {

//    private FirebaseAuth mAuth;
    NumberPicker fontsize; //數字
    private Button execute_create1;//確定鈕
    Float FontSIZE;//字體大小的數字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fontsize);
//        mAuth = FirebaseAuth.getInstance();

        execute_create1 = (Button)findViewById(R.id.enter);//確定鈕
        execute_create1.setOnClickListener(btnListener); //確定鈕
        fontsize = (NumberPicker)findViewById(R.id.numberpicker);//數字選擇器
        fontsize.setEnabled(true);
        fontsize.setMaxValue(35); //選擇器最大35
        fontsize.setMinValue(12); //選擇器最小12
        fontsize.setValue(FONTsize); //一開使顯示20

        TextView size = (TextView)findViewById(R.id.fontsize);//取得前端文字id
        size.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize); //當下畫面的 '字體大小' 轉變成剛剛調的大小

        fontsize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                TextView size = (TextView)findViewById(R.id.fontsize);//取得前端文字id
                size.setTextSize(TypedValue.COMPLEX_UNIT_SP,newVal); //當下畫面的 '字體大小' 轉變成剛剛調的大小
            }
        });
    }
    /**
    private void size(){
       mAuth = FirebaseAuth.getInstance();
        //   if(mAuth.getCurrentUser())
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("membersitting"); //連接membersitting資料庫
        myRef.addValueEventListener(new ValueEventListener() { //修改數字
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid=mAuth.getCurrentUser().getUid();//取得使用者UID
                String fontsize = dataSnapshot.child(uid).child("fontsize").getValue(String.class);//該使用者的fontsize是多少，放入String fontsize
                FontSIZE = Float.parseFloat(fontsize); //String fontsize轉Float
                TextView size = (TextView)findViewById(R.id.fontsize);//取得前端文字id
                size.setTextSize(TypedValue.COMPLEX_UNIT_SP,FontSIZE); //當下畫面的 '字體大小' 轉變成剛剛調的大小
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
     */
    //將字型大小寫入置資料庫
    private class Size extends AsyncTask<Void,Void,String>{
        String uuid = getUUID(getApplicationContext());//自動取得此手機之UUID
        String res;//紀錄回傳結果用
        int fontSize = 0;
        @Override
        protected void onPreExecute() {
            fontSize = fontsize.getValue();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                Statement st = con.createStatement();
                st.execute("update client set fontsize = "+ fontSize +" where acc = '"+uuid+"';");
                ResultSet rs = st.executeQuery("select fontsize from client where acc = '"+uuid+"';");
                rs.next();
                res = rs.getString(1);
            } catch (Exception e) {
                e.printStackTrace();
                res ="Error: " + e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            lv(res);
            super.onPostExecute(s);
            if (s.contains("Error")){s="20";}
            FONTsize = Integer.parseInt(s);
            TextView size = (TextView)findViewById(R.id.fontsize);//取得前端文字id
            size.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(s)); //當下畫面的 '字體大小' 轉變成剛剛調的大小
        }
    }


    private View.OnClickListener btnListener = v -> {
        //呼叫size()
        Size size = new Size();
        size.execute();
        new AlertDialog.Builder(fontsize.this)//跳框框顯示"修改字體大小成功!"
                .setMessage("修改字體大小成功!")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(fontsize.this, AlterMember.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    };
/**
    public static class membersitting {
        public String uid,fontsize;

        public membersitting(String fontsize) {//將剛剛調的fontsize放入資料庫
            this.uid = uid;
            this.fontsize = fontsize;
        }
    }
*/
}

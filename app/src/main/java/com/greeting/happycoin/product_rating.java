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

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class product_rating extends AppCompatActivity {
    ArrayAdapter adapter;
//    String comment = null;//客戶輸入評價用的變數
//    int star = 0; //客戶的評分
    private ListView listview;
//    private FirebaseAuth mAuth;

    ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_rating);
        lv("loading success");
        setTitle("平價商品");
        clear();
//        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        //此頁面為小胖註解
//        getData();
        DataProcess getData = new DataProcess();
        getData.execute();
    }
//    private void getData() {
//        //連線資料庫 連線到兌換的紀錄
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("exchange_record");
//        myRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                adapter.clear();
//                mAuth = FirebaseAuth.getInstance();
//                String uid = mAuth.getCurrentUser().getUid();//取得目前使用者UID
//
//                myListView = (ListView) findViewById(R.id.myListView);
//                //创建ArrayList对象 并添加数据
//                ArrayList<HashMap<String, String>> myArrayList = new ArrayList<HashMap<String, String>>();
//                //for迴圈跑數據 列出所有數據
//                for (DataSnapshot contact : dataSnapshot.getChildren()) {
//                    //getchild就等於呼叫資料庫的這個欄位名稱的資料
//                    String product_name = contact.child("p_name").getValue().toString();
//                    String e_pid = contact.child("exchange_pid").getValue().toString();
//                    String e_uid = contact.child("exchange_uid").getValue().toString();
//                    String e_time = contact.child("time").getValue().toString();
//                    String e_key = contact.getKey();
//                    //這邊要特別說明 這個key在firebase裡面是代表這筆資料的
//                    //特別id 亂碼的那種 有點像mysql的流水號 反正當成主鍵來看待
//
//                    //hashmap放資料 我只會用 概念是甚麼不知道
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("itemName", product_name);
//                    map.put("e_pid", e_pid);
//                    map.put("e_uid", e_uid);
//                    map.put("e_time", e_time);
//                    map.put("e_key", e_key);
//                    myArrayList.add(map);
//                }
//                SimpleAdapter mySimpleAdapter = new SimpleAdapter(product_rating.this, myArrayList,
//                        R.layout.list_star,//ListView内部数据展示形式的布局文件listitem.xml
//                        new String[]{"itemName", "e_time"},//HashMap中的两个key值 itemTitle和itemContent
//                        new int[]{R.id.p_name, R.id.e_time});/*布局文件listitem.xml中组件的id
//                                                            布局文件的各组件分别映射到HashMap的各元素上，完成适配*/
//                //用陣列讀取 然後再去xml裡面找 然後帶入印出資料
//
//                myListView.setAdapter(mySimpleAdapter);
//
//                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                        //获得选中项的HashMap对象
//                        HashMap<String, String> map = (HashMap<String, String>) myListView.getItemAtPosition(arg2);
//                        String product_id = map.get("e_pid");
//                        String title = map.get("itemName");
//                        String key = map.get("e_key");
//                        //這邊是在做送值 上面hashmap的陣列讀出資料 炫告String來裝 下面你看得懂
//                        Intent intent = new Intent(product_rating.this, product_star.class);
//                        intent.putExtra("product_uid",product_id);
//                        intent.putExtra("e_key",key);//主鍵
//                        //這是以兌換資料 所以這是這筆資料的主鍵 這邊等等會把它傳送到下一個頁面 方便在下個頁面可以找到這筆對面資料
//                        startActivity(intent);
//                        Toast.makeText(getApplicationContext(),
//                                "進入  : "   + title + "的資料頁面" ,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//
//        });
//    }

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

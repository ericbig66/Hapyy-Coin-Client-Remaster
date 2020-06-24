package com.greeting.happycoin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.PID;
import static com.greeting.happycoin.MainActivity.PIMG;
import static com.greeting.happycoin.MainActivity.Pamount;
import static com.greeting.happycoin.MainActivity.Pname;
import static com.greeting.happycoin.MainActivity.Pprice;
import static com.greeting.happycoin.MainActivity.Vendor;
import static com.greeting.happycoin.MainActivity.happypi;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class product_rating extends AppCompatActivity {
    ArrayAdapter adapter;
    private ListView listview;
//    private FirebaseAuth mAuth;

    ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_rating);
        setTitle("平價商品");
        clear();
//        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        //此頁面為小胖註解
//        getData();
        DataProcess getData = new DataProcess();
        getData.execute("GET");
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

    private class DataProcess extends AsyncTask<String, Void, String>{
        String res;
        String uuid = getUUID(getApplicationContext());
        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                CallableStatement cstmt = null;//因需多次使用故先設為null
                if (strings.equals("GET")){

                }else if (strings.equals("SET")){

                }
            }catch (Exception e){

            }

            return null;
        }
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

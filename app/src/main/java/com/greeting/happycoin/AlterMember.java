package com.greeting.happycoin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.inf;
import static com.greeting.happycoin.LoginAndRegister.nm;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.pf;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.FONTsize;
import static com.greeting.happycoin.MainActivity.getPfr;
import static com.greeting.happycoin.MainActivity.hideKB;
import static com.greeting.happycoin.MainActivity.popup;
import static com.greeting.happycoin.MainActivity.setPfr;
import static java.lang.String.valueOf;

public class AlterMember extends AppCompatActivity {
    Switch greet;//稱呼方式選擇鈕
    CircularImageView profile; //頭像
    Button CD, RD, Upload, Rotate, Cancel; // 換機鈕, 資料繼承鈕, 上傳頭像鈕, 頭像旋轉鈕, 取消選取性別
    RadioButton male,female;// 男, 女
    EditText Name, Nname, Address; // 性名, 暱稱, 通訊地址
    DatePicker Birthday;//生日
    ImageView CDQR; //換機用QRcode顯示處
    SurfaceView RDCAM; //資料接收時掃描畫面顯示處
    BarcodeDetector barcodeDetector; //繼承資料掃描器
    CameraSource cameraSource; //繼承資料時需要相機使用權限
    String operate = "sav";//操作功能別(sav = 保存修改、CD = 換機、RD = 資料繼承、back = 返回)
    String ouuid; //舊裝置的UUID
    String pwd; //換機用密碼
    LinearLayout common;//正常模式下的內容
    TextView CDhint, RDhint;//換機與繼承資料的提示
    String npf = inf[3], nnm, nnn, nge, nbd = null, nad; //待輸出的新值(頭像、姓名、暱稱、性別、生日、通訊地址)
    Float npfr = Float.parseFloat(inf[4]);//待輸出的新值(頭像旋轉角度)
    final Integer OPEN_PIC = 1021;//開啟頭像時須使用的程式執行序號

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_member);
        //定義區
        common = findViewById(R.id.common);
        greet = findViewById(R.id.greet);
        profile = findViewById(R.id.profile);
        CD = findViewById(R.id.CD);
        RD = findViewById(R.id.RD);
        Upload = findViewById(R.id.Upload);
        Rotate = findViewById(R.id.Rotate);
        Cancel = findViewById(R.id.Cancel);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        Name = findViewById(R.id.Name);
        Nname = findViewById(R.id.Nname);
        Address = findViewById(R.id.Address);
        CDQR = findViewById(R.id.CDQR);
        RDCAM = findViewById(R.id.RDCAM);
        Birthday = findViewById(R.id.Birthday);
        CDhint = findViewById(R.id.CDhint);
        RDhint = findViewById(R.id.RDhint);
        //設定區
        //設定相機
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1080,1920).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();

        //設定生日選擇器(最晚日期為今日)
        Birthday.setMaxDate(new Date().getTime());

        //自動帶入稱呼偏好設定
        greet.setChecked(getPfr("HCgreet",getApplicationContext()));
        //當設定被改變時將自動儲存
        greet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setPfr("HCgreet",isChecked, getApplicationContext());
            greet.setText(isChecked?"以暱稱稱呼您":"以姓名稱呼您");
        });
        // 頭像[3]    頭像角度[4]      生日[5]    性別[6]   送貨地址[7]
        //自動帶入舊資料
        if(inf[3].equals("null")){profile.setImageResource(R.drawable.df_profile);}//若無頭像則使用預設提供的
        else{profile.setImageBitmap(pf);}//若有頭像則使用設定的
        Name.setText(nm[0].equals("null")?"":nm[0]);//姓名
        Nname.setText(nm[1].equals("null")?"":nm[1]);//暱稱
        if(inf[6].equals("m")){male.setChecked(true);}//性別
        else if(inf[6].equals("f")){female.setChecked(true);}
//        Log.v("test","Your birthday is "+inf[5]);
        if((!inf[5].equals("null")) && inf[5].length()==10){//生日
            String[] tmp;
            tmp = inf[5].split("-");
//            Log.v("test","setting birthday...");
            //帶入生日
            Birthday.init(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1])-1,Integer.parseInt(tmp[2]),null);
        }
        Address.setText(inf[7].equals("null")?"":inf[7]);//通訊地址
        profile.setRotation(Float.parseFloat(inf[4]));//頭像角度
        //點擊換機時的動作
        CD.setOnClickListener(v -> {
            setAlert("換機確認", "您是否要將此帳號轉移至另一支手機?\n", "是", "否", this);
        });

        //點選繼承資料時的動作
        RD.setOnClickListener(v -> {
            setAlert("繼承資料確認", "您是否要使用這支手機取代舊手機?\n舊手機上的資料將被刪除由這支手機繼承\n且此手機上原有之帳號將被清除且無法自行復原","確認換機", "再考慮一下", this);
        });

        findViewById(R.id.FontSizeAlter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlterMember.this, fontsize.class);
                startActivity(intent);
                finish();
            }
        });
        SetFontSize();//設定字型大小
        hideKB(this);
    }

    //更新會員資料、換機與資料繼承(連接資料庫)
    private class AlterSQL extends AsyncTask<Void,Void,String> {
        String ip = null;//IP功能目前開發中***
        public String uuid = getUUID(getApplicationContext());//取得裝置UUID供資料更新使用
        //連接資料庫前的資料轉換
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (operate){//取得轉換方式
                case "sav"://資料儲存
                    getBirthday();
                    popup(getApplicationContext(),"資料儲存中，請稍後...");
                    break;
                case "CD"://換機
                    CDQR.setVisibility(View.VISIBLE);
                    CDhint.setVisibility(View.VISIBLE);
                    CD.setVisibility(View.GONE);
                    RD.setVisibility(View.GONE);
                    popup(getApplicationContext(),"QRcode產生中，請稍後...");
                    break;
                case "RD"://資料繼承
                    popup(getApplicationContext(),"資料繼承中，請稍後...");
                    break;
                case "back":
                    popup(getApplicationContext(),"請稍後...");
                    break;
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = null;
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                Statement st = con.createStatement();
                ResultSet rs;
                rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");
                rs.next();
                ip = rs.getString(1);
                CallableStatement cstmt;
                switch (operate){
                    case "CD"://換機
                        cstmt = con.prepareCall("{? = call change_device(?,?,?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2,ip);
                        cstmt.setString(3,uuid);
                        cstmt.setString(4,"g;4jo4gk42u/4");
                        cstmt.setString(5,"");
                        cstmt.execute();
                        res = cstmt.getString(1);
                        break;

                    case "RD"://繼承資料
                        cstmt = con.prepareCall("{? = call change_device(?,?,?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2,ip);
                        cstmt.setString(3,ouuid);
                        cstmt.setString(4,uuid);
                        cstmt.setString(5,pwd);
                        cstmt.execute();
                        res = cstmt.getString(1);
//                        Log.v("test","your ip = "+ip+" ouuid = "+ouuid+" uuid = "+uuid+" pwd = "+pwd);
//                        res = "暫停";
                        break;

                    case "sav"://儲存變更
                        cstmt = con.prepareCall("{? = call alter_member(?,?,?,?,?,?,?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2,uuid);
                        cstmt.setString(3,npf);
                        cstmt.setFloat(4,npfr);
                        cstmt.setString(5,nnm);
                        cstmt.setString(6,nnn);
                        cstmt.setString(7,nge);
                        cstmt.setString(8,nbd);
                        cstmt.setString(9,nad);
                        cstmt.execute();
                        res = cstmt.getString(1);
                        break;

                    case "back"://返回主畫面
//                        Log.v("test","your sql = "+"select pwd from client where acc = '"+uuid+"'");
                        rs = st.executeQuery("select isnull(pwd), count(acc) from client where acc = '"+uuid+"'");
                        Log.v("test","your sql = "+"select isnull(pwd), count(acc) from client where acc = '"+uuid+"'");
//                        rs = st.executeQuery("select isnull(pwd), count(acc) from client where acc = '2222'");
//                        String compare = "0";
//                        if(!rs.next()){Log.v("test","rs.next is false");}
//                        else{rs.next();compare = rs.getString(1);}
                        rs.next();
//                        compare = rs.getString(1);
                       //******************bug auto back and close QRcode fail!!!!!!!!!!!!
                        res = (rs.getString(1).equals("1"))?"換機成功":"取消成功";
                        Log.v("test","res of back = "+res);
                        if(res.equals("換機成功")){
                            Intent intent = new Intent(getApplicationContext(), LoginAndRegister.class);
                            startActivity(intent);
                            finish();
                        }
                        if(res.equals("取消成功")){
                           // query account id and delete the pwd of changing device to ensure account secure
                           rs = st.executeQuery("select ID from client where acc = '"+uuid+"'");
                           rs.next();
                           String id = rs.getString(1);
                           st.executeUpdate("update client set pwd = "+null+ " where acc = '"+uuid+"' and ID = '"+id+"'");
                        }
                        break;
                    default:
                        res = "發生錯誤請稍後重試或連繫客服人員";
                }
            }catch (Exception e){
                e.printStackTrace();
                res ="catched!!"+ e.toString();
            }
            Log.v("test","mode = "+operate+"  res = "+res);
            return res;
        }
        //執行結果
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("test","mode = "+operate+"  result = "+result);
            //轉換成功時回到主畫面，否則提示錯誤
            if(result.contains("成功")){
                switch (operate){
                    case "sav":
                        Log.v("test","DATA saved");
                        Intent intent = new Intent(getApplicationContext(), LoginAndRegister.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "CD":
                        operate = "back";
                        String[] tmp = result.split(",,");
                        Log.v("test","render text = "+tmp[1]);
                        renderQRcode(tmp[1]);
                        break;
                    case "RD":
                        operate = "back";
                        onBackPressed();
                        break;
                    case "back":
                        if(result.contains("取消")){
                            operate = "cancel";
                            Log.v("test","going to operate = "+ operate);
                            onBackPressed();
                        }else{
                            Intent intent2 = new Intent(getApplicationContext(), LoginAndRegister.class);
                            startActivity(intent2);
                            finish();
                        }
                        break;
                }
            }else{popup(getApplicationContext(),"發生錯誤請稍後重試或連繫客服人員");}

        }
    }
//-------------------------------------------------------------------------------------------
    //訊息確認框(兩個按鈕)
    public void setAlert(String title, String msg, String pos, String nag, Context context) {
        //建立更新資訊提示
        boolean rs = false;//預設按下否定按鈕
        AlertDialog.Builder alert = new AlertDialog.Builder(context);//建立訊息框
        alert.setTitle(title);//設定訊息框標題
        alert.setMessage(msg);//設定訊息內容
        //新增開關(按鈕)
        alert.setPositiveButton(pos, (dialog, id) -> {
            // 點選確認按鈕
            switch (title){
                case "換機確認":
                    operate = "CD";
                    AlterSQL alterSQL = new AlterSQL();
                    alterSQL.execute();
                    break;
                case "繼承資料確認":
                    operate = "RD";
                    inheritData();
                    break;
            }


        });
        alert.setNegativeButton(nag, (dialog, id) -> {
            // 點選否定(取消)時不須執行動作
        });

        // Create the AlertDialog
        AlertDialog dialog = alert.create();
        dialog.show();
    }
//-----------------------------------------------------------------
    //重新定義返回鈕動作，以返回主畫面
    @Override
    public void onBackPressed() {
        switch (operate){
            //若前次執行動作為換機、儲存、返回時
            case "CD":
            case "sav":
            case "back":
                checker();
                AlterSQL alterSQL = new AlterSQL();
                alterSQL.execute();
                break;
            case "RD":
            case "cancel":
                common.setVisibility(View.VISIBLE);
                CD.setVisibility(View.VISIBLE);
                RD.setVisibility(View.VISIBLE);
                CDQR.setVisibility(View.GONE);
                RDCAM.setVisibility(View.GONE);
                CDhint.setVisibility(View.GONE);
                RDhint.setVisibility(View.GONE);
                operate = "sav";
        }
    }
//------------------------------------------------------------------
    // 頭像[3]    頭像角度[4]      生日[5]    性別[6]   送貨地址[7]
    // 將資料設定到參數中以插入資料庫
    private void checker(){
       nnm = Name.getText().toString();
       nnn = Nname.getText().toString();
       nge = male.isChecked()?"m":(female.isChecked()?"f":null);
       nad = Address.getText().toString();
       //頭像、頭像角度、生日將個別由專用函式處理
    }
//--------------------------------------------------------------------------------
    //頭像處理函式

    //選擇頭像圖片
    public void picOpen(View v){
        ((BitmapDrawable) profile.getDrawable()).getBitmap().recycle();//一定要做否則會當機
        Intent intent = new Intent();//準備開啟檔案選擇器
        intent.setType("image/*");//限制只能選圖片
        intent.setAction(Intent.ACTION_GET_CONTENT);//開啟檔案選擇器
        startActivityForResult(Intent.createChooser(intent,"請選擇您的頭像"), OPEN_PIC);//等待使用者選擇檔案或取消選取
    }
    //取得圖片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        //確認選擇或取消完畢
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){//如果有選圖片
            Uri imgdata = data.getData();//取得圖片資訊
            profile.setImageURI(imgdata);//設定頭像預覽圖
            pf = ((BitmapDrawable)profile.getDrawable()).getBitmap();//取得投向圖片
            //將圖片轉換為Base64
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    //圖片轉換器(點陣圖轉Base64)
    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pf.compress(Bitmap.CompressFormat.JPEG, 30, baos);//設定轉換品質
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return imageString;//回傳轉換結果
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            npf = s;//設定新頭像的Base64值
        }
    }
//---------------------------------------------------------------------------------
    //頭像旋轉處理器
    public void profileRotater(View v){
        npfr = (npfr+90f)>=360f?0f:(npfr+90f);//設定角度(在0、90、180，270間循環)
        profile.setRotation(npfr);//設定預覽圖角度
    }
//-------------------------------------------------------------------------------------
    //生日處理器

    //系統時間處理器
    Date curDate = new Date(System.currentTimeMillis());//取得系統時間
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//將系統時間格式化為 年年年年-月月-日日
    //格式化出可直接使用的年月日變數
    String today = formatter.format(curDate);
    //取得生日
    private void getBirthday(){
        String Y, M, D;
        Y = valueOf(Birthday.getYear());
        M = valueOf(Birthday.getMonth()+1);
        M = M.length()<2?"0"+M:M;
        D = valueOf(Birthday.getDayOfMonth());
        D = D.length()<2?"0"+D:D;
        nbd = Y+"-"+M+"-"+D;
        nbd = nbd.equals(today)?null:nbd;
    }
//----------------------------------------------------------------------------------------
    //QRcode 產生器 (必要資料: uuid, 換機密碼)
    private void renderQRcode(String pwd){
        BarcodeEncoder encoder = new BarcodeEncoder();
        try{
            common.setVisibility(View.GONE);//隱藏資料修改區
            //繪製QRcode
            Bitmap code = encoder.encodeBitmap(getUUID(getApplicationContext())+"cj04ru,"+pwd, BarcodeFormat.QR_CODE,1000,1000);
            CDQR.setImageBitmap(code);//將換機QRcode放入容器
        }catch (Exception e){
            Log.v("test",e.toString());
        }
    }
//--------------------------------------------------------------------------------------------
    //QRcode掃描器
    String[] RDdata;
    String qdata = "", tmp = "";
    private void inheritData(){

        common.setVisibility(View.GONE);//引藏資料修改區
        RDCAM.setVisibility(View.VISIBLE);//開啟掃描器預覽畫面
        RDhint.setVisibility(View.VISIBLE);//顯示資料繼承提示
        CD.setVisibility(View.GONE);//隱藏換機按鈕
        RD.setVisibility(View.GONE);//隱藏資料接收鈕
        popup(getApplicationContext(),"請稍後...");//請使用者等一下
        //要求相機權限
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else{
            RDCAM.getHolder().addCallback(new SurfaceHolder.Callback() {
                @SuppressLint("MissingPermission")
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

//                    Toast.makeText(AlterMember.this,"取得權限中...",Toast.LENGTH_LONG).show();
//                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
//                        recreate();
//                        return;
//                    }

                    try{
                        cameraSource.start(holder);
                    }catch(IOException e){
                        Toast.makeText(AlterMember.this,"無法啟動您的相機!!\n請允許使用權限!!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.v("test","surface changed!!");
                }
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
            //取得權限後開始掃描條碼
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> qrCodes=detections.getDetectedItems();

                    if(qrCodes.size()!=0){
                        RDCAM.post(() -> {
                            qdata=(qrCodes.valueAt(0).displayValue);
                            if(!tmp.equals(qdata)){//若條碼尚未被掃描過則繼續執行
                                tmp=qdata;//紀錄剛掃描的條碼
                                RDdata = qdata.split("cj04ru,");//切分CRcode到專用陣列
                                processQdata();//處理QRcode
                            }
                        });
                    }
                }
            });
        }
    }
    //QRcode處理器
    private void processQdata(){
        Log.v("test","data get!");
        ouuid = RDdata[0];//舊機器UUID
        pwd = RDdata[1];//換機金鑰
        Log.v("test","data received : acc ="+RDdata[0]+" pwd = "+RDdata[1]);
        //執行換機
        AlterSQL alterSQL = new AlterSQL();
        alterSQL.execute();
    }
    //字型大小設定
    private void SetFontSize(){
        TextView tv = findViewById(R.id.textView);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-2);//一般
        greet.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//以暱稱稱呼您
        Button FontSizeAlter = findViewById(R.id.FontSizeAlter);
        FontSizeAlter.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//修改字型大小
        TextView tv2 = findViewById(R.id.textview2);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-2);//帳戶資訊
        TextView tv3 = findViewById(R.id.tv3);
        tv3.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//用戶頭像
        Upload.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//變更頭像
        Rotate.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//旋轉頭像
        TextView tv4 = findViewById(R.id.tv4);
        tv4.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//姓名
        Name.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//姓名欄位
        TextView tv5 = findViewById(R.id.tv5);
        tv5.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//暱稱
        Nname.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//暱稱欄位
        TextView tv6 = findViewById(R.id.tv6);
        tv6.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//性別
        male.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-4);//男
        female.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-4);//女
        Cancel.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//取消選取
        TextView tv7 = findViewById(R.id.tv7);
        tv7.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//生日
        TextView tv8 = findViewById(R.id.tv8);
        tv8.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//通訊地址
        TextView tv9 = findViewById(R.id.tv9);
        tv9.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize-2);//帳戶轉移
        CD.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//我要換機
        RD.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//我是新手機
        CDhint.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//換機說明
        RDhint.setTextSize(TypedValue.COMPLEX_UNIT_SP,FONTsize);//帳號繼承說明
    }
}

package com.greeting.happycoin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.popup;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static com.greeting.happycoin.MainActivity.popupL;

public class scan extends AppCompatActivity {
    SurfaceView scanner;//掃描畫面顯示處
    CameraSource cameraSource; //使用相機
    BarcodeDetector barcodeDetector; //掃描套件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan);
        //定義區
        scanner = findViewById(R.id.scaner);
        //設定相機
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();//初始化掃描器與掃描條碼類型
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1080,1920).build();//設定預覽畫面大小
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();//設定掃描器為自動對焦
        //相機使用權限確認，若無權限則提示使用者允許
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            //若使用者拒絕權限獲權限無法取得時提示錯誤訊息，否則啟動相機
            scanner.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        cameraSource.start(holder);//啟動相機
                        Analyzing();//分析QRcode
                        Log.v("test","60");
                    } catch (IOException e) {
                        popupL(getApplication(),"無法啟動您的相機!!\n請允許使用權限!!");
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.v("test", "surface changed!!");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {cameraSource.stop();}//畫面關閉時停止相機
            });
        }
    }
    //QRcode 掃描器
    String[] RDdata;//裝入QRcode分析後的資料
    String qdata = "", tmp = "";//掃描到的原始資料,上一次掃描到的內容
    //QRcode資料分析
    private void Analyzing(){
        Log.v("test","dick");
        popup(getApplicationContext(),"請稍後...");//提示使用者等候
        //在次確認相機權限***檢查是否需刪除重複檢查
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else{
            scanner.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    try{
                        cameraSource.start(holder);
                        Log.v("test","95");
                    }catch(IOException e){
                        Toast.makeText(scan.this,"無法啟動您的相機!!\n請允許使用權限!!",Toast.LENGTH_LONG).show();
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
            //掃描器核心
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {}
                //掃描到資料時
                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> qrCodes=detections.getDetectedItems();
                    if(qrCodes.size()!=0){//若QRcode內有東西
                        scanner.post(() -> {//資料處理
                            qdata=(qrCodes.valueAt(0).displayValue);
                            Log.v("test",qdata);
                            if(!tmp.equals(qdata)){//若此QRcode未重複掃描==>判斷QRcode之處理方式
                                tmp=qdata;
//                                RDdata = qdata.split("cj04ru,");
                                if(qdata.contains("cj/61l," )) {//若原始資料中包含該字串則視為紅包處理
                                    RDdata = qdata.split("cj/61l,");//將原與資料切割至陣列內
                                    processQdata();//將分析後的QRcode資料陣列資料裝入專用變數內並與資料庫溝通
                                }
                                else if(qdata.contains("e.4a93,")){//若原始資料中包含該字串則視為購買處理
                                    RDdata = qdata.split("e.4a93,");//將原與資料切割至陣列內
                                    processQdata();//將分析後的QRcode資料陣列資料裝入專用變數內並與資料庫溝通
                                }
                                else if(qdata.contains("fu02l4,")){
                                    Log.v("test","qrdata= "+qdata);
                                    RDdata = qdata.split("fu02l4,");
                                    Log.v("test","Rd = " + RDdata[0]);
                                    processQdata();
                                }
                                else {
                                    popup(getApplicationContext(),"請確認您的條碼是否可用於交易");//無法識別條碼用途時提示錯誤
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    String sender;//送紅包者
    String password;//紅包金鑰
    int sum;//紅包金額
    String ip = "111231123";//使用者IP***目前仍在開發中
    String ID;//紅包發送者身分別
    String ProductId, FirmId;//產品代碼,公司代碼
    int amount,ActivityId;//購買商品數量,活動代碼
    //處理QRcode資料陣列並連接資料庫
    private void processQdata(){
        Log.v("test","data get!");
//        ouuid = RDdata[0];
//        pwd = RDdata[1];
        if(qdata.contains("cj/61l,")) {//處理紅包資料
            sender = RDdata[0];//送出者
            password = RDdata[1];//紅包金鑰
            sum = Integer.parseInt(RDdata[2]);//紅包金額
            ID = RDdata[4];//送出者身分別
            //交送資料庫處理函式
            Redbag alterSQL = new Redbag();
            alterSQL.execute();
        }
        else if(qdata.contains("e.4a93,")){//處理購物資料
            ProductId = RDdata[0];//產品代碼
            amount = Integer.parseInt(RDdata[1]);//購買數量
            FirmId = RDdata[2];//公司代碼
            //交送資料庫處理函式
            Redbag alterSQL = new Redbag();
            alterSQL.execute();
        }
        else if(qdata.contains("fu02l4,")){
            Log.v("test","suck");
            ActivityId = Integer.parseInt(RDdata[0]);
            Log.v("test","Acti= "+ActivityId);
            Redbag alterSQL = new Redbag();
            alterSQL.execute();
        }
//        Log.v("test","data received : acc ="+RDdata[0]+" pwd = "+RDdata[1]);

    }

    private class Redbag extends AsyncTask<Void,Void,String> {
        public String uuid = getUUID(getApplicationContext());//取得裝置UUID(交易用)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = null;
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
//                Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");//抓ip
//                rs.next();
//                ip = rs.getString(1);
                CallableStatement cstmt = null;
                if(qdata.contains("cj/61l,")) {
                    Log.v("test","---------------------------\n"+sender+"\n"+ID+"\n"+uuid+"\n"+"C"+"\n"+sum+"\n"+"yee"+"\n"+password+"\n-------------------------------");
                    cstmt = con.prepareCall("{? = call redenvelope_manager(?,?,?,?,?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2,sender);
                    cstmt.setString(3,ID);
                    cstmt.setString(4,uuid);
                    cstmt.setString(5,"C");
                    cstmt.setInt(6,sum);
                    cstmt.setString(7,"紅包");
                    cstmt.setString(8,password);
                }
                else if(qdata.contains("e.4a93,")){
                    Log.v("test",ProductId+" "+amount+" "+uuid+" "+FirmId);
                    cstmt = con.prepareCall("{? = call purchase(?,?,?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2,ProductId);
                    cstmt.setInt(3,amount);
                    cstmt.setString(4,uuid);
                    cstmt.setString(6,"n/a");
                }
                else if(qdata.contains("fu02l4,")){
                    cstmt = con.prepareCall("{? = call activity_sign(?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setInt(2,ActivityId);
                    cstmt.setString(3,uuid);
                    cstmt.setString(4,"n/a");

                }
                cstmt.execute();
                res = cstmt.getString(1);
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("test","after deal"+result);
            if(qdata.contains("cj/61l," )||qdata.contains("e.4a93,")) {
                if(result.contains("錯誤")){
                    popup(getApplicationContext(),"交易失敗");
                }
                else {
                    popup(getApplicationContext(),"發生錯誤，請聯繫客服人員協助您解決:)");
                }
            }
            else if(qdata.contains("fu02l4,")){
                popup(getApplicationContext(),result);
            }
            else{
                popup(getApplicationContext(),"交易成功");
                onBackPressed();
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(scan.this,LoginAndRegister.class);
        startActivity(intent);
        finish();
    }
}

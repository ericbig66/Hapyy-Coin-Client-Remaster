package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;

import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.popup;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;

public class scan extends AppCompatActivity {
    SurfaceView scaner;
    CameraSource cameraSource; // Receive Data Camera
    BarcodeDetector barcodeDetector; // Receive Data Camera
    String Qrcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan);
        scaner = findViewById(R.id.scaner);
        //set camera
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1080,1920).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            scaner.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    try {
                        cameraSource.start(holder);
                        Analyzing();
                        Log.v("test","60");
                    } catch (IOException e) {
                        Toast.makeText(scan.this, "無法啟動您的相機!!\n請允許使用權限!!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.v("test", "surface changed!!");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }
    }
    //QRcode scanner (data required )
    String[] RDdata;//1,2,3

    String qdata = "", tmp = "";
    private void Analyzing(){
        Log.v("test","dick");
        popup(getApplicationContext(),"請稍後...");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else{
            scaner.getHolder().addCallback(new SurfaceHolder.Callback() {
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

            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> qrCodes=detections.getDetectedItems();

                    if(qrCodes.size()!=0){
                        scaner.post(() -> {
                            qdata=(qrCodes.valueAt(0).displayValue);
                            Log.v("test",qdata);
                            if(!tmp.equals(qdata)){
                                tmp=qdata;
//                                RDdata = qdata.split("cj04ru,");
                                if(qdata.contains("cj/61l," )) {
                                    RDdata = qdata.split("cj/61l,");
                                    processQdata();
                                }
                                else if(qdata.contains("e.4a93,")){
                                    RDdata = qdata.split("e.4a93,");
                                    processQdata();
                                }
                                else {
                                    popup(getApplicationContext(),"請確認您的條碼是否可用於交易");
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    String sender;
    String password;
    int sum;
    String ip = "111231123";
    String ID;

    String ProductId, FirmId;
    int amount;
    private void processQdata(){
        Log.v("test","data get!");
//        ouuid = RDdata[0];
//        pwd = RDdata[1];
        if(qdata.contains("cj/61l,")) {

            sender = RDdata[0];
            password = RDdata[1];
            sum = Integer.parseInt(RDdata[2]);
            ID = RDdata[4];
            Redbag alterSQL = new Redbag();
            alterSQL.execute();
        }
        else if(qdata.contains("e.4a93,")){
            ProductId = RDdata[0];
            amount = Integer.parseInt(RDdata[1]);
            FirmId = RDdata[2];
            Redbag alterSQL = new Redbag();
            alterSQL.execute();
        }
//        Log.v("test","data received : acc ="+RDdata[0]+" pwd = "+RDdata[1]);

    }

    private class Redbag extends AsyncTask<Void,Void,String> {
        String ip = null;
        public String uuid = getUUID(getApplicationContext());
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
                    cstmt = con.prepareCall("{? = call purchase(?,?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2,ProductId);
                    cstmt.setInt(3,amount);
                    cstmt.setString(4,uuid);
                    cstmt.setString(5,FirmId);

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
            if(result.contains("錯誤")){
                popup(getApplicationContext(),"交易失敗");
                Log.v("test","錯誤: "+result);
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

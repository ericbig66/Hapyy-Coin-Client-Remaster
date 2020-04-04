package com.greeting.happycoin;

import android.Manifest;
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

import static com.greeting.happycoin.LoginAndRegister.popup;

public class gift extends AppCompatActivity {

    SurfaceView RDCAM; // Receive Data Camera
    BarcodeDetector barcodeDetector; // Receive Data Camera
    CameraSource cameraSource; // Receive Data Camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gift);
        //set camera
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1080,1920).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();

    }

    //QRcode scanner (data required )
//    String[] RDdata;
    String qdata = "", tmp = "";
    private void inheritData(){

        popup(getApplicationContext(),"請稍後...");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else{
            RDCAM.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try{
                        cameraSource.start(holder);
                    }catch(IOException e){
                        Toast.makeText(gift.this,"無法啟動您的相機!!\n請允許使用權限!!",Toast.LENGTH_LONG).show();
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
                        RDCAM.post(() -> {
                            qdata=(qrCodes.valueAt(0).displayValue);
                            if(!tmp.equals(qdata)){
                                tmp=qdata;
                                processQdata();
                            }
                        });
                    }
                }
            });
        }
    }

    private void processQdata(){
        Log.v("test","data get!");
//        Log.v("test","data received : acc ="+RDdata[0]+" pwd = "+RDdata[1]);
        Trade trade = new Trade();
        trade.execute();
    }

    //----------------------------------------------------------------------------------------
    //trade
    private class Trade extends AsyncTask<Void,Void,String> {
        //multi entry process
        String[] SendData;//container for data extracted form QRcode reader
        String mode ="error";
        @Override
        protected void onPreExecute() {
            //red envelope  cj/61l, ==> 紅包,
            if(qdata.contains("cj/61l,")){SendData = qdata.split("cj/61l,"); mode = "redEnv";}
            //purchase
            else if (qdata.contains("e.4a93,")){SendData = qdata.split("e.4a93,"); mode = "purchase";}
            //register-activity
            else if (qdata.contains("1l42l4,")){SendData = qdata.split("1l42l4,"); mode = "register";}
            else{popup(getApplicationContext(),"您掃描的QRcode似乎無法使用\n請確認其是否符合使用規範");}
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}

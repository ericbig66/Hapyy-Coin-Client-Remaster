package com.greeting.happycoin;

import android.Manifest;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.ArrayList;
import java.util.Date;

import static com.greeting.happycoin.LoginAndRegister.getPfr;
import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.inf;
import static com.greeting.happycoin.LoginAndRegister.nm;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.pf;
import static com.greeting.happycoin.LoginAndRegister.popup;
import static com.greeting.happycoin.LoginAndRegister.setPfr;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;
import static java.lang.String.valueOf;

public class AlterMember extends AppCompatActivity {

    Switch greet;
    CircularImageView profile; //Profile image
    Button CD, RD, Upload, Rotate, Cancel; // changeDevice, ReceiveData, UploadProfile, ProfileRotate, CancelGenderSelect
    RadioButton male,female;// male, female
    EditText Name, Nname, Address; // Name, NickName, CommunicationAddress
    DatePicker Birthday;
    ImageView CDQR; // chamge Device QRcode
    SurfaceView RDCAM; // Receive Data Camera
    BarcodeDetector barcodeDetector; // Receive Data Camera
    CameraSource cameraSource; // Receive Data Camera
    String operate = "sav";
    String ouuid; //uuid of old device
    String pwd; // pass word for change device
    LinearLayout common;
    TextView CDhint, RDhint;


    String npf = inf[3], nnm, nnn, nge, nbd = null, nad; // parameter to output
    Float npfr = Float.parseFloat(inf[4]);// parameter to output

    final Integer OPEN_PIC = 1021;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_member);

        //initialize components
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

        //set camera
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1080,1920).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();


        //set limit to birthday picker (Set Max Date to today)
        Birthday.setMaxDate(new Date().getTime());

        //restore the preference of greeting on login page
        greet.setChecked(getPfr("HCgreet",getApplicationContext()));
        //save preference when preference is changed
        greet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setPfr("HCgreet",isChecked, getApplicationContext());
            greet.setText(isChecked?"以暱稱稱呼您":"以姓名稱呼您");
        });
        // 頭像[3]    頭像角度[4]      生日[5]    性別[6]   送貨地址[7]
        //restore the personal info. into form
        //****restore profile here (include pfr)
        if(inf[3].equals("null")){profile.setImageResource(R.drawable.df_profile);}
        else{profile.setImageBitmap(pf);}
        Name.setText(nm[0].equals("null")?"":nm[0]);//Name
        Nname.setText(nm[1].equals("null")?"":nm[1]);//Nick Name
        if(inf[6].equals("m")){male.setChecked(true);}//Gender
        else if(inf[6].equals("f")){female.setChecked(true);}
//        Log.v("test","Your birthday is "+inf[5]);
        if((!inf[5].equals("null")) && inf[5].length()==10){
            String[] tmp;
            tmp = inf[5].split("-");
//            Log.v("test","setting birthday...");
            Birthday.init(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1])-1,Integer.parseInt(tmp[2]),null);
        }
        Address.setText(inf[7].equals("null")?"":inf[7]);
        profile.setRotation(Float.parseFloat(inf[4]));
        //when click on change device
        CD.setOnClickListener(v -> {
            setAlert("換機確認", "您是否要將此帳號轉移至另一支手機?\n", "是", "否", this);
        });

        //when click on receive data
        RD.setOnClickListener(v -> {
            setAlert("繼承資料確認", "您是否要使用這支手機取代舊手機?\n舊手機上的資料將被刪除由這支手機繼承\n且此手機上原有之帳號將被清除且無法自行復原","確認換機", "再考慮一下", this);
        });

       closekeybord();
    }

    //update personal info and change device *********************
    private class AlterSQL extends AsyncTask<Void,Void,String> {
        String ip = null;
        public String uuid = getUUID(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (operate){
                case "sav":
                    getBirthday();
                    popup(getApplicationContext(),"資料儲存中請稍後...");
                    break;
                case "CD":
                    CDQR.setVisibility(View.VISIBLE);
                    CDhint.setVisibility(View.VISIBLE);
                    CD.setVisibility(View.GONE);
                    RD.setVisibility(View.GONE);
                    popup(getApplicationContext(),"請稍後...");
                    break;
                case "RD":

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
                    case "CD":
                        cstmt = con.prepareCall("{? = call change_device(?,?,?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2,ip);
                        cstmt.setString(3,uuid);
                        cstmt.setString(4,"g;4jo4gk42u/4");
                        cstmt.setString(5,"");
                        cstmt.execute();
                        res = cstmt.getString(1);
                        break;

                    case "RD":

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

                    case "sav":
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

                    case "back":
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("test","mode = "+operate+"  result = "+result);
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
                        //************************** continue here *****************************
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
    //alert dialoue with two button
    public void setAlert(String title, String msg, String pos, String nag, Context context) {
        //建立更新資訊提示
        boolean rs = false;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(msg);
        // Add the buttons
        alert.setPositiveButton(pos, (dialog, id) -> {
            // User clicked OK button
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
            // User cancelled the dialog

        });

        // Create the AlertDialog
        AlertDialog dialog = alert.create();
        dialog.show();
    }
//-----------------------------------------------------------------
    //override onBackPressed method to reopen menu (Login) page
    @Override
    public void onBackPressed() {
        switch (operate){
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
    // to put new data into output parameter
    private void checker(){
       nnm = Name.getText().toString();
       nnn = Nname.getText().toString();
       nge = male.isChecked()?"m":(female.isChecked()?"f":null);
       nad = Address.getText().toString();
       // profile, profile rotate, birthday will be handle by each of their handler
    }
//--------------------------------------------------------------------------------
    //profile handler

    //image selector
    public void picOpen(View v){
        ((BitmapDrawable) profile.getDrawable()).getBitmap().recycle();//一定要做否則會當機
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"請選擇您的頭像"), OPEN_PIC);
    }
    //get actual image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri imgdata = data.getData();
            profile.setImageURI(imgdata);
            pf = ((BitmapDrawable)profile.getDrawable()).getBitmap();
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    //Image converter
    private class ConvertToBase64 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pf.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return imageString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            npf = s;
        }
    }
//---------------------------------------------------------------------------------
    //profile rotate handler
    public void profileRotater(View v){
        npfr = (npfr+90f)>=360f?0f:(npfr+90f);
        profile.setRotation(npfr);
    }
//-------------------------------------------------------------------------------------
    //birthday handler

    //System time handler
    Date curDate = new Date(System.currentTimeMillis());//get system date time
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//format system date into yyyy-mm-dd
    //格式化出可直接使用的年月日變數
    String today = formatter.format(curDate);

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
    //QRcode generator (data required: uuid, pwd) *********************
    private void renderQRcode(String pwd){
        BarcodeEncoder encoder = new BarcodeEncoder();
        try{
            common.setVisibility(View.GONE);
            Bitmap code = encoder.encodeBitmap(getUUID(getApplicationContext())+"cj04ru,"+pwd, BarcodeFormat.QR_CODE,1000,1000);
            CDQR.setImageBitmap(code);
        }catch (Exception e){
            Log.v("test",e.toString());
        }
    }
//--------------------------------------------------------------------------------------------
    //QRcode scanner (data required )
    String[] RDdata;
    String qdata = "", tmp = "";
    private void inheritData(){

        common.setVisibility(View.GONE);
        RDCAM.setVisibility(View.VISIBLE);
        RDhint.setVisibility(View.VISIBLE);
        CD.setVisibility(View.GONE);
        RD.setVisibility(View.GONE);
        popup(getApplicationContext(),"請稍後...");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else{
            RDCAM.getHolder().addCallback(new SurfaceHolder.Callback() {
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
                                RDdata = qdata.split("cj04ru,");
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
        ouuid = RDdata[0];
        pwd = RDdata[1];
        Log.v("test","data received : acc ="+RDdata[0]+" pwd = "+RDdata[1]);
        AlterSQL alterSQL = new AlterSQL();
        alterSQL.execute();
    }
    //---------------------------------------------------------------------------------------------
    //close key bord
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

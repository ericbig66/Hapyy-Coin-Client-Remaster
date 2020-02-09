package com.greeting.happycoin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.happycoin.LoginAndRegister.getPfr;
import static com.greeting.happycoin.LoginAndRegister.getUUID;
import static com.greeting.happycoin.LoginAndRegister.inf;
import static com.greeting.happycoin.LoginAndRegister.nm;
import static com.greeting.happycoin.LoginAndRegister.pass;
import static com.greeting.happycoin.LoginAndRegister.pf;
import static com.greeting.happycoin.LoginAndRegister.setPfr;
import static com.greeting.happycoin.LoginAndRegister.url;
import static com.greeting.happycoin.LoginAndRegister.user;

public class AlterMember extends AppCompatActivity {

    Switch greet;
    CircularImageView profile; //Profile image
    Button CD, RD, Upload, Rotate, Cancel; // changeDevice, ReceiveData, UploadProfile, ProfileRotate, CancelGenderSelect
    RadioButton male,female;// male, female
    EditText Name, Nname, Address; // Name, NickName, CommunicationAddress
    ImageView CDQR; // chamge Device QRcode
    SurfaceView RDCAM; // Receive Data Camera
    String operate = "sav";
    String ouuid; //uuid of old device
    String pwd; // pass word for change device
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_member);

        //initialize components
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
        //****restore Birthday here
        Address.setText(inf[7].equals("null")?"":inf[7]);

        //when click on change device
        CD.setOnClickListener(v -> {
            setAlert("換機確認", "您是否要將此帳號轉移至另一支手機?\n", "是", "否", this);
        });

        //when click on receive data
        RD.setOnClickListener(v -> {
            setAlert("繼承資料確認", "您是否要使用這支手機取代舊手機?\n(舊手機上的資料將被刪除由這支手機繼承)","確認換機", "再考慮一下", this);
        });

    }

    //update personal info and change device *********************
    private class AlterSQL extends AsyncTask<Void,Void,String> {
        String ip = null;
        public String uuid = getUUID(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"資料儲存中請稍後...",Toast.LENGTH_SHORT).show();
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
                        break;

                    case "sav":
                        break;

                    default:
                        result = "發生錯誤請稍後重試或連繫客服人員";
                }
                
                
                
                
                





            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (operate.equals("sav")) {
                Intent intent = new Intent(getApplicationContext(), LoginAndRegister.class);
                startActivity(intent);
                finish();
            } else if (operate.equals("CD")) {
                operate = "sav";
            } else if (operate.equals("RD")){
                operate = "sav";
            }
        }
    }

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
                    break;
                case "繼承資料確認":
                    operate = "RD";
                    break;
            }

            AlterSQL alterSQL = new AlterSQL();
            alterSQL.execute();
        });
        alert.setNegativeButton(nag, (dialog, id) -> {
            // User cancelled the dialog

        });

        // Create the AlertDialog
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    //override onbackPressed method to reopen menu (Login) page
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlterSQL alterSQL = new AlterSQL();
        alterSQL.execute();
    }
}

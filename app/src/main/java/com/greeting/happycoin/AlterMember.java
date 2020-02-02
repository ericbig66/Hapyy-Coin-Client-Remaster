package com.greeting.happycoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import static com.greeting.happycoin.LoginAndRegister.getPfr;
import static com.greeting.happycoin.LoginAndRegister.setPfr;


public class AlterMember extends AppCompatActivity {

    Switch greet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_member);

        greet = findViewById(R.id.greet);
        //define preference memory space
//        preferences = this.getPreferences(Context.MODE_PRIVATE);
        //define preference type and default content
//        boolean greeting = preferences.getBoolean(String.valueOf(R.bool.greet_pref),true);
//        greet.setChecked(greeting);
        greet.setChecked(getPfr("HCgreet",getApplicationContext()));
        //save preference when preference is changed
        greet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(String.valueOf(R.bool.greet_pref),isChecked);
//                editor.apply();
//                boolean rs = preferences.getBoolean(String.valueOf(R.bool.greet_pref),true);
//                Log.v("test","after edit my preference is "+rs);
                setPfr("HCgreet",isChecked, getApplicationContext());
                greet.setText(isChecked?"以暱稱稱呼您":"以姓名稱呼您");
            }
        });
    }

    //override onbackPressed method to reopen menu (Login) page

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginAndRegister.class);
        startActivity(intent);
        finish();
    }
}

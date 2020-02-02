package com.greeting.happycoin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import static com.greeting.happycoin.LoginAndRegister.getPfr;
import static com.greeting.happycoin.LoginAndRegister.setPfr;


public class AlterMember extends AppCompatActivity {

    Switch greet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_member);

        greet = findViewById(R.id.greet);

        greet.setChecked(getPfr("HCgreet",getApplicationContext()));
        //save preference when preference is changed
        greet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

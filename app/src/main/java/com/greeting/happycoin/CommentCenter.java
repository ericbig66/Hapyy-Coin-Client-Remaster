package com.greeting.happycoin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.greeting.happycoin.ui.rating.SectionsPagerAdapter;

public class CommentCenter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comment_center);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }
    public void onBackPressed() {//返回鈕動作
        Intent intent = new Intent(CommentCenter.this,LoginAndRegister.class);//準備轉跳回首頁
        startActivity(intent);//轉跳
        finish();//結束本頁面
    }
}
//由評價返回時無法自動回到上次評價的頁面，都會回到評價商品頁面
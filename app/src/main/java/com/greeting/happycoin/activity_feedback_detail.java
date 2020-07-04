package com.greeting.happycoin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

public class activity_feedback_detail extends AppCompatActivity {
    LinearLayout ll;
    TextView activityName;
    ImageView actpic;
    TextView place;
    TextView activityDescription;
    ArrayList<Date> RateDate = new ArrayList<>();
    ArrayList<Float> score = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    ProgressBar one;
    ProgressBar two;
    ProgressBar three;
    ProgressBar four;
    ProgressBar five;
    TextView average_score;
    RatingBar average_star;
    TextView rated;
    float [] score_distribute = {0,0,0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_feedback_detail);
        ll = findViewById(R.id.ll);
//        ConnectMySql connectMySql = new ConnectMySql();
//        connectMySql.execute();
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        average_score = findViewById(R.id.average_score);
        average_star = findViewById(R.id.average_star);
        rated = findViewById(R.id.rated);
        activityName = findViewById(R.id.txtName);
        actpic = findViewById(R.id.actpic);
        place = findViewById(R.id.place);
        activityDescription = findViewById(R.id.activityDescription);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(activity_feedback_detail.this, CommentCenter.class);
        startActivity(intent);
        finish();
    }
}
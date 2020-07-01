package com.greeting.happycoin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Collections;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class game extends AppCompatActivity {


    ImageView iv_11, iv_12, iv_13, iv_14, iv_21, iv_22, iv_23, iv_24, iv_31, iv_32, iv_33, iv_34;

    //陣列圖片編號
    Integer[] cardsArray = {101,102,103,104,105,106,201,202,203,204,205,206};
//    private FirebaseAuth mAuth;

    //圖片變數
    int image101, image102, image103, image104, image105, image106,
            image201, image202, image203, image204, image205, image206;

    int firstCard, secondCard;
    int clickedFirst,clickedSecond;
    int cardNumber = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);


        //給每張牌位置(先設定十二個位置)
        iv_11 = (ImageView) findViewById(R.id.iv_11);
        iv_12 = (ImageView) findViewById(R.id.iv_12);
        iv_13 = (ImageView) findViewById(R.id.iv_13);
        iv_14 = (ImageView) findViewById(R.id.iv_14);
        iv_21 = (ImageView) findViewById(R.id.iv_21);
        iv_22 = (ImageView) findViewById(R.id.iv_22);
        iv_23 = (ImageView) findViewById(R.id.iv_23);
        iv_24 = (ImageView) findViewById(R.id.iv_24);
        iv_31 = (ImageView) findViewById(R.id.iv_31);
        iv_32 = (ImageView) findViewById(R.id.iv_32);
        iv_33 = (ImageView) findViewById(R.id.iv_33);
        iv_34 = (ImageView) findViewById(R.id.iv_34);


        //將每個位置編號
        iv_11.setTag("0");
        iv_12.setTag("1");
        iv_13.setTag("2");
        iv_14.setTag("3");
        iv_21.setTag("4");
        iv_22.setTag("5");
        iv_23.setTag("6");
        iv_24.setTag("7");
        iv_31.setTag("8");
        iv_32.setTag("9");
        iv_33.setTag("10");
        iv_34.setTag("11");

        frontOfCardsResources();//將實際圖片放入圖片變數image101,image102...中

        Collections.shuffle(Arrays.asList(cardsArray));//洗牌
        //洗牌後，找出每個位置對應的陣列
        // 點擊位置的觸發動作
        iv_11.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_11,theCard);//判斷陣列位置相應的圖片變數
                // 配對動作
            }
        });

        iv_12.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_12,theCard);
            }
        });

        iv_13.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_13,theCard);
            }
        });

        iv_14.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_14,theCard);
            }
        });

        iv_21.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_21,theCard);
            }
        });

        iv_22.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_22,theCard);
            }
        });

        iv_23.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_23,theCard);
            }
        });

        iv_24.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_24,theCard);
            }
        });

        iv_31.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_31,theCard);
            }
        });

        iv_32.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_32,theCard);
            }
        });

        iv_33.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_33,theCard);
            }
        });

        iv_34.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_34,theCard);
            }
        });





    }


    private void doStuff(ImageView iv,int card) {
        //洗牌後，判斷陣列位置相應的圖片變數
        if (cardsArray[card] == 101) {
            iv.setImageResource(image101);
        } else if (cardsArray[card] == 102) {
            iv.setImageResource(image102);
        } else if (cardsArray[card] == 103) {
            iv.setImageResource(image103);
        } else if (cardsArray[card] == 104) {
            iv.setImageResource(image104);
        } else if (cardsArray[card] == 105) {
            iv.setImageResource(image105);
        } else if (cardsArray[card] == 106) {
            iv.setImageResource(image106);
        } else if (cardsArray[card] == 201) {
            iv.setImageResource(image201);
        } else if (cardsArray[card] == 202) {
            iv.setImageResource(image202);
        } else if (cardsArray[card] == 203) {
            iv.setImageResource(image203);
        } else if (cardsArray[card] == 204) {
            iv.setImageResource(image204);
        } else if (cardsArray[card] == 205) {
            iv.setImageResource(image205);
        } else if (cardsArray[card] == 206) {
            iv.setImageResource(image206);
        }


        //玩家選擇的第一張牌及選第二張牌
        if(cardNumber == 1){
            firstCard = cardsArray[card];
            if(firstCard > 200){
                firstCard = firstCard - 100;
            }
            cardNumber = 2;
            clickedFirst = card;
            iv.setEnabled(false);

        }else if(cardNumber == 2){
            secondCard = cardsArray[card];
            if(secondCard > 200){
                secondCard = secondCard - 100;
            }
            cardNumber = 1;
            clickedSecond = card;

            iv_11.setEnabled(false);
            iv_12.setEnabled(false);
            iv_13.setEnabled(false);
            iv_14.setEnabled(false);
            iv_21.setEnabled(false);
            iv_22.setEnabled(false);
            iv_23.setEnabled(false);
            iv_24.setEnabled(false);
            iv_31.setEnabled(false);
            iv_32.setEnabled(false);
            iv_33.setEnabled(false);
            iv_34.setEnabled(false);

            //跑配對迴圈
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    calculate();

                }
            },1000);
        }




    }
    //配對
    private  void calculate(){
        if(firstCard == secondCard){
            if(clickedFirst == 0){
                iv_11.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 1){
                iv_12.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 2){
                iv_13.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 3){
                iv_14.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 4){
                iv_21.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 5){
                iv_22.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 6){
                iv_23.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 7){
                iv_24.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 8){
                iv_31.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 9){
                iv_32.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 10){
                iv_33.setVisibility(View.INVISIBLE);
            }else if(clickedFirst == 11){
                iv_34.setVisibility(View.INVISIBLE);
            }

            if(clickedSecond == 0){
                iv_11.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 1){
                iv_12.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 2){
                iv_13.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 3){
                iv_14.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 4){
                iv_21.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 5){
                iv_22.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 6){
                iv_23.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 7){
                iv_24.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 8){
                iv_31.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 9){
                iv_32.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 10){
                iv_33.setVisibility(View.INVISIBLE);
            }else if(clickedSecond == 11){
                iv_34.setVisibility(View.INVISIBLE);
            }

        }
        else{
            iv_11.setImageResource(R.drawable.question);
            iv_12.setImageResource(R.drawable.question);
            iv_13.setImageResource(R.drawable.question);
            iv_14.setImageResource(R.drawable.question);
            iv_21.setImageResource(R.drawable.question);
            iv_22.setImageResource(R.drawable.question);
            iv_23.setImageResource(R.drawable.question);
            iv_24.setImageResource(R.drawable.question);
            iv_31.setImageResource(R.drawable.question);
            iv_32.setImageResource(R.drawable.question);
            iv_33.setImageResource(R.drawable.question);
            iv_34.setImageResource(R.drawable.question);

        }

        iv_11.setEnabled(true);
        iv_12.setEnabled(true);
        iv_13.setEnabled(true);
        iv_14.setEnabled(true);
        iv_21.setEnabled(true);
        iv_22.setEnabled(true);
        iv_23.setEnabled(true);
        iv_24.setEnabled(true);
        iv_31.setEnabled(true);
        iv_32.setEnabled(true);
        iv_33.setEnabled(true);
        iv_34.setEnabled(true);

        checkEnd();


    }

    //判斷是否完全配對，遊戲結束
    private void checkEnd(){
        if(iv_11.getVisibility() == View.INVISIBLE &&
                iv_12.getVisibility() == View.INVISIBLE &&
                iv_13.getVisibility() == View.INVISIBLE &&
                iv_14.getVisibility() == View.INVISIBLE &&
                iv_21.getVisibility() == View.INVISIBLE &&
                iv_22.getVisibility() == View.INVISIBLE &&
                iv_23.getVisibility() == View.INVISIBLE &&
                iv_24.getVisibility() == View.INVISIBLE &&
                iv_31.getVisibility() == View.INVISIBLE &&
                iv_32.getVisibility() == View.INVISIBLE &&
                iv_33.getVisibility() == View.INVISIBLE &&
                iv_34.getVisibility() == View.INVISIBLE){


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(game.this);
            alertDialogBuilder
                    .setMessage("遊戲結束，恭喜完成")
                    .setCancelable(false)
                    .setPositiveButton("finish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

//                            Intent intent = new Intent(getApplicationContext(), list.class);
                            Intent intent = new Intent(getApplicationContext(), LoginAndRegister.class);
                            startActivity(intent);
                            finish();
//                            getData();//連接資料庫



                        }



                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();



        }


    }

    //設圖片變數，將實際圖片放入變數中
    private void frontOfCardsResources(){
        image101 = R.drawable.ic_image101;
        image102 = R.drawable.ic_image102;
        image103 = R.drawable.ic_image103;
        image104 = R.drawable.ic_image104;
        image105 = R.drawable.ic_image105;
        image106 = R.drawable.ic_image106;
        image201 = R.drawable.ic_image201;
        image202 = R.drawable.ic_image202;
        image203 = R.drawable.ic_image203;
        image204 = R.drawable.ic_image204;
        image205 = R.drawable.ic_image205;
        image206 = R.drawable.ic_image206;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(game.this,LoginAndRegister.class);
        startActivity(intent);
        finish();
    }

    //連接資料庫
//    private void getData() {
//        mAuth = FirebaseAuth.getInstance();
//        //   if(mAuth.getCurrentUser())
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference myRef = database.getReference("member");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String uid=mAuth.getCurrentUser().getUid();//取得使用者UID
//
//                String money=dataSnapshot.child(uid).child("money").getValue(String.class);
//                int cmoney = Integer.parseInt(money);
//                String tmoney;
//                tmoney = Integer.toString(cmoney +100);
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("member");
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put(uid + "/money", tmoney);//前面的字是child後面的字是要修改的value值
//                myRef.updateChildren(childUpdates);
//                System.exit(0);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        });
//
//        finish();//遊戲結束
//    }
}
package com.example.takemeuserapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONObject;

public class RideRating extends AppCompatActivity {
    TextView msg;
    RatingBar rbStars;
    TextView tvFeedback;
    Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_rating);
        msg = findViewById(R.id.msg);
        rbStars = findViewById(R.id.rbStars);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnSend = findViewById(R.id.btnSend);


        msg.setText(getIntent().getStringExtra("msg"));

        rbStars.setRating(5);
        rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating==5)
                {
                    tvFeedback.setText("Very Satisfied");

                }
                else if(rating>=4 && rating<5)
                {
                    tvFeedback.setText("Satisfied");

                }
                else if(rating>=2 && rating<4)
                {
                    tvFeedback.setText("OK");
                }
                else if(rating>=1 && rating <2)
                {
                    tvFeedback.setText("Dissatisfied");
                }
                else if(rating>=0 && rating <1)
                {
                    tvFeedback.setText("Very Dissatisfied");
                }
                else
                {

                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("inside btnSend onclick");
                float rating = rbStars.getRating();
                ApiDataService apiDataService = new ApiDataService(RideRating.this);
                String rideId =getIntent().getStringExtra("rideID") ;

                apiDataService.rateRide(MainActivity.main_token,rideId,rating, new ApiDataService.VolleyResponseListener() {

                    @Override
                    public void onError(Object message) {
                        System.out.println("Problem in feedback submission");
                    }

                    @Override
                    public void onResponse(Object responseObject) {
                        System.out.println("Feedback submitted!");
                    }
                });

                Intent intent = new Intent(RideRating.this, MainActivity.class);
                RideRating.this.finish();
                startActivity(intent);

            }
        });
    }
}
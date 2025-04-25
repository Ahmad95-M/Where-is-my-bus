package com.example.user.transport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
private Button mDriver , mCustomer ,mBus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDriver =(Button)findViewById(R.id.driver);
        mCustomer =(Button)findViewById(R.id.customer);
        mBus =(Button)findViewById(R.id.d2);

        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this ,DriverLoginActivity.class);
                startActivity(intent);
                //finish();
                return;


            }
        });
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this ,CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        mBus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this ,BusListActivity.class);
                        startActivity(intent);
                        //finish();
                        return;
            }
        });

    }


    }


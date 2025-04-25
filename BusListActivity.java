package com.example.user.transport;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BusListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] member_names;
    TypedArray profile_pics;
    String[] statues;
    String[] contactType;
  //  private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener FireBaseAuthListener;

    List<RowItem> rowItems;
    ListView mylistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
      //  mAuth = FirebaseAuth.getInstance();
       /* FireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //Intent intent = new Intent(this,. class);
                   // startActivity(intent);
                    finish();
                    return;

                }
            }
        };*/
        rowItems = new ArrayList<RowItem>();

        member_names = getResources().getStringArray(R.array.Member_names);

        profile_pics = getResources().obtainTypedArray(R.array.profile_pics);

        statues = getResources().getStringArray(R.array.statues);

        contactType = getResources().getStringArray(R.array.contactType);

        for (int i = 0; i < member_names.length; i++) {
            RowItem item = new RowItem(member_names[i],
                    profile_pics.getResourceId(i, -1), statues[i],
                    contactType[i]);
            rowItems.add(item);
        }

        mylistView = (ListView) findViewById(R.id.list);

        CustomAdapter adapter = new CustomAdapter(this, rowItems);

        mylistView.setAdapter(adapter);

        mylistView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String member_name = rowItems.get(position).getMember_name();

        Toast.makeText(getApplicationContext(), "" + member_name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CustomerMapActivity.class);
        startActivity(intent);
        finish();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(FireBaseAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(FireBaseAuthListener);
    }*/
}
  /*  private Button  mCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        mCustomer =(Button)findViewById(R.id.bus01);
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusListActivity.this ,CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
}*/

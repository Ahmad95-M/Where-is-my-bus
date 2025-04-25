package com.example.user.transport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CustomerSettingsActivty extends AppCompatActivity {

    private EditText mNameField , mPhoneField;
    private Button mBack, mConfrim;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private  String userID;
    private  String mName;
    private  String mPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_settings_activty);

        mNameField = (EditText) findViewById(R.id.Name);
        mPhoneField = (EditText) findViewById(R.id.Phone);

        mBack = (Button) findViewById(R.id.back);
        mConfrim = (Button) findViewById(R.id.confrim);

        mAuth = FirebaseAuth.getInstance();
        userID=mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        getUserInfo();
        mConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });


mBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        finish();
        return;
    }
});
    }
    private  void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String , Object> map = (Map<String , Object> )dataSnapshot.getValue();
                    if (map.get("Name")!=null){
                        mName = map.get("Name").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("Phone")!=null){
                        mPhone = map.get("Phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void saveUserInformation(){
        mName= mNameField.getText().toString();
        mPhone= mPhoneField.getText().toString();


        Map userInfo = new HashMap();
        userInfo.put("Name",mName);
        userInfo.put("Phone", mPhone);
        mCustomerDatabase.updateChildren(userInfo);

        finish();

    }
}

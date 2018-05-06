package com.filote.cosmin1.metrorex.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.filote.cosmin1.metrorex.Model.UserInformation;
import com.filote.cosmin1.metrorex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class EditProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView userEmail;
    private EditText userName;
    private EditText userNumber;
    private EditText userStatus;
    private String numberDB;
    private String nameDB;
    private String statusDB;
    private DatabaseReference databaseReference;
    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //user not logged in

            startActivity(new Intent(this, LoginRegister.class));
        }
        userEmail = (TextView)findViewById(R.id.tv_user_email);
        userEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        userName = (EditText)findViewById(R.id.first_name_edit_text);
        userNumber = (EditText)findViewById(R.id.phone_number_edit_text);
        userStatus = (EditText)findViewById(R.id.status_edit_text);

        databaseReference= FirebaseDatabase.getInstance().getReference();


        databaseReference.
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            if (firebaseAuth.getCurrentUser().getUid().toString().trim().equals(postSnapShot.getKey().toString().trim())) {
                                UserInformation userInformation = postSnapShot.getValue(UserInformation.class);

                                if(!userInformation.getName().equals("null")){
                                    userName.setText(userInformation.getName());
                                }
                                if(!userInformation.getNumber().equals("null")){
                                    userNumber.setText(userInformation.getNumber());
                                }
                                if(!userInformation.getStatus().equals("null")){
                                    userStatus.setText(userInformation.getStatus());
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        System.out.println("The read failed: " + databaseError.getMessage());

                    }
                });

        update = (Button)findViewById(R.id.button_update_profile);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberDB=userNumber.getText().toString().trim();
                nameDB=userName.getText().toString().trim();
                statusDB=userStatus.getText().toString().trim();

                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserInformation userInformation = new UserInformation(dataSnapshot.getValue(UserInformation.class));
                                userInformation.setName(nameDB);
                                userInformation.setNumber(numberDB);
                                userInformation.setStatus(statusDB);
                                databaseReference.child(dataSnapshot.getKey()).setValue(userInformation);
                                Toast.makeText(EditProfile.this, "Save completed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        });








    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(EditProfile.this,Profile.class));
    }
}

package com.filote.cosmin1.metrorex.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.filote.cosmin1.metrorex.R;
import com.google.firebase.auth.FirebaseAuth;

public class EditProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //user not logged in

            startActivity(new Intent(this, LoginRegister.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(EditProfile.this,Profile.class));
    }
}

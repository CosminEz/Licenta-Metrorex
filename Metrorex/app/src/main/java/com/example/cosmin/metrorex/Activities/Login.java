package com.example.cosmin.metrorex.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cosmin.metrorex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button buttonLogin;
    private EditText editTextMailLogin;
    private EditText editTextPasswordLogin;
    private TextView textViewLogin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin=(Button)findViewById(R.id.buttonLogin);
        editTextMailLogin=(EditText)findViewById(R.id.etusernamelogin);
        editTextPasswordLogin=(EditText)findViewById(R.id.etpasswordlogin);
        textViewLogin=(TextView)findViewById(R.id.tvlogin);

        buttonLogin.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);

        progressDialog=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //the user is already logged in , start the profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),Profile.class));


        }
    }

    private void userLogin(){
        String email=editTextMailLogin.getText().toString().trim();
        String password=editTextPasswordLogin.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this,"Please enter an email address", Toast.LENGTH_SHORT).show();
            //stop the function to progress
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT).show();
            //stop the function to progress
            return;
        }

        //if validations are ok show progresDialog
        progressDialog.setMessage("Login user.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //start the profile activity
                            Toast.makeText(Login.this,"Login Succesfully !",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                            startActivity(new Intent(getApplicationContext(),Profile.class));
                        }
                        else{
                            //error on login
                            Toast.makeText(Login.this,"Login Unsuccesfully, please try again",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });



    }

    @Override
    public void onClick(View view) {

        if(view == buttonLogin){
            userLogin();
        }
        if(view == textViewLogin){
            //will open the register Activity
            finish();
            startActivity(new Intent(this,Register.class));

        }

    }
}

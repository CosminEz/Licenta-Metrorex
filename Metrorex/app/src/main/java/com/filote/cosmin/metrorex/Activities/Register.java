package com.filote.cosmin.metrorex.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.filote.cosmin.metrorex.Model.UserInformation;
import com.filote.cosmin.metrorex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        firebaseAuth= FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference();


        storageReference=FirebaseStorage.getInstance().getReference();


        progressDialog=new ProgressDialog(this);

        buttonRegister=(Button)findViewById(R.id.buttonRegister);

        editTextEmail=(EditText)findViewById(R.id.etusername);
        editTextPassword=(EditText)findViewById(R.id.etpassword);

        textViewSignIn=(TextView)findViewById(R.id.tvsignin);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);





    }

    private void registerUser() {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

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
        progressDialog.setMessage("Registering user.");
        progressDialog.show();



        firebaseAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user is succesfully registered and logged in
                            //start the profile activty
                            Toast.makeText(Register.this, "Registered Succesfully !", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            UserInformation userInformation = new UserInformation();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            databaseReference.child(user.getUid()).setValue(userInformation);

                            QRCodeWriter writer = new QRCodeWriter();
                            try {
                                BitMatrix bitMatrix = writer.encode("MetrorexPass"+user.getUid(), BarcodeFormat.QR_CODE, 512, 512);
                                int width = bitMatrix.getWidth();
                                int height = bitMatrix.getHeight();
                                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                    }
                                }

                                StorageReference userStorage=storageReference.child("BMP/"+"MetrorexPass"+firebaseAuth.getCurrentUser().getUid());
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 0 , bos);
                                byte[] bitmapdata = bos.toByteArray();
                                UploadTask uploadTask=userStorage.putBytes(bitmapdata);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(Register.this,"Upload failed for bmp",Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(Register.this,"Upload succes for bmp",Toast.LENGTH_SHORT).show();
                                    }
                                });



                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            startActivity(new Intent(Register.this, Login.class));
                        } else {
                            Toast.makeText(Register.this, "Could not register, please try again .", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }


    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            registerUser();
        }
        if(view == textViewSignIn) {
        //will open login activty here
            finish();
            startActivity(new Intent(this,Login.class));
        }

    }


}




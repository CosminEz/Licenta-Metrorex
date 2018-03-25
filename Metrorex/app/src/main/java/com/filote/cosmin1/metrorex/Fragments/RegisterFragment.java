package com.filote.cosmin1.metrorex.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.filote.cosmin1.metrorex.Activities.Profile;
import com.filote.cosmin1.metrorex.AppStatus;
import com.filote.cosmin1.metrorex.Interfaces.OnAddFragment;
import com.filote.cosmin1.metrorex.Model.UserInformation;
import com.filote.cosmin1.metrorex.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Calendar;

/**
 * Created by Cristian on 07.07.2017.
 */

public class RegisterFragment extends LoginRegisterFragmentBase implements View.OnClickListener {


    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private OnAddFragment mOnAddFragment;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        // views


        view.findViewById(R.id.buttonRegister).setOnClickListener(this);


        progressDialog = new ProgressDialog(getActivity());

        buttonRegister = (Button) view.findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) view.findViewById(R.id.etusername);
        editTextPassword = (EditText) view.findViewById(R.id.etpassword);


        buttonRegister.setOnClickListener(this);


        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        storageReference = FirebaseStorage.getInstance().getReference();


    }

    public void setOnAddFragment(OnAddFragment onAddFragment) {
        mOnAddFragment = onAddFragment;
    }


    private void displayErrorOnUI(final String errorMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayError(errorMessage);
            }
        });
    }


    /**
     * method for displaying errors in a TextView.
     *
     * @param errorMessage is used to show the error
     */

    public void displayError(String errorMessage) {
        final TextView errorTextView = (TextView) getView().findViewById(R.id.errorTextView);
        errorTextView.setText(errorMessage);

        errorTextView.setVisibility(View.VISIBLE);
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                // You don't need anything here
            }

            public void onFinish() {
                errorTextView.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    /**
     * @param email checks if email is valid
     */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 5;
    }


    /**
     * With this method the user is registered .
     * Checks for the email and password to be correct ( not empty and valid)
     * With the firebase it registers the user and creates a qr-code that will be stored in FirebaseStorage.
     * It will also send a verification mail .
     */


    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(email)) {
            editTextEmail.setError("required");
            editTextPassword.setError("required");
            Toast.makeText(getActivity().getApplicationContext(), "You must enter an email and password", Toast.LENGTH_SHORT).show();

            return;

        }

        if (TextUtils.isEmpty(email)) {
            //email is empty

            Toast.makeText(getActivity().getApplicationContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
            editTextEmail.setError("required");

            //stop the function to progress
            return;
        } else if (!isValidEmail(email)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            editTextEmail.setError("Invalid email");
            return;

        }

        if (TextUtils.isEmpty(password)) {
            //password is empty

            Toast.makeText(getActivity().getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            editTextPassword.setError("required");

            //stop the function to progress
            return;
        }

        //if validations are ok show progresDialog
        progressDialog.setMessage("Registering user.");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user is succesfully registered and logged in
                            //start the profile activty
                            Toast.makeText(getActivity(), "Registered Succesfully !", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            final UserInformation userInformation = new UserInformation();

                            Calendar cal = Calendar.getInstance();
                            userInformation.setExpirareAbonament(cal.getTime());

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            databaseReference.child(user.getUid()).setValue(userInformation);

                            QRCodeWriter writer = new QRCodeWriter();
                            try {
                                BitMatrix bitMatrix = writer.encode("MetrorexPass" + user.getUid(), BarcodeFormat.QR_CODE, 512, 512);
                                int width = bitMatrix.getWidth();
                                int height = bitMatrix.getHeight();
                                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                    }
                                }

                                StorageReference userStorage = storageReference.child("BMP/" + "MetrorexPass" + firebaseAuth.getCurrentUser().getUid());
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 0, bos);
                                byte[] bitmapdata = bos.toByteArray();

                                UploadTask uploadTask = userStorage.putBytes(bitmapdata);

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }


                            sendVerificationMail();


                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Could not register, please try again .", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });


    }

    /**
     * this method sends a verification mail with Firebase
     */

    protected void sendVerificationMail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getActivity().getApplicationContext(), "Email sent,please verify your mail!", Toast.LENGTH_LONG).show();


                        } else {

                        }

                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            if (AppStatus.getInstance(getActivity()).isOnline()) {
                registerUser();
                progressDialog.dismiss();
            } else
                displayError("You have to connect to the internet to be able to log in!");
        }


    }

}




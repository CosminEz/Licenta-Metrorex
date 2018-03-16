package com.filote.cosmin1.metrorex.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.filote.cosmin1.metrorex.Activities.Profile;
import com.filote.cosmin1.metrorex.AppStatus;
import com.filote.cosmin1.metrorex.Interfaces.OnAddFragment;
import com.filote.cosmin1.metrorex.Model.UserInformation;
import com.filote.cosmin1.metrorex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Cristian on 06.07.2017.
 */

public class LoginFragment extends LoginRegisterFragmentBase implements View.OnClickListener, TextView.OnEditorActionListener {

    private Button buttonLogin;
    private EditText editTextMailLogin;
    private EditText editTextPasswordLogin;
    private TextView textViewLogin;
    private Button buttonFacebook;

    private OnAddFragment mOnAddFragment;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private CallbackManager callbackManager;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // buttons


        buttonLogin = (Button) view.findViewById(R.id.buttonLogin);
        editTextMailLogin = (EditText) view.findViewById(R.id.etusernamelogin);
        editTextPasswordLogin = (EditText) view.findViewById(R.id.etpasswordlogin);
        textViewLogin = (TextView) view.findViewById(R.id.tvlogin);
        buttonFacebook = (LoginButton) view.findViewById(R.id.LoginFacebookBtn);
        buttonLogin.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        buttonFacebook.setOnClickListener(this);


        progressDialog = new ProgressDialog(getActivity());


        return view;
    }


    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        storageReference = FirebaseStorage.getInstance().getReference();

        if (firebaseAuth.getCurrentUser() != null) {
            //the user is already logged in , start the profile activity here

            if (AppStatus.getInstance(getActivity()).isOnline()) {
                startActivity(new Intent(getActivity().getApplicationContext(), Profile.class));
                getActivity().finish();
            } else
                Toast.makeText(getActivity().getApplicationContext(), "You must be connected to the Internet!", Toast.LENGTH_LONG).show();


        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getActivity());

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().logOut();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccesToken(loginResult.getAccessToken());


                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity().getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        if (error instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();

                            }
                            Log.e("Facebook", error.getMessage().toString());
                        }
                    }


                });


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

    }

    private void handleFacebookAccesToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final UserInformation userInformation = new UserInformation();


                            Calendar cal = Calendar.getInstance();
                            userInformation.setExpirareAbonament(cal.getTime());

                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            /** daca exista deja baza de date cu fb */

                            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        // user exists
                                    } else {
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
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }


                            });

                            mOnChangeActivity.changeActivity(Profile.class);


                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Could not register, please try again .", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    public void setOnAddFragment(OnAddFragment onAddFragment) {
        mOnAddFragment = onAddFragment;
    }

    // displayErrorOnUI
    // use this if you are on another thread
    //
    private void displayErrorOnUI(final String errorMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayError(errorMessage);
            }
        });
    }

    // displayError
    // displays the error in the textview from the botton of the page
    //
    public void displayError(String errorMessage) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

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


    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password);
    }

    //
    //  checks if name and password are correct
    //  authenticates the user
    //
    private void userLogin() {
        String email = editTextMailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(email)) {
            editTextMailLogin.setError("required");
            editTextPasswordLogin.setError("required");
            Toast.makeText(getActivity().getApplicationContext(), "You must enter an email and password", Toast.LENGTH_SHORT).show();

            return;

        }

        if (TextUtils.isEmpty(email)) {
            //email is empty

            Toast.makeText(getActivity().getApplicationContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
            editTextMailLogin.setError("required");

            //stop the function to progress
            return;
        } else if (!isValidEmail(email)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            editTextMailLogin.setError("Invalid email");
            return;

        }

        if (TextUtils.isEmpty(password)) {
            //password is empty

            Toast.makeText(getActivity().getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            editTextPasswordLogin.setError("required");

            //stop the function to progress
            return;
        }


        //if validations are ok show progresDialog
        progressDialog.setMessage("Login user.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //start the profile activity
                            Toast.makeText(getActivity().getApplicationContext(), "Login Succesfully !", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            getActivity().finish();
                            startActivity(new Intent(getActivity().getApplicationContext(), Profile.class));
                        } else {
                            //error on login
                            Toast.makeText(getActivity().getApplicationContext(), "Login Unsuccesfully, please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });


    }


    @Override
    public void onClick(View v) {


        if (v == buttonLogin) {
            if (AppStatus.getInstance(getActivity()).isOnline()) {
                userLogin();
                progressDialog.dismiss();
            } else
                displayError("You have to connect to the internet to be able to log in!");
        }
        if (v == textViewLogin) {
            //will open the register fragment
            mOnAddFragment.addFragment(null);

        }
        if (v == buttonFacebook) {
            if (AppStatus.getInstance(getActivity()).isOnline()) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
                progressDialog.dismiss();
            } else
                displayError("You have to connect to the internet to be able to log in!");
        }


    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            getActivity().findViewById(R.id.buttonLogin).performClick();
            return true;
        }
        return false;
    }
}

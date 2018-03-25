package com.filote.cosmin1.metrorex.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.filote.cosmin1.metrorex.Fragments.LoginFragment;
import com.filote.cosmin1.metrorex.Fragments.RegisterFragment;
import com.filote.cosmin1.metrorex.Interfaces.OnAddFragment;
import com.filote.cosmin1.metrorex.Interfaces.OnChangeActivity;
import com.filote.cosmin1.metrorex.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginRegister extends AppCompatActivity implements OnAddFragment, OnChangeActivity {
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        mFragmentManager = getFragmentManager();
        // add

        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setOnAddFragment(this);
        loginFragment.setOnChangeActivity(this);

        mFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, loginFragment)
                .addToBackStack("login")
                .commit();





    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * method to start the register fragment with animation
     */
    private void startRegister() {
        RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.setOnChangeActivity(this);

        mFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                .add(R.id.fragment_container, registerFragment)
                .addToBackStack("register")
                .commit();
    }

    @Override
    public void addFragment(Bundle b) {
        if (mFragmentManager.getBackStackEntryCount() <= 1)
            startRegister();
        else{
            Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .remove(fragment)
                    .commit();

            mFragmentManager.popBackStack();
        }


    }




    @Override
    public void changeActivity(Class intentClass) {
        startActivity(new Intent(this, intentClass));
        finish();

    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .remove(fragment)
                    .commit();

            mFragmentManager.popBackStack();
        } else
            finish();
    }
}

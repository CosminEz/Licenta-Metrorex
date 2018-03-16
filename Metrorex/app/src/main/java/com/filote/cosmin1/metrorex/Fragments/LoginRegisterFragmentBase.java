package com.filote.cosmin1.metrorex.Fragments;

import android.app.Fragment;


import com.filote.cosmin1.metrorex.Interfaces.OnChangeActivity;

/**
 * Created by Cosmin on 3/13/2018.
 */

public class LoginRegisterFragmentBase extends Fragment
{
    protected OnChangeActivity mOnChangeActivity;

    public void setOnChangeActivity(OnChangeActivity onChangeActivity)
    {
        mOnChangeActivity = onChangeActivity;
    }


}

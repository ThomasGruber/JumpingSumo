package com.support;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.parrot.sdksample.drone.JSDrone;


public class SelectListener implements OnItemSelectedListener {

    private JSDrone mJSDrone;

    public void setSelectListener (JSDrone mDrone) {

        mJSDrone = mDrone;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        mJSDrone.soundSwitch(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
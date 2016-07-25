package com.support;

import com.parrot.sdksample.drone.JSDrone;

/**
 * Created by Thomas on 24.07.16.
 */
public class DataSingelton {
    private static DataSingelton ourInstance = new DataSingelton();
    private JSDrone mJSDrone;


    public static DataSingelton getInstance() {
        return ourInstance;
    }

    private DataSingelton() {
    }

    public void setmJSDrone(JSDrone drone) {
        mJSDrone = drone;
    }

    public JSDrone getmJSDrone() {
        return mJSDrone;
    }
}

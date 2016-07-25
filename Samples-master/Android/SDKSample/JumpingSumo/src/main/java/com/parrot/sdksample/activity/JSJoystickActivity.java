package com.parrot.sdksample.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parrot.arsdk.arcommands.ARCOMMANDS_JUMPINGSUMO_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerCodec;
import com.parrot.arsdk.arcontroller.ARFrame;
import com.parrot.sdksample.R;
import com.parrot.sdksample.drone.JSDrone;
import com.parrot.sdksample.view.JSVideoView;
import com.support.DataSingelton;
import com.support.JoystickView;

import java.io.File;

public class JSJoystickActivity extends AppCompatActivity {
    private static final String TAG = "JSActivityJ";
    private JSDrone mJSDrone = null;

    private ProgressDialog mConnectionProgressDialog;
    private ProgressDialog mDownloadProgressDialog;

    private JSVideoView mVideoView;

    private TextView mBatteryLabel;
    private TextView mPicCount;
    private com.support.JoystickView joystickView;


    private int mNbMaxDownload;
    private int mCurrentDownloadIndex;
    private int GpicCount;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_joystick);

        initIHM();
        mPicCount = (TextView) findViewById(R.id.PicCountJ);

        joystickView=(JoystickView)findViewById(R.id.joystickviewJS);
        joystickView.setJoystickChangeListener(new JoystickView.JoystickChangeListener() {
            @Override
            public void onJoystickChanged(int power, int degree) {
                Log.i(TAG, "power:".concat(String.valueOf(power).concat(" - ")).concat("degree:").concat(String.valueOf(degree)));
            }
        });
        joystickView.setSelectListener();

        int[] recData;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                recData= null;
            } else {
                recData= extras.getIntArray("data");
            }
        } else {
            recData= savedInstanceState.getIntArray("data");
        }

        mBatteryLabel.setText(String.format("%d%%", recData[0]));
        mPicCount.setText(String.format("%d", recData[1]));
        GpicCount = recData[1];


    }

    @Override
    protected void onStart() {
        super.onStart();
        DataSingelton dataSingelton = DataSingelton.getInstance();
        mJSDrone = dataSingelton.getmJSDrone();
        mJSDrone.addListener(mJSListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i(TAG, "flip to portrait");
            if (mJSDrone != null)
            {
                Toast.makeText(getBaseContext(), "Disconnecting ...", Toast.LENGTH_SHORT).show();
                if (!mJSDrone.disconnect()) {
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mJSDrone != null)
        {
            mConnectionProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            mConnectionProgressDialog.setIndeterminate(true);
            mConnectionProgressDialog.setMessage("Disconnecting ...");
            mConnectionProgressDialog.show();

            if (!mJSDrone.disconnect()) {
                finish();
            }
        }
    }

    int i = 0;
    private void initIHM() {
        mVideoView = (JSVideoView) findViewById(R.id.videoViewJ);
        findViewById(R.id.takePictureJBt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mJSDrone.takePicture();
                Toast.makeText(getBaseContext(), "Picture was taken", Toast.LENGTH_LONG).show();
                Log.i(TAG, "take Picture");
            }
        });

        findViewById(R.id.jump_longJBt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mJSDrone.jump_long();
            }
        });

        findViewById(R.id.jump_highJBt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mJSDrone.jump_high();
            }
        });

        findViewById(R.id.JoystickJBt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.turnaroundJBt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                i++;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                    }
                };
                if (i == 1) {
                    mJSDrone.turnaround();
                } else if (i == 2) {
                    i = 0;
                    mJSDrone.turnaround_2();
                }

            }
        });

        mBatteryLabel = (TextView) findViewById(R.id.batteryLabelJ);
    }

    private final JSDrone.Listener mJSListener = new JSDrone.Listener() {
        @Override
        public void onDroneConnectionChanged(ARCONTROLLER_DEVICE_STATE_ENUM state) {
            switch (state)
            {
                case ARCONTROLLER_DEVICE_STATE_RUNNING:
                    mConnectionProgressDialog.dismiss();
                    break;

                case ARCONTROLLER_DEVICE_STATE_STOPPED:
                    // if the deviceController is stopped, go back to the previous activity
                    mConnectionProgressDialog.dismiss();
                    finish();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onBatteryChargeChanged(int batteryPercentage) {
            mBatteryLabel.setText(String.format("%d%%", batteryPercentage));
            Log.i(TAG,"battery: " + String.format("%d%%", batteryPercentage));
        }

        @Override
        public void onPictureTaken(ARCOMMANDS_JUMPINGSUMO_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM error) {
        }

        public void onPictureCount(int picCount) {
            GpicCount = picCount;
            runThread();
        }

        private void runThread(){
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    mPicCount.setText(String.format("%d", GpicCount));
                }
            }));
        }

        @Override
        public void configureDecoder(ARControllerCodec codec) {
        }

        @Override
        public void onFrameReceived(ARFrame frame) {
            mVideoView.displayFrame(frame);
        }

        @Override
        public void onMatchingMediasFound(int nbMedias) {

        }

        @Override
        public void onDownloadProgressed(String mediaName, int progress) {

        }

        @Override
        public void onDownloadComplete(String mediaName) {

        }


    };
}

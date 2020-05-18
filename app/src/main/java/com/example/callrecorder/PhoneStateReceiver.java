package com.example.callrecorder;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class PhoneStateReceiver extends BroadcastReceiver {

  /*  Date callStartTime;
    boolean isRecording;
    String savedNum;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNum = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            int state = 0;

            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }



    public void onCallStateChanged(Context context, int state, String number) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isRecording = true;
                callStartTime = new Date();
                savedNum = number;
                String stamp1 = "onIncomingCallReceived" + number + " " + callStartTime.toString();
                Toast.makeText(context, "" + stamp1, Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                    isRecording = true;
                    callStartTime = new Date();
                    startRecording(context);
                    String stamp3 = "onIncomingCallAnswered" + number + " " + callStartTime.toString();
                    Toast.makeText(context, "" + stamp3, Toast.LENGTH_LONG).show();
                    break;
            case TelephonyManager.CALL_STATE_IDLE:
                    if (isRecording) {
                        isRecording=false;
                    stopRecording(context);
                    String stamp5 = "onIncomingCallEnded" + number + " " + callStartTime.toString() + "\t" + new Date().toString();
                    Toast.makeText(context, "" + stamp5, Toast.LENGTH_LONG).show();
                }
        }



    } */

    private static final String TAG = PhoneStateReceiver.class.getSimpleName();

    public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";

    private static final String IDLE = "IDLE";
    private static final String OFFHOOK = "OFFHOOK";
    private static final String RINGING = "RINGING";

    public static final String LONGDATEKEY = "longdatekey";
    public static final String INCOMINGCALLKEY = "incomingcallkey";
    public static final String NUMBERKEY = "numberkey";

    private static boolean OUTGOING = false;
    private static boolean INCOMING = false;
    private static boolean ANSWERED = false;

    private static Intent sIntent;
    private static Intent sIntentFetching;
    private static String sNumber;

    @Override
    public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d("action",action);
            if (action.equals(OUTGOING_CALL)) {
                OUTGOING = true;
                sNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.d(TAG, "outgoing number is: " + sNumber);
                Log.d(TAG, "real action = " + action);
                Log.d(TAG, "state = " + state);
            }


            if (action.equals(PHONE_STATE)) {
                if (state != null) {
                    if (state.equals(RINGING)) {
                        INCOMING = true;
                        sNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        Log.d(TAG, "mobile phone is ringing..." + sNumber);
                    }
                }
            }

            if (OUTGOING || INCOMING && action.equals(PHONE_STATE)) {
                if (state != null) {
                    if (sIntent == null) {
                        sIntent = new Intent(context, RecordService.class);
                    }
                    if (state.equals(OFFHOOK)) {
                        ANSWERED = true;
                        Log.d(TAG, "start recording --> offhook");
                        // TODO: 30.05.2017 if - else should be replaced
                        sNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        Log.d(TAG, "mobile phone is ringing..." + sNumber);
                      //  mRecord.setNumber(sNumber);
                     //   sIntent.putExtra(mConstant.REC_PARC_KEY, (Parcelable) mRecord);
                      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(sIntent);
                        } else {*/
                            context.startService(sIntent);
                    //    }


                    } else if (state.equals(IDLE)) {
                        OUTGOING = false;
                        INCOMING = false;
                        Log.d(TAG, "call has been cancelled");
                        if (ANSWERED) {
                            Log.d(TAG, "stop recording");
                            context.stopService(sIntent);
                            ANSWERED = false;
                            Log.d(TAG, "number = " + sNumber);
                            if (sIntentFetching == null) {
                                sIntentFetching = new Intent(context, RecordService.class);
                             //   sIntent.putExtra(mConstant.REC_PARC_KEY, (Parcelable) mRecord);
                            }
                        }
                    }
                }
            }

    }

    private void stopRecording(Context context) {
        context.stopService(new Intent(context,RecordService.class));
    }

    private void startRecording(Context context) {
        Intent reivToServ = new Intent(context, RecordService.class);
 //       reivToServ.putExtra("number", phoneNumber);
        context.startService(reivToServ);
    }


}

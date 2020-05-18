package com.example.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import androidx.core.app.NotificationCompat;

import static android.content.ContentValues.TAG;

public class RecordService extends Service {
    static int count=0;
    MediaRecorder recorder;
    RecordInitiaizer recordInitiaizer;

    public RecordService() {
    }


    private Context context;
  //  private NotificationHandler notificationHandler;
  public static final String CHANNEL_ID = "ForegroundServiceChannel";
  private static final String IDLE = "IDLE";
    private static final String OFFHOOK = "OFFHOOK";
    private static final String RINGING = "RINGING";


    private static boolean OUTGOING = false;
    private static boolean INCOMING = false;
    private static boolean ANSWERED = false;

    private static String sNumber, sName;
    public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BroadcastReceiver CallStateReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("action","Broadcast");
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
                        Log.d(TAG, "mobile phone is ringing..." + sNumber);
                    }
                }
            }

            if (OUTGOING || INCOMING && action.equals(PHONE_STATE)) {
                if (state != null) {

                    if (state.equals(OFFHOOK)) {
                        ANSWERED = true;
                        Log.d(TAG, "start recording --> offhook");
                        // TODO: 30.05.2017 if - else should be replaced
                        if(sNumber==null)
                            sNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                        Log.d(TAG, "mobile phone is offHook..." + sNumber);
                      startRecording();



                    } else if (state.equals(IDLE)) {
                        OUTGOING = false;
                        INCOMING = false;
                        Log.d(TAG, "call has been cancelled");
                        if (ANSWERED) {
                            Log.d(TAG, "stop recording");
                            stopRecording();
                            ANSWERED = false;


                            }
                        }
                    }
                }
            }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        recordInitiaizer= new RecordInitiaizer();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Call Recorder")
                .setContentText("Listening for calls")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        Log.d("ss","Service started");
        IntentFilter filter = new IntentFilter();
        filter.addAction(PHONE_STATE);
        filter.addAction(OUTGOING_CALL);
        registerReceiver(CallStateReceiver,filter);

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static int getAudioSource(String str) {
        if (str.equals("MIC")) {
            return MediaRecorder.AudioSource.MIC;
        }
        else if (str.equals("VOICE_COMMUNICATION")) {
            return MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        }
        else if (str.equals("VOICE_CALL")) {
            return MediaRecorder.AudioSource.VOICE_CALL;
        }
        else if (str.equals("VOICE_DOWNLINK")) {
            return MediaRecorder.AudioSource.VOICE_DOWNLINK;
        }
        else if (str.equals("VOICE_UPLINK")) {
            return MediaRecorder.AudioSource.VOICE_UPLINK;
        }
        else if (str.equals("VOICE_RECOGNITION")) {
            return MediaRecorder.AudioSource.VOICE_RECOGNITION;
        }
        else if (str.equals("CAMCORDER")) {
            return MediaRecorder.AudioSource.CAMCORDER;
        }
        else {
            return MediaRecorder.AudioSource.DEFAULT;
        }
    }


    private boolean startMediaRecorder(final int audioSource){
        recorder = new MediaRecorder();
        try{
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
          //  recorder.setAudioSamplingRate(8000);
          //  recorder.setAudioEncodingBitRate(12200);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            ++count;
            String path=recordInitiaizer.getPath();
            String rec=path+"/"+recordInitiaizer.getTIme()+".mp4";
            Log.d("ss",rec);
            //  String fileName = audiofile.getAbsolutePath();
            recorder.setOutputFile(rec);

            recorder.prepare();
            // Sometimes prepare takes some time to complete
       //     Thread.sleep(2000);
            recorder.start();
            return true;
        }catch (Exception e){
            e.getMessage();
            return false;
        }
    }



    @Override
    public void onDestroy()
    {


        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            Toast.makeText(this, "Stopped Service", Toast.LENGTH_LONG).show();
            Log.d("Destroy", "onDestroy: " + "Recording stopped");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if(CallStateReceiver!=null) {
            unregisterReceiver(CallStateReceiver);
            CallStateReceiver = null;
        }
        super.onDestroy();

    }

    private void stopRecording() {
        Log.d(TAG, "number = " + sNumber);
        sName=recordInitiaizer.getContactName(sNumber,RecordService.this);
        Log.d("Name :",sName);
        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            Toast.makeText(this, "Stopped recording", Toast.LENGTH_LONG).show();
            Log.d("Destroy", "onDestroy: " + "Recording stopped");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        startMediaRecorder(getAudioSource("MIC"));
    }
}

package com.example.callrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class RecordService extends Service {

    MediaRecorder recorder;

    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        startMediaRecorder(getAudioSource("MIC"));

        return super.onStartCommand(intent, flags, startId);
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
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            String path=getPath();
            String rec=path+"/"+getTIme()+".3gp";

            //  String fileName = audiofile.getAbsolutePath();
            recorder.setOutputFile(rec);

        /*    MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
                public void onError(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e("ok", "OnErrorListener " + arg1 + "," + arg2);
                    //    terminateAndEraseFile();
                }
            };
            recorder.setOnErrorListener(errorListener);

            MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
                public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e("ok", "OnInfoListener " + arg1 + "," + arg2);
                    //    terminateAndEraseFile();
                }
            };
            recorder.setOnInfoListener(infoListener);*/


            recorder.prepare();
            // Sometimes prepare takes some time to complete
       //     Thread.sleep(2000);
            recorder.start();
            //   isRecordStarted = true;
            return true;
        }catch (Exception e){
            e.getMessage();
            return false;
        }
    }
    public String getPath()
    {
        String internalFile=getDate();
/*
        File file1=new File(Environment.getDataDirectory()+"/My Records/"+internalFile+"/");

        if(!file1.exists())
            file1.mkdir();*/


        File dir=new File(Environment.getExternalStorageDirectory()+"/My Records/");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        File file = new File(Environment.getExternalStorageDirectory()+"/My Records/"+internalFile+"/");
   //     Toast.makeText(this,root.getAbsolutePath(),Toast.LENGTH_LONG).show();
        if(!file.exists())
            file.mkdir();

        String path=file.getAbsolutePath();

    //    Log.d("TAGCM", "Path "+path);

        return path;
    }
    Calendar cal=Calendar.getInstance();

    public String getDate()
    {
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH)+1;
        int day=cal.get(Calendar.DATE);
        String date=String.valueOf(day)+"_"+String.valueOf(month)+"_"+String.valueOf(year);

     //   Log.d("TAGCM", "Date "+date);
        return date;
    }

    public String getTIme()
    {
        String am_pm="";
        int sec=cal.get(Calendar.SECOND);
        int min=cal.get(Calendar.MINUTE);
        int hr=cal.get(Calendar.HOUR);
        int amPm=cal.get(Calendar.AM_PM);
        if(amPm==1)
            am_pm="PM";
        else if(amPm==0)
            am_pm="AM";

        String time=String.valueOf(hr)+":"+String.valueOf(min)+":"+String.valueOf(sec)+" "+am_pm;

        Log.d("Time", "Date "+time);
        return time;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

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

    }
}

package com.example.callrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button start,stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start=findViewById(R.id.start);
        stop=findViewById(R.id.stop);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getBaseContext(),RecordService.class));
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getBaseContext(),RecordService.class));
            }
        });

       /*String state=Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state))
        {

            File root=Environment.getExternalStorageDirectory();

            File dir = new File(root.getAbsolutePath()+"/MyApp");
            Toast.makeText(this,root.getAbsolutePath(),Toast.LENGTH_LONG).show();
            if(!dir.exists()) {
                dir.mkdir();

            }
            File file = new File(dir,"mysg.txt");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write("Hello".getBytes());
                fos.close();
                Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();

            } catch (Exception e) {
               // Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(this,"Not found storage",Toast.LENGTH_LONG).show();
        }
//        String path = Environment.getExternalStorageDirectory().toString()+"/Music";
//        Log.d("Files", "Path: " + path);
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (int i = 0; i < files.length; i++)
//        {
//            Log.d("Files", "FileName:" + files[i].getName());
//        }*/
    }

  /*  public String getPath()
    {
        String internalFile="okay";
      //  File file=new File(Environment.getExternalStorageDirectory()+"/My Records/");
        try{
            File file1=new File(Environment.getExternalStorageDirectory()+"/text/");
            if(!file1.exists())
            {
                Toast.makeText(this,"created",Toast.LENGTH_LONG).show();
                file1.mkdir();
            }
            File f=new File(file1,"temp.txt");
            try{
                FileOutputStream os = new FileOutputStream(f);
                os.write("Hello friends".getBytes());
                os.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        //    f.createNewFile();
            String path=file1.getAbsolutePath();
            Log.d("TAGCM", "Path "+path);

            return path;
        }catch (Exception e)
        {
            return "hi";
        }

    }*/


}

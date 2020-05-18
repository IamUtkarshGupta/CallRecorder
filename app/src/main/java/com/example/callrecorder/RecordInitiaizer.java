package com.example.callrecorder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.util.Calendar;



public class RecordInitiaizer {



    public String getPath()
    {
        String internalFile=getDate();

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


    public String getDate()
    {
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH)+1;
        int day=cal.get(Calendar.DATE);
        String date=String.valueOf(day)+"_"+String.valueOf(month)+"_"+String.valueOf(year);

        //   Log.d("TAGCM", "Date "+date);
        return date;
    }

    public String getTIme()
    {
        Calendar cal =Calendar.getInstance();
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

    public String getContactName(final String number, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        String[] projection=new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        if(contactName!=null && !contactName.equals(""))
            return contactName;
        else
            return "Unknown number";
    }

}

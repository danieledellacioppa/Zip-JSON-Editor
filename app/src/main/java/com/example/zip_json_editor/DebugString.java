package com.example.zip_json_editor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;


public class DebugString
{
    final String TAG = this.getClass().toString();
    boolean DEBUG =true;
    ArrayList<String> dbg = new ArrayList<>();
    TextView textView;
    String operationTime;

    // this constructor allows to pass an array of information which can be of any size.
    // Depends on how many thing you want to take into account

    private void initialSetting(TextView t)
    {
        //This is roughly the time when DebugString class has been initiated
        operationTime="["+new Date().getTime()+"]";

        //I'm taking the textView to display output on
        textView=t;

        //this is the thread id of the current thread asking to write debug info
        dbg.add(" ◂ "+Thread.currentThread().getId()+"◗"+" ");
    }

    public void appendString(String s)
    {
        if (s == null)
            dbg.add("NULL");
        else
            dbg.add(s);
    }

    DebugString(String[] array, TextView t)
    {
            initialSetting(t);
            for (String s : array)
                appendString(s);
            this.debugInfo();
    }

    // This Constructor allows us to pass just a string as parameter instead of an array
    DebugString(String s, TextView t)
    {
            initialSetting(t);
            appendString(s);
            this.debugInfo();
    }

    public void debugInfo()
    {
        Handler handler = new Handler(Looper.getMainLooper());

        //this is the only place where you can actually use the textView.
        //the runnable we're passing to handler.post() will be executed on the main thread
        //which is the thread who better controls graphic objects and also created the textView in
        //the first place. Never allow another thread to modify a graphical object who was created
        //in another thread
        handler.post(() -> {
            if(DEBUG)
               textView.append(operationTime);
            Log.d(TAG, operationTime);
            if(DEBUG)
                textView.append("◖"+Thread.currentThread().getId());
            Log.d(TAG, "◖"+Thread.currentThread().getId());
            if(DEBUG)
                for (int i=0; i<dbg.size();i++)
                    textView.append(dbg.get(i));
            Log.d(TAG, dbg.toString());
            if(DEBUG)
                textView.append("\n\n");
            Log.d(TAG, "\n");
        });

    }

}

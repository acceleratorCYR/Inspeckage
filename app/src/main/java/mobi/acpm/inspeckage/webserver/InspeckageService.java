package mobi.acpm.inspeckage.webserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import mobi.acpm.inspeckage.R;
import mobi.acpm.inspeckage.ui.MainActivity;

/**
 * Created by acpm on 17/11/15.
 */
public class InspeckageService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    private WebServer ws;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Context context = getApplicationContext();

        String host = null;
        int port = 8008;
        if (intent != null && intent.getExtras() != null) {
            host = intent.getStringExtra("host");
            port = intent.getIntExtra("port", 8008);
        }

        try {
/*
            Throwable th = new Throwable();
            StackTraceElement[] stackTraces = th.getStackTrace();

            StringBuffer stackOutput = new StringBuffer("monitor InspeckageService.onStartCommand ");

            if(stackTraces != null)
            {
                int i = 0;
                for(; i< (stackTraces.length < 22? stackTraces.length: 22); i++)
                {
                    stackOutput.append(String.format("\n      StackTrace: %s #%s :%s",
                            stackTraces[i].getClassName(),
                            stackTraces[i].getMethodName(), stackTraces[i].getLineNumber()));
                    if(stackTraces[i].getClassName().endsWith("Log"))
                        break;

                }
                if(i >= 22)
                {
                    stackOutput.append("\n      ......   ...... \n");
                }
            }
            Log.i("Inspeckage", stackOutput.toString());

            Log.i("Inspeckage", "InspeckageService start WebServer");
            */

            ws = new WebServer(host, port, context);

            //添加下列代码将后台Service变成前台Service
            //构建"点击通知后打开MainActivity"的Intent对象
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            String CHANNEL_ONE_ID = "Inspeckage";
            String CHANNEL_ONE_NAME = "WebService";
            NotificationChannel notificationChannel = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
            }

            //新建Builer对象
            Notification.Builder builer = new Notification.Builder(this);
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builer.setChannelId(CHANNEL_ONE_ID);
            }

            builer.setContentTitle("WebService");//设置通知的标题
            builer.setContentText("WebServer.Inspeckage is running");//设置通知的内容
            builer.setSmallIcon(R.mipmap.ic_launcher);//设置通知的图标
            builer.setContentIntent(pendingIntent);//设置点击通知后的操作

            //Notification notification = builer.getNotification();//将Builder对象转变成普通的notification
            startForeground(110, builer.build());//让Service变成前台Service,并在系统的状态栏显示出来

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Service started on port " + port, Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        /*
        Log.i("Inspeckage", "InspeckageService.onDestory()");
        Throwable th = new Throwable();
        StackTraceElement[] stackTraces = th.getStackTrace();

        StringBuffer stackOutput = new StringBuffer("monitor InspeckageService.OnDestory ");

        if(stackTraces != null)
        {
            int i = 0;
            for(; i< (stackTraces.length < 22? stackTraces.length: 22); i++)
            {
                stackOutput.append(String.format("\n      StackTrace: %s #%s :%s",
                        stackTraces[i].getClassName(),
                        stackTraces[i].getMethodName(), stackTraces[i].getLineNumber()));
                if(stackTraces[i].getClassName().endsWith("Log"))
                    break;

            }
            if(i >= 22)
            {
                stackOutput.append("\n      ......   ...... \n");
            }
        }
        Log.i("Inspeckage", stackOutput.toString());
        */
        super.onDestroy();
        if(ws!=null)
            ws.stop();

        stopForeground(true);

        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }
}

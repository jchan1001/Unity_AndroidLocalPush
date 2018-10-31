package jchan1001.co.jp.unitylocalpush;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Calendar;

public class LocalPush extends Activity {

    public static int APP_NOT_LAUNCHED = 0;
    public static int APP_LAUNCHED = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // アプリが一回でも起動した場合
        SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);

        if (data.getInt("isAppLunched",APP_NOT_LAUNCHED ) == APP_NOT_LAUNCHED) {
            SharedPreferences.Editor editor = data.edit();
            editor.putInt("isAppLunched", APP_LAUNCHED);
            editor.apply();
        }
        SetPush(getApplicationContext());
    }

    public void SetPush(Context context) {
        SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("title", "Title");
        editor.putString("msg", "Msg");
        editor.apply();

        Calendar triggerTime = Calendar.getInstance();
        triggerTime.add(Calendar.SECOND, 5); //5秒後
        Intent intent = new Intent(context.getApplicationContext(), UnityNotifier.class);

        PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager != null) {
            manager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), sender);
        }
    }
}

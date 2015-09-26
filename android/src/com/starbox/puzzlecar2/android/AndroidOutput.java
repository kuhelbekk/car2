package com.starbox.puzzlecar2.android;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.starbox.puzzlecar2.PayCar2;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.sql.Time;

public class AndroidOutput implements PayCar2 {

    Handler uiThread;
    Context appContext;
    AndroidLauncher parent;
    long timerToast;

    public AndroidOutput(Context appContext,AndroidLauncher parent) {

        uiThread = new Handler();
        this.appContext = appContext;
        this.parent = parent;
    }

    @Override
    public void payClick() {
        parent.onUpgradeClicked();
    }

    @Override
    public void youTubeClick(final String langStr) {
        parent.youTubeClick(langStr);
    }

    @Override
    public String getAId() {
        return parent.getAId();
    }

    @Override
    public void showToast(final CharSequence str) {
        if ((timerToast+2000)>TimeUtils.millis()) {
            return;
        }
        timerToast = TimeUtils.millis();
        uiThread.post(new Runnable() {
            public void run() {
                Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getAccuracy() {
        return parent.getAccuracy();
    }

}

package com.example.SocyMusic;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.example.musicplayer.R;

/**
 * This class is necessary to create notifications according to different Android versions
 */
public class SocyMusicApp extends Application {
    // Unique ID for the media channel only!
    public static final String MEDIA_CHANNEL_ID = "media_channel";

    /**
     * When this class gets invoked, this method gets called
     */
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    /**
     * According to which version of Android is being used it is necessary to create a
     * NotificationChannel.
     */
    private void createNotificationChannels() {
        // Checks for Buildversion == Androidversion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    MEDIA_CHANNEL_ID,
                    "Media Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription(getString(R.string.channel_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

}

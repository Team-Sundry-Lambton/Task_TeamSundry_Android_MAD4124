package mad4124.team_sundry.task;

import androidx.multidex.MultiDexApplication;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

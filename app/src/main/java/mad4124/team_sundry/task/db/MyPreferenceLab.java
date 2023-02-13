package mad4124.team_sundry.task.db;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferenceLab {

    private static final String PREFERENCE_NAME = "Sundry_Preference";
    SharedPreferences sharedPreferences;

    public MyPreferenceLab(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    private static final String FIRST_LOAD = "firstLoad";
    public boolean isFirstLoad() {
        return sharedPreferences.getBoolean(FIRST_LOAD,false);
    }
    public void setFirstLoad(boolean firstLoad) {
        sharedPreferences.edit().putBoolean(FIRST_LOAD,firstLoad).apply();
    }
}

package mad4124.team_sundry.task.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import mad4124.team_sundry.task.db.MyPreferenceLab;

@InstallIn(SingletonComponent.class)
@Module()
public class PreferenceModule {

    @Provides
    @Singleton
    MyPreferenceLab getPreferenceLab(@ApplicationContext Context context){
        return new MyPreferenceLab(context);
    }

}

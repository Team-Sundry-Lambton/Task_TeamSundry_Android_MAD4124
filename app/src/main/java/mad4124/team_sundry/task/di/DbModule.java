package mad4124.team_sundry.task.di;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import mad4124.team_sundry.task.db.AppDatabase;
import mad4124.team_sundry.task.db.DbDao;

@InstallIn(SingletonComponent.class)
@Module()
public class DbModule {

    @Provides
    @Singleton
    AppDatabase provideDB(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "SundryDatabase")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    DbDao provideDbDao(AppDatabase appDatabase) {
        return appDatabase.dbDao();
    }

}

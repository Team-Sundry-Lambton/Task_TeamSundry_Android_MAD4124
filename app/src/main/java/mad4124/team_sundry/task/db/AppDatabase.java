package mad4124.team_sundry.task.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MapLocation;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;

@Database(entities = {
        Category.class,
        Task.class,
        SubTask.class,
        MapLocation.class,
        MediaFile.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DbDao dbDao();

}

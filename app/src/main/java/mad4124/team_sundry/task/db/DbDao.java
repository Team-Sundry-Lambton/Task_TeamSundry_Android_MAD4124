package mad4124.team_sundry.task.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.Task;

@Dao
public interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCategory(Category category);

    @Query("Select * from categories")
    LiveData<List<Category>> getAllCategories();

    @Delete
    void deleteCategory(Category category);

    @Query("Select * from tasks")
    List<Task> getAllTasks();

}

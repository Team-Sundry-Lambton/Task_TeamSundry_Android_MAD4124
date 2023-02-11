package mad4124.team_sundry.task.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;

@Dao
public interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCategory(Category category);

    @Query("Select * from categories")
    LiveData<List<Category>> getAllCategories();

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY title ASC")
    List<Task> getAllTasks(int categoryID);

    @Query("Select * from medias  WHERE task_id = :taskID")
    List<MediaFile> getAllMedias(int taskID);

    @Delete
    public abstract void delete(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Task task);

    @Query("Select * from subTasks  WHERE task_id = :taskID")
    List<SubTask> getAllSubTask(int taskID);

    @Query("UPDATE tasks SET status =:complete WHERE id = :id")
    void markTaskCompleted(boolean complete, int id);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY createdDate ASC")
    List<Task> getAllTasksSortByCreatedDate(int categoryID);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY dueDate ASC")
    List<Task> getAllTasksSortByDueDate(int categoryID);

    @Query("UPDATE tasks SET category_id =:categoryId WHERE id in :(taskIDs)")
    void changeParentOfSelectedTasks(int categoryId, List<Integer> taskIds);
}

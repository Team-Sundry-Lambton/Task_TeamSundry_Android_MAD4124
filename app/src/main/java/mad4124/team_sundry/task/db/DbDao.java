package mad4124.team_sundry.task.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MapLocation;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;

@Dao
public interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCategory(Category category);

    @Query("Select * from categories")
    List<Category> getAllCategories();

    @Query("Select * from categories where name like '%'||:text||'%'")
    LiveData<List<Category>> getAllLiveCategories(String text);

    @Query("Select COUNT(*) from categories where LOWER(name) = LOWER(:text) AND (id <> :categoryId OR :categoryId = 0)")
    int getCategoryByName(String text, long categoryId);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY title ASC")
    List<Task> getAllTasks(long categoryID);

    @Query("Select * from medias  WHERE task_id = :taskID")
    List<MediaFile> getAllMedias(long taskID);

    @Update
    void update(Task task);
    @Delete
    void delete(Task task);
    @Query("Delete from medias where task_id = :taskId")
    void deleteMedias(long taskId);
    @Query("Delete from subTasks where task_id = :taskId")
    void deleteSubTasks(long taskId);
    @Query("Delete from locations where task_id = :taskId")
    void deleteLocations(long taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SubTask subTask);
    @Update
    void update(SubTask subTask);
    @Delete
    void delete(SubTask subTask);
    @Query("Select * from subTasks where task_id = :taskId order by id")
    List<SubTask> getAllSubTasks(long taskId);
    @Query("Select * from subTasks where task_id = :taskId order by id")
    LiveData<List<SubTask>> getAllLiveSubTasks(long taskId);
    @Query("Select * from medias where task_id = :taskId and isImage = :isImage")
    List<MediaFile> getAllMedias(long taskId,boolean isImage);
    @Query("Select * from medias where task_id = :taskId and isImage = :isImage")
    LiveData<List<MediaFile>> getAllLiveMedias(long taskId,boolean isImage);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Task task);

    @Query("Select * from subTasks  WHERE task_id = :taskID")
    List<SubTask> getAllSubTask(long taskID);

    @Query("UPDATE tasks SET status =:complete WHERE id = :id")
    void markTaskCompleted(boolean complete, long id);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY title ASC")
    LiveData<List<Task>> getAllLiveTasks(long categoryID);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY createdDate ASC")
    List<Task> getAllTasksSortByCreatedDate(long categoryID);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY dueDate ASC")
    List<Task> getAllTasksSortByDueDate(long categoryID);

    @Query("UPDATE tasks SET category_id =:categoryId WHERE id = :taskIs")
    void changeParentOfSelectedTasks(long categoryId,long taskIs);

    @Query("UPDATE categories SET name =:categoryName WHERE id = :id")
    void updateCategoryName(String categoryName,long id);

    @Query("Select * from categories where name like '%'||:text||'%' AND  id != :categoryID")
    LiveData<List<Category>> getAllCategoriesExceptSelected(long categoryID,String text);

    @Query("Select * from locations where category_id = :categoryID")
    List<MapLocation> getAllMapPin(long categoryID);

    @Query("Select * from locations where task_id = :taskId limit 1")
    MapLocation getMapPin(long taskId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMap(MapLocation mapLocation);
    @Update
    void updateMap(MapLocation mapLocation);
    @Delete
    void deleteMap(MapLocation mapLocation);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MediaFile mediaFile);
    @Update
    void update(MediaFile mediaFile);
    @Delete
    void delete(MediaFile mediaFile);

    @Query("UPDATE locations SET category_id =:categoryId WHERE id = :locationId")
    void changeCategoryOfSelectedLocation(long categoryId,long locationId);
}

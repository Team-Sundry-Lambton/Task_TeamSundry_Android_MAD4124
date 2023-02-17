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
    List<Category> getAllCategories();

    @Query("Select * from categories where name like '%'||:text||'%'")
    LiveData<List<Category>> getAllLiveCategories(String text);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY title ASC")
    List<Task> getAllTasks(int categoryID);

    @Query("Select * from medias  WHERE task_id = :taskID")
    List<MediaFile> getAllMedias(int taskID);

    @Delete
    void delete(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
   void insert(Task task);

    @Query("Select * from subTasks  WHERE task_id = :taskID")
    List<SubTask> getAllSubTask(int taskID);

    @Query("UPDATE tasks SET status =:complete WHERE id = :id")
    void markTaskCompleted(boolean complete, int id);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY createdDate ASC")
    List<Task> getAllTasksSortByCreatedDate(int categoryID);

    @Query("SELECT * FROM tasks WHERE category_id = :categoryID ORDER BY dueDate ASC")
    List<Task> getAllTasksSortByDueDate(int categoryID);

    @Query("UPDATE tasks SET category_id =:categoryId WHERE id = :taskIs")
    void changeParentOfSelectedTasks(int categoryId,int taskIs);

    @Query("UPDATE categories SET name =:categoryName WHERE id = :id")
    void updateCategoryName(String categoryName,int id);

    @Query("Select * from categories where id != :categoryID")
    List<Category> getAllCategoriesExceptSelected(int categoryID);
}

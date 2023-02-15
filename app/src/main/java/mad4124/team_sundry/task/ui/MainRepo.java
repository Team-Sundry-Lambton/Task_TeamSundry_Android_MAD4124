package mad4124.team_sundry.task.ui;


import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import mad4124.team_sundry.task.db.DbDao;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;

public class MainRepo {

    DbDao dbDao;

    @Inject
    public MainRepo(DbDao dbDao) {
        this.dbDao = dbDao;
    }

    void addCategory(Category category) {
        dbDao.addCategory(category);
    }

    List<Category> getAllCategory() {
       return dbDao.getAllCategories();
    }

    LiveData<List<Category>> getAllLiveCategory(String text) {
        return dbDao.getAllLiveCategories(text);
    }

    void changeParentOfSelectedTasks(int categoryId,int taskIs){
        dbDao.changeParentOfSelectedTasks(categoryId,taskIs);
    }

    List<Task> getAllTasks(int categoryID){
        return dbDao.getAllTasks(categoryID);
    }

    void delete(Task task){
        dbDao.delete(task);
    }

    void insert(Task task){
        dbDao.insert(task);
    }

    void markTaskCompleted(boolean complete, int id){
        dbDao.markTaskCompleted(complete, id);
    }

    List<SubTask> getAllSubTask(int taskID){
        return dbDao.getAllSubTask(taskID);
    }

    List<Task> getAllTasksSortByCreatedDate(int categoryID){
        return dbDao.getAllTasksSortByCreatedDate(categoryID);
    }

    List<Task> getAllTasksSortByDueDate(int categoryID){
        return dbDao.getAllTasksSortByDueDate(categoryID);
    }

    List<MediaFile> getAllMedias(int taskID){
        return dbDao.getAllMedias(taskID);
    }
    void updateCategoryName(String categoryName,int id){ dbDao.updateCategoryName(categoryName, id); }

    void deleteCategory(Category category) { dbDao.deleteCategory(category);}
}

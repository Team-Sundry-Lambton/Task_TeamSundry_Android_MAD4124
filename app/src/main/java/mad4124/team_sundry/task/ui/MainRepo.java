package mad4124.team_sundry.task.ui;


import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import mad4124.team_sundry.task.db.DbDao;
import mad4124.team_sundry.task.db.MyPreferenceLab;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MapLocation;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;

public class MainRepo {

    DbDao dbDao;

    ExecutorService executorService;

    MyPreferenceLab myPreferenceLab;

    @Inject
    public MainRepo(DbDao dbDao,ExecutorService executorService,MyPreferenceLab myPreferenceLab) {
        this.dbDao = dbDao;
        this.executorService = executorService;
        this.myPreferenceLab = myPreferenceLab;
    }

    boolean getFirstLoad(){
        return myPreferenceLab.isFirstLoad();
    }
    void setFirstLoad(boolean firstLoad){
        myPreferenceLab.setFirstLoad(firstLoad);
    }

    void addCategory(Category category) {
        executorService.execute( () -> {
            dbDao.addCategory(category);
        });
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

    void update(Task task){
        executorService.execute( () -> {
            dbDao.update(task);
        });
    }
    void delete(Task task){
        executorService.execute( () -> {
            dbDao.deleteSubTasks(task.getId());
            dbDao.deleteMedias(task.getId());
            dbDao.deleteLocations(task.getId());
            dbDao.delete(task);
        });
    }

    void insert(SubTask subTask){
        executorService.execute( () -> {
            dbDao.insert(subTask);
        });
    }
    void update(SubTask subTask){
        executorService.execute( () -> {
            dbDao.update(subTask);
        });
    }
    void delete(SubTask subTask){
        executorService.execute( () -> {
            dbDao.delete(subTask);
        });
    }
    LiveData<List<SubTask>> getSubTasksLive(int taskId){
        return dbDao.getAllLiveSubTasks(taskId);
    }
    LiveData<List<MediaFile>> getMediaLive(int taskId,boolean isImage){
        return dbDao.getAllLiveMedias(taskId,isImage);
    }

    void insert(Task task){
        executorService.execute( () -> {
            dbDao.insert(task);
        });
    }

    void markTaskCompleted(boolean complete, int id){
        executorService.execute( () -> {
            dbDao.markTaskCompleted(complete, id);
        });
    }

    List<SubTask> getAllSubTask(int taskID){
        return dbDao.getAllSubTask(taskID);
    }

    List<Task> getAllTasksSortByCreatedDate(int categoryID){
//        Future<List<Task>> future =  Executors.newSingleThreadExecutor().submit(() -> dbDao.getAllTasksSortByCreatedDate(categoryID));
        return dbDao.getAllTasksSortByCreatedDate(categoryID);
    }

    List<Task> getAllTasksSortByDueDate(int categoryID){
        return dbDao.getAllTasksSortByDueDate(categoryID);
    }

    List<MediaFile> getAllMedias(int taskID){
        return dbDao.getAllMedias(taskID);
    }
    void updateCategoryName(String categoryName,int id){ dbDao.updateCategoryName(categoryName, id); }


    void deleteCategory(Category category) {
        executorService.execute(() -> {
            dbDao.deleteCategory(category);
        });
    }

    //void deleteCategory(Category category) { dbDao.deleteCategory(category);}

    List<Category> getAllCategoriesExceptSelected(int categoryID){
        return dbDao.getAllCategoriesExceptSelected(categoryID);
    }

    List<MapLocation> getAllMapPin(int categoryID) {
        return dbDao.getAllMapPin(categoryID);
    }

    MapLocation getMapPin(int taskId) {
        return dbDao.getMapPin(taskId);
    }

    void updateMap(MapLocation mapLocation){
        executorService.execute( () -> {
            dbDao.updateMap(mapLocation);
        });
    }
    void deleteMap(MapLocation mapLocation){
        executorService.execute( () -> {
            dbDao.deleteMap(mapLocation);
        });
    }

    void insertMap(MapLocation mapLocation){
        executorService.execute( () -> {
            dbDao.insertMap(mapLocation);
        });
    }
    void insert(MediaFile mediaFile){
        executorService.execute( () -> {
            dbDao.insert(mediaFile);
        });
    }
    void update(MediaFile mediaFile){
        executorService.execute( () -> {
            dbDao.update(mediaFile);
        });
    }
    void delete(MediaFile mediaFile){
        executorService.execute( () -> {
            dbDao.delete(mediaFile);
        });
    }

    LiveData<List<Task>> getAllLiveTasks(int categoryID){
        return dbDao.getAllLiveTasks(categoryID);
    }
}

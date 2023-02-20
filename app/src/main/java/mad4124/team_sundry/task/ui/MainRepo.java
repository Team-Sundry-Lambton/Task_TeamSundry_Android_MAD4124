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

    void changeParentOfSelectedTasks(long categoryId,long taskIs){
        dbDao.changeParentOfSelectedTasks(categoryId,taskIs);
    }

    List<Task> getAllTasks(long categoryID){
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
    List<SubTask> getSubTasks(long taskId){
        return dbDao.getAllSubTasks(taskId);
    }
    LiveData<List<SubTask>> getSubTasksLive(long taskId){
        return dbDao.getAllLiveSubTasks(taskId);
    }
    List<MediaFile> getMedia(long taskId,boolean isImage){
        return dbDao.getAllMedias(taskId,isImage);
    }
    LiveData<List<MediaFile>> getMediaLive(long taskId,boolean isImage){
        return dbDao.getAllLiveMedias(taskId,isImage);
    }

    void insert(Task task){
        executorService.execute( () -> {
            dbDao.insert(task);
        });
    }
    void insert(Task task,List<MediaFile> audios,List<MediaFile> images,List<SubTask> subTasks){
        executorService.execute( () -> {
            long id = dbDao.insert(task);
            for(MediaFile audio:audios){
                audio.setTaskId(id);
                dbDao.insert(audio);
            }
            for(MediaFile image:images){
                image.setTaskId(id);
                dbDao.insert(image);
            }
            for(SubTask subTask:subTasks){
                subTask.setParentTaskId(id);
                dbDao.insert(subTask);
            }
        });
    }
    void update(Task task,List<MediaFile> audios,List<MediaFile> images,List<SubTask> subTasks){
        executorService.execute( () -> {
            dbDao.update(task);
            for(MediaFile audio:audios){
                audio.setTaskId(task.getId());
                dbDao.insert(audio);
            }
            for(MediaFile image:images){
                image.setTaskId(task.getId());
                dbDao.insert(image);
            }
            for(SubTask subTask:subTasks){
                subTask.setParentTaskId(task.getId());
                dbDao.insert(subTask);
            }
        });
    }

    void markTaskCompleted(boolean complete, long id){
        executorService.execute( () -> {
            dbDao.markTaskCompleted(complete, id);
        });
    }

    List<SubTask> getAllSubTask(long taskID){
        return dbDao.getAllSubTask(taskID);
    }

    List<Task> getAllTasksSortByCreatedDate(long categoryID){
//        Future<List<Task>> future =  Executors.newSingleThreadExecutor().submit(() -> dbDao.getAllTasksSortByCreatedDate(categoryID));
        return dbDao.getAllTasksSortByCreatedDate(categoryID);
    }

    List<Task> getAllTasksSortByDueDate(long categoryID){
        return dbDao.getAllTasksSortByDueDate(categoryID);
    }

    List<MediaFile> getAllMedias(int taskID,boolean isImage){
        return dbDao.getAllMedias(taskID,isImage);
    }
    void updateCategoryName(String categoryName,long id){ dbDao.updateCategoryName(categoryName, id); }


    void deleteCategory(Category category) {
        executorService.execute(() -> {
            dbDao.deleteCategory(category);
        });
    }

    //void deleteCategory(Category category) { dbDao.deleteCategory(category);}

    LiveData<List<Category>> getAllCategoriesExceptSelected(long categoryID,String text){
        return dbDao.getAllCategoriesExceptSelected(categoryID,text);
    }

    List<MapLocation> getAllMapPin(long categoryID) {
        return dbDao.getAllMapPin(categoryID);
    }

    MapLocation getMapPin(long taskId) {
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

    LiveData<List<Task>> getAllLiveTasks(long categoryID){
        return dbDao.getAllLiveTasks(categoryID);
    }
}

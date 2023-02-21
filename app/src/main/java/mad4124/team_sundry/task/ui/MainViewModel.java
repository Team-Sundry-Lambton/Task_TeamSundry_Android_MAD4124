package mad4124.team_sundry.task.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MapLocation;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;

@HiltViewModel
public class MainViewModel extends ViewModel {

    MainRepo repo;

    @Inject
    public MainViewModel(MainRepo repo) {
        this.repo = repo;
    }

    public boolean getFirstLoad(){
        return repo.getFirstLoad();
    }
    public void setFirstLoad(boolean firstLoad){
        repo.setFirstLoad(firstLoad);
    }

    public void addCategory(Category category) {
        repo.addCategory(category);
    }

    public List<Category> getAllCategories(){
        return repo.getAllCategory();
    }

    public LiveData<List<Category>> getAllLiveCategories(String text){
        return repo.getAllLiveCategory(text);
    }

    public void changeParentOfSelectedTasks(long categoryId,long taskIs){
        repo.changeParentOfSelectedTasks(categoryId,taskIs);
    }

    public List<Task> getAllTasks(long categoryID){
        return repo.getAllTasks(categoryID);
    }

    public void update(Task task){
        repo.update(task);
    }
    public void delete(Task task){
        repo.delete(task);
    }

    public void insert(SubTask subTask){
        repo.insert(subTask);
    }
    public void update(SubTask subTask){
        repo.update(subTask);
    }
    public void delete(SubTask subTask){
        repo.delete(subTask);
    }

    public void insertMap(MapLocation mapLocation){
        repo.insertMap(mapLocation);
    }
    public void updateMap(MapLocation mapLocation){
        repo.updateMap(mapLocation);
    }
    public void deleteMap(MapLocation mapLocation){
        repo.deleteMap(mapLocation);
    }

    public List<SubTask> getSubTasks(long taskId){
        return repo.getSubTasks(taskId);
    }
    public LiveData<List<SubTask>> getSubTasksLive(long taskId){
        return repo.getSubTasksLive(taskId);
    }
    public List<MediaFile> getMedia(long taskId,boolean isImage){
        return repo.getMedia(taskId,isImage);
    }
    public LiveData<List<MediaFile>> getMediaLive(long taskId,boolean isImage){
        return repo.getMediaLive(taskId,isImage);
    }

    public void insert(Task task){
        repo.insert(task);
    }
    public void insert(Task task,List<MediaFile> audios,List<MediaFile> images,List<SubTask> subTasks,MapLocation location){
        repo.insert(task,audios,images,subTasks,location);
    }
    public void update(Task task,List<MediaFile> audios,List<MediaFile> images,List<SubTask> subTasks,MapLocation location){
        repo.update(task,audios,images,subTasks,location);
    }

    public void markTaskCompleted(boolean complete, long id){
        repo.markTaskCompleted(complete,id);
    }

    public List<SubTask> getAllSubTask(long taskID){
        return repo.getAllSubTask(taskID);
    }

    public List<Task> getAllTasksSortByCreatedDate(long categoryID){
        return repo.getAllTasksSortByCreatedDate(categoryID);
    }

    public List<Task> getAllTasksSortByDueDate(long categoryID){
        return repo.getAllTasksSortByDueDate(categoryID);
    }

    public List<MediaFile> getAllMedias(long taskID,boolean isImage){
        return repo.getAllMedias(taskID, isImage);
    }

    public void updateCategoryName(String categoryName,long id){ repo.updateCategoryName(categoryName, id); }

    public void deleteCategory(Category category){ repo.deleteCategory(category); }

    public LiveData<List<Category>> getAllCategoriesExceptSelected(long categoryID,String text){
        return repo.getAllCategoriesExceptSelected(categoryID,text);
    }

    public List<MapLocation> getAllMapPin(long categoryID) {
        return repo.getAllMapPin(categoryID);
    }

    public MapLocation getMapPin(long taskId) {
        return repo.getMapPin(taskId);
    }

    public void insert(MediaFile mediaFile){
        repo.insert(mediaFile);
    }
    public void update(MediaFile mediaFile){
        repo.update(mediaFile);
    }
    public void delete(MediaFile mediaFile){
        repo.delete(mediaFile);
    }

    public LiveData<List<Task>> getAllLiveTasks(long categoryID){
        return repo.getAllLiveTasks(categoryID);
    }

    public void changeCategoryOfSelectedLocation(long categoryId,long locationID){
        repo.changeCategoryOfSelectedLocation(categoryId,locationID);
    }
}

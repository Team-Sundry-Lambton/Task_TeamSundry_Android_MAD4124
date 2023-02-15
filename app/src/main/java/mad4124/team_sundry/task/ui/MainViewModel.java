package mad4124.team_sundry.task.ui;

import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import mad4124.team_sundry.task.model.Category;
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

    public void addCategory(Category category) {
        repo.addCategory(category);
    }

    public List<Category> getAllCategories(){
        return repo.getAllCategory();
    }

    public void changeParentOfSelectedTasks(int categoryId,int taskIs){
        repo.changeParentOfSelectedTasks(categoryId,taskIs);
    }

    public List<Task> getAllTasks(int categoryID){
        return repo.getAllTasks(categoryID);
    }

    public void delete(Task task){
        repo.delete(task);
    }

    public void insert(Task task){
        repo.insert(task);
    }

    public void markTaskCompleted(boolean complete, int id){
        repo.markTaskCompleted(complete,id);
    }

    public List<SubTask> getAllSubTask(int taskID){
        return repo.getAllSubTask(taskID);
    }

    public List<Task> getAllTasksSortByCreatedDate(int categoryID){
        return repo.getAllTasksSortByCreatedDate(categoryID);
    }

    public List<Task> getAllTasksSortByDueDate(int categoryID){
        return repo.getAllTasksSortByDueDate(categoryID);
    }

    public List<MediaFile> getAllMedias(int taskID){
        return repo.getAllMedias(taskID);
    }

    public void updateCategoryName(String categoryName,int id){ repo.updateCategoryName(categoryName, id); }

    public void deleteCategory(Category category){ repo.deleteCategory(category); }
}

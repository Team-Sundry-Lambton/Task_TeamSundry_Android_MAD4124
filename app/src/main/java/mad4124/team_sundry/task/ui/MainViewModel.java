package mad4124.team_sundry.task.ui;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import mad4124.team_sundry.task.model.Category;

@HiltViewModel
public class MainViewModel extends ViewModel {

    MainRepo repo;

    @Inject
    public MainViewModel(MainRepo repo) {
        this.repo = repo;
    }

    void addCategory(Category category) {
        repo.addCategory(category);
    }

}

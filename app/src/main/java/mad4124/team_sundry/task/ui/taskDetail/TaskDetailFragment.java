package mad4124.team_sundry.task.ui.taskDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.FragmentTaskDetailBinding;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;

@AndroidEntryPoint
public class TaskDetailFragment extends Fragment {


    FragmentTaskDetailBinding binding;
    MainViewModel viewModel;
    Task task = null;

    ArrayList<MediaFile> imageFiles = new ArrayList<>();
    ArrayList<MediaFile> audioFiles = new ArrayList<>();
    ArrayList<SubTask> subTasks = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            initData(task);
        }

        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireActivity(),R.id.fragContainerView).popBackStack();
        });

        binding.fab.setOnClickListener(v -> {
            showMoreOptions();
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_pin:
                    break;
                case R.id.menu_archive:
                    break;
                case R.id.menu_notify:
                    break;
                default:
                    break;
            }
            return false;
        });

    }

    void showMoreOptions() {

    }

    void saveData(){

        String title = binding.etTitle.getText().toString();
        String desc = binding.etDesc.getText().toString();

        if(title.isEmpty() && desc.isEmpty() && audioFiles.isEmpty() && imageFiles.isEmpty() && subTasks.isEmpty()){
            return;
        }

        task.setTitle(binding.etTitle.getText().toString());
        task.setDescription(binding.etDesc.getText().toString());

        for(MediaFile audio: audioFiles){
            //save audio file
            audio.setTaskId(task.getId());

        }

        for(MediaFile image: imageFiles){
            //save image file
            image.setTaskId(task.getId());
        }

        for(SubTask subTask:subTasks){
            //save subTask of this task
            subTask.setParentTaskId(task.getId());

        }
    }

    void initData(Task task) {
        binding.etTitle.setText(task.getTitle());
        binding.etDesc.setText(task.getDescription());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveData();
    }
}

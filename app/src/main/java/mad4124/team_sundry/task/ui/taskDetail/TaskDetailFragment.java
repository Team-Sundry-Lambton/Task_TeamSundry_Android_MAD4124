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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

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
    int parentCategoryId = -1;
    boolean isEditing = false;

    ArrayList<MediaFile> imageFiles = new ArrayList<>();
    ArrayList<MediaFile> audioFiles = new ArrayList<>();
    ArrayList<SubTask> subTasks = new ArrayList<>();

    SubTaskAdapter subTaskAdapter;
    ImagesAdapter imagesAdapter;
    AudioAdapter audioAdapter;


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
            parentCategoryId = getArguments().getInt("id",-1);
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

        initAdapters();

        binding.btnAddSubTask.setOnClickListener(v->{
            SubTask subTask = new SubTask();
            if(task == null){
                task = new Task();
                task.setParentCategoryId(parentCategoryId);
            }
            subTask.setParentTaskId(task.getId());
            subTaskAdapter.addNewSubTask(subTask);
        });

    }



    public void initAdapters(){
        SubTaskAdapter.SubTaskAdapterListener subTaskAdapterListener = new SubTaskAdapter.SubTaskAdapterListener() {
            @Override
            public void remove(int position, SubTask subTask) {
//                if(isEditing){
//                    viewModel.delete(subTask);
//                }
//                subTaskAdapter.getSubTasks().remove(position);
//                subTaskAdapter.notifyItemRemoved(position);
            }

            @Override
            public void update(int position, SubTask subTask) {
//                if(isEditing){
//                    viewModel.update(subTask);
//                }
//                subTaskAdapter.updateData(subTask,position);
            }
        };
        subTaskAdapter = new SubTaskAdapter(requireContext(),subTaskAdapterListener);
        binding.rvSubTasks.setAdapter(subTaskAdapter);
        binding.rvSubTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        subTaskAdapter.setData(new ArrayList<>());

        ImagesAdapter.ImageAdapterListener imageAdapterListener = new ImagesAdapter.ImageAdapterListener() {
            @Override
            public void remove(int position, MediaFile model) {

            }
        };
        imagesAdapter = new ImagesAdapter(requireContext(),imageAdapterListener);
        binding.rvImages.setAdapter(imagesAdapter);
        binding.rvImages.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false));
        imagesAdapter.setData(new ArrayList<>());

        AudioAdapter.AudioAdapterListener audioAdapterListener = new AudioAdapter.AudioAdapterListener() {
            @Override
            public void playPause(boolean play, MediaFile model) {

            }

            @Override
            public void remove(int position, MediaFile model) {

            }

            @Override
            public void scrubberProgress(int progress, MediaFile mediaFile) {

            }
        };
        audioAdapter = new AudioAdapter(requireContext(),audioAdapterListener);
        binding.rvAudios.setAdapter(audioAdapter);
        binding.rvAudios.setLayoutManager(new LinearLayoutManager(requireContext()));
        audioAdapter.setData(new ArrayList<>());

    }

    void showMoreOptions() {

    }

    void saveData(){

        String title = binding.etTitle.getText().toString();
        String desc = binding.etDesc.getText().toString();

        if(title.isEmpty() && desc.isEmpty() && audioFiles.isEmpty() && imageFiles.isEmpty() && subTasks.isEmpty()){
            return;
        }

        if(task == null){
            task = new Task();
            task.setCreatedDate(Calendar.getInstance().getTimeInMillis());
        }
        task.setDueDate(0L);
        task.setParentCategoryId(parentCategoryId);
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(false);
        task.setTask(true);

        viewModel.insert(task);
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

        loadTaskData(task);
    }

    void loadTaskData(Task task){
        viewModel.getSubTasksLive(task.getId()).observe(getViewLifecycleOwner(),subTasks -> {
            subTaskAdapter.setData(subTasks);
        });

        viewModel.getMediaLive(task.getId(),true).observe(getViewLifecycleOwner(),images -> {
            if(isEditing){
                if(images.isEmpty()){
                    binding.ivTask.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getMediaLive(task.getId(),false).observe(getViewLifecycleOwner(),audios -> {

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveData();
    }
}

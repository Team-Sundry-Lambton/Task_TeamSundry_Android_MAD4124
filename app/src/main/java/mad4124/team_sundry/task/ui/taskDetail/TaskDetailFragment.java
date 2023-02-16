package mad4124.team_sundry.task.ui.taskDetail;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.FragmentTaskDetailBinding;
import mad4124.team_sundry.task.databinding.TaskDetailBottomSheetAddMoreOptionsBinding;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;

@AndroidEntryPoint
public class TaskDetailFragment extends Fragment {

    FragmentTaskDetailBinding binding;
    TaskDetailBottomSheetAddMoreOptionsBinding addMoreOptionsBinding;
    MainViewModel viewModel;
    Task task = null;
    int parentCategoryId = -1;
    boolean isEditing = false;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int REQUEST_STORAGE_PERMISSION = 102;
    String currentPhotoPath;


    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private AlertDialog dialog;

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

    private void showMoreOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View bottomBarView = LayoutInflater.from(requireContext()).inflate(R.layout.task_detail_bottom_sheet_add_more_options, null);
        bottomSheetDialog.setContentView(bottomBarView);

        // Find the buttons in the bottom bar view and set their onClickListeners
        //addMoreOptionsBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
        Button takePhotoButton = bottomBarView.findViewById(R.id.takePhoto);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Take photo", "clicked");
                // Handle "Open camera and take a photo" option
                if (hasCameraPermission()) {
                    launchCamera();
                } else {
                    requestCameraPermission();
                }
                bottomSheetDialog.dismiss();
            }
        });

        Button addImageButton = bottomBarView.findViewById(R.id.addImage);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if permission to access external storage is granted
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, launch gallery intent
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                } else {
                    // Permission is not granted, request permission
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                }
                bottomSheetDialog.dismiss();
            }
        });

        Button startRecordingButton = bottomBarView.findViewById(R.id.addRecording);
        startRecordingButton.setOnClickListener(v -> {
            // Handle "Start recording" option
            bottomSheetDialog.dismiss();
        });

        // Show the bottom bar
        bottomSheetDialog.show();
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

    //////////////////////////////////////////////////////////////////////////////////////////
    // HANDLE TAKE PHOTO FROM CAMERA
    //////////////////////////////////////////////////////////////////////////////////////////
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireContext(), "mad4124.team_sundry.task.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureLauncher.launch(photoURI);
        }
    }

    // Create an instance of ActivityResultLauncher
    ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> {
        if (result) {
            // The picture was taken successfully
            Log.d("Take photo", "The picture was taken successfully");
            // Get the image from the camera
            Glide.with(this)
                    .load(currentPhotoPath)
                    .into(binding.ivTask);
        } else {
            // The user cancelled the operation
            Log.d("Take photo", "Taken picture was unsuccessfully");
        }
    });

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the directory where the image will be saved
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create the image file
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE TAKE PHOTO FROM CAMERA
    //////////////////////////////////////////////////////////////////////////////////////////

}

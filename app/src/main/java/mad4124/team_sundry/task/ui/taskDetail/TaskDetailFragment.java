package mad4124.team_sundry.task.ui.taskDetail;


import static mad4124.team_sundry.task.ui.task.TaskListFragment.IsShowAllMap;
import static mad4124.team_sundry.task.ui.task.TaskListFragment.TASK_ID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.database.Cursor;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;

import android.os.Environment;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.Manifest;
import android.widget.Toast;

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
import androidx.navigation.fragment.NavHostFragment;
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
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.FragmentTaskDetailBinding;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;
import mad4124.team_sundry.task.ui.task.TaskListFragment;
import mad4124.team_sundry.task.utils.NotificationHelper;

@AndroidEntryPoint
public class TaskDetailFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    FragmentTaskDetailBinding binding;
    MainViewModel viewModel;
    Task task = null;
    int parentCategoryId = -1;
    boolean isEditing = false;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    String currentPhotoPath;
    String fileName;
    private boolean isImage = false;


    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private AlertDialog dialog;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private File audioFile;
    private static final int REQUEST_GALLERY_PERMISSION = 300;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;



    ArrayList<MediaFile> imageFiles = new ArrayList<>();
    ArrayList<MediaFile> audioFiles = new ArrayList<>();
    ArrayList<SubTask> subTasks = new ArrayList<>();

    ArrayList<MediaFile> imageToDelete = new ArrayList<>();
    ArrayList<MediaFile> audioToDelete = new ArrayList<>();
    ArrayList<SubTask> subTaskToDelete = new ArrayList<>();

    SubTaskAdapter subTaskAdapter;
    ImagesAdapter imagesAdapter;
    AudioAdapter audioAdapter;

    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    Calendar calendar;

    boolean isDeleting = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mediaPlayer = new MediaPlayer();
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
        parentCategoryId = getArguments().getInt(TaskListFragment.CATEGORY_ID,-1);

        initAdapters();

        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable(TaskListFragment.TASK_MODEL);
            if(task != null){
                initData(task);
            }
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
                    deleteTask(task);
                    break;
                case R.id.menu_notify:
                    showNotify();
                    break;
                default:
                    break;
            }
            return false;
        });

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


    public void deleteTask(Task task1){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Are you sure ?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, which) -> {
                    isDeleting = true;
                    if(isEditing){
                        viewModel.delete(task1);
                    }
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .setNegativeButton("Cancel",((dialog, which) -> {

                }));
        builder.show();

    }


    public void initAdapters(){
        SubTaskAdapter.SubTaskAdapterListener subTaskAdapterListener = new SubTaskAdapter.SubTaskAdapterListener() {
            @Override
            public void remove(int position, SubTask subTask) {
                subTaskToDelete.add(subTask);
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
//                viewModel.delete(model);
                imageToDelete.add(model);
                imagesAdapter.notifyItemRemoved(position);
                handleImages();
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
                audioToDelete.add(model);
                audioAdapter.notifyItemRemoved(position);
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
        Button addSubTasks = bottomBarView.findViewById(R.id.addSubTasks);
        addSubTasks.setOnClickListener(v->{
            binding.rvSubTasks.setVisibility(View.VISIBLE);
            binding.btnAddSubTask.setVisibility(View.VISIBLE);
            bottomSheetDialog.dismiss();
        });

        Button takePhotoButton = bottomBarView.findViewById(R.id.takePhoto);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "take photo" option
                if (isPermissionGranted(REQUEST_CAMERA_PERMISSION)) {
                   launchCamera();
                } else {
                    requestPermission(REQUEST_CAMERA_PERMISSION);
                }
                bottomSheetDialog.dismiss();
            }
        });

        Button addImageButton = bottomBarView.findViewById(R.id.addImage);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "add image" option
                if (isPermissionGranted(REQUEST_GALLERY_PERMISSION)) {
                    pickImage();
                } else {
                    requestPermission(REQUEST_GALLERY_PERMISSION);
                }
                bottomSheetDialog.dismiss();
            }
        });

        Button startRecordingButton = bottomBarView.findViewById(R.id.addRecording);
        startRecordingButton.setOnClickListener(v -> {
            // Handle "Start recording" option
            if (isPermissionGranted(REQUEST_RECORD_AUDIO_PERMISSION)) {
                addRecording();
            } else {
                requestPermission(REQUEST_RECORD_AUDIO_PERMISSION);
            }
            bottomSheetDialog.dismiss();
        });

        Button pickLocation = bottomBarView.findViewById(R.id.btnPickLocation);
        pickLocation.setOnClickListener(v -> {
            // Handle "pick location" option
            pickLocation();
            bottomSheetDialog.dismiss();
        });

        // Show the bottom bar
        bottomSheetDialog.show();
    }


    void saveData(){

        if(isDeleting){
            return;
        }

        String title = binding.etTitle.getText().toString();
        String desc = binding.etDesc.getText().toString();

        audioFiles = (ArrayList<MediaFile>) audioAdapter.getList();
        imageFiles = (ArrayList<MediaFile>) imagesAdapter.getList();
        subTasks = (ArrayList<SubTask>)  subTaskAdapter.getSubTasks();

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

        Log.d("TaskDetail",""+task.toString());

        for(MediaFile audio:audioToDelete){
            viewModel.delete(audio);
        }
        for(SubTask subTask:subTaskToDelete){
            viewModel.delete(subTask);
        }
        for(MediaFile image:imageToDelete){
            viewModel.delete(image);
        }


        if(isEditing){
            viewModel.update(task);
        }
        else{
            viewModel.insert(task);
        }

        for(MediaFile audio: audioFiles){
            //save audio file
            audio.setTaskId(task.getId());
            viewModel.insert(audio);
        }

        for(MediaFile image: imageFiles){
            //save image file
            image.setTaskId(task.getId());
            viewModel.insert(image);
        }

        for(SubTask subTask:subTasks){
            //save subTask of this task
            subTask.setParentTaskId(task.getId());
            viewModel.insert(subTask);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationHelper.scheduleNotification(NotificationHelper.getNotification(task.getTitle(),getActivity()),calendar,getActivity());
        }
    }

    void initData(Task task) {

        parentCategoryId = task.getParentCategoryId();

        isEditing = true;

        binding.etTitle.setText(task.getTitle());
        binding.etDesc.setText(task.getDescription());

        loadTaskData(task);
    }

    void loadTaskData(Task task){
        viewModel.getSubTasksLive(task.getId()).observe(getViewLifecycleOwner(),subTasks -> {
            if(!subTasks.isEmpty()){
                binding.btnAddSubTask.setVisibility(View.VISIBLE);
                binding.rvSubTasks.setVisibility(View.VISIBLE);
            }
            subTaskAdapter.setData(subTasks);
        });

        viewModel.getMediaLive(task.getId(),true).observe(getViewLifecycleOwner(),images -> {
            imagesAdapter.setData(images);
            handleImages();
        });

        viewModel.getMediaLive(task.getId(),false).observe(getViewLifecycleOwner(),audios -> {
            audioAdapter.setData(audios);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveData();
    }


    // Request permission
    private void requestPermission(Integer requestCode) {
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            String[] permissions = {Manifest.permission.CAMERA};
            requestPermissions(permissions, REQUEST_CAMERA_PERMISSION);
        }
        if(requestCode == REQUEST_RECORD_AUDIO_PERMISSION){
            String[] permissions = {Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
        if(requestCode == REQUEST_GALLERY_PERMISSION){
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_GALLERY_PERMISSION);
        }
    }

    // Check if permission to camera has been granted
    private boolean isPermissionGranted(Integer requestCode) {
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        if(requestCode == REQUEST_RECORD_AUDIO_PERMISSION){
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
        if(requestCode == REQUEST_GALLERY_PERMISSION){
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Checking the request code of our request
        if (requestCode == NOTIFICATION_PERMISSION_CODE ) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                showNotify();
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_CAMERA_PERMISSION ) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can open camera", Toast.LENGTH_LONG).show();
                launchCamera();
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_GALLERY_PERMISSION ) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can gallery", Toast.LENGTH_LONG).show();
                pickImage();
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION ) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can open microphone for recording", Toast.LENGTH_LONG).show();
                addRecording();
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // HANDLE TAKE PHOTO FROM CAMERA
    //////////////////////////////////////////////////////////////////////////////////////////
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
            Uri uri = FileProvider.getUriForFile(requireActivity(), "mad4124.team_sundry.task.fileprovider", photoFile);
            takePictureLauncher.launch(uri);

            //save to array list of images
            // Create a new MediaFile object using the photoFile object
            MediaFile mediaFile = new MediaFile(photoFile.getName(), true, photoFile.getAbsolutePath(), task.getId());

            imagesAdapter.addData(mediaFile);
//            imageFiles.add(mediaFile);

            handleImages();
        }
    }

    // Create an instance of ActivityResultLauncher
    private ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> {
        if (result) {
            // The picture was taken successfully
            // Get the image from the camera
            if(imageFiles.size() == 1){
                Glide.with(this)
                        .load(currentPhotoPath)
                        .into(binding.ivTask);
            } else {

            }
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


    //-------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////
    // HANDLE ADD IMAGE FROM GALLERY
    //////////////////////////////////////////////////////////////////////////////////////////

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // Create the ActivityResultLauncher for the gallery intent
    private ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // The user has selected an image from the gallery
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImage = data.getData();

                                // Get the image name and path
                                String[] projection = {MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
                                Cursor cursor = requireContext().getContentResolver().query(selectedImage, projection, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                                    // Create a new MediaFile object and set the file name and path
                                    isImage = true;
                                    MediaFile mediaFile = new MediaFile(imageName, true, imagePath, task.getId());

                                    // Add the new MediaFile object to the ArrayList
//                                    imageFiles.add(mediaFile);
                                    imagesAdapter.addData(mediaFile);

                                    cursor.close();
                                }

                                handleImages();
                            }
                        }
                    });


    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE ADD IMAGE FROM GALLERY
    //////////////////////////////////////////////////////////////////////////////////////////

    public void handleImages(){
        int imagesCount = imagesAdapter.getList().size();
        if(imagesCount == 0){
            binding.ivTask.setVisibility(View.GONE);
            binding.rvImages.setVisibility(View.GONE);
        }
        else if(imagesCount == 1){
            binding.rvImages.setVisibility(View.GONE);
            binding.ivTask.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imagesAdapter.getList().get(0).getPath())
                    .into(binding.ivTask);
        }
        else{
            binding.rvImages.setVisibility(View.VISIBLE);
            binding.ivTask.setVisibility(View.GONE);
        }
    }

    //---------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////
    // HANDLE RECORDING
    //////////////////////////////////////////////////////////////////////////////////////////
    private void addRecording(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.task_detail_recording_dialog, null);
        builder.setView(view);
        dialog = builder.create();
        Button btnHandleRecording = view.findViewById(R.id.btnHandleRecording);

        btnHandleRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    isRecording = false;
                    btnHandleRecording.setText("Start");
                    stopRecording();
                    dialog.dismiss();
                } else {
                    isRecording = true;
                    btnHandleRecording.setText("Stop");
                    startRecording();
                }
            }
        });
        dialog.show();

    }

    // Start recording audio
    private void startRecording() {
        // Check if permission to record audio has been granted
        if (!isPermissionGranted(REQUEST_RECORD_AUDIO_PERMISSION)) {
            requestPermission(REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        // Create a new MediaRecorder instance
        mediaRecorder = new MediaRecorder();

        // Set the audio source to the microphone
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // Set the output format to AAC ADTS
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);

        // Set the audio encoder to AAC
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // Generate a new file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        fileName = "AUDIO_" + timeStamp + ".aac";

        // Create a new file to save the recorded audio
        audioFile = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);

        // Set the output file
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            // Prepare the MediaRecorder
            mediaRecorder.prepare();

            // Start recording
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stop recording audio
    private void stopRecording() {
        // Stop recording
        mediaRecorder.stop();
        mediaRecorder.release();
//        mediaRecorder = null;

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            File audioFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();

            // save to array list
            int taskId = (task == null) ? 0 : task.getId();
            MediaFile mediaAudioFile = new MediaFile(audioFile.getName(), false, audioFile.getAbsolutePath(), taskId);
//            audioFiles.add(mediaAudioFile);
            audioAdapter.addData(mediaAudioFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder = null;
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE RECORDING
    //////////////////////////////////////////////////////////////////////////////////////////

    //---------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE PICK LOCATION
    //////////////////////////////////////////////////////////////////////////////////////////
    private void pickLocation() {
        Bundle bundle = new Bundle();
        if (isEditing) {
            bundle.putSerializable(TASK_ID, task.getId());
        }

        bundle.putSerializable(IsShowAllMap, true);
        Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_taskDetailFragment_to_mapPickerFragment,bundle);
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE PICK LOCATION
    //////////////////////////////////////////////////////////////////////////////////////////


    //---------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE SHOW NOTIFY
    //////////////////////////////////////////////////////////////////////////////////////////
    private void requestNotificationPermission() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
//            return;

        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE );
    }

    private void showNotify() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View bottomBarView = LayoutInflater.from(requireContext()).inflate(R.layout.task_detail_bottom_sheet_show_notify, null);
        bottomSheetDialog.setContentView(bottomBarView);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        Button pickDateTimeButton = bottomSheetDialog.findViewById(R.id.btnPickDateTime);
        pickDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDateTime();
                bottomSheetDialog.dismiss();
            }
        });


        Button laterToday = bottomSheetDialog.findViewById(R.id.btnLaterToday);
        laterToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.HOUR, 18);
                Date today = calendar.getTime();
                myHour = 18;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NotificationHelper.scheduleNotification(NotificationHelper.getNotification("5 second more",getActivity()),calendar,getActivity());
                }
                bottomSheetDialog.dismiss();
            }
        });

        Button tmrMorning = bottomSheetDialog.findViewById(R.id.btnTmrMorning);
        tmrMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR, 8);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                myHour = 8;
                bottomSheetDialog.dismiss();
            }
        });


        // Show the bottom bar
        bottomSheetDialog.show();
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE SHOW NOTIFY
    //////////////////////////////////////////////////////////////////////////////////////////

    public void pickDateTime(){

        // Get Current Date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this,year,month,day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        Calendar calendar = Calendar.getInstance();
        calendar.set(myYear,myMonth,myday,myHour,myMinute);
//        calendar.add(Calendar.SECOND, calendar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationHelper.scheduleNotification(NotificationHelper.getNotification("5 second more",getActivity()),calendar,getActivity());
        }
    }

}

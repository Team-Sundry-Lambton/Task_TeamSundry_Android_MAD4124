package mad4124.team_sundry.task.ui.taskDetail;

import static android.app.Activity.RESULT_OK;

import static mad4124.team_sundry.task.ui.task.TaskListFragment.IsShowAllMap;
import static mad4124.team_sundry.task.ui.task.TaskListFragment.TASK_ID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TimePicker;
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
import mad4124.team_sundry.task.ui.task.TaskListFragment;
import mad4124.team_sundry.task.utils.NotificationHelper;

@AndroidEntryPoint
public class TaskDetailFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    FragmentTaskDetailBinding binding;
    TaskDetailBottomSheetAddMoreOptionsBinding addMoreOptionsBinding;
    MainViewModel viewModel;
    Task task = null;
    int parentCategoryId = -1;
    boolean isEditing = false;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int REQUEST_STORAGE_PERMISSION = 102;
    String currentPhotoPath;
    String fileName;
    private static final int GALLERY_REQUEST_CODE = 103;
    private boolean isImage = false;


    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private AlertDialog dialog;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private File audioFile;

    ArrayList<MediaFile> imageFiles = new ArrayList<>();
    ArrayList<MediaFile> audioFiles = new ArrayList<>();
    ArrayList<SubTask> subTasks = new ArrayList<>();

    SubTaskAdapter subTaskAdapter;
    ImagesAdapter imagesAdapter;
    AudioAdapter audioAdapter;

    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

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
        parentCategoryId = getArguments().getInt(TaskListFragment.CATEGORY_ID,-1);
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


        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    // If the media player is already playing, pause it and update the button icon
                    mediaPlayer.pause();
                    binding.btnPlay.setImageResource(R.drawable.ic_play_arrow);
//                    binding.recordingName.setText("Playing " + fileName.toString());
                } else {
                    // If the media player is not playing, start playing and update the button icon
                    String audioFilePath = (String) binding.btnPlay.getTag();
                    if (audioFilePath != null) {
                        try {
                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            binding.btnPlay.setImageResource(R.drawable.ic_play_arrow);
//                            binding.recordingName.setText("Play " + fileName.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_pin:
                    break;
                case R.id.menu_archive:
                    break;
                case R.id.menu_notify:
                    showNotify();
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
                // Handle "take photo" option
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
                // Handle "add image" option
                pickImage();
                bottomSheetDialog.dismiss();
            }
        });

        Button startRecordingButton = bottomBarView.findViewById(R.id.addRecording);
        startRecordingButton.setOnClickListener(v -> {
            // Handle "Start recording" option
            addRecording();
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

        Log.d("TaskDetail",""+task.toString());

        if(isEditing){
            viewModel.update(task);
        }
        else{
            viewModel.insert(task);
        }

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

        parentCategoryId = task.getParentCategoryId();

        isEditing = true;

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
            Uri uri = FileProvider.getUriForFile(requireActivity(), "mad4124.team_sundry.task.fileprovider", photoFile);
            takePictureLauncher.launch(uri);

            // Create a new MediaFile object and set the file name
            isImage = true;
            MediaFile mediaFile = new MediaFile(photoFile.getName(), isImage, photoFile.getAbsolutePath(), 1);
            // Add the new MediaFile object to the ArrayList
            viewModel.insert(mediaFile);
        }
    }

    // Create an instance of ActivityResultLauncher
    private ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> {
        if (result) {
            // The picture was taken successfully
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


    //-------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////
    // HANDLE ADD IMAGE FROM GALLERY
    //////////////////////////////////////////////////////////////////////////////////////////


    public void pickImage() {
        // Check if the app has permission to read external storage
        /*if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If the app does not have permission, request it
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            // If the app already has permission, launch the gallery intent
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            galleryLauncher.launch(Intent.createChooser(intent, "Select Picture"));
        }*/
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
                                // Do something with the selected image URI
                                Glide.with(this)
                                        .load(selectedImage)
                                        .into(binding.ivTask);

                                // Get the image name and path
                                String[] projection = {MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
                                Cursor cursor = requireContext().getContentResolver().query(selectedImage, projection, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                                    // Create a new MediaFile object and set the file name and path
                                    isImage = true;
                                    MediaFile mediaFile = new MediaFile(imageName, isImage, imagePath, 1);

                                    // Add the new MediaFile object to the ArrayList
                                    viewModel.insert(mediaFile);

                                    cursor.close();
                                }
                            }
                        }
                    });


    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE ADD IMAGE FROM GALLERY
    //////////////////////////////////////////////////////////////////////////////////////////

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
    // Request permission to record audio
    private void requestRecordAudioPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    // Check if permission to record audio has been granted
    private boolean isRecordAudioPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    // Start recording audio
    private void startRecording() {
        // Check if permission to record audio has been granted
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission();
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

            // Set the audio file path to the play button
            binding.btnPlay.setTag(audioFile.getAbsolutePath());
            binding.recordingGroup.setVisibility(View.VISIBLE);

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
    // END OF HANDLE RECORDING
    //////////////////////////////////////////////////////////////////////////////////////////


    //---------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE SHOW NOTIFY
    //////////////////////////////////////////////////////////////////////////////////////////
    private void showNotify() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View bottomBarView = LayoutInflater.from(requireContext()).inflate(R.layout.task_detail_bottom_sheet_show_notify, null);
        bottomSheetDialog.setContentView(bottomBarView);

        Calendar calendar = Calendar.getInstance();
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NotificationHelper.scheduleNotification(NotificationHelper.getNotification("5 second more",getActivity()),calendar,getActivity());
                }
                myHour = 8;
                bottomSheetDialog.dismiss();
            }
        });


        // Show the bottom bar
        bottomSheetDialog.show();
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // END OF HANDLE RECORDING
    //////////////////////////////////////////////////////////////////////////////////////////

    public void pickDateTime(){

        // Get Current Date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getTargetFragment(),year, month,day);
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getTargetFragment(), hour, minute, DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        Calendar calendar = Calendar.getInstance();
        calendar.set(myYear,myMonth,myday,myHour,myMinute);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationHelper.scheduleNotification(NotificationHelper.getNotification("5 second more",getActivity()),calendar,getActivity());
        }

//        textView.setText("Year: " + myYear + " " +
//                "Month: " + myMonth + " " +
//                "Day: " + myday + " " +
//                "Hour: " + myHour + " " +
//                "Minute: " + myMinute);

    }

}

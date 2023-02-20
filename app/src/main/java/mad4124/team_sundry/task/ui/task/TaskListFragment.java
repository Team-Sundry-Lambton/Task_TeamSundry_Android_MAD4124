package mad4124.team_sundry.task.ui.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.CategoryListRecyclerViewAdapter;
import mad4124.team_sundry.task.adapter.TaskRecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentTaskListBinding;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;
import mad4124.team_sundry.task.utils.BsItemOptions;

@AndroidEntryPoint
public class TaskListFragment extends Fragment implements TaskRecyclerViewAdapter.OnItemClickListener{
    public static final String TASK_MODEL = "task";
    public static final String TASK_ID = "task_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String IsShowAllMap = "isShowAllMap";
    private FragmentTaskListBinding binding;

    private List<Task> taskList = new ArrayList<>();
    private TaskRecyclerViewAdapter adapter;

    MainViewModel viewModel;
    private Task deletedTask;

    private long categoryId = 0L;

    private List<Task> selectedTasks = new ArrayList<>();

    boolean isMultiSelection = false;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setTaskListRecyclerView();
        binding.bottomAppBar.setVisibility(View.GONE);
        categoryId = getArguments().getLong(CATEGORY_ID);
        loadTasks();
        binding.addTask.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putLong(CATEGORY_ID,categoryId);
            NavHostFragment.findNavController(TaskListFragment.this)
                    .navigate(R.id.action_taskListFragment_to_taskDetailFragment,bundle);
        });

        binding.taskSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Override onQueryTextSubmit method which is call when submit query is searched
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Task> taskArrayList = searchTask(query);
                if (taskArrayList.size() > 0) {

                    adapter.setDataList(
                            taskArrayList
                    );

                } else {
                    Toast.makeText(requireContext(), "No found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Task> taskArrayList = searchTask(newText);
                adapter.setDataList(
                        taskArrayList
                );
                return false;
            }
        });

        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.done:
                    moveOptionSelected();
                    return true;
                case R.id.move:
                    loadCategoryList();
                    return true;
                case R.id.delete:
                    deleteSelectedTasks();
                    return true;
            }
            return false;
        });
        binding.optionMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), binding.optionMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.task_popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.select_task:
                                moveOptionSelected();
                                return true;
                            case R.id.sort_task:
                                createSortMenuOptions();
                                return true;
                            case R.id.view_map:
                                loadMapView();
                                return true;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMultiSelection) {
            selectedTasks.clear();
            moveOptionSelected();
        }
        loadTasks();
    }

    private void setTaskListRecyclerView(){
        adapter = new TaskRecyclerViewAdapter(taskList, getContext(), this,viewModel, isMultiSelection);
        binding.recyclerView.setHasFixedSize(true);
//        binding.recyclerView.setLayoutManager(new  GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadTasks() {
        viewModel.getAllLiveTasks(categoryId).observe(getViewLifecycleOwner(),tasks -> {
                    taskList = tasks;
            if (taskList.size() > 0) {
                binding.emptyView.setVisibility(View.INVISIBLE);
                binding.optionMenu.setVisibility(View.VISIBLE);
            }else {
                binding.emptyView.setVisibility(View.VISIBLE);
                binding.optionMenu.setVisibility(View.GONE);
            }
            adapter.setDataList(taskList);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("TAG", "onDestroyView: ");
    }

    @Override
    public void onItemClick(int position) {
        Task task = taskList.get(position);
        if(isMultiSelection){
            if(selectedTasks.contains(task)){
                selectedTasks.remove(task);
            }else {
                selectedTasks.add(task);
            }
        }else {
            BsItemOptions.ActionProvider provider = new BsItemOptions.ActionProvider() {
                @Override
                public void complete() {
                    markTaskCompleted(position);
                }

                @Override
                public void edit() {
                    editTask(position);
                }

                @Override
                public void delete() {
                    deleteTask(position);
                }
            };
            long id = task.getId();
            List<SubTask> subTasks = viewModel.getAllSubTask(id);
            BsItemOptions options = new BsItemOptions(task.isTask(),task.isStatus());
            options.provider = provider;
            options.show(getChildFragmentManager(), "ITEM_OPTIONS");
        }
    }

    //Single Task Selection Option
    private void editTask(int position){
        Task task = taskList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TASK_MODEL, task);
        bundle.putSerializable(TASK_ID, task.getId());
        Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_taskListFragment_to_taskDetailFragment,bundle);
    }

    private void deleteTask(int position) {
        Task task = taskList.get(position);
        showDeleteTaskDialog(task,position);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Deleting Task will delete it subtasks also. Do you want to delete?");
//        builder.setPositiveButton("Yes", (dialog, which) -> {
//            deletedTask = task;
//            viewModel.delete(task);
//            adapter.notifyItemRemoved(position);
//            Snackbar.make(binding.recyclerView, deletedTask.getTitle() + " is deleted!", Snackbar.LENGTH_LONG)
//                    .setAction("Undo", v -> viewModel.insert(deletedTask)).show();
//            Toast.makeText(getActivity(), task.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
//        });
//        builder.setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position));
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
    }

    private void markTaskCompleted(int position){
        Task task = taskList.get(position);
        long id = task.getId();
        boolean status = taskContainInCompleteSubTask(id);
        if(status){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Mark Complete");
            builder.setMessage("You have uncompleted subtasks under this Task.Therefore you cannot completed the task. Please complete all subtasks and continue...");
            builder.setPositiveButton("OK", (dialog, which) -> adapter.notifyItemChanged(position));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {
            if (task.isStatus()){
                viewModel.markTaskCompleted(false,id);
            }else {
                viewModel.markTaskCompleted(true, id);
            }
            Toast.makeText(getActivity(), "Tasks completed", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean taskContainInCompleteSubTask(long id) {

        List<SubTask> subTasks = viewModel.getAllSubTask(id);
        for ( SubTask subTask:subTasks) {
            if (subTask.isStatus() == false){
                return true;
            }
        }
        return false;
    }

    //Menu Context View Options
    private void moveOptionSelected(){
        if (isMultiSelection){
            isMultiSelection = false;
            binding.bottomAppBar.setVisibility(View.GONE);
            binding.addTask.setVisibility(View.VISIBLE);
            binding.addTaskBottomBar.setVisibility(View.VISIBLE);
            adapter.setSelection(isMultiSelection);
        }else {
            isMultiSelection = true;
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.addTask.setVisibility(View.GONE);
            binding.addTaskBottomBar.setVisibility(View.GONE);
            adapter.setSelection(isMultiSelection);
        }
    }

    private void createSortMenuOptions() {
        String[] options = {"Title","Created Date","Due Date"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sort_option)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                taskList = viewModel.getAllTasks(categoryId);
                                break;
                            case 1:
                                taskList = viewModel.getAllTasksSortByCreatedDate(categoryId);
                                break;
                            case 2:
                                taskList = viewModel.getAllTasksSortByDueDate(categoryId);
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadMapView(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORY_ID, categoryId);
        bundle.putSerializable(IsShowAllMap, true);
        Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_taskListFragment_to_mapAllTasksFragment,bundle);
    }

    //Selected Tasks Options
    private void deleteSelectedTasks(){

        if (selectedTasks.size() > 0) {
            showDeleteTaskDialog(selectedTasks.get(0),0);
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Deleting Tasks will delete it subtasks also. Do you want to delete?");
//            builder.setPositiveButton("Yes", (dialog, which) -> {
//                for (Task task : selectedTasks
//                ) {
//                    viewModel.delete(task);
//
//                }
//                adapter.notifyDataSetChanged();
//                Toast.makeText(getActivity(), "Tasks deleted", Toast.LENGTH_SHORT).show();
//            });
//            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
//                dialog.cancel();
//                adapter.notifyDataSetChanged();
//            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
        }else {
            Toast.makeText(getActivity(),"Please select tasks to delete...",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCategoryList(){
        if (selectedTasks.size() > 0) {
//            ArrayList<Long> taskIds = new ArrayList<>();
            long[] taskIds = new long[selectedTasks.size()];
//            for (Task task : selectedTasks
//            ) {
//                taskIds.add(task.getId());
//            }
            for(int i = 0;i<selectedTasks.size();i++){
                taskIds[i] = selectedTasks.get(i).getId();
            }
            Bundle bundle = new Bundle();
            bundle.putLongArray(TASK_ID,taskIds);
//            bundle.putIntegerArrayList(TASK_ID,taskIds);
            bundle.putSerializable(CATEGORY_ID, categoryId);
            Navigation.findNavController(requireActivity(), R.id.fragContainerView).navigate(R.id.action_taskListFragment_to_taskMoveCategoryListFragment, bundle);
        }else {
            Toast.makeText(getActivity(),"Please select tasks to move...",Toast.LENGTH_SHORT).show();
        }
    }


    //Search Tasks
    private ArrayList<Task> searchTask(String text) {
        ArrayList<Task> matches = new ArrayList<>();
        for(Task task : taskList) {
            if (task.getTitle().toLowerCase().contains(text.toLowerCase())) {
                matches.add(task);
            }else  if (task.getDescription().toLowerCase().contains(text.toLowerCase())) {
                matches.add(task);
            }
        }
        return matches;
    }


    private void showDeleteTaskDialog(Task task, int position) {
        AlertDialog dialog;
        // Create the dialog using an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.category_delete_dialog, null);

        // Set the dialog's view
        builder.setView(dialogView);
        dialog = builder.create();

        TextView title = dialogView.findViewById(R.id.tv_title);
        TextView message = dialogView.findViewById(R.id.tv_message);
        // Get references to the dialog's views
        Button btnNegative = dialogView.findViewById(R.id.btn_negative);
        Button btnPositive = dialogView.findViewById(R.id.btn_positive);
        if (isMultiSelection) {
            title.setText("Delete Selected Tasks");
            message.setText("Deleting Tasks will delete it subtasks also. Do you want to delete?");
        }else{
            title.setText("Delete Selected Task");
            message.setText("Deleting Task will delete it subtasks also. Do you want to delete?");
        }

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                dialog.dismiss();
            }
        });

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                if (isMultiSelection) {
                    for (Task task : selectedTasks
                    ) {
                        viewModel.delete(task);
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Tasks deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    deleteSelectedTask(task, position);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void deleteSelectedTask(Task task, int position){
        deletedTask = task;
        viewModel.delete(task);
        adapter.notifyItemRemoved(position);
        Snackbar.make(binding.recyclerView, deletedTask.getTitle() + " is deleted!", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> viewModel.insert(deletedTask)).show();
        Toast.makeText(getActivity(), task.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
    }

}

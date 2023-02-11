package mad4124.team_sundry.task.ui.task;

import static android.content.Intent.getIntent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.RecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentTaskListBinding;
import mad4124.team_sundry.task.db.AppDatabase;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.category.CategoryListFragment;
import mad4124.team_sundry.task.ui.maps.MapAllTasksFragment;
import mad4124.team_sundry.task.ui.taskDetail.TaskDetailFragment;
import mad4124.team_sundry.task.utils.BsItemOptions;

@AndroidEntryPoint
public class TaskListFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener{
    public static final String TASK_ID = "task_id";
    private FragmentTaskListBinding binding;

    private List<Task> taskList = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    private AppDatabase appDatabase;

    private Task deletedTask;

    private int categoryId = 0;

    private List<Task> selectedTasks = new ArrayList<>();
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

        adapter = new RecyclerViewAdapter(taskList, getContext(), this);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);
        appDatabase = AppDatabase.getInstance(getActivity());
        loadTasks();
        binding.addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TaskListFragment.this)
                        .navigate(R.id.action_taskListFragment_to_taskDetailFragment);
            }
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
    }

    private void loadTasks() {
        taskList = appDatabase.dbDao().getAllTasks(categoryId);
        if (taskList.size() > 0) {
            binding.emptyView.setVisibility(View.INVISIBLE);
        }else {
            binding.emptyView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("TAG", "onDestroyView: ");
    }

    @Override
    public void onItemClick(int position) {
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
        BsItemOptions options = new BsItemOptions();
        options.provider = provider;
        options.show(getChildFragmentManager(),"ITEM_OPTIONS");
    }

    private void editTask(int position){
        Task task = taskList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", null);
        Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_taskListFragment_to_taskDetailFragment,bundle);
    }

    private void deleteTask(int position) {
        Task task = taskList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deleting Task will delete it subtasks also. Do you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deletedTask = task;
            appDatabase.dbDao().delete(task);
            adapter.notifyItemRemoved(position);
            Snackbar.make(binding.recyclerView, deletedTask.getTitle() + " is deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> appDatabase.dbDao().insert(deletedTask)).show();
            Toast.makeText(getActivity(), task.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void markTaskCompleted(int position){
        Task task = taskList.get(position);
        int id = task.getId();
        boolean status = taskContainInCompleteSubTask(id);
        if(status){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("You have uncompleted subtask under this Task.Therefore you cannot completed the task");
            builder.setPositiveButton("OK", (dialog, which) -> adapter.notifyItemChanged(position));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {
            appDatabase.dbDao().markTaskCompleted(true,id);
        }
    }
    private boolean taskContainInCompleteSubTask(int id) {

        List<SubTask> subTasks = appDatabase.dbDao().getAllSubTask(id);
        for ( SubTask subTask:subTasks) {
            if (subTask.isStatus() == false){
                return true;
            }
        }
        return false;
    }

    private void createMenuOptions() {
        String[] options = {"Title","Created Date","Due Date"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sort_option)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                taskList = appDatabase.dbDao().getAllTasks(categoryId);
                                break;
                            case 1:
                                taskList = appDatabase.dbDao().getAllTasksSortByCreatedDate(categoryId);
                                break;
                            case 2:
                                taskList = appDatabase.dbDao().getAllTasksSortByDueDate(categoryId);
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadMapView(int position){
        Intent intent = new Intent(getActivity(), MapAllTasksFragment.class);
        intent.putExtra(TASK_ID, taskList.get(position).getId());
        startActivity(intent);
    }

    private void deleteSelectedTasks(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deleting Tasks will delete it subtasks also. Do you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            for (Task task:selectedTasks
                 ) {
                appDatabase.dbDao().delete(task);

            }
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "Tasks deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            adapter.notifyDataSetChanged();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void moveSelectedTasks() {

    }

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
}

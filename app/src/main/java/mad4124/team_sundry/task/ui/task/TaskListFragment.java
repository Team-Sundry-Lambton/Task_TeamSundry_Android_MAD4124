package mad4124.team_sundry.task.ui.task;

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
import androidx.fragment.app.Fragment;
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
import mad4124.team_sundry.task.ui.taskDetail.TaskDetailFragment;

@AndroidEntryPoint
public class TaskListFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener{
    public static final String TASK_ID = "task_id";
    private FragmentTaskListBinding binding;

    private List<Task> taskList = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    private AppDatabase appDatabase;

    private Task deletedTask;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        adapter = new RecyclerViewAdapter(taskList, getContext(), this);

        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);
        appDatabase = AppDatabase.getInstance(getActivity());
        loadTasks();
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TaskListFragment.this)
                        .navigate(R.id.action_taskListFragment_to_taskDetailFragment);
            }
        });
    }

    private void loadTasks() {
        taskList = appDatabase.dbDao().getAllTasks();
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
        Intent intent = new Intent(getActivity(), TaskDetailFragment.class);
        intent.putExtra(TASK_ID, taskList.get(position).getId()).toString();
        startActivity(intent);
    }

    private void deleteTask(int position) {
        Task task = taskList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deleting Task will delete it subtasks also. Are you sure?");
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

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] options = {"Title","Created Date","Due Date"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sort_option)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                taskList = appDatabase.dbDao().getAllTasks();
                                break;
                            case 1:
                                taskList = appDatabase.dbDao().getAllTasksSortByCreatedDate();
                                break;
                            case 2:
                                taskList = appDatabase.dbDao().getAllTasksSortByDueDate();
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        return builder.create();
    }
}

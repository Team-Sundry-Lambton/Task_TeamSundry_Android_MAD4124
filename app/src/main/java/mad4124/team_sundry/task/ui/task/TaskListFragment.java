package mad4124.team_sundry.task.ui.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.RecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentTaskListBinding;
import mad4124.team_sundry.task.db.AppDatabase;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.taskDetail.TaskDetailFragment;

@AndroidEntryPoint
public class TaskListFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener{
    public static final String TASK_NAME = "task_name";
    private FragmentTaskListBinding binding;

    private List<Task> taskList = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    private AppDatabase appDatabase;
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
        intent.putExtra(TASK_NAME, taskList.get(position).getId()).toString();
        startActivity(intent);
    }
}

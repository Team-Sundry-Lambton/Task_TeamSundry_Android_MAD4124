package mad4124.team_sundry.task.ui.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.TaskRecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentTaskListBinding;
import mad4124.team_sundry.task.model.SubTask;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;
import mad4124.team_sundry.task.utils.BsItemOptions;

@AndroidEntryPoint
public class TaskListFragment extends Fragment implements TaskRecyclerViewAdapter.OnItemClickListener{
    public static final String TASK_ID = "task_id";
    private FragmentTaskListBinding binding;

    private List<Task> taskList = new ArrayList<>();
    private TaskRecyclerViewAdapter adapter;

    MainViewModel viewModel;
    private Task deletedTask;

    private int categoryId = 0;

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
        adapter = new TaskRecyclerViewAdapter(taskList, getContext(), this,viewModel);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);

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

        registerForContextMenu(binding.optionMenu);
    }

    //Context Menu Creation
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Select Task");
        menu.add(1, v.getId(), 1, "Sort Task");
        menu.add(2, v.getId(), 2, "View On Map");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            moveOptionSelected();
        }
        else if (item.getItemId() == 1) {
            createSortMenuOptions();
        }else if (item.getItemId() == 2) {
            loadMapView();
        }
        return true;
    }
    private void loadTasks() {
        taskList = viewModel.getAllTasks(categoryId);
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

        if(isMultiSelection){
            Task task = taskList.get(position);
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
            BsItemOptions options = new BsItemOptions();
            options.provider = provider;
            options.show(getChildFragmentManager(), "ITEM_OPTIONS");
        }
    }

    //Single Task Selection Option
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
            viewModel.delete(task);
            adapter.notifyItemRemoved(position);
            Snackbar.make(binding.recyclerView, deletedTask.getTitle() + " is deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> viewModel.insert(deletedTask)).show();
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
            viewModel.markTaskCompleted(true,id);
        }
    }
    private boolean taskContainInCompleteSubTask(int id) {

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
        }else {
            isMultiSelection = true;
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.addTask.setVisibility(View.GONE);
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
        bundle.putSerializable("data", null);
        Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_taskListFragment_to_mapAllTasksFragment,bundle);
    }

    //Selected Tasks Options
    private void deleteSelectedTasks(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deleting Tasks will delete it subtasks also. Do you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            for (Task task:selectedTasks
                 ) {
                viewModel.delete(task);

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

    private void loadCategoryList(){
        List<Integer> taskIds = new ArrayList<>();
        for (Task task:selectedTasks
             ) {
            taskIds.add(task.getId());
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", null);
        Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_taskListFragment_to_mapAllTasksFragment,bundle);
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
}

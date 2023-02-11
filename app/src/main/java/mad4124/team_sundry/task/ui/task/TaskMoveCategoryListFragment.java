package mad4124.team_sundry.task.ui.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.RecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentTaskListBinding;
import mad4124.team_sundry.task.databinding.FragmentTaskMoveCategoryListBinding;
import mad4124.team_sundry.task.db.AppDatabase;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.Task;

public class TaskMoveCategoryListFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener{


    private FragmentTaskMoveCategoryListBinding binding;

    private List<Category> categoryList = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    private AppDatabase appDatabase;

    private  List<Integer> selectedTasksIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTaskMoveCategoryListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RecyclerViewAdapter(categoryList, getContext(), this);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);
        appDatabase = AppDatabase.getInstance(getActivity());
        loadFolders();
    }

    private void loadFolders() {
        categoryList = appDatabase.dbDao().getAllCategories();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Do you want to move those tasks?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Category category = categoryList.get(position);
            moveSelectedTasks(category.getId());
            Toast.makeText(getActivity(), "Tasks moved", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            adapter.notifyDataSetChanged();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void moveSelectedTasks(Integer categoryId) {
        for (Integer taskId:selectedTasksIds
        ) {
            appDatabase.dbDao().changeParentOfSelectedTasks(categoryId,taskId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("TAG", "onDestroyView: ");
    }
}
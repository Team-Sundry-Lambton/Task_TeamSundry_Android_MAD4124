package mad4124.team_sundry.task.ui.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.CategoryListRecyclerViewAdapter;
import mad4124.team_sundry.task.adapter.CategoryRecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentTaskMoveCategoryListBinding;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.MapLocation;
import mad4124.team_sundry.task.ui.MainViewModel;

public class TaskMoveCategoryListFragment extends Fragment implements CategoryRecyclerViewAdapter.OnItemClickListener{


    private FragmentTaskMoveCategoryListBinding binding;

    private List<Category> categoryList = new ArrayList<>();
    private CategoryRecyclerViewAdapter adapter;

    MainViewModel viewModel;

//    private  List<Integer> selectedTasksIds = new ArrayList<>();
    private  long[] selectedTasksIds = new long[0];

    private long categoryId = 0;

    public static final String TASK_ID = "task_id";
    public static final String CATEGORY_ID = "category_id";

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

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        categoryId = getArguments().getLong(CATEGORY_ID);
//        selectedTasksIds = getArguments().getIntegerArrayList(TASK_ID);
        selectedTasksIds = getArguments().getLongArray(TASK_ID);
        setCategoryListRecyclerView();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadFolders(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadFolders(newText);
                return false;
            }
        });

        loadFolders("");
    }

    private void setCategoryListRecyclerView(){
        adapter = new CategoryRecyclerViewAdapter(categoryList, getContext(), this,viewModel);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);
    }
    private void loadFolders(String keyword){
//        categoryList = viewModel.getAllCategoriesExceptSelected(categoryId,keyword);

        viewModel.getAllCategoriesExceptSelected(categoryId,keyword).observe(getViewLifecycleOwner(),categories -> {
            categoryList = categories;
            adapter.setDataList(categories);

            if(keyword.isEmpty()){
                binding.categoryToolbar.setTitle("All Categories ("+categories.size()+" available)");
            }
            else{
                binding.categoryToolbar.setTitle("Searched Categories ("+categories.size()+" available)");
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Do you want to move those tasks?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Category category = categoryList.get(position);
            moveSelectedTasks(category.getId());
            Toast.makeText(getActivity(), "Tasks moved", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireActivity(), R.id.fragContainerView).popBackStack();
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            adapter.notifyDataSetChanged();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void moveSelectedTasks(long categoryId) {
        for (long taskId:selectedTasksIds
        ) {
            MapLocation location = viewModel.getMapPin(taskId);
            if(location != null) {
                viewModel.changeCategoryOfSelectedLocation(categoryId, location.getId());
            }
        }

        for (long taskId:selectedTasksIds
        ) {
            viewModel.changeParentOfSelectedTasks(categoryId,taskId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("TAG", "onDestroyView: ");
    }
}
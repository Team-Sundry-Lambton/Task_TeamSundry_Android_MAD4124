package mad4124.team_sundry.task.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.adapter.CategoryListRecyclerViewAdapter;
import mad4124.team_sundry.task.adapter.CategoryRecyclerViewAdapter;
import mad4124.team_sundry.task.adapter.TaskRecyclerViewAdapter;
import mad4124.team_sundry.task.databinding.FragmentCategoryListBinding;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;

@AndroidEntryPoint
public class CategoryListFragment extends Fragment implements CategoryListRecyclerViewAdapter.OnItemClickListener {

    FragmentCategoryListBinding binding = null;

    private List<Category> categoryList = new ArrayList<>();
    private CategoryListRecyclerViewAdapter adapter;
    private MainViewModel viewModel;
    private AlertDialog dialog;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = FragmentCategoryListBinding.inflate(inflater,container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setCategoryListRecyclerView();
        binding.fabCategory.setOnClickListener(v->{
            showAddCategoryModal();
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadRecyclerData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadRecyclerData(newText);
                return false;
            }
        });

        loadRecyclerData("");

    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecyclerData("");
    }

    @Override
    public void onItemClick(int position) {
            Category clickedCategory = categoryList.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("categoryId", clickedCategory.getId());
            Navigation.findNavController(requireActivity(),R.id.fragContainerView).navigate(R.id.action_categoryListFragment_to_taskListFragment,bundle);
    }

    // set adapter

    private void setCategoryListRecyclerView(){
        adapter = new CategoryListRecyclerViewAdapter(categoryList, getContext(), this, viewModel);
        recyclerView = binding.categoryRecycler;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }

    // add new category dialog

    private void showAddCategoryModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.modal_add_category, null);
        builder.setView(view);
        dialog = builder.create();
        final EditText etCategoryTitle = view.findViewById(R.id.category_title_input);
        Button btnAdd = view.findViewById(R.id.add_button);
        Button btnCancel = view.findViewById(R.id.cancel_button);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add the new category to the database or data source
                String categoryTitle = etCategoryTitle.getText().toString();
                Category category = new Category();
                category.setName(categoryTitle);
                viewModel.addCategory(category);
                dialog.dismiss();
                Toast.makeText(getContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the modal dialog
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void loadRecyclerData(String keyword){
        viewModel.getAllLiveCategories(keyword).observe(getViewLifecycleOwner(),categories -> {
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




}

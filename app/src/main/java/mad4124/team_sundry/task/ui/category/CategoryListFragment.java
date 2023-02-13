package mad4124.team_sundry.task.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    MainViewModel viewModel;
    private AlertDialog dialog;

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
        loadCategories();
        adapter = new CategoryListRecyclerViewAdapter(categoryList, getContext(), this);
        binding.categoryRecycler.setHasFixedSize(true);
        binding.categoryRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.categoryRecycler.setAdapter(adapter);

        binding.fabCategory.setOnClickListener(v->{
            showAddCategoryModal();
        });

    }

    public void loadCategories() {
//        Category category = new Category();
//        category.setId(5);
//        category.setName("Shopping");
//        viewModel.addCategory(category);
        categoryList = viewModel.getAllCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {

    }

    // add new category dialog

    private void showAddCategoryModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.modal_add_category, null);
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
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the modal dialog
                dialog.dismiss();
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }


}

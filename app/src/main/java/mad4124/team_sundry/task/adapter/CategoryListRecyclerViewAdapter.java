package mad4124.team_sundry.task.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.CategoryRowBinding;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;


public class CategoryListRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListRecyclerViewAdapter.ViewHolder> {

    final private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Category> categoryList ;

    private MainViewModel viewModel;
    private AlertDialog dialog;

    private int[] backgroundImages = new int[] { R.drawable.asset_bg_green, R.drawable.asset_bg_paleblue, R.drawable.asset_bg_paleorange , R.drawable.asset_bg_palegreen, R.drawable.asset_bg_yellow};
    private int[] backgroundColors= new int[] { R.drawable.gradient_color_2, R.drawable.gradient_color_1, R.drawable.gradient_color_5, R.drawable.gradient_color_4, R.drawable.gradient_color_3};
    public CategoryListRecyclerViewAdapter(List<Category> categoryList, Context context, OnItemClickListener onItemClickListener, MainViewModel viewModel){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.categoryList = categoryList;
        this.viewModel = viewModel;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryRowBinding binding = CategoryRowBinding.inflate(
                LayoutInflater.from(context),parent,false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(categoryList.get(position));
//        int randomIndex = new Random().nextInt(backgroundImages.length);
//        holder.itemView.setBackgroundResource(backgroundImages[randomIndex]);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Show the action popup
                int position = holder.getAdapterPosition();
                showPopupMenu(v,position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CategoryRowBinding binding;
        public ViewHolder(CategoryRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Category model){
            int id = model.getId();
            List<Task> tasks = viewModel.getAllTasksSortByCreatedDate(id);
            int randomIndex = new Random().nextInt(backgroundImages.length);
            binding.imageClassAdapter.setBackgroundResource(backgroundImages[randomIndex]);
            binding.frameBg.setBackgroundResource(backgroundColors[randomIndex]);
            binding.categoryName.setText(model.getName());
            binding.totalTasks.setText("Total tasks/notes: "+ tasks.size());

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setDataList(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    // pop menu

    private void showPopupMenu(View view, int position) {
        Category category = categoryList.get(position);
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.category_context_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        // Handle the edit action
                        showEditCategoryDialog(category);
                        return true;
                    case R.id.action_delete:
                        // Handle the delete action
                        showDeleteCategoryDialog(category);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    // edit dialog box

    private void showEditCategoryDialog(Category category) {

        // Create the dialog using an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_category_dialog, null);

        // Get references to the dialog's views
        EditText categoryTitleEditText = dialogView.findViewById(R.id.et_category_name);
        categoryTitleEditText.setText(category.getName());

        // Set the dialog's view
        builder.setView(dialogView);
        dialog = builder.create();

        // Get references to the dialog's views
        Button btnNegative = dialogView.findViewById(R.id.cancel_button);
        Button btnPositive = dialogView.findViewById(R.id.add_button);

        // Add a positive button for saving the changes
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the category object with the new title
                String newCategory = categoryTitleEditText.getText().toString();
                int id = category.getId();
                viewModel.updateCategoryName(newCategory,id);
                dialog.dismiss();
                Toast.makeText(context, "Category updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add a negative button for cancelling the changes
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    // delete dialog box
    private void showDeleteCategoryDialog(Category category) {

        // Create the dialog using an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.category_delete_dialog, null);
        // Set the dialog's view
        builder.setView(dialogView);
        dialog = builder.create();

        // Get references to the dialog's views
        Button btnNegative = dialogView.findViewById(R.id.btn_negative);
        Button btnPositive = dialogView.findViewById(R.id.btn_positive);
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
                viewModel.deleteCategory(category);
                dialog.dismiss();
                Toast.makeText(context, "Category removed successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
    }

}


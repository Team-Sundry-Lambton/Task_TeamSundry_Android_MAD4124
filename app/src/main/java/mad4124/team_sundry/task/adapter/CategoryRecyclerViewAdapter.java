package mad4124.team_sundry.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.RowCategoryListLayoutBinding;
import mad4124.team_sundry.task.model.Category;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;


public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    final private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Category> categoryList ;

    private MainViewModel viewModel;

    private int[] backgroundImages = new int[] { R.drawable.asset_bg_green, R.drawable.asset_bg_paleblue, R.drawable.asset_bg_paleorange , R.drawable.asset_bg_palegreen, R.drawable.asset_bg_yellow};
    private int[] backgroundColors= new int[] { R.drawable.gradient_color_2, R.drawable.gradient_color_1, R.drawable.gradient_color_5, R.drawable.gradient_color_4, R.drawable.gradient_color_3};
    public CategoryRecyclerViewAdapter(List<Category> categoryList, Context context, OnItemClickListener onItemClickListener, MainViewModel viewModel){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.categoryList = categoryList;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public CategoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCategoryListLayoutBinding binding = RowCategoryListLayoutBinding.inflate(
                LayoutInflater.from(context),parent,false
        );
        return new CategoryRecyclerViewAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bind(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RowCategoryListLayoutBinding binding;
        public ViewHolder(RowCategoryListLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }
        public void bind(Category model){
            long id = model.getId();
            List<Task> tasks = viewModel.getAllTasksSortByCreatedDate(id);
            int randomIndex = new Random().nextInt(backgroundImages.length);
            binding.imageClassAdapter.setBackgroundResource(backgroundImages[randomIndex]);
            binding.frameBg.setBackgroundResource(backgroundColors[randomIndex]);
            binding.categoryName.setText(model.getName());
            binding.totalTasks.setText("Tasks/Notes: "+ tasks.size());
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setDataList(List<Category> tList) {
        this.categoryList = tList;
        notifyDataSetChanged();
    }

}
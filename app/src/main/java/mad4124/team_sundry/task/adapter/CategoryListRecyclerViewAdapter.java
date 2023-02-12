package mad4124.team_sundry.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mad4124.team_sundry.task.databinding.CategoryRowBinding;
import mad4124.team_sundry.task.model.Category;


public class CategoryListRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListRecyclerViewAdapter.ViewHolder> {

    final private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Category> categoryList ;

    public CategoryListRecyclerViewAdapter(List<Category> categoryList, Context context, OnItemClickListener onItemClickListener){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.categoryList = categoryList;
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
            binding.categoryName.setText(model.getName());
            binding.totalTasks.setText(model.getName());
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
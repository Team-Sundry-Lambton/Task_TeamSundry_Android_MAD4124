package mad4124.team_sundry.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mad4124.team_sundry.task.databinding.RowCategoryListLayoutBinding;
import mad4124.team_sundry.task.model.Category;


public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    final private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Category> categoryList ;

    public CategoryRecyclerViewAdapter(List<Category> categoryList, Context context, OnItemClickListener onItemClickListener){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.categoryList = categoryList;
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
            binding.titleRow.setText(model.getName());
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
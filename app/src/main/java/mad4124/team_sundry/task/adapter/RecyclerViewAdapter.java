package mad4124.team_sundry.task.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.Task;

public class RecyclerViewAdapter <T> extends RecyclerView.Adapter<RecyclerViewAdapter<T>.ViewHolder> {
    private static final String TAG = "Cannot invoke method length() on null object";

    private List<T> tList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isGridSelected;
    public void setGridSelected(boolean gridSelected) {
        isGridSelected = gridSelected;
    }

    public RecyclerViewAdapter(List<T> tList, Context context, OnItemClickListener onItemClickListener) {
        this.tList = tList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T t = tList.get(position);
        if (t instanceof Task) {
            Task task = (Task) t;
            holder.title.setText(task.getTitle());
            if(task.isTask()) {
                holder.category.setText("Task");
            }else{
                holder.category.setText("Note");
            }
            holder.description.setText(task.getDescription());
             holder.due_date.setText(task.getDueDate().toString());
            holder.imageView.setVisibility(View.VISIBLE);
            Log.d(TAG, "onBindViewHolder: task");
        } /*else if (t instanceof Category) {
            Category d = (Category) t;
            Log.d(TAG, "onBindViewHolder: categoru");
        } else if (t instanceof CategoryWithTask) {
            CategoryWithTask dwe = (CategoryWithTask) t;
        }*/
        Log.d(TAG, "onBindViewHolder: none");
    }

    @Override
    public int getItemCount() {
        return tList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView category;
        private TextView description;
        private TextView due_date;
        private ImageView imageView;
        private boolean is_taskView = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_row);
            category = itemView.findViewById(R.id.category_row);
            description = itemView.findViewById(R.id.description_row);
            due_date = itemView.findViewById(R.id.due_date_row);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);

        }

        public boolean isTaskView() {
            return is_taskView;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

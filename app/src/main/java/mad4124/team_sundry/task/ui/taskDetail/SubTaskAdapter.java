package mad4124.team_sundry.task.ui.taskDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mad4124.team_sundry.task.databinding.ItemTaskSubtaskBinding;
import mad4124.team_sundry.task.model.SubTask;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.VH>{

    private List<SubTask> subTasks = new ArrayList<>();
    final Context context;
    private SubTaskAdapterListener listener;

    interface SubTaskAdapterListener{
        void remove(int position,SubTask subTask);
        void update(int position,SubTask subTask);
    }

    public SubTaskAdapter(Context context,SubTaskAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<SubTask> subTasks){
        this.subTasks = subTasks;
        notifyDataSetChanged();
    }
    public void updateData(SubTask subTask,int position){
        subTasks.set(position,subTask);
//        notifyDataSetChanged();
        notifyItemChanged(position);
    }
    public void addNewSubTask(SubTask subTask){
        subTasks.add(subTask);
        notifyItemInserted(subTasks.size()-1);
    }
    public List<SubTask> getSubTasks(){
        return subTasks;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskSubtaskBinding binding = ItemTaskSubtaskBinding.inflate(
                LayoutInflater.from(context),parent,false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(subTasks.get(position),listener,position);
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    class VH extends RecyclerView.ViewHolder{

        ItemTaskSubtaskBinding binding;
        public VH(ItemTaskSubtaskBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        boolean isDeleting = false;

        public void bind(SubTask subTask,SubTaskAdapterListener listener,int position){

            binding.etSubTask.setText(subTask.getDescriptionSubTask());
            binding.etSubTask.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    binding.ivDelete.setVisibility(View.VISIBLE);
                }
                else{
                    subTask.setDescriptionSubTask(binding.etSubTask.getText().toString());
                    if(!isDeleting){
                        subTasks.set(position,subTask);
                    }
                    binding.ivDelete.setVisibility(View.INVISIBLE);
                }
            });

            binding.checkbox.setChecked(subTask.isStatus());
            binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                subTask.setStatus(isChecked);
                subTasks.set(position,subTask);
                listener.update(position,subTask);
            });

            binding.ivDelete.setOnClickListener(v->{
                isDeleting = true;
                subTasks.remove(position);
                notifyDataSetChanged();
                listener.remove(position,subTask);
            });
        }

    }

}

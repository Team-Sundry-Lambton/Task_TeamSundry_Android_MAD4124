package mad4124.team_sundry.task.ui.taskDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mad4124.team_sundry.task.databinding.ItemTaskImageBinding;
import mad4124.team_sundry.task.model.MediaFile;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.VH>{

    private List<MediaFile> list = new ArrayList<>();
    final Context context;
    final private ImageAdapterListener listener;

    interface ImageAdapterListener{
        void remove(int position,MediaFile model);
    }

    public ImagesAdapter(Context context, ImageAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<MediaFile> list){
        this.list = list;
        notifyDataSetChanged();
    }
    public void addData(MediaFile data){
        list.add(data);
        notifyItemInserted(list.size());
    }
    public List<MediaFile> getList(){
        return list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskImageBinding binding = ItemTaskImageBinding.inflate(
                LayoutInflater.from(context),parent,false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(list.get(position),listener,position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder{

        ItemTaskImageBinding binding;
        public VH(ItemTaskImageBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(MediaFile model,ImageAdapterListener listener,int position){

            binding.delete.setOnClickListener(v-> listener.remove(position,model));
            Glide.with(context).load(model.getPath()).into(binding.image);

        }

    }

}

package mad4124.team_sundry.task.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import mad4124.team_sundry.task.databinding.ItemOptionsBinding;

public class BsItemOptions extends BottomSheetDialogFragment {

    ItemOptionsBinding binding;

    public interface ActionProvider{
        void complete();
        void edit();
        void delete();
    }
    public ActionProvider provider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemOptionsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.viewDetail.setOnClickListener(v ->{
            provider.complete();
            dismiss();
        });
        binding.editDetail.setOnClickListener(v ->{
            provider.edit();
            dismiss();
        });
        binding.deleteDetail.setOnClickListener(v ->{
            provider.delete();
            dismiss();
        });
    }
}
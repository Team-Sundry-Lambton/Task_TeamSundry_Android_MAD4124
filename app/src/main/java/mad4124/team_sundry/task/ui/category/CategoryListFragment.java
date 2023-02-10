package mad4124.team_sundry.task.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.databinding.FragmentCategoryListBinding;

@AndroidEntryPoint
public class CategoryListFragment extends Fragment {

    FragmentCategoryListBinding binding = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = FragmentCategoryListBinding.inflate(inflater,container, false);
        }
        return binding.getRoot();
    }
}

package mad4124.team_sundry.task.ui.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {

    FragmentSplashBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Animation animation = AnimationUtils.loadAnimation(requireContext(),R.anim.anim_zoom_in_logo);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Navigation.findNavController(requireActivity(), R.id.fragContainerView).navigate(R.id.action_splashFragment_to_categoryListFragment);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        binding.logo.startAnimation(animation);
    }

}

package mad4124.team_sundry.task.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.databinding.ActivityOnboardingBinding;
import mad4124.team_sundry.task.ui.MainActivity;
import mad4124.team_sundry.task.ui.MainViewModel;

@AndroidEntryPoint
public class OnBoardingActivity extends AppCompatActivity {

    ActivityOnboardingBinding binding;

    MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (!viewModel.getFirstLoad()) {
            startHome(binding.getRoot());
            return;
        }

        binding.container.setVisibility(View.VISIBLE);

        binding.finalContainer.setVisibility(View.INVISIBLE);

        binding.container.onStartClickListener(() -> {
            binding.container.setVisibility(View.INVISIBLE);
            binding.finalContainer.setVisibility(View.VISIBLE);
        });
    }

    public void startHome(View v) {
        viewModel.setFirstLoad(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

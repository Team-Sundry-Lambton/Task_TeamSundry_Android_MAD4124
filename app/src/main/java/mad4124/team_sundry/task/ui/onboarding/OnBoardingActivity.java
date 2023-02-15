package mad4124.team_sundry.task.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.databinding.ActivityOnboardingBinding;
import mad4124.team_sundry.task.ui.MainActivity;

@AndroidEntryPoint
public class OnBoardingActivity extends AppCompatActivity {

    ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.container.setVisibility(View.VISIBLE);

        binding.finalContainer.setVisibility(View.INVISIBLE);

        binding.container.onStartClickListener(() -> {
            binding.container.setVisibility(View.INVISIBLE);
            binding.finalContainer.setVisibility(View.VISIBLE);
        });
    }

    public void startHome(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

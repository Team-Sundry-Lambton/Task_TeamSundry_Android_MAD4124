package mad4124.team_sundry.task.ui.onboarding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mad4124.team_sundry.task.databinding.OnboardingPageItemBinding;

public class OnBoardingPagerAdapter extends RecyclerView.Adapter<OnBoardingPagerAdapter.PagerViewHolder> {

    private final OnBoardingEnum[] onBoardingList = OnBoardingEnum.values();

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OnboardingPageItemBinding binding = OnboardingPageItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new PagerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        holder.bind(onBoardingList[position]);
    }

    @Override
    public int getItemCount() {
        return onBoardingList.length;
    }

    static class PagerViewHolder extends RecyclerView.ViewHolder {
        OnboardingPageItemBinding binding;

        public PagerViewHolder(OnboardingPageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OnBoardingEnum onBoardingEnum) {
            binding.titleTv.setText(onBoardingEnum.titleRes);
            binding.subTitleTv.setText(onBoardingEnum.subTitleRes);
            binding.descTV.setText(onBoardingEnum.descriptionRes);
            binding.img.setImageResource(onBoardingEnum.logoRes);
        }
    }

}

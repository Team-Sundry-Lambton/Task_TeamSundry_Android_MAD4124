package mad4124.team_sundry.task.ui.onboarding;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import mad4124.team_sundry.task.R;

public enum OnBoardingEnum {

    ONE(R.string.onboarding_slide1_title, R.string.onboarding_slide1_subtitle,R.string.onboarding_slide1_desc, R.drawable.screen1),
    TWO(R.string.onboarding_slide2_title, R.string.onboarding_slide2_subtitle,R.string.onboarding_slide2_desc, R.drawable.screen2),
    THREE(R.string.onboarding_slide3_title, R.string.onboarding_slide3_subtitle,R.string.onboarding_slide3_desc, R.drawable.screen3);

    @StringRes final int titleRes;
    @StringRes final int subTitleRes;
    @StringRes final int descriptionRes;
    @DrawableRes final int logoRes;

    OnBoardingEnum(@StringRes int titleRes,@StringRes int subTitleRes,@StringRes int descriptionRes,@DrawableRes int logoRes){
        this.titleRes = titleRes;
        this.subTitleRes = subTitleRes;
        this.descriptionRes = descriptionRes;
        this.logoRes = logoRes;
    }
}

package mad4124.team_sundry.task.ui.onboarding

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import mad4124.team_sundry.task.R
import mad4124.team_sundry.task.databinding.OnboardingScreenBinding


class OnBoardingView @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val numberOfPages by lazy { OnBoardingEnum.values().size }
    private lateinit var adapter:OnBoardingPagerAdapter
    private var binding:OnboardingScreenBinding
    private var startListener: StartListener? = null

    fun onStartClickListener(startListener: StartListener) {
        this.startListener = startListener
    }

    interface StartListener {
        fun onStart()
    }

    init {
        binding = OnboardingScreenBinding.inflate(
            LayoutInflater.from(context),this,true
        )
        initViews()
    }

    private fun initViews() {
        with(binding){
            nextBtn.setOnClickListener { navigateToNextSlide() }
            skipBtn.setOnClickListener { navigateToMainActivity() }
            slider.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        setUpSlider()

    }

    private fun navigateToNextSlide() {
        with(binding){
            val currentItemPos = slider.currentItem
            val nextSlidePos = currentItemPos + 1
            if (nextSlidePos == numberOfPages) {
                navigateToMainActivity()
            } else {
                slider.setCurrentItem(nextSlidePos, true)
            }
        }

    }

    private fun navigateToMainActivity() {
        if (startListener != null) {
            startListener!!.onStart()
        }
    }

    private fun setUpSlider() {
        with(binding){
            adapter = OnBoardingPagerAdapter()
            slider.adapter = adapter
            slider.setPageTransformer { page, position ->
                val img: ImageView = page.findViewById(R.id.img)
                if (position > -1) {
                    page.alpha = 1f
                } else if (position <= 1) {
                    val pos = -position * (page.width / 2)
                    img.translationX = pos
                } else {
                    page.alpha = 0f
                }
            }
            addSlideChangeListener()
            pageIndicator.setViewPager2(slider)
        }

    }

    private fun addSlideChangeListener() {
        with(binding){
            slider.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    if (numberOfPages > 1) {
                        val progress: Float = (position + positionOffset) / (numberOfPages - 1)
                        onboardingRoot.progress = progress
                    }
                }
            })
        }
    }

}
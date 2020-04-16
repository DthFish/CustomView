package com.dthfish.slidingmarquee

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import java.util.*

/**
 * Description
 * Author DthFish
 * Date  2020/4/16.
 */
class UnspecifiedWidthMarqueeLayout : FrameLayout {
    companion object {
        const val WHAT_SCHEDULE_NEXT = 1
        const val TRANSLATE_PATENT_WIDTH_TIME = 5000//滑过父布局宽度所需要的时间
    }

    private var weakHandler: WeakHandler? = WeakHandler(Handler.Callback { msg ->
        when (msg.what) {
            WHAT_SCHEDULE_NEXT -> {
                canStartMarquee = true
                scheduleNextMarquee()
            }
            else -> {
            }
        }
        false
    })
    private var canStartMarquee = true
    private val marqueeTask = ArrayDeque<String>()
    private var isShow = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        isShow = visibility == View.VISIBLE
    }

    fun offerMarquee(text: String?) {
        if (text.isNullOrEmpty()) return
        marqueeTask.offer(text)
        if (canStartMarquee) {
            show()
            scheduleNextMarquee()
        }
    }

    private fun addViewToMarquee(text: String): View {
        val textView = TextView(context)
        textView.text = text
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.visibility = View.INVISIBLE
        addView(
            textView,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL
            )
        )
        return textView
    }

    private fun removeViewFinishedMarquee() {
        if (childCount > 0) {
            removeView(getChildAt(0))
            if (childCount == 0 && marqueeTask.isEmpty()) {
                dismiss()
            }
        }
    }

    private fun show() {
        if (isShow) {
            return
        }
        this.visibility = View.VISIBLE
        isShow = true
        val animation = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_in_top_anim
        )
        startAnimation(animation)
    }

    private fun dismiss() {
        if (!isShow) {
            return
        }
        isShow = false
        val animation = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_out_top_anim
        )
        startAnimation(animation)
        postDelayed(Runnable {
            // 在gone invisible情况下onAnimationEnd会收不到的
            if (!isShow) {
                this.visibility = View.GONE
            }
        }, 200)
    }

    private fun scheduleNextMarquee() {
        var item: String? = null
        if (canStartMarquee && marqueeTask.poll().also { item = it } != null) {
            canStartMarquee = false
            val view = addViewToMarquee(item!!)

            view.post {


                val distance = view.measuredWidth + this.measuredWidth
                val totalTime = distance * TRANSLATE_PATENT_WIDTH_TIME / this.measuredWidth
                val nextCanStartTime = totalTime - TRANSLATE_PATENT_WIDTH_TIME / 2
                view.translationX = this.measuredWidth.toFloat()
                val animator: ObjectAnimator = ObjectAnimator.ofFloat(
                    view,
                    "translationX",
                    this.measuredWidth.toFloat(),
                    -view.measuredWidth.toFloat()
                )
                animator.interpolator = LinearInterpolator()
                animator.duration = totalTime.toLong()
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        removeViewFinishedMarquee()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }

                })
                view.visibility = View.VISIBLE
                animator.target = view
                animator.start()
                weakHandler?.sendEmptyMessageDelayed(WHAT_SCHEDULE_NEXT, nextCanStartTime.toLong())
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        weakHandler?.removeCallbacksAndMessages(null)
        weakHandler = null
        marqueeTask.clear()
        canStartMarquee = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.UNSPECIFIED),
            heightMeasureSpec
        )
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }
}
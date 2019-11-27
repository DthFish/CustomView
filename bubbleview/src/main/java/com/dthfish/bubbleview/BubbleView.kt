package com.dthfish.bubbleview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

/**
 * Description
 * Author DthFish
 * Date  2019-11-27.
 */
class BubbleView : FrameLayout {
    private var ftv: ImageView
    private var bitmapBg: Bitmap
    private var bitmapCircleBg: Bitmap
    private val paint = Paint()

    private val fixWidth: Int//整体的宽度
    private val fixHeight: Int//整体的高度,也是收起的text时候View的宽度
    private var curWidth = 0f
    private var curHeight = 0f

    private var outOnClickListener: OnClickListener? = null
    private var slideOutAnimator: ValueAnimator? = null
    private var slideInAnimator: ValueAnimator? = null
    private var isExtend = true
    private val slideDuration: Long = 1500

    private val red = Color.parseColor("#FFFF5959")
    private var redCount = 0//红点数量
    private val redCountTextSize: Float//红点字体大小
    private val redRectF = RectF()//用来绘制红点
    private val redRadius: Int //红点圆弧半径

    private var text: String = ""//轮播文字
    private val textSize: Float
    private val textRect = Rect()//用来测量文字
    private val fixTextWidth: Int//84为ui图上的宽度，但是有 3dp 被遮挡了 84 - 3
    private val fixTextHeight: Int// 轮播文字区域的高度
    private var offsetX = 0f//轮播文字的偏移
    private var textXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private var textAnimator: ValueAnimator? = null

    private val clip: Rect
    private val clipRight: Rect

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.bv_view_bubble, this, true)
        ftv = findViewById(R.id.ftv)
        setWillNotDraw(false)
        setBackgroundColor(Color.TRANSPARENT)

        fixWidth = dp2px(context, 165f)
        fixHeight = dp2px(context, 70f)
        redCountTextSize = dp2px(context, 12f).toFloat()
        redRadius = dp2px(context, 8f)
        textSize = dp2px(context, 14f).toFloat()
        fixTextWidth = dp2px(context, 81f)
        fixTextHeight = dp2px(context, 20f)

        bitmapBg = BitmapFactory.decodeResource(resources, R.drawable.bv_icon_bubble_bg)
        bitmapCircleBg =
            BitmapFactory.decodeResource(resources, R.drawable.bv_icon_bubble_bg_circle)
        paint.isAntiAlias = true

        //初始化
        clip = Rect(0, 0, fixWidth - fixHeight / 2, fixHeight)
        clip.right = fixHeight / 2
        clipRight = Rect(fixWidth - fixHeight / 2, 0, fixWidth, fixHeight)
        isClickable = false
        isExtend = false
    }


    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        ftv.setOnClickListener(l)
        outOnClickListener = l
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.makeMeasureSpec(fixWidth, MeasureSpec.EXACTLY)
        val height = MeasureSpec.makeMeasureSpec(fixHeight, MeasureSpec.EXACTLY)
        super.onMeasure(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        curWidth = w.toFloat()

    }

    fun setTextAndAvatar(text: String?, avatarResId: Int, isRound: Boolean = false) {
        if (visibility == View.GONE) {
            visibility = View.VISIBLE
        }
        this.text = text ?: ""

        if (isRound) {
            ftv.setImageResource(avatarResId)
        } else {
            val bitmap = BitmapFactory.decodeResource(resources, avatarResId)
            val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
            roundedBitmapDrawable.isCircular = true
            ftv.setImageDrawable(roundedBitmapDrawable)
        }

        offsetX = 0f
        removeCallbacks(textAnimRunnable)
        if (isExtend) {
            startTextAnimation()
        } else {
            startSlideInAnimation()
            postDelayed(textAnimRunnable, slideDuration)
        }

    }

    val textAnimRunnable = {
        startTextAnimation()
    }

    fun setTextAndAvatar(text: String?, onLoadImageListener: OnLoadImageListener?) {

        if (visibility == View.GONE) {
            visibility = View.VISIBLE
        }
        this.text = text ?: ""
        onLoadImageListener?.onLoadImage(ftv)
        offsetX = 0f
        removeCallbacks(textAnimRunnable)
        if (isExtend) {
            startTextAnimation()
        } else {
            startSlideInAnimation()
            postDelayed(textAnimRunnable, slideDuration)
        }
    }

    fun setRedCount(redCount: Int) {
        this.redCount = redCount
        postInvalidate()
    }

    private fun startTextAnimation() {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = textSize
        val rect = Rect()//用来测量文字
        paint.getTextBounds(this.text, 0, this.text.length, rect)

        cancelTextAnimation()

        if (rect.width() <= fixTextWidth) {
            postInvalidate()
            startSlideOutAnimation()
        } else {
            val dx = rect.width() - fixTextWidth
            var duration = (dx.toFloat() / fixTextWidth * 1500).toLong()
            duration = if (duration < 1000) 1000 else duration
            textAnimator = ValueAnimator.ofFloat(0f, dx.toFloat())
            textAnimator?.duration = duration
            textAnimator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    offsetX = -dx.toFloat()
                    startSlideOutAnimation()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    offsetX = 0f
                }

                override fun onAnimationStart(animation: Animator?) {
                    offsetX = 0f

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
            textAnimator?.addUpdateListener {
                offsetX = -(it.animatedValue as Float)
                postInvalidate()
            }
            textAnimator?.start()
        }
    }

    private fun cancelTextAnimation() {
        textAnimator?.cancel()
        textAnimator = null
    }

    fun startSlideOutAnimation() {
        cancelSlideOutAnimation()
        if (slideOutAnimator == null) {
            slideOutAnimator = ValueAnimator.ofInt(fixWidth - fixHeight / 2, fixHeight / 2)
            slideOutAnimator?.duration = slideDuration
            slideOutAnimator?.startDelay = 2000
            slideOutAnimator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    clip.right = fixHeight / 2

                    this@BubbleView.isClickable = false
                    isExtend = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    clip.right = fixWidth - fixHeight / 2

                }

                override fun onAnimationStart(animation: Animator?) {
                    clip.right = fixWidth - fixHeight / 2

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
            slideOutAnimator?.addUpdateListener {
                clip.right = it.animatedValue as Int
                postInvalidate()
            }
        }
        slideOutAnimator?.start()

    }

    private fun cancelSlideOutAnimation() {
        slideOutAnimator?.cancel()
    }

    private fun startSlideInAnimation() {
        cancelSlideInAnimation()
        if (slideInAnimator == null) {
            slideInAnimator = ValueAnimator.ofInt(fixHeight / 2, fixWidth - fixHeight / 2)
            slideInAnimator?.duration = slideDuration

            slideInAnimator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    clip.right = fixWidth - fixHeight / 2
                    if (outOnClickListener != null) {
                        this@BubbleView.isClickable = true
                    }
                    isExtend = true
                }

                override fun onAnimationCancel(animation: Animator?) {
                    clip.right = fixHeight / 2

                }

                override fun onAnimationStart(animation: Animator?) {
                    clip.right = fixHeight / 2

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
            slideInAnimator?.addUpdateListener {
                clip.right = it.animatedValue as Int
                postInvalidate()
            }
        }
        slideInAnimator?.start()

    }

    private fun cancelSlideInAnimation() {
        slideInAnimator?.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(textAnimRunnable)
        cancelTextAnimation()
        cancelSlideOutAnimation()
        cancelSlideInAnimation()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        // 画右边半个圆形
        canvas.save()
        canvas.clipRect(clipRight)
        canvas.drawBitmap(bitmapBg, 0f, 0f, paint)
        canvas.restore()
        // 画左边动画区域
        canvas.save()
        canvas.translate(-(clip.right - fixWidth + fixHeight / 2).toFloat(), 0f)
        canvas.clipRect(clip)
        canvas.drawBitmap(bitmapBg, 0f, 0f, paint)

        // 画文字
        if (text.isNotEmpty()) {
            val leftTop = dp2px(context, 25f).toFloat()
            paint.color = Color.WHITE

            val saveLayer = canvas.saveLayer(
                leftTop,
                leftTop,
                leftTop + fixTextWidth,
                leftTop + fixTextHeight,
                paint,
                Canvas.ALL_SAVE_FLAG
            )
            canvas.drawRect(
                leftTop,
                leftTop,
                leftTop + fixTextWidth,
                leftTop + fixTextHeight,
                paint
            )

            paint.xfermode = textXfermode
            paint.textSize = textSize
            paint.color = Color.BLACK
            paint.getTextBounds(text, 0, text.length, textRect)

            val centerVerticalToBaseLine = (-paint.ascent() + paint.descent()) / 2 - paint.descent()
            if (textRect.width() <= fixTextWidth) {
                canvas.drawText(text, leftTop, height / 2 + centerVerticalToBaseLine, paint)
            } else {
                canvas.drawText(
                    text,
                    leftTop + offsetX,
                    height / 2 + centerVerticalToBaseLine,
                    paint
                )
            }
            paint.xfermode = null
            canvas.restoreToCount(saveLayer)

        }
        canvas.restore()

        // 画圆形背景，最小宽度就是 fixHeight
        canvas.drawBitmap(bitmapCircleBg, curWidth - fixHeight, 0f, paint)

    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        // 画小红点
        if (redCount > 0) {
            paint.textSize = redCountTextSize
            val countStr = redCount.toString()
            // getTextBounds 获取到的不包含文字的内边距，所以直接用它的宽度计算后 drawText 会显得偏右
            // measureText 包含文字内边距，所以这里结合了两种方式目的就是让数据显示居中
            paint.getTextBounds(countStr, 0, countStr.length, textRect)
            val textWidth = paint.measureText(countStr)
            textRect.right = textRect.left + textWidth.toInt()


            val centerX = curWidth - dp2px(context, 18f).toFloat()

            paint.color = red
            val appendWidth = textRect.width().toFloat() - redRadius
            val left = centerX - redRadius
            val top = dp2px(context, 10f).toFloat()

            if (appendWidth > 0) {
                redRectF.set(
                    left - appendWidth / 2,
                    top,
                    left + redRadius * 2 + appendWidth / 2,
                    top + redRadius * 2
                )
            } else {
                redRectF.set(left, top, left + redRadius * 2, top + redRadius * 2)
            }
            canvas.drawRoundRect(redRectF, redRadius.toFloat(), redRadius.toFloat(), paint)

            /*
            // 测试代码
            paint.color = Color.GREEN
            canvas.drawRect(
                centerX - textRect.width() * 0.5f,
                dp2px(context, 18f).toFloat() - textRect.height() * 0.5f,
                centerX + textRect.width() * 0.5f,
                dp2px(context, 18f).toFloat() + textRect.height() * 0.5f,
                paint
            )
            */
            paint.color = Color.WHITE
            canvas.drawText(
                countStr,
                0,
                countStr.length,
                centerX - textRect.width() * 0.5f,
                dp2px(context, 18f).toFloat() + textRect.height() * 0.5f,
                paint
            )
        }
    }

    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    interface OnLoadImageListener {

        fun onLoadImage(imageView: ImageView)
    }
}
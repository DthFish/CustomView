package com.dthfish.slidingmarquee

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout

/**
 * Description
 * Author DthFish
 * Date  2020/4/16.
 */
class SlidingMarqueeLayout : FrameLayout {
    private var unspecifiedWidthLayout: UnspecifiedWidthMarqueeLayout? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.view_sliding_marquee, this, true)
        unspecifiedWidthLayout = findViewById(R.id.uwl)
    }

    fun offerMarquee(item: String?) {
        unspecifiedWidthLayout?.offerMarquee(item)
    }

}
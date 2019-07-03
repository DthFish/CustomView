package com.dthfish.customview

import android.content.Context

/**
 * Description
 * Author DthFish
 * Date  2019-07-02.
 */
object DemoUtils {

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}
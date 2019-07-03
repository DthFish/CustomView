package com.dthfish.customview.decoration

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dthfish.commondecoration.DecorationWidthSpend
import com.dthfish.customview.DemoUtils

class DemoFourHolder(v: View) : RecyclerView.ViewHolder(v), DecorationWidthSpend {
    val spend = DemoUtils.dp2px(v.context, 10f)

    override fun getLeftRightSpend(): Int {
        return spend
    }

}

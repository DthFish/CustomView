package com.dthfish.customview.decoration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dthfish.commondecoration.CommonGridDecoration
import com.dthfish.customview.DemoUtils
import com.dthfish.customview.R
import kotlinx.android.synthetic.main.activity_decoration.*

/**
 * Description
 * Author DthFish
 * Date  2019-07-02.
 */
class DecorationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decoration)

        val adapter = DemoAdapter()
        val spanCount = 12
        val layoutManager = GridLayoutManager(this, spanCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p: Int): Int {
                // 如果 recyclerView 做过封装比如 header 啥的，这里的 position 要自己计算以下
                val viewType = adapter.getItemViewType(p)
                return DemoAdapter.getSpanCount(viewType, spanCount)

            }
        }
        rv.addItemDecoration(CommonGridDecoration(layoutManager.spanSizeLookup, spanCount, DemoUtils.dp2px(this, 10f)))

        rv.layoutManager = layoutManager
        rv.adapter = adapter

        val array = arrayListOf("1", "2", "2", "2", "3", "4", "4", "4", "4", "4","3","5","5","5","5","5","5", "5")
        adapter.addAll(array)

    }
}
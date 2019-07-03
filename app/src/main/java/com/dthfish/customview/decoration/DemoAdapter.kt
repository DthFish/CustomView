package com.dthfish.customview.decoration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dthfish.customview.R

/**
 * Description
 * Author DthFish
 * Date  2019-07-02.
 */
class DemoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listData = mutableListOf<String>()

    companion object {
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
        const val TYPE_THREE = 3
        const val TYPE_FOUR = 4
        const val TYPE_FIVE = 5

        fun getSpanCount(viewType: Int, totalCount: Int): Int {

            return when (viewType) {
                TYPE_ONE -> totalCount
                TYPE_TWO -> totalCount / 3
                TYPE_THREE -> totalCount
                TYPE_FOUR -> totalCount / 3
                TYPE_FIVE -> totalCount / 2
                else -> totalCount
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return when (viewType) {
            TYPE_ONE -> {
                val v = inflater.inflate(R.layout.item_decoration_one, parent, false)
                DemoOneHolder(v)
            }
            TYPE_TWO -> {
                val v = inflater.inflate(R.layout.item_decoration_two, parent, false)
                DemoTwoHolder(v)
            }
            TYPE_THREE -> {
                val v = inflater.inflate(R.layout.item_decoration_three, parent, false)
                DemoThreeHolder(v)
            }
            TYPE_FOUR -> {
                val v = inflater.inflate(R.layout.item_decoration_four, parent, false)
                DemoFourHolder(v)
            }
            TYPE_FIVE -> {
                val v = inflater.inflate(R.layout.item_decoration_five, parent, false)
                DemoFiveHolder(v)
            }
            else -> {
                val v = inflater.inflate(R.layout.item_decoration_one, parent, false)
                DemoOneHolder(v)
            }
        }

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int {
        if (position < 0 || position >= listData.size) {
            return super.getItemViewType(position)
        }
        return when (listData[position]) {
            TYPE_ONE.toString() -> TYPE_ONE
            TYPE_TWO.toString() -> TYPE_TWO
            TYPE_THREE.toString() -> TYPE_THREE
            TYPE_FOUR.toString() -> TYPE_FOUR
            TYPE_FIVE.toString() -> TYPE_FIVE

            else -> super.getItemViewType(position)
        }
    }

    fun addAll(data: List<String>?) {
        if (!data.isNullOrEmpty()) {
            listData.addAll(data)
            notifyDataSetChanged()
        }
    }
}
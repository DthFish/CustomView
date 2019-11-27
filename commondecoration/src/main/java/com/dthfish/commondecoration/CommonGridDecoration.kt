package com.dthfish.commondecoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Description
 * Author DthFish
 * Date  2019/7/2.
 */
class CommonGridDecoration constructor(
    private val lookup: GridLayoutManager.SpanSizeLookup,
    private val spanCount: Int,
    private val space: Int,
    @ColorInt color: Int = Color.WHITE
) :
    RecyclerView.ItemDecoration() {
    private val paint: Paint

    init {
        lookup.isSpanIndexCacheEnabled = true
        paint = Paint()
        paint.color = color
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val size = lookup.getSpanSize(position)
        if (size == spanCount) {
            // do nothing
        } else {
            val viewHolder = parent.getChildViewHolder(view)
            // if 里面是根据 ViewHolder 设置 tag 来控制一行上的最左最右的 span 距离 rv 左右的距离（tag + margin left 为实际 ui 效果），
            // span 之间的距离由 view 的 magin 控制
            // else 里面根据构造方法里面的 space 来控制分割线宽度，最左最右的 span 距离 rv 左右的没有距离
            // onDraw 里面同理
            if (viewHolder is DecorationWidthSpend) {
                val leftRightSpend = viewHolder.getLeftRightSpend()
                // 左边数起的跨度索引值[0,spanCount)之间
                val spanIndexLeft = lookup.getSpanIndex(position, spanCount)
                // 右边数起的跨度索引值[0,spanCount)之间
                val spanIndexRight = spanCount - spanIndexLeft - size
                // 目前只支持同一行上的item size 都相同的情况
                // column = spanCount / size;该行多少列

                val column = spanCount / size

                // 分两种情况，如果 column 为 2 则，只需要修改左侧的 left 和右侧的 right
                // 否则，最左侧的 right 要像下一个 view 延伸，相邻它的则要被占用
                // 同理最右侧的 left 要向它前一个 view 延伸
                // 那么中间的改如何处理呢？两边向里延伸的部分就是中间消耗的并且每个 view 消耗的要和最左最右消耗的一致，
                // 每个 view 消耗的宽度 = leftRightSpend - 延伸 = （延伸 * 2）/ (column - 2) ->
                // 延伸 = leftRightSpend*(column -2) / column
                if (column == 2) {
                    if (spanIndexLeft == 0) {
                        outRect.left = leftRightSpend
                    } else if (spanIndexRight == 0) {
                        outRect.right = leftRightSpend
                    }

                } else {
                    val extend = leftRightSpend * (column - 2) / column
                    val midSpend = leftRightSpend - extend

                    // 注意这里要根据 item 在改行的 index 处理，所以需要除以 size
                    val columnIndex = spanIndexLeft / size
                    // 公式由归纳法得出
                    outRect.left = -((columnIndex - 1) * midSpend - extend)
                    outRect.right = midSpend * columnIndex - extend
                }


            } else {

                // 左边数起的跨度索引值[0,spanCount)之间
                val spanIndexLeft = lookup.getSpanIndex(position, spanCount)
                // 右边数起的跨度索引值[0,spanCount)之间
                val spanIndexRight = spanCount - spanIndexLeft - size
                // 目前只支持同一行上的item size 都相同的情况
                // column = spanCount / size;该行多少列
                // dividerCount = column - 1; 分割线数量
                // spendWidth = space * dividerCount/ column;每个横向item需要给分割线消耗的宽度
                // 分四种情况来分析：
                // 1. 仅spanIndexLeft == 0;紧靠左侧，需要左侧无分割线，右侧有分割线
                //          左：0                                     右：spendWidth
                // 2. 仅spanIndexRight == 0;紧靠右侧，需要右侧无分割线，左侧有分割线
                //          左：spendWidth                            右：0
                // 3. spanIndexLeft 和 spanIndexRight 都不等于0，且 spanIndexLeft 为奇数
                //          左：spendWidth/dividerCount               右：spendWidth - spendWidth/dividerCount
                // 4. spanIndexLeft 和 spanIndexRight 都不等于0，且 spanIndexLeft 为偶数
                //          左：spendWidth - spendWidth/dividerCount  右：spendWidth/dividerCount
                val column = spanCount / size
                val dividerCount = column - 1
                val spendWidth = (space.toFloat() * 1f * dividerCount.toFloat() / column).toInt()
                val tempWidth = (space * 1f / column).toInt()// spendWidth/dividerCount

                if (spanIndexLeft == 0) {
                    outRect.right = spendWidth
                } else if (spanIndexRight == 0) {
                    outRect.left = spendWidth
                } else {
                    if (spanIndexLeft % 2 == 1) {
                        outRect.left = tempWidth
                        outRect.right = spendWidth - tempWidth
                    } else {
                        outRect.left = spendWidth - tempWidth
                        outRect.right = tempWidth
                    }
                }
            }

        }
    }

    /*override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i) ?: continue
            val position = parent.getChildAdapterPosition(view)
            val size = lookup.getSpanSize(position)

            val viewHolder = parent.getChildViewHolder(view)

            if (viewHolder is DecorationWidthSpend) {
                //这里不需要画分割线
                continue

            } else {

                *//*if (size != spanCount) {
                    val spanIndexLeft = lookup.getSpanIndex(position, spanCount)
                    val spanIndexRight = spanCount - spanIndexLeft - size

                    val column = spanCount / size
                    val dividerCount = column - 1
                    val spendWidth = (space.toFloat() * 1f * dividerCount.toFloat() / column).toInt()
                    val tempWidth = (space * 1f / column).toInt()// spendWidth/dividerCount

                    val right: Int
                    val left: Int
                    if (spanIndexLeft == 0) {
                        right = spendWidth
                        drawRightDecoration(c, view, right)
                    } else if (spanIndexRight == 0) {
                        left = spendWidth
                        drawLeftDecoration(c, view, left)
                    } else {
                        if (spanIndexLeft % 2 == 1) {
                            left = tempWidth
                            right = spendWidth - tempWidth
                        } else {
                            left = spendWidth - tempWidth
                            right = tempWidth
                        }
                        drawBoth(c, view, left, right)

                    }
                }*//*
            }
        }
    }*/

    private fun drawBoth(c: Canvas, view: View, left: Int, right: Int) {
        if (left != 0) {
            drawLeftDecoration(c, view, left)
        }
        if (right != 0) {
            drawRightDecoration(c, view, right)
        }

    }

    private fun drawLeftDecoration(c: Canvas, child: View, leftSpace: Int) {
        val top = child.top
        val bottom = child.bottom
        val left = child.left - leftSpace
        val right = child.left
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)

    }

    private fun drawRightDecoration(c: Canvas, child: View, rightSpace: Int) {
        val top = child.top
        val bottom = child.bottom
        val left = child.right
        val right = left + rightSpace
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
    }
}
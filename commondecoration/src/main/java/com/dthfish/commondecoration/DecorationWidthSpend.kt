package com.dthfish.commondecoration

/**
 * Description 给 ViewHolder 继承使用，用于提供行上左右消耗的宽度
 * Author DthFish
 * Date  2019-07-02.
 */
interface DecorationWidthSpend {
    /**
     * return px
     */
    fun getLeftRightSpend(): Int
}
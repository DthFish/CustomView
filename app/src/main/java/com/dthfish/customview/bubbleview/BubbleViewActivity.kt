package com.dthfish.customview.bubbleview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dthfish.customview.R
import kotlinx.android.synthetic.main.activity_bubble_view.*
import kotlin.random.Random

/**
 * Description
 * Author DthFish
 * Date  2019-11-27.
 */
class BubbleViewActivity : AppCompatActivity() {
    private val textArray = arrayOf(
        "名不显时心不朽，再挑灯火看文章。",
        "采得百花成蜜后，为谁辛苦为谁甜？",
        "醉后不知天在水，满船清梦压星河。",
        "简单"
    )

    private val iconArray = arrayOf(
        R.drawable.icon_avatar1,
        R.drawable.icon_avatar2
    )

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble_view)
        bubbleView.setOnClickListener {
            Toast.makeText(this, "触发点击了", Toast.LENGTH_SHORT).show()
        }
        btn.setOnClickListener {

            val random = Random(System.currentTimeMillis())
            val index = random.nextInt(0, 4)
            val index2 = random.nextInt(0, 2)

            bubbleView.setTextAndAvatar(textArray[index], iconArray[index2])
        }

        btnCount.setOnClickListener {

            bubbleView.setRedCount(++count)
        }


    }
}
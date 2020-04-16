package com.dthfish.customview.slidingmarquee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dthfish.customview.R
import kotlinx.android.synthetic.main.activity_sliding_marquee.*
import kotlin.random.Random

/**
 * Description
 * Author DthFish
 * Date  2020/4/16.
 */
class SlidingMarqueeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding_marquee)

        val random = Random(System.currentTimeMillis())
        btnAdd.setOnClickListener {

            val count = random.nextInt(10) + 1
            var text = ""
            for (i in 1..count) {
                text += "0123456789"
            }
            marqueeLayout.offerMarquee(text)
        }

    }


}
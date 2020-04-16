package com.dthfish.customview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dthfish.customview.bubbleview.BubbleViewActivity
import com.dthfish.customview.decoration.DecorationActivity
import com.dthfish.customview.slidingmarquee.SlidingMarqueeActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDecoration.setOnClickListener {
            startActivity(Intent(this, DecorationActivity::class.java))
        }

        btnBubble.setOnClickListener {
            startActivity(Intent(this, BubbleViewActivity::class.java))
        }

        btnMarquee.setOnClickListener {
            startActivity(Intent(this, SlidingMarqueeActivity::class.java))
        }
    }
}

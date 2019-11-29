package com.droid.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.droid.myapplication.R
import kotlinx.android.synthetic.main.gallery_item_view.view.*

class DetailViewActivity : AppCompatActivity() {


    var imageView: AppCompatImageView? = null

    var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)

        imagePath = intent.getStringExtra("path")

        imageView = findViewById(R.id.iv)

        if (imagePath != null && imageView != null)
            Glide.with(this).load(imagePath).into(imageView!!)
    }
}

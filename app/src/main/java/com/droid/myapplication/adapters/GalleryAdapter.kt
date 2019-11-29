package com.droid.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.droid.myapplication.R
import com.droid.myapplication.data.ImageGallery
import com.droid.myapplication.ui.DetailViewActivity
import com.droid.myapplication.ui.GalleryActivity
import kotlinx.android.synthetic.main.gallery_item_view.view.*

class GalleryAdapter(
    private val context: Context,
    private val imagesList: ArrayList<ImageGallery>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var galleryList: ArrayList<ImageGallery> = ArrayList()


    init {
        this.galleryList = imagesList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ImageGalleryViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.gallery_item_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return galleryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val galleyViewHolder = holder as ImageGalleryViewHolder
        if (galleryList.size > 0)
            galleyViewHolder.bindView(galleryList[position])
    }

    fun updateList(galleryList: ArrayList<ImageGallery>) {
        this.galleryList = galleryList
        notifyDataSetChanged()
    }


    class ImageGalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(imageGallery: ImageGallery) {
            Glide.with(itemView.context).load(imageGallery.path).into(itemView.iv)
            itemView.iv.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            DetailViewActivity::class.java
                        ).putExtra("path", imageGallery.path)
                    )
                }
            })
        }


    }

}
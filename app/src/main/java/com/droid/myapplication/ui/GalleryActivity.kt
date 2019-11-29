package com.droid.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.droid.myapplication.R
import com.droid.myapplication.adapters.GalleryAdapter
import com.droid.myapplication.data.ImageGallery
import java.io.File
import java.io.FilenameFilter

class GalleryActivity : AppCompatActivity() {


    var fileArray = ArrayList<String>()// list of file paths
    var listFile: Array<File>? = null

    var file: File? = null

    var imageGalleyList: ArrayList<ImageGallery> = ArrayList()


    var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        mRecyclerView = findViewById(R.id.mRecyclerView)

        getFromSdcard()

        imageGalleyList.clear()

        /*  for (path in fileArray) {
              imageGalleyList.add(ImageGallery("", path))
          }*/

        for (i in listFile!!.indices) {

//            fileArray.add(listFile!![i].absolutePath)

            imageGalleyList.add(ImageGallery(listFile!![i].name, listFile!![i].absolutePath))

        }


        mRecyclerView?.layoutManager = GridLayoutManager(this, 2)

        /*    //This will for default android divider
            mRecyclerView?.addItemDecoration(GridItemDecoration(10, 2))*/

        val movieListAdapter = GalleryAdapter(this, imageGalleyList)
        mRecyclerView?.adapter = movieListAdapter
        movieListAdapter.updateList(imageGalleyList)

//        readFilesFormMemory()


    }


    fun getFromSdcard() {
        file = File(Environment.getExternalStorageDirectory(), "customPictures")

        if (file!!.isDirectory) {
            listFile = file?.listFiles()

            fileArray.clear()

            for (i in listFile!!.indices) {

                fileArray.add(listFile!![i].absolutePath)

            }
        }
    }

    fun readFilesFormMemory() {

        val folder = File(Environment.getExternalStorageDirectory().toString() + "/customPictures/")
        folder.mkdirs()
        val allFiles = folder.listFiles(object : FilenameFilter {
            override fun accept(dir: File, name: String): Boolean {
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
            }
        })

        var i = 0


    }


}

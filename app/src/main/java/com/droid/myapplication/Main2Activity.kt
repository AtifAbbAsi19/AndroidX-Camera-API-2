package com.droid.myapplication


import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.os.*
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class Main2Activity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PictureCallback {


    var camera: Camera? = null
    var surfaceView: SurfaceView? = null
    var surfaceHolder: SurfaceHolder? = null

    var previewing = false


    val REQUEST_CODE = 100

    private val neededPermissions = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)

    var recordingButton: AppCompatImageButton? = null

    var captureImageButton: AppCompatImageButton? = null

    var galleryButton: AppCompatImageButton? = null


    //camera two implementation

    private var mFlashSupported: Boolean = false
    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private var captureRequest: CaptureRequest? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null
    private var imageReader: ImageReader? = null
    private var file: File? = null
    val CAMERA2_REQUEST_CODE = 200
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

//    private var textureCamera: TextureView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        if (surfaceView != null) {

            val result = checkPermission()

            if (result) {  //check if Permission is granted or not
                // setupSurfaceHolder()

            } else {
                finish()
            }
        } else {
            finish()
        }//null check for surface view


    }//end of onCreate


    //init
    private fun initView() {

//        textureCamera = findViewById(R.id.texture_camera_view)

        surfaceView = findViewById(R.id.surface_camera)

//        recordingButton = findViewById(R.id.recordingBtn)

        captureImageButton = findViewById(R.id.captureImageBtn)

        captureImageButton?.setOnClickListener { captureImage() }

//        galleryButton = findViewById(R.id.galleryBtn)
    }


    private fun captureImage() {

        if (camera != null && previewing) {
            camera!!.takePicture(null, null, this)
        }
    }


    private fun setupSurfaceHolder() {
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this)
//        surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

    }

    // start camera
    fun startCamera() {

        if (!previewing) {
            camera = Camera.open()
            camera?.setDisplayOrientation(90)
            try {
                camera?.setPreviewDisplay(surfaceHolder)
                camera?.startPreview()
                previewing = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }//end of preview check

    }//end of start camera

    // rest Camera
    fun resetCamera() {

        if (surfaceHolder?.surface == null) {
            // Return if preview surface does not exist
            return
        }

        if (camera != null && previewing) {
            // Stop if preview surface is already running.
            releaseCamera()
            try {
                // Set preview display
                camera?.setPreviewDisplay(surfaceHolder)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Start the camera preview...
            camera?.startPreview()
        }
    }

    //stop & release camera
    fun releaseCamera() {
        if (camera != null && previewing) {
            camera?.stopPreview()
            camera?.release()
            previewing = false

        }
    }//end of stop camera

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        resetCamera()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        releaseCamera()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        //start Camera service
        startCamera()

    }

    override fun onPictureTaken(bytes: ByteArray?, camera: Camera?) {
        bytes?.let { saveImage(it) }
        resetCamera()

    }

    private fun saveImage(bytes: ByteArray) {
        val outStream: FileOutputStream
        try {
            val fileName = "Capture_" + System.currentTimeMillis() + ".jpg"
            val file = File(Environment.getExternalStorageDirectory(), fileName)
            outStream = FileOutputStream(file)
            outStream.write(bytes)
            outStream.close()
            Toast.makeText(this@Main2Activity, "Picture Saved: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            val permissionsNotGranted = ArrayList<String>()

            for (permission in neededPermissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsNotGranted.add(permission)
                }
            }
            if (permissionsNotGranted.size > 0) {
                var shouldShowAlert = false
                for (permission in permissionsNotGranted) {
                    shouldShowAlert =
                        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }
                if (shouldShowAlert) {
                    showPermissionAlert(
                        permissionsNotGranted.toArray(
                            arrayOfNulls<String>(
                                permissionsNotGranted.size
                            )
                        )
                    )
                } else {
                    requestPermissions(
                        permissionsNotGranted.toArray(
                            arrayOfNulls<String>(
                                permissionsNotGranted.size
                            )
                        )
                    )
                }
                return false
            }
        }
        return true
    }

    private fun showPermissionAlert(permissions: Array<String>) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(R.string.permission_required)
        alertBuilder.setMessage(R.string.permission_message)
        alertBuilder.setPositiveButton(android.R.string.yes,
            DialogInterface.OnClickListener { dialog, which -> requestPermissions(permissions) })
        val alert = alertBuilder.create()
        alert.show()
    }

    private fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this@Main2Activity, permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    // Not all permissions granted. Show message to the user.
                    return
                }
            }
        }// All permissions are granted. So, do the appropriate work now.

        setupSurfaceHolder()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}//end of class

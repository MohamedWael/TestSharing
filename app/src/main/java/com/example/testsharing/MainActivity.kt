package com.example.testsharing

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.graphics.applyCanvas
import androidx.core.view.ViewCompat
import java.io.File
import java.io.FileOutputStream

val dirPath: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Screenshots"

class MainActivity : AppCompatActivity() {
    private val url = "https://developer.android.com/training/sharing/send"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnShareImageAndText = findViewById<Button>(R.id.btnShare)
        val btnShareImageOnly = findViewById<Button>(R.id.btnShareImageOnly)
        val btnShareTextOnly = findViewById<Button>(R.id.btnShareTextOnly)

        val btnSaveScreenShot = findViewById<Button>(R.id.btnSaveScreenShot)
        val ivImage = findViewById<ImageView>(R.id.ivImage)
        val flowerImage = AppCompatResources.getDrawable(this, R.drawable.img)

        btnSaveScreenShot.setOnClickListener {
            val flowerBitmap = ivImage.drawToBitmap()
            store(flowerBitmap!!) {
                showMessage("Image saved")
            }
        }

        btnShareImageOnly.setOnClickListener {
            val photoURI: Uri = FileProvider.getUriForFile(
                this, applicationContext.packageName + ".provider",
                File(dirPath, "image.png")
            )

            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                putExtra(Intent.EXTRA_TITLE, "Watch out how to share content")
                putExtra(Intent.EXTRA_STREAM, arrayListOf(photoURI))
                // (Optional) Here you're passing a content URI to an image to be displayed
                setDataAndType(photoURI, "*/*")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, "Share"))
        }

        btnShareTextOnly.setOnClickListener {
            val textOnlyShareIntent = Intent.createChooser(Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                putExtra(Intent.EXTRA_TITLE, "Watch out how to share content")
                putExtra(Intent.EXTRA_TEXT, ("Watch out how to share content on:\n $url"))

                type = "*/*"

            }, "Share")
            startActivity(textOnlyShareIntent)
        }

        btnShareImageAndText.setOnClickListener {
            val photoURI: Uri = FileProvider.getUriForFile(
                this, applicationContext.packageName + ".provider",
                File(dirPath, "image.png")
            )

            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                putExtra(Intent.EXTRA_TITLE, "Watch out how to share content")
                putExtra(Intent.EXTRA_STREAM, arrayListOf(photoURI))
                putExtra(Intent.EXTRA_TEXT, ("Watch out how to share content on:\n $url"))

                // (Optional) Here you're passing a content URI to an image to be displayed
                setDataAndType(photoURI, "*/*")

                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, "Share"))
        }

    }

    private fun showMessage(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun store(bm: Bitmap, fileName: String = "image.png", onFinished:()->Unit) {
        Thread {
            val dir = File(dirPath)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dirPath, fileName)
            try {
                val fOut = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
                fOut.close()
                runOnUiThread {
                    onFinished()
                }
            } catch (e: Exception) {
                showMessage("Error saving image")
                e.printStackTrace()
            }
        }.start()
    }

    private fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun View.drawToBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        if (!ViewCompat.isLaidOut(this)) {
            throw IllegalStateException("View needs to be laid out before calling drawToBitmap()")
        }
        return Bitmap.createBitmap(width, height, config).applyCanvas {
            translate(-scrollX.toFloat(), -scrollY.toFloat())
            draw(this)
        }
    }
}
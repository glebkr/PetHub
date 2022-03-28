package com.example.pethub.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import java.io.FileNotFoundException

class ChoosePhoto {
    companion object {
        private fun decodeBitmap(uri: Uri, activity: Activity): Bitmap? {
            return try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(activity.contentResolver.openInputStream(uri), null, options)
                var scale = 1
                while (options.outWidth / scale / 2 >= 2000 && options.outHeight / scale / 2 >= 2000) scale *= 2

                val scaleOptions = BitmapFactory.Options()
                scaleOptions.inSampleSize = scale
                BitmapFactory.decodeStream(activity.contentResolver.openInputStream(uri), null, scaleOptions)
            } catch (e: FileNotFoundException) {
                null
            }
        }

        var bitmapImg: Bitmap? = null
        fun Intent.createBitmapFromResult(activity: Activity, x: String, y: String): Bitmap? {
            val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
            sharedPrefs.edit().apply {
                putString("x_coord", x)
                putString("y_coord", y)
            }.apply()
            val intentBundle = this.extras
            val intentUri = this.data
            var bitmap: Bitmap? = null
            if (intentBundle != null) {
                bitmap = (intentBundle.get("data") as? Bitmap)
                /*?.apply {
                    compress(Bitmap.CompressFormat.JPEG, 75, ByteArrayOutputStream())
                }
                     */
            }
            if (bitmap == null && intentUri != null) {
                intentUri.let { bitmap = decodeBitmap(intentUri, activity) }
            }
            bitmapImg = bitmap
            return bitmap
        }

        fun loadPhotoFromDevice() : Intent {
            /*
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).takeIf { intent ->
            intent.resolveActivity(requireActivity().packageManager) != null
        }

         */

            val galleryIntent = Intent(Intent.ACTION_PICK).apply { this.type = "image/*" }

            val intentChooser = Intent(Intent.ACTION_CHOOSER).apply {
                this.putExtra(Intent.EXTRA_INTENT, galleryIntent)
                /*
            cameraIntent?.let { intent ->
                this.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayListOf(intent).toTypedArray<Parcelable>())
            }

             */
                this.putExtra(Intent.EXTRA_TITLE, "Выбор действия")
            }
            return intentChooser

        }


    }
}
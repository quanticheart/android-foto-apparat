package com.quanticheart.camera.file

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private fun Context.getApplicationName(): String {
    return applicationInfo.loadLabel(packageManager).toString()
}

private fun getUniqueName(): String {
    val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
    val now = Date()
    return formatter.format(now) + "_" + System.currentTimeMillis() + "_photo.jpg"
}

/**Check If SD Card is present or not method */
private val isSDCardPresent: Boolean =
    (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)

/**
 * Create file in external card, if not exits, create in app Repo
 *
 * @param context for verify app package
 */
fun Context.getExternalFile(): File {
    //Get File if SD card is present
    if (isSDCardPresent) {
        val apkStorage = File(
            getExternalStorageDirectory().toString() +
                    "/" + getApplicationName()
        )
        //If File is not present create directory
        if (!apkStorage.exists()) {
            apkStorage.mkdir()
            Log.w(TAG, "Directory Created.")
        } else {
            Log.w(TAG, "$apkStorage Directory Exists.")
        }
        return File(apkStorage, getUniqueName())
    } else
        Log.w(ContentValues.TAG, "SDCard No Exists.")
    return getInternalFile()
}

fun Context.getInternalFile(): File =
    File(getExternalFilesDir("photos"), getUniqueName())

private const val TAG = "CameraBasic"

fun Context.storeImageExternal(image: Bitmap) {
    val pictureFile = getExternalFile()
    try {
        val fos = FileOutputStream(pictureFile)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    } catch (e: FileNotFoundException) {
        Log.wtf(TAG, e)
    } catch (e: IOException) {
        Log.wtf(TAG, e)
    }
}

fun Context.storeImageInternal(image: Bitmap) {
    val pictureFile = getInternalFile()
    try {
        val fos = FileOutputStream(pictureFile)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    } catch (e: FileNotFoundException) {
        Log.wtf(TAG, e)
    } catch (e: IOException) {
        Log.wtf(TAG, e)
    }
}
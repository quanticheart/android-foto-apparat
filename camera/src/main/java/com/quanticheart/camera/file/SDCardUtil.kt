/*
 *
 *  *                                     /@
 *  *                      __        __   /\/
 *  *                     /==\      /  \_/\/
 *  *                   /======\    \/\__ \__
 *  *                 /==/\  /\==\    /\_|__ \
 *  *              /==/    ||    \=\ / / / /_/
 *  *            /=/    /\ || /\   \=\/ /
 *  *         /===/   /   \||/   \   \===\
 *  *       /===/   /_________________ \===\
 *  *    /====/   / |                /  \====\
 *  *  /====/   /   |  _________    /      \===\
 *  *  /==/   /     | /   /  \ / / /         /===/
 *  * |===| /       |/   /____/ / /         /===/
 *  *  \==\             /\   / / /          /===/
 *  *  \===\__    \    /  \ / / /   /      /===/   ____                    __  _         __  __                __
 *  *    \==\ \    \\ /____/   /_\ //     /===/   / __ \__  ______  ____ _/ /_(_)____   / / / /__  ____ ______/ /_
 *  *    \===\ \   \\\\\\\/   ///////     /===/  / / / / / / / __ \/ __ `/ __/ / ___/  / /_/ / _ \/ __ `/ ___/ __/
 *  *      \==\/     \\\\/ / //////       /==/  / /_/ / /_/ / / / / /_/ / /_/ / /__   / __  /  __/ /_/ / /  / /_
 *  *      \==\     _ \\/ / /////        |==/   \___\_\__,_/_/ /_/\__,_/\__/_/\___/  /_/ /_/\___/\__,_/_/   \__/
 *  *        \==\  / \ / / ///          /===/
 *  *        \==\ /   / / /________/    /==/
 *  *          \==\  /               | /==/
 *  *          \=\  /________________|/=/
 *  *            \==\     _____     /==/
 *  *           / \===\   \   /   /===/
 *  *          / / /\===\  \_/  /===/
 *  *         / / /   \====\ /====/
 *  *        / / /      \===|===/
 *  *        |/_/         \===/
 *  *                       =
 *  *
 *  * Copyright(c) Developed by John Alves at 2020/2/21 at 10:57:54 for quantic heart studios
 *
 */

package com.quanticheart.camera.file

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns

fun Activity.getAllImages(): ArrayList<ImageDataModel> {
    val allImages: ArrayList<ImageDataModel> = ArrayList()

    val projection = arrayOf(MediaColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME)

    //get all images from external storage
    val uriExternal: Uri? = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    uriExternal?.let { uri ->
        val cursorExt = contentResolver.query(uri, projection, null, null, null)

        cursorExt?.let { cursor ->
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
            val columnIndexFolderName =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                allImages.add(
                    ImageDataModel(
                        cursor.getString(columnIndexFolderName),
                        cursor.getString(columnIndexData)
                    )
                )
            }
        }
        cursorExt?.close()
    }

    // Get all Internal storage images
    val uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI
    uriInternal.let { uri ->
        val cursorIntr = contentResolver.query(uri, projection, null, null, null)
        cursorIntr?.let { cursor ->
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
            val columnIndexFolderName =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                allImages.add(
                    ImageDataModel(
                        cursor.getString(columnIndexFolderName),
                        cursor.getString(columnIndexData)
                    )
                )
            }
        }
        cursorIntr?.close()
    }
    return allImages
}

data class ImageDataModel(
    var imageTitle: String,
    var imagePath: String
)
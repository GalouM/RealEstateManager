package com.openclassrooms.realestatemanager.utils.extensions

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import com.openclassrooms.realestatemanager.utils.ICON_MAP_FOLDER
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by galou on 2019-08-26
 */

fun Bitmap.saveToInternalStorage(context: Context?, name: String): Uri{
    val wrapper = ContextWrapper(context)
    val directory = wrapper.getDir(ICON_MAP_FOLDER, MODE_PRIVATE)
    val file = File(directory, "$name.jpeg")

    try {
        val stream = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException){
        e.printStackTrace()
    }

    return Uri.parse(file.absolutePath)
}
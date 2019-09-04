package com.openclassrooms.realestatemanager.utils.extensions

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import com.openclassrooms.realestatemanager.utils.TypeImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by galou on 2019-08-26
 */

fun Bitmap.saveToInternalStorage(context: Context?, name: String, type: TypeImage): Uri{
    val wrapper = ContextWrapper(context)
    val directory = wrapper.getDir(type.folder, MODE_PRIVATE)
    val file = File(directory, "JPEG_${type}_$name.jpeg")

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


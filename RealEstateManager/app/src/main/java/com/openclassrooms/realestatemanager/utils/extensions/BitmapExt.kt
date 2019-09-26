package com.openclassrooms.realestatemanager.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.openclassrooms.realestatemanager.utils.TypeImage
import com.openclassrooms.realestatemanager.utils.displayData
import com.openclassrooms.realestatemanager.utils.filePathToInternalStorage
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files

/**
 * Created by galou on 2019-08-26
 */

fun Bitmap.saveToInternalStorage(context: Context?, name: String, type: TypeImage): Uri{
    val file = filePathToInternalStorage(context!!, name, type)
    if(file.exists()) file.delete()

    try {
        val stream = FileOutputStream(file, false)
        this.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException){
        e.printStackTrace()
    }

    return Uri.parse(file.absolutePath)
}


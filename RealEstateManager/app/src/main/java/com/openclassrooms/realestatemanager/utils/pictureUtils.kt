package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PhotoForDisplay
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.utils.extensions.saveToInternalStorage
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by galou on 2019-09-05
 */


fun intentSeveralPicture(): Intent {
    return Intent().apply {
        action = Intent.ACTION_PICK
        type = IMAGE_ONLY_TYPE
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
}

fun intentSinglePicture(): Intent{
    return Intent().apply {
        action = Intent.ACTION_PICK
        type = IMAGE_ONLY_TYPE
    }
}

fun addPictureToGallery(context: Context, photoPath: String){
    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
        val f = File(photoPath)
        mediaScanIntent.data = Uri.fromFile(f)
        context.sendBroadcast(mediaScanIntent)
    }

}

@Throws(IOException::class)
fun createImageFileFromCamera(): File{
    val name = generateName()
    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${TypeImage.PROPERTY}_$name",".jpeg", directory)

}

@AfterPermissionGranted(RC_IMAGE_PERMS)
fun requestPermissionStorage(fragment: Fragment): Boolean {
    if (!EasyPermissions.hasPermissions(fragment.activity!!, PERMS_EXT_STORAGE)) {
        EasyPermissions.requestPermissions(
                fragment, fragment.activity!!.getString(R.string.storage_perm_request), RC_IMAGE_PERMS, PERMS_EXT_STORAGE)
        return false
    }

    return true
}

fun getThumbnailFromPicture(picture: String, context: Context): String{
    val picturePath = File(picture)
    val bitmap = MediaStore.Images.Media
            .getBitmap(context.contentResolver, Uri.fromFile(picturePath))
    return bitmap.saveToInternalStorage(
            context, generateName(), TypeImage.PROPERTY ).toString()

}

fun getPicturesPathFromData(data: Intent): List<String>{
    val listPicture = mutableListOf<String>()
    val uris = data.clipData
    if(uris != null) {
        for(i in 0 until uris.itemCount){
            val uri = uris.getItemAt(i).uri
            listPicture.add(uri.toString())
        }
    } else {
        val uri = data.data
        uri?.let{listPicture.add(it.toString())}
    }

    return listPicture
}

fun generateName(): String{
    return SimpleDateFormat(DATE_FORMAT_FOR_NAME).format(Date())
}

fun getMainPictureUrl(pictures: List<PhotoForDisplay>): String{
    val firstPicture = pictures[0]
    return firstPicture.uriThumbnail ?: pictures[0].uriPicture
}

fun createPictureForDisplay(pictures: List<Picture>): List<PhotoForDisplay>{
    val pictureForDisplay = mutableListOf<PhotoForDisplay>()
    pictures.forEach {
        val photo = PhotoForDisplay(it.url, it.description, it.thumbnailUrl)
        pictureForDisplay.add(photo)
    }
    return pictureForDisplay
}
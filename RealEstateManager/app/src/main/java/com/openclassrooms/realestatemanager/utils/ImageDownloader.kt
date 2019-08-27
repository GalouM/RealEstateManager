package com.openclassrooms.realestatemanager.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by galou on 2019-08-26
 */
class ImageDownloader(callback: Listeners)
    : AsyncTask<URL, Void, Bitmap>() {

    interface Listeners{
        fun onPostExecute(bitmap: Bitmap)
    }

    private val callBackWeakReference: WeakReference<Listeners> = WeakReference(callback)

    override fun doInBackground(vararg urls: URL?): Bitmap? {
        val url = urls[0]
        var connection: HttpURLConnection? = null

        try {
            connection = url?.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)

        } catch (e: IOException){
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        result?.let { callBackWeakReference.get()!!.onPostExecute(it) }
    }
}
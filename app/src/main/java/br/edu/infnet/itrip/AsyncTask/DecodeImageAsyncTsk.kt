package br.edu.infnet.itrip.AsyncTask

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.widget.ImageView

class DecodeImageAsyncTsk (val photo_trip: ImageView)
    : AsyncTask<String, Void, Bitmap>()
{
    override fun doInBackground(vararg params: String): Bitmap? {
        val bytarray: ByteArray = Base64.decode(params[0], Base64.DEFAULT)
        val bmimage = BitmapFactory.decodeByteArray(
            bytarray, 0,
            bytarray.size
        )
        return bmimage
    }

    override fun onPostExecute(result: Bitmap?) {
        photo_trip.setImageBitmap(result)
    }
}
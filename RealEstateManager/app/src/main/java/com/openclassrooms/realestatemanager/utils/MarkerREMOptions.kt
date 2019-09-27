package com.openclassrooms.realestatemanager.utils

import android.os.Parcel
import android.os.Parcelable
import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.exceptions.InvalidMarkerPositionException
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Created by galou on 2019-09-03
 */
class MarkerREMOptions() : BaseMarkerOptions<MarkerREM, MarkerREMOptions>(), Parcelable {

    var idRem: String = ""

    constructor(parcel: Parcel) : this()

    override fun getMarker(): MarkerREM {
        if (this.position == null) {
            throw InvalidMarkerPositionException()
        }

        return MarkerREM(this, idRem)
    }

    override fun getThis(): MarkerREMOptions {
        return this
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeParcelable(position, flags)
        out.writeString(snippet)
        out.writeString(title)
        out.writeString(idRem)
        val icon = icon
        out.writeByte((if (icon != null) 1 else 0).toByte())
        if (icon != null) {
            out.writeString(icon.id)
            out.writeParcelable(icon.bitmap, flags)
        }
    }

    companion object CREATOR : Parcelable.Creator<MarkerREMOptions> {
        override fun createFromParcel(parcel: Parcel): MarkerREMOptions {
            return MarkerREMOptions(parcel)
        }

        override fun newArray(size: Int): Array<MarkerREMOptions?> {
            return arrayOfNulls(size)
        }
    }

    fun getPosition(): LatLng
    {
        return position
    }

    /**
     * Gets the snippet set for this [MarkerOptions] object.
     *
     * @return A string containing the marker's snippet.
     */
    fun getSnippet(): String? {
        return snippet
    }

    /**
     * Gets the title set for this [MarkerOptions] object.
     *
     * @return A string containing the marker's title.
     */
    fun getTitle(): String? {
        return title
    }

    /**
     * Gets the custom icon set for this [MarkerOptions] object.
     *
     * @return A [Icon] object that the marker is using. If the icon wasn't set, default icon
     * will return.
     */
    fun getIcon(): Icon? {
        return icon
    }


}
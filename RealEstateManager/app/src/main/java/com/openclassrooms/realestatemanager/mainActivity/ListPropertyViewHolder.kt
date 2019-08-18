package com.openclassrooms.realestatemanager.mainActivity

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.extensions.toDollar
import com.openclassrooms.realestatemanager.extensions.toDollarDisplay
import com.openclassrooms.realestatemanager.extensions.toEuroDisplay
import com.openclassrooms.realestatemanager.utils.Currency
import timber.log.Timber

/**
 * Created by galou on 2019-08-12
 */
class ListPropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.list_property_item_picture) lateinit var pictureView: ImageView
    @BindView(R.id.list_property_item_type) lateinit var type: TextView
    @BindView(R.id.list_property_item_neighborhood) lateinit var neighborhood: TextView
    @BindView(R.id.list_property_item_price) lateinit var price: TextView
    @BindView(R.id.list_property_item_layout) lateinit var layout: LinearLayout


    init {
        ButterKnife.bind(this, view)
    }

    fun updateWithProperty(property: PropertyForListDisplay, glide: RequestManager, currency: Currency, context: Context){


        Timber.e(property.toString())
        property.pictureUrl?.let{
            glide.load(this).apply(RequestOptions.centerCropTransform()).into(pictureView)
        }

        type.text = property.type
        neighborhood.text = property.neighborhood
        price.text = when(currency){
            Currency.EURO -> "${property.price.toEuroDisplay()}€"
            Currency.DOLLAR -> "$${property.price.toDollar().toDollarDisplay()}"
        }

        if(property.sold) {
            layout.setBackgroundColor(getColor(context, R.color.colorPrimaryUltraLight))
            price.setTextColor(getColor(context, R.color.colorAccentLight))
            type.setTextColor(getColor(context, R.color.colorTextPrimaryAlpha))
        }
    }
}
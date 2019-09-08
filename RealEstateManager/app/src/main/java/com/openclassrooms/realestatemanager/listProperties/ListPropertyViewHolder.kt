package com.openclassrooms.realestatemanager.listProperties

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.extensions.toDollar
import com.openclassrooms.realestatemanager.utils.extensions.toDollarDisplay
import com.openclassrooms.realestatemanager.utils.extensions.toEuroDisplay

/**
 * Created by galou on 2019-08-12
 */
class ListPropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.list_property_item_picture) lateinit var pictureView: ImageView
    @BindView(R.id.list_property_item_type) lateinit var type: TextView
    @BindView(R.id.list_property_item_neighborhood) lateinit var neighborhood: TextView
    @BindView(R.id.list_property_item_price) lateinit var price: TextView
    @BindView(R.id.list_property_item_background) lateinit var background: CardView

    private var backgroundColor: Int? = null
    private var priceColor: Int? = null


    init {
        ButterKnife.bind(this, view)
    }

    fun updateWithProperty(property: PropertyWithAllData, glide: RequestManager, currency: Currency, context: Context){

        if(property.pictures.isNotEmpty()){
            val firstPicture = property.pictures.minBy{it.orderNumber!!}
            val pictureUrl = firstPicture!!.thumbnailUrl ?: firstPicture.url
            glide.load(pictureUrl).apply(RequestOptions.centerCropTransform()).into(pictureView)
        }

        type.text = property.property.type.typeName
        neighborhood.text = property.address[0].neighbourhood
        price.text = when(currency){
            Currency.EURO -> "${property.property.price.toEuroDisplay()}€"
            Currency.DOLLAR -> "$${property.property.price.toDollar().toDollarDisplay()}"
        }

        if(property.property.sold) {
            background.setCardBackgroundColor(getColor(context, R.color.colorPrimaryUltraLight))
            price.setTextColor(getColor(context, R.color.colorAccentLight))
            type.setTextColor(getColor(context, R.color.colorTextPrimaryAlpha))
        }

        priceColor = price.currentTextColor

        backgroundColor = background.cardBackgroundColor.defaultColor



    }

    fun displaySelection(positionSelected: Int, context: Context){
        if(this.adapterPosition == positionSelected){
            background.setCardBackgroundColor(getColor(context, R.color.colorAccent))
            price.setTextColor(getColor(context, R.color.colorTextAccent))
        } else {
            background.setCardBackgroundColor(backgroundColor!!)
            price.setTextColor(priceColor!!)
        }
    }
}
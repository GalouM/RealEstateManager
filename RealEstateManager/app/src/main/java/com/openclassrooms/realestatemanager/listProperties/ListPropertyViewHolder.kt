package com.openclassrooms.realestatemanager.listProperties

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
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
import java.lang.Exception

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
    private var typeColor: Int? = null

    private var isSelected = false
    private var isSold = false
    private lateinit var context: Context

    init {
        ButterKnife.bind(this, view)
        priceColor = price.currentTextColor
        backgroundColor = background.cardBackgroundColor.defaultColor
        typeColor = type.currentTextColor
    }

    fun updateWithProperty(
            property: PropertyWithAllData, glide: RequestManager,
            currency: Currency?, context: Context, isDoubleScreen: Boolean
    ){
        this.context = context
        isSold = property.property.sold

        if(property.pictures.isNotEmpty()){
            val firstPicture = property.pictures.minBy{it.orderNumber!!}
            val pictureUrl = firstPicture!!.thumbnailUrl ?: firstPicture.url
            try {
                glide.load(pictureUrl).apply(RequestOptions.centerCropTransform()).into(pictureView)
            } catch (e: Exception) {
            }
        }

        type.text = property.property.type.typeName
        neighborhood.text = property.address[0].neighbourhood
        price.text = when(currency){
            Currency.EURO -> "${property.property.price.toEuroDisplay()}€"
            Currency.DOLLAR -> "$${property.property.price.toDollar().toDollarDisplay()}"
            else -> ""
        }

        if(!isDoubleScreen || !isSelected){
            configureCardToNormalState()
        }
    }

    fun displaySelection(positionSelected: Int?){
        if(positionSelected != null) {
            if (this.adapterPosition == positionSelected) {
                isSelected = true
                configureCardToSelectedState()
            } else {
                isSelected = false
                configureCardToNormalState()
            }
        } else {
            configureCardToNormalState()
        }

    }

    private fun configureCardToNormalState(){
        if(isSold){
            background.setCardBackgroundColor(getColor(context, R.color.colorPrimaryUltraLight))
            price.setTextColor(getColor(context, R.color.colorAccentLight))
            type.setTextColor(getColor(context, R.color.colorTextPrimaryAlpha))
        } else {
            background.setCardBackgroundColor(backgroundColor!!)
            price.setTextColor(priceColor!!)
            type.setTextColor(typeColor!!)
        }
    }

    private fun configureCardToSelectedState(){
        background.setCardBackgroundColor(getColor(context, R.color.colorAccent))
        price.setTextColor(getColor(context, R.color.colorTextAccent))
    }
}
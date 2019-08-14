package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-08-12
 */
data class PropertyListViewState(
        val errorSource: ErrorSourceListProperty? = null,
        val isLoading: Boolean = false,
        val listProperties: List<PropertyForListDisplay>? = null
)

sealed class PropertyListResult{
    data class DisplayPropertiesResult(val properties: List<PropertyForListDisplay>?) : PropertyListResult()
}

sealed class PropertyListIntent{
    object DisplayPropertiesIntent : PropertyListIntent()
}
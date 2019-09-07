package com.openclassrooms.realestatemanager.listProperties

import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState

/**
 * Created by galou on 2019-08-12
 */
data class PropertyListViewState(
        val errorSource: ErrorSourceListProperty? = null,
        val isLoading: Boolean = false,
        val listProperties: List<PropertyWithAllData>? = null,
        val openDetails: Boolean = false
) : REMViewState

sealed class PropertyListResult : REMResult{
    data class DisplayPropertiesResult(val properties: List<PropertyWithAllData>?) : PropertyListResult()
    object OpenPropertyDetailResult : PropertyListResult()
}

sealed class PropertyListIntent : REMIntent{
    object DisplayPropertiesIntent : PropertyListIntent()
    data class OpenPropertyDetailIntent(val property: PropertyWithAllData) : PropertyListIntent()
    data class SetActionTypeIntent(val actionType: ActionTypeList) : PropertyListIntent()
}
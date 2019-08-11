package com.openclassrooms.realestatemanager.addProperty

/**
 * Created by galou on 2019-07-27
 */
enum class ErrorSourceAddProperty {
    NO_TYPE_SELECTED,
    NO_PRICE,
    NO_SURFACE,
    NO_ROOMS,
    NO_ADDRESS,
    NO_NEIGHBORHOOD,
    NO_ON_MARKET_DATE,
    NO_SOLD_DATE,
    NO_AGENT,
    INCORRECT_SOLD_DATE,
    INCORRECT_ON_MARKET_DATE,
    ERROR_FETCHING_AGENTS,
    TOO_MANY_ADDRESS,
    INCORECT_ADDRESS,
    UNKNOW_ERROR
}
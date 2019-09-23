package com.openclassrooms.realestatemanager.utils

import android.Manifest

/**
 * Created by galou on 2019-07-27
 */

// TAG DIALOG
const val PICK_DATE_TAG = "PickDateView"
const val AGENT_LIST_TAG = "AgentListView"

//URL API
const val BASE_URL_MAP_API = "https://maps.googleapis.com/maps/api/"


// API MAP VALUE
const val MAP_ICON_SIZE = 300
const val MAP_ICON_ZOOM = 14
const val MAP_ICON_MARKER_COLOR = "0xff4081"

const val ACTION_TYPE_ADD_PROPERTY = "ActionTypeAddProperty"
const val ACTION_TYPE_LIST_PROPERTY = "ActionTypeListProperty"

// DEFAULT VALUE
const val MAX_VALUE = 999999999999.99
const val MIN_VALUE = 0.0
const val IMAGE_ONLY_TYPE = "image/*"


//DATE FORMAT
const val DATE_FORMAT_WITH_TIME = "dd/MM/yyyy HH:mm:ss"
const val DATE_FORMAT_FOR_NAME = "yyyyMMdd_HHmmss"
const val DATE_FORMAT = "dd/MM/yyyy"

// KEY SHARED PREF
const val KEY_PREF_CURRENCY = "prefCurrencyKey"
const val KEY_PREF = "prefKey"
const val KEY_PREF_TEMP_PROPERTY = "tempProperty"
const val KEY_PREF_LAST_UPDATE = "lastUpdateDate"

//RC_CODE
const val RC_IMAGE_PERMS = 100
const val RC_CHOOSE_PHOTO = 101
const val PICK_DATE_DIALOG_CODE = 102
const val AGENT_LIST_DIALOG_CODE = 103
const val RC_CODE_ADD_AGENT = 104
const val RC_CODE_ADD_PROPERTY = 105
const val RC_LOCATION_PERMS = 106
const val RC_CODE_TAKE_PHOTO = 107
const val RC_CODE_DETAIL_PROPERTY = 108

// RESULT CODE
const val RESULT_SAVED_TO_DB = 500
const val RESULT_SAVED_TO_DRAFT = 501

// PERMISSIONS
const val PERMS_LOCALISATION = Manifest.permission.ACCESS_FINE_LOCATION
const val PERMS_EXT_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

// FIRESTORE COLLECTION
const val AGENT_COLLECTION = "agentCollection"
const val PROPERTY_COLLECTION = "propertyCollection"
const val PICTURE_COLLECTION = "pictureCollection"
const val ADDRESS_COLLECTION = "addressCollection"
const val AMENITY_COLLECTION = "amenityCollection"

// STORAGE PATH
const val STORAGE_PATH_AGENT_PICTURE = "images/agentsPicture/"
const val STORAGE_PATH_PROPERTY_PICTURE = "images/propertyPicture/"
const val STORAGE_PATH_PROPERTY_PICTURE_THUMBNAIL = "images/propertyPicture/thumbnail/"
const val STORAGE_PATH_MAP = "map/"

// PROVIDER
const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
const val URI_PATH = "content://$AUTHORITY/%s"

// TABLE NAME
const val PROPERTY_TABLE_NAME = "properties"
const val PICTURE_TABLE_NAME = "pictures"
const val ADDRESS_TABLE_NAME = "address"
const val AMENITY_TABLE_NAME = "amenities"
const val AGENT_TABLE_NAME = "agents"

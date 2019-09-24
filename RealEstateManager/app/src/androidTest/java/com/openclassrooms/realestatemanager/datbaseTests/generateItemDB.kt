package com.openclassrooms.realestatemanager.datbaseTests

import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty
import com.openclassrooms.realestatemanager.utils.idGenerated
import com.openclassrooms.realestatemanager.utils.todaysDate

/**
 * Created by galou on 2019-09-24
 */

fun generateAgent() =  Agent(
            idGenerated, "Galou", "Minisini", "galou@rem.com",
            "+999-803-999", "http://mypictute"
    )

fun generateSecondAgent() = Agent(
        idGenerated, "David", "Smith", "david@rem.com",
        "+999-888-9876", "http://otherPicture"
)

fun generateProperty(agentId: String) = Property(
        idGenerated, TypeProperty.HOUSE, 500000.00, 150.00, 3,
        2, 1, "new property",  todaysDate, false, null,
        agentId, true, todaysDate
)

fun generatePropertyWithNoPicture(agentId: String) = Property(
        idGenerated, TypeProperty.PENTHOUSE, 800000.00, 200.00, 4,
        3, 2, "another property",  todaysDate, false, null,
        agentId, true, todaysDate
)

fun generateAddress(propertyId: String) = Address(
        propertyId, "12 rue de nulle part", "unknown", "54342", "My Country",
        "None", -13.0987, 544.3454, "Olympic Village"
)

fun generateAmenity(propertyId: String, typeAmenity: TypeAmenity) = Amenity(idGenerated, propertyId, typeAmenity)

fun generatePicture(propertyId: String, orderNumber: Int) = Picture(
        idGenerated, "myUrl", null, "", propertyId, "newPicture", orderNumber
)



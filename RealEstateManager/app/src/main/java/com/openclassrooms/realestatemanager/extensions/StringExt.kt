package com.openclassrooms.realestatemanager.extensions

/**
 * Created by galou on 2019-07-25
 */

fun String.isCorrectName(): Boolean{
    val pattern = "[^a-z ]".toRegex(RegexOption.IGNORE_CASE)
    return !this.contains(pattern) && this.length > 3

}

fun String.isCorrectEmail(): Boolean{
    val emailPart = this.split("@")
    if(emailPart.size > 1){
        val domain = emailPart[1].split(".")
            return domain.size > 1

    }
    return false

}

fun String.isCorrectPhoneNumber(): Boolean{
    val pattern = "(\\+\\d+)?\\d+".toRegex()
    return this.matches(pattern) &&
            this.length >= 10 &&
            this.length <= 13

}
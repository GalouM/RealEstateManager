package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.extensions.isCorrectEmail
import com.openclassrooms.realestatemanager.extensions.isCorrectName
import com.openclassrooms.realestatemanager.extensions.isCorrectPhoneNumber
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by galou on 2019-07-26
 */
class StringExtUnitTest {

    @Test
    fun checkNameContainsSpecialCharacter(){
        val myString = "@estt123"
        assertFalse(myString.isCorrectName())
    }

    @Test
    fun checkNameTooShort(){
        val myString = "My"
        assertFalse(myString.isCorrectName())
    }

    @Test
    fun checkNameNoSpecialCharacter(){
        val myString = "My name"
        assertTrue(myString.isCorrectName())
    }

    @Test
    fun checkEmailWithNoAt(){
        val myString = "test"
        assertFalse(myString.isCorrectEmail())
    }

    @Test
    fun checkEmailWithNoDomainExt(){
        val myString = "test@test"
        assertFalse(myString.isCorrectEmail())
    }

    @Test
    fun checkEmailCorrect(){
        val myString = "test@test.com"
        assertTrue(myString.isCorrectEmail())
    }

    @Test
    fun checkPhoneNumberNotOnlyNumbers(){
        val number = "33344ttt"
        assertFalse(number.isCorrectPhoneNumber())
    }

    @Test
    fun checkPhoneNumberTooShort(){
        val number = "3334"
        assertFalse(number.isCorrectPhoneNumber())
    }

    @Test
    fun checkPhoneNumberTooLong(){
        val number = "33344567861234"
        assertFalse(number.isCorrectPhoneNumber())
    }

    @Test
    fun checkPhoneNumberCorrect(){
        val number = "16048030356"
        assertTrue(number.isCorrectPhoneNumber())
        val number2 = "+1608030356"
        assertTrue(number2.isCorrectPhoneNumber())
    }
}
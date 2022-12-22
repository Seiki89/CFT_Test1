package com.example.testcft

import android.provider.ContactsContract.CommonDataKinds.Phone
import java.net.URL

class LoadData(
    val scheme: String?,
    val type: String?,
    val brand: String?,
    val prepaid: Boolean,
    val number: Number,
    val country: Country?,
    val bank: Bank?
)

class Number(
    val length: Int?,
    val luhn: Boolean,
)
class Country(
    val name: String,
    val emoji: String,
    val latitude: Int,
    val longitude: Int
)
class Bank(
    val name :String,
    val url: String,
    val phone: String,
    val city: String,
)
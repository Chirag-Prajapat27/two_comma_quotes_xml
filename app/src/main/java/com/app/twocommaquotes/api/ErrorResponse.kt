package com.app.twocommaquotes.api

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    val status: Int,
    val message: String,
    @SerializedName("errors", alternate = ["result"])
    val err: HashMap<String, List<String>>
)

data class ErrorValidations(val key: String, val list: List<*>)

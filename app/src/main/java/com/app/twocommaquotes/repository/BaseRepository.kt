package com.app.twocommaquotes.repository

import com.app.twocommaquotes.api.ErrorResponse
import com.app.twocommaquotes.api.ErrorValidations
import com.app.twocommaquotes.api.Resource
import com.app.twocommaquotes.model.BaseModel
import com.app.twocommaquotes.utility.PrintLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<BaseModel<T>>,
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                handleResponse(apiCall.invoke())
            } catch (throwable: Throwable) {
                Resource.Error(throwable.message!!, null)
            }
        }
    }

    private fun <T> handleResponse(response: Response<BaseModel<T>>): Resource<T> {
        return if (response.isSuccessful) {
            response.body()?.let { it ->
                if (it.result.isEmpty()) {
                    Resource.Success(message = it.message)
                } else {
                    Resource.Success(it.result.values.iterator().next(), it.message)
                }
            }!!
        } else {
            if (response.code() == 500){
                Resource.Error("Internal server error")
            }else{
                val errorBodyString = response.errorBody()!!.string()
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                getErrorMessage(errorResponse.err, errorResponse.message)
            }
        }
    }

    private fun <T> getErrorMessage(
        errors: Map<*, List<*>>,
        message: String
    ): Resource.Error<T> {
        return try {
            run {
                if (errors.isNotEmpty()) {
                    val errorList = parseIteratorResponse(errors.entries)
                    val sb = StringBuilder()
                    for (e in errorList.indices) {
                        sb.append(errorList[e].list.joinToString("\n"))
                        if (e != (errorList.size - 1)) {
                            sb.append("\n")
                        }
                    }
                    var errorMessage = sb.toString()
                    PrintLog.logE(errorMessage)
                    if (errorMessage.isEmpty()) {
                        errorMessage = message
                    }
                    Resource.Error(errorMessage)
                } else {
                    Resource.Error(message)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message!!)
        }
    }

    private fun parseIteratorResponse(entrySet: Set<Map.Entry<*, List<*>>>): ArrayList<ErrorValidations> {
        val errorList: ArrayList<ErrorValidations> = arrayListOf()
        val iterator: Iterator<*> = entrySet.iterator()
        for (j in entrySet.indices) {
            try {
                val (key1, values) = iterator.next() as Map.Entry<*, *>
                errorList.add(ErrorValidations(key1.toString(), values as List<*>))
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
            }
        }
        return errorList
    }

    private fun parseIteratorResponse(jsonObject: JSONObject): ArrayList<ErrorValidations> {
        PrintLog.logE(jsonObject.toString())
        val errorList: ArrayList<ErrorValidations> = arrayListOf()
        val typeHashMap = object : TypeToken<Map<*, List<*>>>() {}.type
        val map: Map<*, List<*>> = Gson().fromJson(jsonObject.toString(), typeHashMap)
        val entrySet: Set<Map.Entry<*, List<*>>> = map.entries
        val iterator: Iterator<*> = entrySet.iterator()
        for (j in entrySet.indices) {
            try {
                val (key1, values) = iterator.next() as Map.Entry<*, *>
                errorList.add(ErrorValidations(key1.toString(), values as List<*>))
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
            }
        }
        return errorList
    }
}
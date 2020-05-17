package com.appttude.h_mal.easycc.data.network

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

/**
 * This abstract class extract objects from Retrofit [Response]
 * or throws IOException if object does not exist
 */
private const val TAG = "SafeApiRequest"
abstract class SafeApiRequest {

    suspend fun <T : Any> responseUnwrap(
            call: suspend () -> Response<T>
    ): T {
        val response = call.invoke()

        if (response.isSuccessful) {
            // return the object within the response body
            return response.body()!!
        } else {
            // the response was unsuccessful
            // throw IOException error
            throw IOException(errorMessage(response))
        }
    }

    private fun <T> errorMessage(errorResponse: Response<T>): String {
        val errorBody = errorResponse.errorBody()?.string()
        val errorCode = "Error Code: ${errorResponse.code()}"
        val errorMessageString = errorBody.getError()

        //build a log message to log in console
        val log = if (errorMessageString.isNullOrEmpty()){
            errorCode
        }else{
            StringBuilder()
                    .append(errorCode)
                    .append("\n")
                    .append(errorMessageString)
                    .toString()
        }
        Log.e("Api Response Error", log)

        //return error message
        //if null return error code
        return errorMessageString ?: errorCode
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun String?.getError(): String? {
        this?.let {
            try {
                //convert response to JSON
                //extract ["error"] from error body
                return JSONObject(it).getString("error")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
            }
        }
        return null
    }

}
package com.moviedrinkers.moviedrinkers.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class ApiException(
    val message: String,
    val code: Int
): Parcelable {
    companion object {
        fun fromJson(jsonObject: JSONObject): ApiException {
            val message = jsonObject.optString("message", "")
            val code = jsonObject.optInt("code", 1)
            return ApiException(
                message,
                code
            )
        }
    }
}
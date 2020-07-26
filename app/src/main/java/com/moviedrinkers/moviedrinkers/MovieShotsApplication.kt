package com.moviedrinkers.moviedrinkers

import android.app.Application
import android.content.Context
import com.moviedrinkers.moviedrinkers.data.Api
import com.moviedrinkers.moviedrinkers.data.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MovieShotsApplication: Application() {

    companion object {
        const val PREFERENCES_FILE_KEY = "MOVIE_SHOTS_PREFERENCES"
        const val APPLICATION_KEY = "MOVIE_SHOTS_APPLICATION_KEY"
    }

    private var serviceBuilder: Retrofit.Builder? = null
    private var httpClient: OkHttpClient.Builder? = null
    private var retrofit: Retrofit? = null

    private fun configureRetrofit(apiKey: String) {
        this.httpClient = OkHttpClient.Builder()
        this.serviceBuilder = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val interceptor = Interceptor { chain ->
            val original: Request = chain.request()

            // Modify the url: append a new query parameter - user API key
            val originalHttpUrl: HttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build()

            // Request customization: add request headers
            val requestBuilder: Request.Builder = original.newBuilder().url(url)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        this.httpClient!!.addInterceptor(interceptor)
        this.retrofit = this.serviceBuilder!!.client(this.httpClient!!.build()).build()
    }

    override fun onCreate() {
        super.onCreate()

        // Read application key from shared preferences
        val apiKey = this.getApiKey()
        // Construct a new retrofit instance with specified api key
        this.configureRetrofit(apiKey)
    }

    fun <S> createService(serviceClass: Class<S>): S {
        return this.retrofit!!.create(serviceClass)
    }

    private fun getApiKey(): String {
        var applicationKey = this.getApplicatonKey()
        if (applicationKey == null) {
            // Application key has not yet been created, do it now
            applicationKey = this.generateApplicationKey()
            this.saveApplicationKey(applicationKey)
        }
        return applicationKey
    }

    fun getApi(): Api {
        var applicationKey = this.getApplicatonKey()
        if (applicationKey == null) {
            // Application key has not yet been created, do it now
            applicationKey = this.generateApplicationKey()
            this.saveApplicationKey(applicationKey)
        }

        // Here, we have either read the application key or generated and saved a new one.
        // We can now create a new Api object with this key
        return Api(applicationKey)
    }

    private fun getApplicatonKey(): String? {
        val preferences = this.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return preferences.getString(APPLICATION_KEY, null)
    }

    private fun saveApplicationKey(key: String) {
        val preferences = this.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with (preferences.edit()) {
            putString(APPLICATION_KEY, key)
            commit()
        }
    }

    private fun generateApplicationKey(): String {
        return UUID.randomUUID().toString()
    }

}
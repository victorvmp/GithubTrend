package com.loc8r.remote

import com.google.gson.Gson
import com.loc8r.remote.interfaces.GithubTrendService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object GithubTrendServiceFactory {

    // function helps enable logging
    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if(isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    // function creates an OkHTTPClient
    private fun makeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor)
            : OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(120,TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
    }

    // A private function that returns the Github service instance
    private fun makeGithubTrendService(okHttpClient: OkHttpClient, gson: Gson)
            : GithubTrendService{
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        return retrofit.create(GithubTrendService::class.java)
    }

    // The external function that returns the Github service
    open fun makeGithubTrendService(isDebug: Boolean): GithubTrendService {
        val okHttpClient = makeOkHttpClient(
                makeLoggingInterceptor(isDebug))
        return makeGithubTrendService(okHttpClient, Gson())
    }
}
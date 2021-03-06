package com.wotin.geniustest.retrofitInterface.user

import com.wotin.geniustest.customClass.SignInAndSignUpCustomClass
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitSignInAndSignUp {

    @FormUrlEncoded
    @POST("genius_test/sign_in/")
    fun signIn(
        @Field("id") id : String,
        @Field("password") password : String
    ): Call<SignInAndSignUpCustomClass>

    @FormUrlEncoded
    @POST("genius_test/sign_up/")
    fun signUp(
        @Field("name") name : String,
        @Field("id") id : String,
        @Field("password") password : String,
        @Field("UniqueId") UniqueId : String
    ): Call<SignInAndSignUpCustomClass>

}
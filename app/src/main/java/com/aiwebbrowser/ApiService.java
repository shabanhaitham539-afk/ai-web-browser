package com.aiwebbrowser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import com.google.gson.JsonObject;

public interface ApiService {
    
    // OpenRouter API endpoints
    @GET("api/v1/models")
    Call<JsonObject> getOpenRouterModels(@Header("Authorization") String authorization);
    
    @POST("api/v1/chat/completions")
    Call<JsonObject> translateWithOpenRouter(
        @Header("Authorization") String authorization,
        @Body JsonObject requestBody
    );
    
    // Mistral API endpoints
    @GET("v1/models")
    Call<JsonObject> getMistralModels(@Header("Authorization") String authorization);
    
    @POST("v1/chat/completions")
    Call<JsonObject> translateWithMistral(
        @Header("Authorization") String authorization,
        @Body JsonObject requestBody
    );
}
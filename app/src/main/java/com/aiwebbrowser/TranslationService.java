package com.aiwebbrowser;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslationService {
    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/";
    private static final String MISTRAL_BASE_URL = "https://api.mistral.ai/";
    
    private ApiService openRouterService;
    private ApiService mistralService;
    private SharedPreferences prefs;
    
    public interface TranslationCallback {
        void onSuccess(String translation);
        void onError(String error);
    }
    
    public TranslationService(Context context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        
        Retrofit openRouterRetrofit = new Retrofit.Builder()
                .baseUrl(OPENROUTER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        Retrofit mistralRetrofit = new Retrofit.Builder()
                .baseUrl(MISTRAL_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        openRouterService = openRouterRetrofit.create(ApiService.class);
        mistralService = mistralRetrofit.create(ApiService.class);
    }
    
    public void translateText(String text, String targetLanguage, TranslationCallback callback) {
        String selectedModel = prefs.getString("selected_model", "");
        String selectedProvider = prefs.getString("selected_provider", "");
        
        if (selectedModel.isEmpty()) {
            callback.onError("No model selected");
            return;
        }
        
        JsonObject requestBody = createTranslationRequest(text, targetLanguage, selectedModel);
        
        if ("openrouter".equals(selectedProvider)) {
            translateWithOpenRouter(requestBody, callback);
        } else if ("mistral".equals(selectedProvider)) {
            translateWithMistral(requestBody, callback);
        } else {
            callback.onError("Unknown provider: " + selectedProvider);
        }
    }
    
    private JsonObject createTranslationRequest(String text, String targetLanguage, String model) {
        JsonObject request = new JsonObject();
        request.addProperty("model", model);
        
        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", 
            "Translate the following text to " + targetLanguage + ": \"" + text + "\"");
        messages.add(message);
        
        request.add("messages", messages);
        request.addProperty("max_tokens", 1000);
        request.addProperty("temperature", 0.3);
        
        return request;
    }
    
    private void translateWithOpenRouter(JsonObject requestBody, TranslationCallback callback) {
        String apiKey = prefs.getString("openrouter_api_key", "");
        if (apiKey.isEmpty()) {
            callback.onError("OpenRouter API key not set");
            return;
        }
        
        Call<JsonObject> call = openRouterService.translateWithOpenRouter("Bearer " + apiKey, requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject responseBody = response.body();
                        String translation = responseBody.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
                        callback.onSuccess(translation);
                    } catch (Exception e) {
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onError("API request failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void translateWithMistral(JsonObject requestBody, TranslationCallback callback) {
        String apiKey = prefs.getString("mistral_api_key", "");
        if (apiKey.isEmpty()) {
            callback.onError("Mistral API key not set");
            return;
        }
        
        Call<JsonObject> call = mistralService.translateWithMistral("Bearer " + apiKey, requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject responseBody = response.body();
                        String translation = responseBody.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
                        callback.onSuccess(translation);
                    } catch (Exception e) {
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onError("API request failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
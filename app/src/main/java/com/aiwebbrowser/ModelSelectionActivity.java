package com.aiwebbrowser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class ModelSelectionActivity extends AppCompatActivity {
    private TextInputEditText openRouterApiKey, mistralApiKey;
    private RecyclerView modelsRecyclerView;
    private ModelAdapter modelAdapter;
    private List<Model> models;
    private SharedPreferences prefs;
    private ApiService openRouterService, mistralService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_selection);
        
        initViews();
        setupServices();
        loadSavedData();
        setupListeners();
    }
    
    private void initViews() {
        openRouterApiKey = findViewById(R.id.openrouter_api_key);
        mistralApiKey = findViewById(R.id.mistral_api_key);
        modelsRecyclerView = findViewById(R.id.models_recycler_view);
        
        models = new ArrayList<>();
        modelAdapter = new ModelAdapter(models);
        modelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        modelsRecyclerView.setAdapter(modelAdapter);
        
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
    }
    
    private void setupServices() {
        Retrofit openRouterRetrofit = new Retrofit.Builder()
                .baseUrl("https://openrouter.ai/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        Retrofit mistralRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.mistral.ai/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        openRouterService = openRouterRetrofit.create(ApiService.class);
        mistralService = mistralRetrofit.create(ApiService.class);
    }
    
    private void loadSavedData() {
        openRouterApiKey.setText(prefs.getString("openrouter_api_key", ""));
        mistralApiKey.setText(prefs.getString("mistral_api_key", ""));
        
        // Load models if API keys are available
        if (!prefs.getString("openrouter_api_key", "").isEmpty() || 
            !prefs.getString("mistral_api_key", "").isEmpty()) {
            refreshModels();
        }
    }
    
    private void setupListeners() {
        findViewById(R.id.btn_refresh_models).setOnClickListener(v -> refreshModels());
        
        findViewById(R.id.btn_save).setOnClickListener(v -> saveSettings());
        
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }
    
    private void refreshModels() {
        models.clear();
        modelAdapter.notifyDataSetChanged();
        
        String openRouterKey = openRouterApiKey.getText().toString().trim();
        String mistralKey = mistralApiKey.getText().toString().trim();
        
        if (!openRouterKey.isEmpty()) {
            fetchOpenRouterModels(openRouterKey);
        }
        
        if (!mistralKey.isEmpty()) {
            fetchMistralModels(mistralKey);
        }
        
        if (openRouterKey.isEmpty() && mistralKey.isEmpty()) {
            Toast.makeText(this, "Please enter at least one API key", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void fetchOpenRouterModels(String apiKey) {
        Call<JsonObject> call = openRouterService.getOpenRouterModels("Bearer " + apiKey);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonArray data = response.body().getAsJsonArray("data");
                        for (JsonElement element : data) {
                            JsonObject modelObj = element.getAsJsonObject();
                            String id = modelObj.get("id").getAsString();
                            String name = modelObj.has("name") ? modelObj.get("name").getAsString() : id;
                            models.add(new Model(id, name, "openrouter"));
                        }
                        runOnUiThread(() -> modelAdapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(ModelSelectionActivity.this, 
                            "Error parsing OpenRouter models", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ModelSelectionActivity.this, 
                        "Failed to fetch OpenRouter models", Toast.LENGTH_SHORT).show());
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(ModelSelectionActivity.this, 
                    "OpenRouter API error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void fetchMistralModels(String apiKey) {
        Call<JsonObject> call = mistralService.getMistralModels("Bearer " + apiKey);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonArray data = response.body().getAsJsonArray("data");
                        for (JsonElement element : data) {
                            JsonObject modelObj = element.getAsJsonObject();
                            String id = modelObj.get("id").getAsString();
                            String name = modelObj.has("name") ? modelObj.get("name").getAsString() : id;
                            models.add(new Model(id, name, "mistral"));
                        }
                        runOnUiThread(() -> modelAdapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(ModelSelectionActivity.this, 
                            "Error parsing Mistral models", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ModelSelectionActivity.this, 
                        "Failed to fetch Mistral models", Toast.LENGTH_SHORT).show());
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(ModelSelectionActivity.this, 
                    "Mistral API error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("openrouter_api_key", openRouterApiKey.getText().toString().trim());
        editor.putString("mistral_api_key", mistralApiKey.getText().toString().trim());
        
        // Save selected model
        for (Model model : models) {
            if (model.isSelected()) {
                editor.putString("selected_model", model.getId());
                editor.putString("selected_provider", model.getProvider());
                break;
            }
        }
        
        editor.apply();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {
        private List<Model> models;
        private int selectedPosition = -1;
        
        public ModelAdapter(List<Model> models) {
            this.models = models;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_model, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Model model = models.get(position);
            holder.modelName.setText(model.getName());
            holder.modelProvider.setText(model.getProvider().toUpperCase());
            holder.radioButton.setChecked(position == selectedPosition);
            
            holder.itemView.setOnClickListener(v -> {
                selectedPosition = position;
                // Clear all selections
                for (Model m : models) {
                    m.setSelected(false);
                }
                // Set current selection
                model.setSelected(true);
                notifyDataSetChanged();
            });
        }
        
        @Override
        public int getItemCount() {
            return models.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton radioButton;
            TextView modelName, modelProvider;
            
            ViewHolder(View itemView) {
                super(itemView);
                radioButton = itemView.findViewById(R.id.radio_button);
                modelName = itemView.findViewById(R.id.model_name);
                modelProvider = itemView.findViewById(R.id.model_provider);
            }
        }
    }
}
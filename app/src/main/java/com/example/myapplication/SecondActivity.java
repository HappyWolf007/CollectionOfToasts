package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {

    private LinearLayout toastContainer;
    private Map<String, String> categoryToasts; // Сохраняем оригинальные данные
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme(); // Применяем сохранённую тему перед созданием View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        loadFavorites();

        toastContainer = findViewById(R.id.container_toasts);
        SearchView searchView = findViewById(R.id.search_view);

        category = getIntent().getStringExtra("category");
        Map<String, Map<String, String>> toasts = MainActivity.toasts;
        categoryToasts = toasts.get(category);

        TextView title = findViewById(R.id.category_title);
        title.setText(category);

        displayToasts(categoryToasts);

        // Обработка ввода в SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Обработка отправки не требуется
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterToasts(newText);
                return true;
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }
    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        String theme = prefs.getString("current_theme", "light");

        if ("light".equals(theme)) {
            setTheme(R.style.AppTheme_Light);
        } else if ("dark".equals(theme)) {
            setTheme(R.style.AppTheme_Dark);
        }
    }
    private void displayToasts(Map<String, String> filteredToasts) {
        toastContainer.removeAllViews();

        SharedPreferences prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        String theme = prefs.getString("current_theme", "light");

        for (String toastTitle : filteredToasts.keySet()) {
            LinearLayout titleContainer = new LinearLayout(this);
            titleContainer.setOrientation(LinearLayout.HORIZONTAL);
            titleContainer.setPadding(16, 16, 16, 16);
            titleContainer.setBackgroundResource(R.drawable.toast_title_background);


            LinearLayout.LayoutParams titleContainerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleContainerParams.setMargins(0, 16, 0, 0);
            titleContainer.setLayoutParams(titleContainerParams);

            TextView toastTitleView = new TextView(this);
            toastTitleView.setText(toastTitle);
            toastTitleView.setTextSize(18);
            toastTitleView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            ImageView heartIcon = new ImageView(this);

            heartIcon.setImageResource(
                    MainActivity.favorites.containsKey(category) &&
                            MainActivity.favorites.get(category).stream()
                                    .anyMatch(pair -> pair.first.equals(toastTitle) && pair.second.equals(filteredToasts.get(toastTitle)))
                            ? R.drawable.heart_fill_full
                            : R.drawable.heart_border
            );

            heartIcon.setPadding(16, 0, 0, 0);
            heartIcon.setOnClickListener(v -> {
                String toastContent = filteredToasts.get(toastTitle);

                if (MainActivity.favorites.containsKey(category)) {
                    List<Pair<String, String>> categoryFavorites = MainActivity.favorites.get(category);

                    boolean exists = false;
                    for (Pair<String, String> pair : categoryFavorites) {
                        if (pair.first.equals(toastTitle) && pair.second.equals(toastContent)) {
                            exists = true;
                            categoryFavorites.remove(pair);
                            heartIcon.setImageResource(R.drawable.heart_border);
                            Toast.makeText(this, "Тост удалён из избранного", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    if (!exists) {
                        categoryFavorites.add(new Pair<>(toastTitle, toastContent));
                        heartIcon.setImageResource(R.drawable.heart_fill_full);
                        Toast.makeText(this, "Тост добавлен в избранное", Toast.LENGTH_SHORT).show();
                    }

                    if (categoryFavorites.isEmpty()) {
                        MainActivity.favorites.remove(category);
                    }
                } else {
                    List<Pair<String, String>> newFavorites = new ArrayList<>();
                    newFavorites.add(new Pair<>(toastTitle, toastContent));
                    MainActivity.favorites.put(category, newFavorites);
                    heartIcon.setImageResource(R.drawable.heart_fill_full);
                    Toast.makeText(this, "Тост добавлен в избранное", Toast.LENGTH_SHORT).show();
                }

                saveFavorites();
            });

            ImageView copyIcon = new ImageView(this);
            copyIcon.setImageResource(R.drawable.ic_copy); // Копирование текста
            copyIcon.setPadding(16, 0, 0, 0);

            copyIcon.setOnClickListener(v -> {
                String toastContent = filteredToasts.get(toastTitle);

                // Получаем ClipboardManager
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Toast Text", toastContent);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(this, "Тост скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
            });

            titleContainer.addView(toastTitleView);
            titleContainer.addView(heartIcon);
            titleContainer.addView(copyIcon);
            titleContainer.setElevation(10);

            TextView toastText = new TextView(this);
            toastText.setText(filteredToasts.get(toastTitle));
            toastText.setTextSize(18);
            toastText.setPadding(16, 16, 16, 16);


            //toastText.setBackgroundResource(R.drawable.toast_text_background);


            LinearLayout.LayoutParams toastTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            toastTextParams.setMargins(0, 0, 0, 0);
            toastText.setLayoutParams(toastTextParams);

            titleContainer.setOnClickListener(v -> {
                if (toastText.getVisibility() == View.GONE) {
                    toastText.setVisibility(View.VISIBLE);
                } else {
                    toastText.setVisibility(View.GONE);
                }
            });

            toastContainer.addView(titleContainer);
            toastContainer.addView(toastText);
        }
    }

    private void filterToasts(String query) {
        if (query == null || query.isEmpty()) {
            displayToasts(categoryToasts);
            return;
        }

        Map<String, String> filteredToasts = new HashMap<>();
        for (Map.Entry<String, String> entry : categoryToasts.entrySet()) {
            if (entry.getKey().toLowerCase().contains(query.toLowerCase()) ||
                    entry.getValue().toLowerCase().contains(query.toLowerCase())) {
                filteredToasts.put(entry.getKey(), entry.getValue());
            }
        }

        displayToasts(filteredToasts);
    }

    private void saveFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(MainActivity.favorites);
        editor.putString("favorites", json);
        editor.apply();

        Log.d("Favorites", "Saving favorites: " + MainActivity.favorites);
    }

    public void loadFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("favorites", null);

        if (json != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, List<Pair<String, String>>>>() {}.getType();
                MainActivity.favorites = gson.fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.favorites = new HashMap<>();
            }
        } else {
            MainActivity.favorites = new HashMap<>();
        }
    }
}
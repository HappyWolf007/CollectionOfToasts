package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavouritesFragment extends Fragment {

    private LinearLayout favoritesContainer;
    private androidx.appcompat.widget.SearchView searchView;
    private Map<String, List<Pair<String, String>>> originalFavorites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesContainer = rootView.findViewById(R.id.favorites_container);
        searchView = rootView.findViewById(R.id.search_view);

        Button backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {

                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_home); // Устанавливаем "Главная" как выбранный элемент
                }

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        originalFavorites = new HashMap<>(MainActivity.favorites);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFavorites(newText);
                return true;
            }
        });

        displayFavorites(originalFavorites);

        return rootView;
    }

    private void displayFavorites(Map<String, List<Pair<String, String>>> favorites) {
        favoritesContainer.removeAllViews();

        if (favorites.isEmpty()) {
            TextView emptyMessage = new TextView(getContext());
            emptyMessage.setText("У вас пока нет избранных тостов.");
            emptyMessage.setTextSize(18);
            emptyMessage.setPadding(16, 16, 16, 16);
            emptyMessage.setGravity(Gravity.CENTER);
            favoritesContainer.addView(emptyMessage);
            return;
        }

        for (Map.Entry<String, List<Pair<String, String>>> entry : favorites.entrySet()) {
            String category = entry.getKey();
            List<Pair<String, String>> favoriteToasts = entry.getValue();

            TextView categoryTitle = new TextView(getContext());
            categoryTitle.setText(category);
            categoryTitle.setTextSize(18);
            categoryTitle.setPadding(16, 16, 16, 16);
            categoryTitle.setTypeface(Typeface.DEFAULT_BOLD);


            favoritesContainer.addView(categoryTitle);

            for (Pair<String, String> toast : favoriteToasts) {
                LinearLayout toastContainer = new LinearLayout(getContext());
                toastContainer.setOrientation(LinearLayout.HORIZONTAL);
                toastContainer.setPadding(16, 16, 16, 16);
                toastContainer.setBackgroundResource(R.drawable.toast_title_background);

                LinearLayout.LayoutParams toastContainerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                toastContainerParams.setMargins(0, 16, 0, 0);
                toastContainer.setLayoutParams(toastContainerParams);

                // Заголовок тоста
                TextView toastTitleView = new TextView(getContext());
                toastTitleView.setText(toast.first);
                toastTitleView.setTextSize(18);
                toastTitleView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                // Текст тоста
                TextView toastTextView = new TextView(getContext());
                toastTextView.setText(toast.second);
                toastTextView.setTextSize(18);
                toastTextView.setPadding(16, 16, 16, 16);
                //toastTextView.setVisibility(View.GONE); // Скрываем текст по умолчанию
                //toastTextView.setBackgroundResource(R.drawable.toast_text_background);

                LinearLayout.LayoutParams toastTextParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                toastTextParams.setMargins(0, 0, 0, 0);
                toastTextView.setLayoutParams(toastTextParams);

                // Обработка клика на контейнере для показа/скрытия текста
                toastContainer.setOnClickListener(v -> {
                    if (toastTextView.getVisibility() == View.GONE) {
                        toastTextView.setVisibility(View.VISIBLE);
                    } else {
                        toastTextView.setVisibility(View.GONE);
                    }
                });

                // Иконка удаления
                ImageView deleteIcon = new ImageView(getContext());
                deleteIcon.setImageResource(R.drawable.ic_delete);
                deleteIcon.setPadding(16, 0, 0, 0);

                // Обработка клика на иконке удаления
                deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(category, toast));

                // Иконка копирования
                ImageView copyIcon = new ImageView(getContext());
                copyIcon.setImageResource(R.drawable.ic_copy); // Используйте свой ресурс для иконки копирования
                copyIcon.setPadding(16, 0, 0, 0);

                // Обработка клика на иконке копирования
                copyIcon.setOnClickListener(v -> {
                    String toastContent = toast.second;

                    // Получаем ClipboardManager
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Toast Text", toastContent);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getContext(), "Тост скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
                });

                copyIcon.setPadding(16, 0, 0, 0);

                // Добавляем заголовок и иконку в контейнер
                toastContainer.addView(toastTitleView);
                toastContainer.addView(deleteIcon);
                toastContainer.addView(copyIcon);

                // Добавляем контейнер и текст в общий контейнер
                favoritesContainer.addView(toastContainer);
                favoritesContainer.addView(toastTextView);
            }
        }
    }

    private void showDeleteConfirmationDialog(String category, Pair<String, String> toast) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удалить тост")
                .setMessage("Вы точно хотите удалить этот тост из избранного?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Удаляем тост из списка
                    List<Pair<String, String>> toasts = MainActivity.favorites.get(category);
                    if (toasts != null) {
                        toasts.remove(toast);
                        if (toasts.isEmpty()) {
                            MainActivity.favorites.remove(category);
                        }
                    }

                    // Сохраняем изменения в SharedPreferences
                    saveFavorites();

                    // Обновляем отображение
                    displayFavorites(MainActivity.favorites);

                    Toast.makeText(getContext(), "Тост удалён", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Настраиваем стиль текста кнопок
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        });

        dialog.show();
    }
    private void saveFavorites() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Сериализуем данные в JSON
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(MainActivity.favorites);
        editor.putString("favorites", jsonFavorites);
        editor.apply();
    }


    private void filterFavorites(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayFavorites(originalFavorites);
            return;
        }

        Map<String, List<Pair<String, String>>> filteredFavorites = new HashMap<>();
        String queryLower = query.trim().toLowerCase();

        for (Map.Entry<String, List<Pair<String, String>>> entry : originalFavorites.entrySet()) {
            String category = entry.getKey();
            List<Pair<String, String>> favoriteToasts = entry.getValue();
            List<Pair<String, String>> filteredToasts = new ArrayList<>();

            for (Pair<String, String> toast : favoriteToasts) {
                String toastTitle = toast.first != null ? toast.first.trim().toLowerCase() : "";
                String toastText = toast.second != null ? toast.second.trim().toLowerCase() : "";

                if (toastTitle.contains(queryLower) || toastText.contains(queryLower)) {
                    filteredToasts.add(toast);
                }
            }

            if (!filteredToasts.isEmpty()) {
                filteredFavorites.put(category, filteredToasts);
            }
        }

        displayFavorites(filteredFavorites);
    }
}
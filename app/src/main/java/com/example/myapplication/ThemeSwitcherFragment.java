package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class ThemeSwitcherFragment extends Fragment {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String PREF_THEME_KEY = "current_theme";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Загружаем разметку
        View rootView = inflater.inflate(R.layout.fragment_theme_switcher, container, false);

        // Настраиваем кнопку "Назад"
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

        // Применяем сохранённую тему (если необходимо)
        applyTheme(null, false);

        // Возвращаем корректно настроенный rootView
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup themeRadioGroup = view.findViewById(R.id.theme_radio_group);
        Button applyThemeButton = view.findViewById(R.id.apply_theme_button);

        // Синхронизируем состояние радиокнопок с текущей темой
        String currentTheme = getCurrentTheme();
        if ("dark".equals(currentTheme)) {
            themeRadioGroup.check(R.id.radio_dark); // Установим флажок на темную тему
        } else if ("light".equals(currentTheme)) {
            themeRadioGroup.check(R.id.radio_light); // Установим флажок на светлую тему
        }

        applyThemeButton.setOnClickListener(v -> {
            int selectedId = themeRadioGroup.getCheckedRadioButtonId();
            String newTheme = null;

            // Проверяем, какая кнопка выбрана
            if (selectedId == R.id.radio_light) {
                newTheme = "light";
            } else if (selectedId == R.id.radio_dark) {
                newTheme = "dark";
            }

            // Если тема изменилась, сохраняем её и применяем
            if (newTheme != null && !newTheme.equals(getCurrentTheme())) {
                saveTheme(newTheme);
                applyTheme(newTheme, true); // Применяем тему и перезапускаем активность
            }
        });

    }

    private String getCurrentTheme() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_THEME_KEY, "light"); // Светлая тема по умолчанию
    }

    private void saveTheme(String theme) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_THEME_KEY, theme).apply();
    }

    private void applyTheme(@Nullable String theme, boolean recreate) {
        if (theme != null) {
            ThemeUtils.saveTheme(requireContext(), theme);
        }
        ThemeUtils.applySavedTheme(requireActivity());

        if (recreate) {
            requireActivity().recreate();
        }
    }
}
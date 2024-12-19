package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инфлейтит макет для этого фрагмента
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получение ссылок на категории
        LinearLayout birthdayCategory = view.findViewById(R.id.birthday_category);
        LinearLayout weddingCategory = view.findViewById(R.id.wedding_category);
        LinearLayout jubileeCategory = view.findViewById(R.id.jubilee_category);
        LinearLayout newYearCategory = view.findViewById(R.id.new_year_category);

        // Установка обработчиков нажатий на категории
        birthdayCategory.setOnClickListener(v -> openCategory("День рождения"));
        weddingCategory.setOnClickListener(v -> openCategory("Свадьба"));
        jubileeCategory.setOnClickListener(v -> openCategory("Юбилей"));
        newYearCategory.setOnClickListener(v -> openCategory("Новый год"));
    }

    private void openCategory(String category) {
        // Создаём интент для перехода на SecondActivity
        Intent intent = new Intent(getActivity(), SecondActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
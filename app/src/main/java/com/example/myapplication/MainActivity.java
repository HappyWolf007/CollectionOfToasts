package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

 class Pair<K, V> {
    public final K first;
    public final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }
}
class ThemeUtils {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String PREF_THEME_KEY = "current_theme";

    public static void applySavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String theme = prefs.getString(PREF_THEME_KEY, "light"); // Светлая тема по умолчанию

        if ("light".equals(theme)) {
            context.setTheme(R.style.AppTheme_Light);

        } else if ("dark".equals(theme)) {
            context.setTheme(R.style.AppTheme_Dark);

        }
    }

    public static void saveTheme(Context context, String theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_THEME_KEY, theme).apply();
    }

    public static String getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_THEME_KEY, "light");
    }
}
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Map<String, Map<String, String>> toasts = new HashMap<>();
    public static Map<String, List<Pair<String, String>>> favorites = new HashMap<>();
    private DrawerLayout drawerLayout;
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // Загружаем избранное при каждом возврате в MainActivity
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Применяем тему, сохранённую в настройках
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Обновление цветов нажатия
        //updateNavigationViewColors(navigationView);


        // Инициализация DrawerLayout и NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);


        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Применение цвета для Toolbar
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        toolbar.setBackgroundColor(colorPrimary);

        // Применение цвета для NavigationView

        theme.resolveAttribute(android.R.attr.textColor, typedValue, true);
        int textColor = typedValue.data;
        navigationView.setItemTextColor(ColorStateList.valueOf(textColor));
        navigationView.setItemIconTintList(ColorStateList.valueOf(textColor));




// Очистка данных
        //SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();


        // Инициализация DrawerLayout и NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        // Настройка "бургер-меню"
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Заполнение данных тостов
        toasts.put("День рождения", Map.of(
                "Tost №1", "Поздравляем с Днем рождения! Пусть сбудутся мечты! Be Happy",
                "Так поднимем же бокал!", "Так поднимем бокал за именинника, на праздник которого собрались такие исключительные люди, как мы!",
                "Был такой анекдот...", "Был такой анекдот в 70-е годы. Мужчина заходит в автомагазин и говорит:\n" +
                        "- Я хочу записаться на автомобиль.\n" +
                        "- Без проблем, - отвечает продавец. – Сейчас мы записываем на 10 лет вперед, причем день в день.\n" +
                        "- А вы не могли бы уточнить, на какую половину дня запись, - спрашивает он.\n" +
                        "- А какая вам разница? Это же случится через 10 лет!\n" +
                        "- Да понимаете, дело в том, что на первую половину я... уже вызвал сантехника!\n" +
                        "Мое пожелание имениннику – обязательно дожить до ста лет! И я заранее сообщаю, что собираюсь быть на этом юбилее, и что вечер этого еще далекого дня у меня абсолютно свободен! За именинника!",
                "Готовка", "Один из известных американских поэтов прошлого века как-то написал: \"Очень досадно, когда жена умеет готовить, но не готовит; однако еще хуже, если жена готовить не умеет, но хочет\".\n" +
                        "Я бы добавил к этому, что самое замечательное – когда женщина любит и умеет готовить, и готовит так аппетитно и вкус но, как наша сегодняшняя именинница!!! А потому предлагаю всем выпить за ее здоровье и умение!",
                "Про Ленина", "Как-то дети попросили Крупскую рассказать о Ленине.\n" +
                        "- Вы хотите знать, каким он был? – сказала она. - Он был очень добрым... Вот был случай: пришли к нам в усадьбу дети, а Ленин в это время отдыхал на скамейке.\n" +
                        "- Владимир Ильич, поиграй с нами! Владимир Ильич, поиграй с нами! - загорланили ребята. А он как на них закричит:\n" +
                        "- Пошли вы к черту! - а у самого глаза такие... добрые-добрые!!!\n" +
                        "Скажу сразу: наша именинница, в отличие от Владимира Ильича, действительно любит детей. Искренне любит она и своих родственников, и многочисленных друзей! И потому, что она  изумительно добрый, у нее и глаза у нее такие добрые-добрые!!! За именинницу!",
                "Анекдот про невыносимую жену", "Врач внимательно разглядывает рентгеновский снимок пациента.\n" +
                        "- Сколько вам лет? - спрашивает он его.\n" +
                        "- Скоро будет сорок.\n" +
                        "- Не будет... Не будет...\n" +
                        "Я, конечно, не врач, но, зная именинника, уверен, что будет ему и сорок, и шестьдесят, и до ста доживет! Вот этого я ему от всей души и желаю!"
        ));
        toasts.put("Свадьба", Map.of(
                "Тост №1", "Поздравляем молодых! Пусть любовь цветет вечно!",
                "Тост №2", "Нередко нам приходится слышать такое суждение: не то чудо, что расходятся, то чудо – что живут. Пожелаем новой семье, чтобы о них такого никогда не сказали. Чтобы жили всем на диво! А еще лучше – на диво дивное! За это стоит осушить бокалы!",
                "Короткий тост", "У меня короткий тост:\n" +
                        "Пусть на свадьбе слово: \"Горько!\"\n" +
                        "Очень громко прозвучит!\n" +
                        "Давайте все вместе и громко – Горько! Чтоб слышала вся вселенная – Горько!",
                "Помните только хорошие качества", "Помните знаменитый метод Франклина для принятия решений? Он частично применим и в семейной жизни." +
                        " Муж и жена садятся за стол, перед каждым - лист бумаги, который делится чертой на две вертикальные половинки. " +
                        "Каждый заполняет свой лист, записывая слева хорошие качества супруга, а справа – те, что ему или ей не нравятся." +
                        "Дальше – в отличие от метода Франклина – правые половинки листов склеиваются и сжигаются. А левые половинки стоит прочитывать каждый день обоим. Это поможет укрепить брак." +
                        "Засим – предлагаю поднять и осушить наши бокалы за все те хорошие качества, которыми обладают наши молодожены!",
                "Муж - голова, а жена - сердце", "Считается, что хороший брак – тот, где муж – это голова, а жена – сердце, без которого голове не обойтись." +
                        " За то, чтобы наших сегодняшних молодоженов ни сердечные, ни головные боли не беспокоили в жизни!",
                "За поцелуй!", "Друзья мои! Когда же, как не на свадьбе, стоит выпить за поцелуй!" +
                        " Ведь это единственное, что смог придумать мужчина, когда он искал способ закрыть рот женщине!",
                "Тёще и тестю", "Я хочу поднять тост за родителей невесты. Они выпестовали свою дочку, умницу и красавицу, веселую и добрую," +
                        " на которую даже смотреть – сплошное удовольствие. Недаром молодой муж так добивался ее. " +
                        "И хотя некоторые полагают, что тёща – острый шип в цветах супружества, горькое лекарство в сладком чае – не верьте." +
                        " Умная теща сумеет стать зятю второй матерью, которая принесет в семью дочери доброту, ласку, совет и помощь." +
                        " Будьте здоровы, новоиспеченные теща и тесть"
        ));
        toasts.put("Юбилей", Map.of(
                "Поздравление-стих", "Прошу прочесть немного строк,\n" +
                        "Они для вас и все - с душою,\n" +
                        "Пускай проснётся в вас любовь,\n" +
                        "Пусть жизнь вся будет золотою,\n" +
                        "\n" +
                        "Пусть этот яркий Юбилей,\n" +
                        "Вам очень многое подарит,\n" +
                        "И только счастье вам доставит,\n" +
                        "Чтобы жилось вам веселей!",
                "Лучший тост на юбилей", "Скажу я лучший тост на юбилей,\n" +
                        "Возьми бокал с вином потяжелей!\n" +
                        "А можешь водки выпить не спеша,\n" +
                        "Ведь этот день она ведь тоже хороша!\n" +
                        "Живи среди бушующих страстей,\n" +
                        "Желаю много ярких и счастливых дней!\n" +
                        "Подарки принимай от всех своих друзей,\n" +
                        "И о прожитых днях ты не жалей!\n" +
                        "Желаю я богатства, доброты,\n" +
                        "Жить радостно, в любви, без суеты!\n" +
                        "Желаю много счастья и тепла,\n" +
                        "И пусть ведет тебя прекрасная звезда!\n" +
                        "Так выпьем же друзья напитки мы,\n" +
                        "Так выпьем же друзья напитки мы,\n" +
                        "И пусть сегодня распускаются цветы!",
                "Тост на юбилей мамы", "Милая моя мамочка! Я хочу сегодня выпить за тебя: за твои прекрасные руки, которые вырастили " +
                        "меня; за материнскую ласку, которая греет меня даже в самые суровые будни; за твое доброе материнское сердце, " +
                        "которое все чувствует; за твою материнскую душу, которая всегда меня понимает! За твой юбилей!",

                "Тост-стих", "Нет, считать не надо годы –\n" +
                        "Молод ты и полон сил,\n" +
                        "Сможешь победить невзгоды,\n" +
                        "И всегда нам будешь мил.\n" +
                        "Жди мечты своей заветной\n" +
                        "Исполненья, не старей,\n" +
                        "Чтоб своей улыбкой светлой\n" +
                        "Встретить каждый юбилей!\n" +
                        "Будь здоров и счастлив будь,\n" +
                        "Впереди – прекрасный путь!",

                "Тост на юбилей коллеге", "Вот и наступил, коллега, юбилей,\n" +
                        "Ты бокал налей, да не жалей!\n" +
                        "Пьем, коллега, все мы за тебя,\n" +
                        "Уважение, почет тебе даря!\n" +
                        "Пусть карьера дальше только в рост идет,\n" +
                        "И зарплата больше тебя ждет!\n" +
                        "Окружают пусть всегда друзья,\n" +
                        "И сбывается заветная мечта!",
                "Нашему юбиляру", "В 20 лет человеком властвует желание, в 30 лет - разум, в 40 лет - рассудок, в 50 лет мудрость...\n" +
                        "Так выпьем же за именинника в котором и в мудрые 50 лет есть и рассудок и разум и желание."

        ));
        toasts.put("Новый год", Map.of(
                "До завтрашнего свидания", "Друзья! Поднимая этот первый бокал,  хочу сказать вам – до завтрашнего свидания! Потому что мы трезвыми с вами сегодня уже не увидимся!",
                "За этот год", "Предлагаю выпить за то, чтобы год наступающий был лучше прошедшего, но намного хуже того, что будет следующим!",
                "За хозяина дома", "Тост мой – за доброго хозяина этого гостеприимного дома! Поднимем бокалы за то, чтобы в наступающем году ты продвинулся в карьере, чтобы у тебя появилась новая машина, на которой ты доедешь в свой новый загородный дом – и в нем мы снова вместе проводим старый и встретим Новый год!",
                "За всё хорошее!", "А я хочу предложить тост за то, чтобы мы все стали похожи на Деда Мороза и юную Снегурочку, потому что – вспомните! – они не меняются, не хворают и всегда при деньгах – вон сколько подарков вручают! За это!",
                "За года прошлого финал","Я поднимаю свой бокал\n" +
                        "За года прошлого финал,\n" +
                        "А, если что-то не  случилось –\n" +
                        "Так жизнь-то не остановилась!\n" +
                        "За Новый год, за счастье жить,\n" +
                        "Мечтать, работать и любить!"

        ));

        // Загрузка начального фрагмента
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

                break;
            case R.id.nav_favourites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavouritesFragment()).commit();
                break;
            case R.id.nav_Settings:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ThemeSwitcherFragment()).commit();
                break;
            case R.id.nav_logout:
                showLogoutConfirmationDialog();
                break;
        }



        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialogInterface, which) -> {
                    // Закрыть приложение
                    finishAffinity(); // Завершает приложение, включая все активности
                    System.exit(0);   // Выход из JVM
                })
                .setNegativeButton("Нет", (dialogInterface, which) -> {
                    // Просто закрываем диалог
                    dialogInterface.dismiss();
                })
                .setCancelable(false) // Запрещаем закрытие окна нажатием за пределами диалога
                .create();

        // Устанавливаем цвет текста кнопок после отображения диалога
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.black));
        });

        dialog.show();
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
                MainActivity.favorites = new HashMap<>(); // В случае ошибки загрузки, очистите список
            }
        } else {
            MainActivity.favorites = new HashMap<>(); // Если нет данных, создайте пустую коллекцию
        }


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void applySavedTheme() {
        ThemeUtils.applySavedTheme(this);
    }

}
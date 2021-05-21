package com.dam.multiplechoicequizapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_DIFFICULTY = "ExtraDifficulty";
    public static final String EXTRA_CATEGORY_ID = "CategoryID";
    public static final String EXTRA_CATEGORY_NAME = "CategoryName";

    public static final String SHARED_PREFER = "SharedPrefer";
    public static final String KEY_HIGHSCORE = "KeyHighscore";

    private TextView tvHighscore;
    private Spinner spinnerDifficulty;
    private Spinner spinnerCategory;


    private int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvHighscore = findViewById(R.id.tvHighscore);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        loadCategory();
        loadDifficulty();
        loadHighscore();

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }



    private void startQuiz() {
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        Intent i = new Intent(this, Quiz.class);
        i.putExtra(EXTRA_CATEGORY_ID, categoryID);
        i.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        i.putExtra(EXTRA_DIFFICULTY, difficulty);
        startActivityForResult(i, REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(Quiz.EXTRA_SCORE, 0);
                if (score > highscore) {
                    updateHighscore(score);
                }
            }
        }
    }

    private void loadCategory() {

        QuizDBHelper dbHelper = QuizDBHelper.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();


        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategories);
    }

    private void loadDifficulty() {

        String[] difficultyLevels = Question.getAllDifficultyLevels();

        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);
    }


    private void loadHighscore() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFER, MODE_PRIVATE);
        highscore = preferences.getInt(KEY_HIGHSCORE, 0);
        tvHighscore.setText("Highscore: " + highscore);

    }

    private void updateHighscore(int highscoreNew) {

        highscore = highscoreNew;
        tvHighscore.setText("Highscore: " + highscore);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFER, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();

    }
}
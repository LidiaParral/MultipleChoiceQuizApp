package com.dam.multiplechoicequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dam.multiplechoicequizapp.QuizContract.*;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuizDBHelper extends SQLiteOpenHelper {

    //Multiple Choice Quiz App with SQLite Integration Part 11 - CATEGORIES - Android Studio Tutorial

    private static final String DATABASE_NAME = "MyQuiz.db";
    private static final int DATABASE_VERSION = 2;

    private static  QuizDBHelper instance;

    private SQLiteDatabase db;

    public QuizDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized  QuizDBHelper getInstance(Context context){
        if(instance == null){
            instance = new QuizDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                CategoriesTable.TABLE_NAME + "(" +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " +
                QuestionTable.TABLE_NAME + " ( " +
                QuestionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionTable.COLUMN_QUESTION + " TEXT, " +
                QuestionTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionTable.COLUMN_OPTION4 + " TEXT, " +
                QuestionTable.COLUMN_ANSWER + " INTEGER," +
                QuestionTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuestionTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                " FOREIGN KEY(" +  QuestionTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ")" + " ON DELETE CASCADE " +
                ")";


        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTION_TABLE);
        fillCategoriesTable();
        fillQuestionTable();
        
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.TABLE_NAME);
        onCreate(db);
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {
        Category c1 = new Category("Plastic");
        addCategory(c1);
        Category c2 = new Category("Glass");
        addCategory(c2);
        Category c3 = new Category("Paper");
        addCategory(c3);
    }

    private void addCategory(Category category) {
        ContentValues cv = new ContentValues();
        cv.put(CategoriesTable.COLUMN_NAME, category.getName());
        db.insert(CategoriesTable.TABLE_NAME, null, cv);
    }




    private void fillQuestionTable() {
        Question q1 = new Question("Easy: A is correct", "A", "B", "C", "D", 1,Question.DIFFICULTY_EASY,Category.PLASTIC);
        addQuestion(q1);
        Question q2 = new Question("Medium: A is correct", "A", "B", "C", "D", 1,Question.DIFFICULTY_MEDIUM,Category.PAPER);
        addQuestion(q2);
        Question q3 = new Question("Easy: B is correct", "A", "B", "C", "D", 2,Question.DIFFICULTY_EASY,Category.PLASTIC);
        addQuestion(q3);
        Question q4 = new Question("Easy: C is correct", "A", "B", "C", "D", 3,Question.DIFFICULTY_EASY,Category.GLASS);
        addQuestion(q4);
        Question q5 = new Question("Hard: D is correct", "A", "B", "C", "D", 4,Question.DIFFICULTY_HARD,Category.PLASTIC);
        addQuestion(q5);

    }


    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuestionTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionTable.COLUMN_OPTION4, question.getOption4());
        cv.put(QuestionTable.COLUMN_ANSWER, question.getAnswer());
        cv.put(QuestionTable.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuestionTable.COLUMN_CATEGORY_ID, question.getCategoryId());

        db.insert(QuestionTable.TABLE_NAME, null, cv);

    }

    public List<Category> getAllCategories(){
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME, null);


        if(c.moveToFirst()){
            do{
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            } while(c.moveToNext());
        }

        c.close();
        return categoryList;
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionTable.TABLE_NAME, null);


        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION4)));
                question.setAnswer(c.getInt(c.getColumnIndex(QuestionTable.COLUMN_ANSWER)));
                question.setCategoryId(c.getInt(c.getColumnIndex(QuestionTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);


            } while (c.moveToNext());
        }

        c.close();
        return questionList;

    }

    public ArrayList<Question> getQuestions(int categoryID, String difficulty) {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuestionTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuestionTable.COLUMN_DIFFICULTY + " = ? ";

        String[] selectionArgs = new String[]{String.valueOf(categoryID),difficulty};

        Cursor c = db.query(
                QuestionTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION4)));
                question.setAnswer(c.getInt(c.getColumnIndex(QuestionTable.COLUMN_ANSWER)));
                question.setCategoryId(c.getInt(c.getColumnIndex(QuestionTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);


            } while (c.moveToNext());
        }

        c.close();
        return questionList;

    }
}

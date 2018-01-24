package com.example.android.pokemonquiz;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Global variables
    private SharedPreferences scoreSettings;
    private SharedPreferences.Editor scoreEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    private static final String scoreFile = "HighScores";
    public static final int number_pokemon = 151;
    public static final String imageFilePrefix = "a";
    private static String DB_NAME ="PokemonQuizDB";
    public LinkedList<Integer> nums;
    public static Map<Integer, Integer> options;
    public Random r;
    public static int correctOption;
    public DataBaseHelper myDbHelper;

    //Create or load app instance
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Random number generator; Removes pokemon from list upon generation
    public int generateRandom(Random ran)
    {
        if(nums.size()<=0)
        {
            Log.i("test","No Pokemon left");
            return 0;
        }
        return nums.remove(ran.nextInt(nums.size()));
    }

    //Method for initial instance of quiz; Instantiates all global quiz variables
    public void toQuiz(View view) throws Error {
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        myDbHelper.openDataBase();

        r = new Random();
        nums = new LinkedList<Integer>();
        for(int i=1;i<number_pokemon+1;i++)
        {
            nums.add(i);
        }
        options = new HashMap<Integer, Integer>();
        options.put(R.id.quiz_option1,1);
        options.put(R.id.quiz_option2,2);
        options.put(R.id.quiz_option3,3);
        options.put(R.id.quiz_option4,4);
        options.put(1,R.id.quiz_option1);
        options.put(2,R.id.quiz_option2);
        options.put(3,R.id.quiz_option3);
        options.put(4,R.id.quiz_option4);

        quizCreate(view);
    }

    //Creates quiz screen on each subsequent click
    public void quizCreate(View view)
    {
        //Create screen
        correctOption = r.nextInt(4) + 1;
        int rand = generateRandom(r);

        setContentView(R.layout.quiz_layout1);
        ImageView pokeImage = (ImageView) findViewById(R.id.quiz_image);
        if (rand <= 0) {
            return;
        }
        int imageInt = getResources().getIdentifier(imageFilePrefix + rand, "drawable", getPackageName());
        pokeImage.setImageResource(imageInt);
        Button correctButton = (Button) findViewById(options.get(correctOption));
        correctButton.setText(myDbHelper.queryName(rand));

    }

    public void quizClick(View view)
    {
        //Check for whether click was correct
        Button iv = (Button)findViewById(view.getId());
        if (options.get(view.getId()) == correctOption) {
            //code for correct choice
            iv.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.MULTIPLY);
            quizCreate(view);
        }
        else {
            //code for incorrect choice
            iv.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
        }
    }

    //Database methods
    public String getName(int num)
    {
        //returns string name of Pokemon based on number
        return "";
    }
    public ArrayList<String> getEvolutions(int num)
    {
        ArrayList<String> evols = new ArrayList<>();
        //returns string array of any other pokemon in the pokemon's evolutionary branch
        return evols;
    }

    //Creates initial instance of high score screen
    public void toHighScore(View view)
    {
        scoreSettings = getSharedPreferences(scoreFile,PREFERENCE_MODE_PRIVATE);
        setContentView(R.layout.highscore_layout);
        TextView firstView = (TextView) findViewById(R.id.first);
        firstView.setText("1st: "+scoreSettings.getString("goldName","")+", "+scoreSettings.getInt("goldScore",0)+" points");
        TextView secondView = (TextView) findViewById(R.id.second);
        secondView.setText("2nd: "+scoreSettings.getString("silverName","")+", "+scoreSettings.getInt("silverScore",0)+" points");
        TextView thirdView = (TextView) findViewById(R.id.third);
        thirdView.setText("3rd: "+scoreSettings.getString("bronzeName","")+", "+scoreSettings.getInt("bronzeScore",0)+" points");
    }

    //Updates the highscore with the score and name; will be executed at the end of the quiz
    public int updateHighScore(int score, String name)
    {
        scoreSettings = getSharedPreferences(scoreFile,PREFERENCE_MODE_PRIVATE);
        scoreEditor = scoreSettings.edit();
        int first = scoreSettings.getInt("goldScore",0);
        int second = scoreSettings.getInt("silverScore",0);
        int third = scoreSettings.getInt("bronzeScore",0);
        String firstName = scoreSettings.getString("goldName","");
        String secondName = scoreSettings.getString("silverName","");

        if(score>=third)
        {
            if(score>=second)
            {
                if(score>=first)
                {
                    scoreEditor.putInt("goldScore",score);
                    scoreEditor.putString("goldName",name);
                    scoreEditor.putInt("silverScore",first);
                    scoreEditor.putString("silverName",firstName);
                    scoreEditor.putInt("bronzeScore",second);
                    scoreEditor.putString("bronzeName",secondName);
                    boolean successfullySaved = scoreEditor.commit();
                    return 1;
                }
                else
                {
                    scoreEditor.putInt("silverScore",score);
                    scoreEditor.putString("silverName",name);
                    scoreEditor.putInt("bronzeScore",second);
                    scoreEditor.putString("bronzeName",secondName);
                    boolean successfullySaved = scoreEditor.commit();
                    return 2;
                }
            }
            else
            {
                scoreEditor.putInt("bronzeScore",score);
                scoreEditor.putString("bronzeName",name);
                boolean successfullySaved = scoreEditor.commit();
                return 3;
            }
        }
        return -1;
    }
}
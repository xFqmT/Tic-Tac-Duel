package com.example.catala;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SoundManager soundManager;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btnReset, btnBack;
    private TextView tvO, tvX, tvTurn;

    int Score_O = 0;
    int Score_X = 0;
    int PLAYER_O = 0;
    int PLAYER_X = 1;
    int activePlayer = PLAYER_X;
    int startingPlayer = PLAYER_X;
    int[] papan = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    boolean isGameActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyBrightness();

        soundManager = SoundManager.getInstance(this);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        tvO = findViewById(R.id.tvO);
        tvX = findViewById(R.id.tvX);
        tvTurn = findViewById(R.id.tvTurn);

        updateScores();

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);

        btnReset.setOnClickListener(v -> {
            soundManager.playClickSound(MainActivity.this);
            resetMatch();
        });

        btnBack.setOnClickListener(v -> {
            soundManager.playClickSound(MainActivity.this);
            Intent backIntent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(backIntent);
            finish();
        });

        // 🔧 Ensure first match starts correctly
        resetGame();
    }

    private void applyBrightness() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int brightness = sharedPreferences.getInt("Brightness", 60);
        float brightnessLevel = brightness / 100f;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessLevel;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyBrightness();
    }

    private void resetGame() {
        if (Score_O >= 10 || Score_X >= 10) {
            determineOverallWinner();
            return;
        }

        activePlayer = startingPlayer;
        tvTurn.setText(activePlayer == PLAYER_O ? "O Turn" : "X Turn");
        papan = new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2};
        resetButtons();
        isGameActive = true;

        // Alternate starting player for the next round
        startingPlayer = (startingPlayer == PLAYER_O) ? PLAYER_X : PLAYER_O;
    }

    private void resetButtons() {
        Button[] buttons = {btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8};
        for (Button button : buttons) {
            button.setText("");
            button.setBackgroundColor(Color.parseColor("#3B9E93"));
        }
    }

    private void resetMatch() {
        Score_O = 0;
        Score_X = 0;
        startingPlayer = PLAYER_X;
        updateScores();
        resetGame();
    }

    private void updateScores() {
        tvO.setText("O : " + Score_O);
        tvX.setText("X : " + Score_X);
    }

    private void determineOverallWinner() {
        String message = (Score_O > Score_X) ?
                "O has won the match with 10 points!" :
                "X has won the match with 10 points!";

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(message)
                .setPositiveButton("Reset Game", (dialog, which) -> resetMatch())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (!isGameActive) return;

        soundManager.playClickSound(this);
        Button btnTekan = findViewById(v.getId());
        int tagTekan = Integer.parseInt(v.getTag().toString());

        if (papan[tagTekan] != 2) return;

        papan[tagTekan] = activePlayer;
        btnTekan.setText(activePlayer == PLAYER_O ? "O" : "X");
        btnTekan.setBackground(getDrawable(R.color.teal_700));

        checkForWin();

        if (isGameActive) {
            activePlayer = (activePlayer == PLAYER_O) ? PLAYER_X : PLAYER_O;
            tvTurn.setText(activePlayer == PLAYER_O ? "O Turn" : "X Turn");
        }
    }

    private Button getButtonByTag(int tag) {
        switch (tag) {
            case 0: return btn0;
            case 1: return btn1;
            case 2: return btn2;
            case 3: return btn3;
            case 4: return btn4;
            case 5: return btn5;
            case 6: return btn6;
            case 7: return btn7;
            case 8: return btn8;
            default: return null;
        }
    }

    private void checkForWin() {
        int[][] winner = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] condition : winner) {
            if (papan[condition[0]] == papan[condition[1]]
                    && papan[condition[1]] == papan[condition[2]]
                    && papan[condition[0]] != 2) {

                isGameActive = false;

                for (int index : condition) {
                    Button btn = getButtonByTag(index);
                    if (btn != null) {
                        btn.setBackgroundColor(Color.parseColor("#29C3B7"));
                    }
                }

                if (papan[condition[0]] == PLAYER_O) {
                    Score_O++;
                    updateScores();
                    showDialog("O is Winner");
                } else {
                    Score_X++;
                    updateScores();
                    showDialog("X is Winner");
                }
                return;
            }
        }

        if (isDraw()) {
            showDialog("Match is Draw");
        }
    }

    private boolean isDraw() {
        for (int cell : papan) if (cell == 2) return false;
        return true;
    }

    private void showDialog(String winner) {
        new AlertDialog.Builder(this)
                .setTitle(winner)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> resetGame())
                .show();
    }
}

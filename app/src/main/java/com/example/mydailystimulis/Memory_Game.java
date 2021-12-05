package com.example.mydailystimulis; //!Dibassi Brahima 19005521

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Memory_Game
  extends AppCompatActivity
  implements View.OnClickListener {

  ArrayList<Game_Card> Game_Cards;
  int Showed_Cards = 0;
  boolean is_turn_done = false;
  Game_Card prev_card;
  int score = 0;
  int combo = 1;
  TextView score_view;
  TextView combo_view;
  String Pseudo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.memory_game);
    score_view = findViewById(R.id.Score);
    combo_view = findViewById(R.id.Combo);
    DisplayScore();
    views_init();

    Pseudo = this.getIntent().getStringExtra("Pseudo");
  }

  @Override
  public void onClick(View v) {
    for (Game_Card card : Game_Cards) {
      if (card.image == v) {
        game_turn(card);
        break;
      }
    }
  }

  public void DisplayScore() {
    String score_txt = "Score : " + score;
    String combo_txt = "Combo : " + combo + " multiply";
    score_view.setText(score_txt);
    combo_view.setText(combo_txt);
  }

  public void game_turn(Game_Card clicked_card) {
    if (clicked_card.showed && !clicked_card.won) {
      if (is_turn_done) clicked_card.Hide();
      if (Showed_Cards == 0) {
        is_turn_done = false;
      }
    } else {
      if (!is_turn_done) {
        clicked_card.Show();
        if (clicked_card.chosen_image == R.drawable.black_card) {
          if (score > 0) score /= 3;
        }
        if (Showed_Cards == 2) {
          is_turn_done = true;
          if (clicked_card.chosen_image == prev_card.chosen_image) {
            is_turn_done = false;
            clicked_card.won = true;
            prev_card.won = true;
            Showed_Cards = 0;

            score += combo * 1000;
            combo++;
          } else {
            combo = 1;
            score -= 100;
            Game_Card tmp = prev_card;
            hide_cards(tmp, clicked_card, 1);
          }
        }
        prev_card = clicked_card;
      }
    }
    DisplayScore();
    end_game();
  }

  public void end_game() {
    int won = 0;
    for (Game_Card card : Game_Cards) {
      if (card.won)
        won++;

    }
    if (won == Game_Cards.size() - 1) {
      new Data_Base_Handling(this).addScore(Pseudo,score);
      Intent Menu = new Intent(Memory_Game.this, MainActivity.class);
      Menu.putExtra("Score", score);
      Menu.putExtra("Pseudo", Pseudo);
      startActivity(Menu);
    }
  }

  public void hide_cards(Game_Card A, Game_Card B, int delay) {
    final Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(
      new Runnable() {
        @Override
        public void run() {
          A.Hide();
          B.Hide();
          Showed_Cards = 0;
          is_turn_done = false;
        }
      },
      delay * 1000
    );
  }

  class Game_Card {

    boolean showed;
    boolean won;
    int chosen_image;
    ImageView image;

    Game_Card(int view_id, int image) {
      this.image = findViewById(view_id);
      won = false;
      showed = true;
      this.chosen_image = image;
      this.Hide();
    }

    void Show() {
      if (!showed) {
        image.setBackgroundResource(chosen_image);
        Showed_Cards++;
        showed = true;
      }
    }

    void Hide() {
      if (showed) {
        image.setBackgroundResource(R.drawable.card_back);
        Showed_Cards--;
        showed = false;
      }
    }
  }

  public void views_init() {
    Game_Cards = new ArrayList<>();

    Vector<Integer> img_ls = new Vector<>();
    img_ls.add(0, R.drawable.as_coeur);
    img_ls.add(1, R.drawable.as_pique);
    img_ls.add(2, R.drawable.as_pique);
    img_ls.add(3, R.drawable.dame);
    img_ls.add(4, R.drawable.dame);
    img_ls.add(5, R.drawable.dice_1);
    img_ls.add(6, R.drawable.dice_1);
    img_ls.add(7, R.drawable.dice_6);
    img_ls.add(8, R.drawable.dice_6);
    img_ls.add(9, R.drawable.joker);
    img_ls.add(10, R.drawable.joker);
    img_ls.add(11, R.drawable.king);
    img_ls.add(12, R.drawable.king);
    img_ls.add(13, R.drawable.black_card);
    img_ls.add(14, R.drawable.as_coeur);

    Collections.shuffle(img_ls);

    List<Integer> Views = new Vector<>();
    Views.add(0, R.id.imageView1);
    Views.add(1, R.id.imageView2);
    Views.add(2, R.id.imageView3);
    Views.add(3, R.id.imageView4);
    Views.add(4, R.id.imageView5);
    Views.add(5, R.id.imageView6);
    Views.add(6, R.id.imageView7);
    Views.add(7, R.id.imageView8);
    Views.add(8, R.id.imageView9);
    Views.add(9, R.id.imageView10);
    Views.add(10, R.id.imageView11);
    Views.add(11, R.id.imageView12);
    Views.add(12, R.id.imageView13);
    Views.add(13, R.id.imageView14);
    Views.add(14, R.id.imageView15);

    for (int i = 0; i < Views.size(); i++) {
      Game_Cards.add(new Game_Card(Views.get(i), img_ls.get(i)));
    }

    for (Game_Card game_card : Game_Cards) {
      game_card.image.setOnClickListener(this);
    }
    Showed_Cards = 0;
  }
}

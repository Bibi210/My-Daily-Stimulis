package com.example.mydailystimulis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener {

  Button main_start;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    main_start = findViewById(R.id.main_start_btn);
    main_start.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (main_start.equals(v)) {
      Intent Go_game = new Intent(MainActivity.this, Ball_Game.class);
      startActivity(Go_game);
    }
  }

}

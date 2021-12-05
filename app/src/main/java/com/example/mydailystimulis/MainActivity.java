package com.example.mydailystimulis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener {

  ArrayList<Class> Game_Mods = new ArrayList<>();
  Button main_start;
  EditText PseudoInput;
  Data_Base_Handling db;

  ListView LeaderBord_List;
  Button LeaderBord;

  boolean LeadBord_Visible = false;
  private void init_views() {
    main_start = findViewById(R.id.main_start_btn);
    LeaderBord = findViewById(R.id.main_score_btn);

    PseudoInput = findViewById(R.id.Pseudo_Field);
    LeaderBord_List = findViewById(R.id.LeaderBord_List);

    main_start.setOnClickListener(this);
    LeaderBord.setOnClickListener(this);
  }

  private void init_game_modes() {
    Game_Mods.add(Calculs.class);
    Game_Mods.add(Memory_Game.class);
    Game_Mods.add(Ball_Game.class);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    init_views();
    init_game_modes();

    Boot_Notifs(); // To init the daily notif
    new Notif_Class(this).Run_Notif(); // For Demo
    db = new Data_Base_Handling(this); // Data BaseBoot

    Intent Prev_Intent = this.getIntent();
    String Prev_Pseudo = Prev_Intent.getStringExtra("Pseudo");
    if (Prev_Pseudo != null){
      int Prev_Score = Prev_Intent.getIntExtra("Score",-1);
      Snackbar mySnackbar = Snackbar.make(LeaderBord_List, Prev_Pseudo + "  :  " +Prev_Score, 10000);
      mySnackbar.show();
    }

  }

  @Override
  public void onClick(View v) {
    //Game Start + ScoreBoard Handling

    if (main_start.equals(v)) {
      String Pseudo = PseudoInput.getText().toString();
      if (isValidUsername(Pseudo)) {
        Intent go_game = random_game().putExtra("Pseudo", Pseudo);
        startActivity(go_game);
      } else PseudoInput.setError("Nécessite un pseudo valide.\nMini 6 Character and Numbers ");
    }
    if (LeaderBord.equals(v)){
      ArrayList<String> Scores_Ls = new ArrayList<>();
      if (!LeadBord_Visible) {
        for (String[] Player_Score : db.allPlayers()) {
          Scores_Ls.add(Player_Score[0] + " : " + Player_Score[1]);
        }
        LeadBord_Visible = true;
      }else
        LeadBord_Visible = false;

      ArrayAdapter adpt = new ArrayAdapter(this, R.layout.list_item, Scores_Ls);
      LeaderBord_List.setAdapter(adpt);
    }
  }


  private Intent random_game() {
    Collections.shuffle(Game_Mods);
    return new Intent(this, Game_Mods.get(0));
  }

  // Start automatic notif
  @RequiresApi(api = Build.VERSION_CODES.M)
  public void Boot_Notifs() {
    Calendar Minuteur = Calendar.getInstance();
    Minuteur.set(Calendar.HOUR_OF_DAY, 8);
    Minuteur.set(Calendar.MINUTE, 30);
    Minuteur.set(Calendar.SECOND, 0);

    if (Minuteur.getTime().compareTo(new Date()) < 0) Minuteur.add(
      Calendar.DAY_OF_MONTH,
      1
    );

    Intent intent = new Intent(
      getApplicationContext(),
      NotificationReceiver.class
    );
    PendingIntent pendingIntent = PendingIntent.getBroadcast(
      getApplicationContext(),
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    if (alarmManager != null) {
      alarmManager.setRepeating(
        AlarmManager.ELAPSED_REALTIME,
        Minuteur.getTimeInMillis(),
        AlarmManager.INTERVAL_DAY,
        pendingIntent
      );
    }
  }

  public boolean isValidUsername(String name) {
    // Regex to check valid username.
    String regex = "^[A-Za-z]\\w{5,29}$";
    Pattern p = Pattern.compile(regex);

    // If the username is empty
    if (name == null || name.equals("")) {
      return false;
    }
    return p.matcher(name).matches();
  }
}

class NotificationReceiver extends BroadcastReceiver {

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onReceive(Context context, Intent intent) {
    Notif_Class notifClass = new Notif_Class(context);
    notifClass.Run_Notif();
  }
}

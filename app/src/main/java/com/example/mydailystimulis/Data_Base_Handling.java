package com.example.mydailystimulis;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class Data_Base_Handling {

  Cursor cursor;
  SQLiteDatabase ScoreDb;
  private Exception PseudoNotHere;

  Data_Base_Handling(Context ctx) {
    ScoreDb = ctx.openOrCreateDatabase("ScoreDb", MODE_PRIVATE, null);
    ScoreDb.execSQL(
      "CREATE TABLE IF NOT EXISTS scores (name TEXT, score INT);"
    );
  }
  // Ajoute les points au compte du joueur
  public void addScore(String Pseudo, int Score) {
    try {
      Score += currentScore(Pseudo);
      removeScore(Pseudo);
    } catch (Exception ignored) {}

    ScoreDb.execSQL(
      "INSERT INTO scores (name, score) VALUES (" +
      "'" +
      Pseudo +
      "'" +
      "," +
      "'" +
      Score +
      "'" +
      ");"
    );
  }
  // Retire un joueur
  public void removeScore(String Pseudo) {
    ScoreDb.execSQL(
      "DELETE FROM scores WHERE name = " + "'" + Pseudo + "'" + ";"
    );
  }
  // Regarde le score d'un joueur
  public int currentScore(String Pseudo) throws Exception {
    cursor =
      ScoreDb.rawQuery(
        "SELECT * FROM scores WHERE name = " + "'" + Pseudo + "'" + ";",
        null
      );
    if (cursor.moveToNext()) {
      return cursor.getInt(1);
    }
    throw (PseudoNotHere);
  }

  // Revoie le score de tout les joueurs
  public ArrayList<String[]> allPlayers() {
    ArrayList<String[]> output = new ArrayList<>();
    cursor = ScoreDb.rawQuery("SELECT * FROM scores ORDER BY score ASC", null);
    while (cursor.moveToNext()) {
      String tmp[] = { cursor.getString(0), cursor.getString(1) };
      output.add(0, tmp);
    }
    return output;
  }

  public long length() {
    cursor = ScoreDb.rawQuery("SELECT * FROM scores ORDER BY score ASC", null);
    return cursor.getCount();
  }

  // Destruction de la base
  public void cleardb(){
    for (String[] Player_Score : this.allPlayers()) {
      removeScore(Player_Score[0]);
    }
  }
}

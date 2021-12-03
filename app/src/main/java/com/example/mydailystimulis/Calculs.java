package com.example.mydailystimulis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;

public class Calculs extends AppCompatActivity implements View.OnClickListener {

  equation[] Equations = new equation[3];
  Button Valid_btn;

  int scores = 0;
  TextView scores_view;

  int turn = 4;
  TextView turn_view;
  boolean correction_mode = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calculs);
    EditText[] Equation_1 = {
      findViewById(R.id.A_Equation1),
      findViewById(R.id.Operator_Equation1),
      findViewById(R.id.B_Equation1),
      findViewById(R.id.C_Equation1),
    };
    EditText[] Equation_2 = {
      findViewById(R.id.A_Equation2),
      findViewById(R.id.Operator_Equation2),
      findViewById(R.id.B_Equation2),
      findViewById(R.id.C_Equation2),
    };
    EditText[] Equation_3 = {
      findViewById(R.id.A_Equation3),
      findViewById(R.id.Operator_Equation3),
      findViewById(R.id.B_Equation3),
      findViewById(R.id.C_Equation3),
    };
    turn_view = findViewById(R.id.calculs_restants);
    scores_view = findViewById(R.id.calculs_score);
    Valid_btn = findViewById(R.id.Calculs_Validate);
    Valid_btn.setOnClickListener(this);

    Equations[0] = new equation(Equation_1);
    Equations[1] = new equation(Equation_2);
    Equations[2] = new equation(Equation_3);
    display_stat();
    nextTurn();
  }

  @Override
  public void onClick(View v) {
    if (v == Valid_btn) {
      verifyEqs();
      display_stat();
    }
  }

  void verifyEqs() {
    equation false_eq = null;
    for (equation eq : Equations) {
      if (!eq.verify()) {
        false_eq = eq;
      } else if (!correction_mode) {
        scores += 1000;
      }
    }
    if (false_eq == null) {
      nextTurn();
      correction_mode = false;
    } else {
      false_eq.focus();
      correction_mode = true;
    }
  }

  void nextTurn() {
    for (equation eq : Equations) {
      eq.reset_equation();
      eq.gen_equation();
      eq.add_trou();
      eq.display();
    }
    turn--;
    if (turn == 0) {
      end_game();
    }
  }

  void end_game() {
    Intent Menu = new Intent(Calculs.this, MainActivity.class);
    Menu.putExtra("Score", scores);
    startActivity(Menu);
  }

  void display_stat() {
    String score_txt = "Score : " + Integer.toString(scores);
    String turn_txt = "Restants : " + Integer.toString(turn);
    scores_view.setText(score_txt);
    turn_view.setText(turn_txt);
  }
}

class equation {

  EditText[] Views;
  Integer[] Equation_data = new Integer[4];
  int trou_pos;

  equation(EditText[] Views) {
    this.Views = Views;
  }

  void reset_equation() {
    trou_pos = -1;
    for (EditText champ : Views) {
      champ.setSelectAllOnFocus(true);
      champ.setCursorVisible(false);
      champ.setFocusable(false);
      champ.setFocusableInTouchMode(false);
    }
    for (Integer Eq_data : Equation_data) {
      Eq_data = null;
    }
  }

  void add_trou() {
    trou_pos = (Utils.rand_int(0, Equation_data.length - 1));
    Views[trou_pos].setFocusable(true);
    Views[trou_pos].setFocusableInTouchMode(true);
  }

  void gen_equation() {
    operators op = operators.chose();
    Integer A = Utils.rand_int(0, 10);
    Integer B = Utils.rand_int(0, 10);
    Integer result = op.apply(A, B);
    Equation_data[0] = A;
    Equation_data[1] = op.toInteger();
    Equation_data[2] = B;
    Equation_data[3] = result;
  }

  @SuppressLint("SetTextI18n")
  void display() {
    for (int i = 0; i < Equation_data.length; i++) {
      if (i != trou_pos) {
        if (i != 1) Views[i].setText(
            Equation_data[i].toString()
          ); else Views[1].setText(
            operators.fromInteger(Equation_data[1]).toString()
          );
      }
    }
    Views[trou_pos].setText("");
    Views[trou_pos].setHint("X");
  }

  boolean verify() {
    String input_str = String.valueOf(Views[trou_pos].getText());
    try {
      Integer input_int = Integer.parseInt(input_str);
      switch (trou_pos) {
        case 0:
          return Equation_data[3].equals(
              operators
                .fromInteger(Equation_data[1])
                .apply(input_int, Equation_data[2])
            );
        case 2:
          return Equation_data[3].equals(
              operators
                .fromInteger(Equation_data[1])
                .apply(Equation_data[0], input_int)
            );
        case 3:
          return input_int.equals(
            operators
              .fromInteger(Equation_data[1])
              .apply(Equation_data[0], Equation_data[2])
          );
        default:
          return false;
      }
    } catch (NumberFormatException nfe) {
      if (trou_pos != 1) {
        return false;
      } else {
        try {
          operators input_op = operators.fromString(input_str);
          return Equation_data[3].equals(
              input_op.apply(Equation_data[0], Equation_data[2])
            );
        } catch (ParseException msg) {
          return false;
        }
      }
    }
  }

  void focus() {
    Views[trou_pos].requestFocus();
  }

  enum operators {
    add,
    sous,
    mult;

    static operators chose() {
      switch (Utils.rand_int(0, 2)) {
        case 1:
          return sous;
        case 2:
          return mult;
        default:
          return add;
      }
    }

    Integer apply(Integer A, Integer B) {
      int result;
      switch (this) {
        case sous:
          result = A - B;
          break;
        case mult:
          result = A * B;
          break;
        default:
          result = A + B;
      }
      return result;
    }

    @NonNull
    @Override
    public String toString() {
      switch (this) {
        case sous:
          return "-";
        case mult:
          return "*";
        default:
          return "+";
      }
    }

    public Integer toInteger() {
      switch (this) {
        case sous:
          return 1;
        case mult:
          return 2;
        default:
          return 0;
      }
    }

    public static operators fromInteger(Integer operators_int) {
      switch (operators_int) {
        case 1:
          return sous;
        case 2:
          return mult;
        default:
          return add;
      }
    }

    public static operators fromString(String operators_string)
      throws ParseException {
      switch (operators_string) {
        case "-":
          return sous;
        case "*":
          return mult;
        case "+":
          return add;
        default:
          throw new ParseException("This Operator is not handled", 0);
      }
    }
  }
}

package com.example.mydailystimulis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Vector;

public class Ball_Game extends AppCompatActivity {

  Game_View MyCanvas; // Canvas du jeu

  int width; // Phone width in pixels
  int height; // Phone height in pixels
  Handler handler = new Handler(); // Main_Loop Thread
  Runnable to_exec; // Main_Loop_Functions
  String Pseudo; // Player Pseudo

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load Display Information
    DisplayMetrics screen = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(screen);


    MyCanvas = new Game_View(this, null);
    width = screen.widthPixels;
    height = screen.heightPixels;

    // First Circle
    MyCanvas.to_draw.get(0).Center.x = (float) width / 2;
    MyCanvas.to_draw.get(0).Center.y = (float) (height - 200) / 2;

    Pseudo = this.getIntent().getStringExtra("Pseudo");

    setContentView(MyCanvas); // FullScreen Game
    main_loop();
  }

  private void main_loop() {
    int delay = 1; // 1000 milliseconds == 1 second
    to_exec =
      new Runnable() {
        public void run() {
          MyCanvas.remaing_sec--;
          handler.postDelayed(this, delay); // Appel Recursif
          MyCanvas.reDraw();

          if (MyCanvas.remaing_sec == 0) {
            MyCanvas.end_game();
          } else if (MyCanvas.remaing_sec % 250 == 0) {
            // Ajouts circle tout les X temps
            Circle to_add = new Circle(new Point(width, height));
            MyCanvas.to_draw.add(to_add);
          }

          // Ajouts circle si aucun visible
          int visible_circle = 0;
          for (Circle circle : MyCanvas.to_draw) {
            circle.update(new Point(width, height));
            circle.reduce_circle();
            if (circle.radius > 10) {
              visible_circle++;
            }
          }
          if (visible_circle == 0) {
            Circle to_add = new Circle(new Point(width, height));
            MyCanvas.to_draw.add(to_add);
          }
          // Ajouts circle si aucun visible

        }
      };
    handler.postDelayed(to_exec, delay);    //Init
  }

  @Override
  protected void onPause() {
    handler.removeCallbacks(to_exec); //Stop Thread quand activiter non focus
    super.onPause();
  }

  class Game_View extends View {

    //Time du dernier clique
    long prev_click = System.currentTimeMillis() * 1000;
    long current_click = 0;
    int remaing_sec = 30 * 100;

    // Cercles en Jeux
    Vector<Circle> to_draw = new Vector<>();
    Context ctx;

    int score = 0;
    Paint Score_Text_Style = new Paint();
    Paint Title_Style = new Paint();
    Paint Time_Style = new Paint();

    public Game_View(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.setBackgroundColor(Color.rgb(106, 98, 98));
      Paint ball_style = new Paint();
      ball_style.setColor(Color.RED);
      ball_style.setStyle(Paint.Style.FILL);
      to_draw.add(new Circle(0, 0, 200, ball_style)); // Cercle 1
      ctx = context;

      Score_Text_Style.setStyle(Paint.Style.FILL);
      Score_Text_Style.setColor(Color.rgb(255, 127, 0));
      Score_Text_Style.setTextAlign(Paint.Align.LEFT);
      Score_Text_Style.setTextSize(50);

      Title_Style.setStyle(Paint.Style.FILL);
      Title_Style.setColor(Color.BLACK);
      Title_Style.setTextAlign(Paint.Align.CENTER);
      Title_Style.setTextSize(100);

      Time_Style.setStyle(Paint.Style.FILL);
      Time_Style.setColor(Color.rgb(255, 127, 0));
      Time_Style.setTextAlign(Paint.Align.RIGHT);
      Time_Style.setTextSize(50);
    }

    private void statsDisplay(Canvas canvas){
      canvas.drawText(
              "Score : " + score,
              (float) (width * 0.1),
              (float) (height * 0.15),
              Score_Text_Style
      );
      canvas.drawText(
              "Timer : " + remaing_sec / 100,
              (float) (width * 0.9),
              (float) (height * 0.15),
              Time_Style
      );

      canvas.drawText(
              "Beat Circle",
              (float) (width * 0.5),
              (float) (height * 0.07),
              Title_Style
      );
    }

    @Override
    protected void onDraw(Canvas canvas) {
      drawCircle(canvas);
      statsDisplay(canvas);
    }

    @Override // Proccess Des balles touch??e
    public boolean onTouchEvent(MotionEvent event) {
      current_click = System.currentTimeMillis();
      // Click Cooldown
      if (current_click - prev_click > 200) {
        float touched_x = event.getX();
        float touched_y = event.getY();
        boolean is_miss = true;
        for (Circle circle : to_draw) {
          if (circle.is_touched(touched_x, touched_y)) {
            circle.gen_circle(new Point(width, height));
            score += 100;
            is_miss = false;
          }
        }
        if (is_miss) score -= 75;

        this.reDraw();
      }
      prev_click = current_click;
      return true;
    }

    public void drawCircle(Canvas canvas) {
      for (Circle circle : to_draw) {
        canvas.drawCircle(
          circle.Center.x,
          circle.Center.y,
          circle.radius,
          circle.Circle_Style
        );
      }
    }

    public void reDraw() {
      this.invalidate();
    }

    public void end_game() { // Fin du jeu inscription du score
      new Data_Base_Handling(ctx).addScore(Pseudo,score);
      Intent Menu = new Intent(Ball_Game.this, MainActivity.class);
      Menu.putExtra("Score", score);
      Menu.putExtra("Pseudo", Pseudo);
      startActivity(Menu);
    }
  }
}

class Circle {

  float radius; // Rayon du cercle
  Point Center = new Point();
  Paint Circle_Style; // Couleur du cercle
  Point Speed = new Point(); // Vecteur de Vitesse

  public Circle(float x, float y, float radius, Paint circleStyle) {
    Center.x = x;
    Center.y = y;
    this.radius = radius;
    Circle_Style = circleStyle;
  }

  public Circle(Point max_coords) {
    gen_circle(max_coords);
  }

  boolean is_touched(float x_touched, float y_touched) {
    if (x_touched < Center.x + radius && x_touched > Center.x - radius) {
      return y_touched < Center.y + radius && y_touched > Center.y - radius;
    }
    return false;
  }

  void gen_circle(Point max_coords) {
    //Generation d'un cercle aleatoire
    if (Circle_Style == null) {
      Circle_Style = new Paint();
      Circle_Style.setStyle(Paint.Style.FILL);
    }
    int new_color = Color.rgb(
      (int) Utils.rand_range(100, 255),
      (int) Utils.rand_range(100, 255),
      (int) Utils.rand_range(100, 255)
    );
    radius = Utils.rand_range(200, 300);
    Center.x = Utils.rand_range(0 + radius, max_coords.x - radius);
    Center.y = Utils.rand_range(0 + radius, (max_coords.y - radius) - 200);
    Circle_Style.setColor(new_color);
    Speed = new Point(Utils.rand_range(-10f, 10f), Utils.rand_range(-10f, 10f));
  }

  // Diminution de la taille du cercle
  void reduce_circle() {
    this.radius--;
  }

  void move(Point to_add) {
    this.Center.add(to_add);
  }

  //Cercle Position Updates
  void update(Point Limit) {
    this.move(this.Speed);
    if (Center.x + radius < 0) {
      Center.x = Limit.x;
    }
    if (Center.x - radius / 2 > Limit.x) {
      Center.x = 0;
    }
    if (Center.y + radius < 0) {
      Center.y = Limit.y - 200;
    }
    if (Center.y - radius / 2 > Limit.y) {
      Center.y = 0;
    }
  }
}

class Point {

  float x;
  float y;

  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Point() {
    this.x = 0;
    this.y = 0;
  }

  void add(Point Other) {
    this.x += Other.x;
    this.y += Other.y;
  }
}

class Utils {

  static float rand_range(float Min, float Max) {
    return (Min + (float) (Math.random() * ((Max - Min) + 1)));
  }

  static int rand_int(int Min, int Max) {
    return (Min + (int) (Math.random() * ((Max - Min) + 1)));
  }
  // As X pourcent de chance de retourner vrai
  static boolean probabilty(float prob) {
    return rand_range(0, 100) < prob;
  }
}

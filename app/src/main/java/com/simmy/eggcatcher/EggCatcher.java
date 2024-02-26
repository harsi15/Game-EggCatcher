package com.simmy.eggcatcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class EggCatcher extends View {
    Context context;
    Bitmap background;
    Handler handler;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 1;
    Paint scorePaint;
    int TEXT_SIZE = 60;
    boolean paused = false;
    Nest nest;
    Chicken chicken;
    Random random;
    ArrayList<Egg> chickenEggs, ourShots;
    boolean chickenEggAction = false;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
           invalidate();
        }
    };


    public EggCatcher(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        chickenEggs = new ArrayList<>();
        ourShots = new ArrayList<>();
        nest = new Nest(context);
        chicken = new Chicken(context);
        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("  Score: " + points, 0, TEXT_SIZE, scorePaint);
        if(life == 0){
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        // Move chicken
        chicken.ex += chicken.enemyVelocity;
        if(chicken.ex + chicken.getChickenWidth() >= screenWidth){
            chicken.enemyVelocity *= -1;
        }
        if(chicken.ex <=0){
            chicken.enemyVelocity *= -1;
        }


        if(chickenEggAction == false){
            if(chicken.ex >= 200 + random.nextInt(400)){
                Egg chickenEgg = new Egg(context, chicken.ex + chicken.getChickenWidth() / 2, chicken.ey );
                chickenEggs.add(chickenEgg);
                chickenEggAction = true;
            }
            if(chicken.ex >= 400 + random.nextInt(800)){
                Egg chickenEgg = new Egg(context, chicken.ex + chicken.getChickenWidth() / 2, chicken.ey );
                chickenEggs.add(chickenEgg);
                // We're making chickenEggAction to true so that enemy can take a short at a time
                chickenEggAction = true;
            }
            else{
                Egg chickenEgg = new Egg(context, chicken.ex + chicken.getChickenWidth() / 2, chicken.ey );
                chickenEggs.add(chickenEgg);
                chickenEggAction = true;
            }
        }
        canvas.drawBitmap(chicken.getChicken(), chicken.ex, chicken.ey, null);
        if(nest.ox > screenWidth - nest.getNestWidth()){
            nest.ox = screenWidth - nest.getNestWidth();
        }else if(nest.ox < 0){
            nest.ox = 0;
        }
        canvas.drawBitmap(nest.getNest(), nest.ox, nest.oy, null);

        for(int i=0; i < chickenEggs.size(); i++){
            chickenEggs.get(i).shy += 15;
            canvas.drawBitmap(chickenEggs.get(i).getEgg(), chickenEggs.get(i).shx, chickenEggs.get(i).shy, null);
            if((chickenEggs.get(i).shx >= nest.ox)
                && chickenEggs.get(i).shx <= nest.ox + nest.getNestWidth()
                && chickenEggs.get(i).shy >= nest.oy
                && chickenEggs.get(i).shy <= screenHeight){
                points++;
                chickenEggs.remove(i);
            }else if(chickenEggs.get(i).shy >= screenHeight){
                life--;
                chickenEggs.remove(i);
            }
            if(chickenEggs.size() < 1){

                chickenEggAction = false;
            }
        }
        if(!paused)
            handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)event.getX();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            nest.ox = touchX;
        }
       if(event.getAction() == MotionEvent.ACTION_MOVE){
            nest.ox = touchX;
        }
        return true;
    }
}

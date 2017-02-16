package com.example.user.imagestreamnsd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by user on 2017.01.04..
 */


    /**
     * Created by user on 2016.05.31..
     */
    public class AttelaSkats extends View {
        public byte[] atelaDati;
        //   ArrayList<HorizontalaLinija> linijas;
        Paint paint = new Paint();
        Canvas canvas = new Canvas();
        Bitmap bitmap;
        boolean uzzimetsPirmais = true;
        public AttelaSkats(Context context) {
            super(context);
            paint.setAntiAlias(true);
            paint.setColor(Color.MAGENTA);


        }
        public void mSetBitmap(Bitmap bitmap){
            if(uzzimetsPirmais)
                this.bitmap = bitmap;

            invalidate();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Rect rect = new Rect(0,0,479,400);
            synchronized (bitmap) {
                canvas.scale(0.25f,0.25f);


if (!(bitmap==null))
                    canvas.drawBitmap(bitmap, 0, 0, paint);


                super.onDraw(canvas);
                }




            }
    }
            // invalidate();






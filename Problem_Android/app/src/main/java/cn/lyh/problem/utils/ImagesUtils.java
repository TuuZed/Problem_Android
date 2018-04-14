package cn.lyh.problem.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

/**
 * Created by LYH on 2015/9/25.
 */
public class ImagesUtils {
    /**
     * 返回圆形的图片
     *
     * @param bitmap
     * @return
     */
    private static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);
        return backgroundBmp;
    }


    public static Bitmap drawHeadIcon(Context context, String s,boolean isBig ,int sex) {
        String myColor[] = new String[]{"#4E4D53", "#EE78AE", "#4DBBD7"};
        int len = 0;
        if (isBig)
            len = 400;
        else
            len = 300;
        Rect targetRect = new Rect(0, 0, len, len);
        Bitmap bitmap = Bitmap.createBitmap(len, len, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        String color;
        if (sex == 2) {
            color = myColor[1];
        } else if (sex == 3) {
            color = myColor[2];
        } else {
            color = myColor[0];
        }
        canvas.drawColor(new Color().parseColor(color));
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        int defaultSize = 35;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (dm.density == 1.5)
            defaultSize = (int) (defaultSize * 2);
        else if (dm.density == 2)
            defaultSize = (int) (defaultSize * 1.7);
        else if (dm.density == 2.5)
            defaultSize = (int) (defaultSize * 1.4);
        else if (dm.density == 3)
            defaultSize = 35;
        int testsize = (int) (defaultSize * dm.density);
        paint.setTextSize(testsize);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = targetRect.top
                + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top)
                / 2 - fontMetrics.top;
        paint.setTextAlign(Paint.Align.CENTER);
        try {
            s = s.substring(0, 1);
            canvas.drawText(s.substring(0, 1), targetRect.centerX(), baseline, paint);
        } catch (Exception e) {
            canvas.drawText(s, targetRect.centerX(), baseline, paint);
        }
        Bitmap newbitBitmap = toRoundBitmap(bitmap);
        return newbitBitmap;
    }


}

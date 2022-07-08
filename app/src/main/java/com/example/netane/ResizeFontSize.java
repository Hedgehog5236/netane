package com.example.netane;

import android.content.res.Resources;
import android.widget.TextView;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.TypedValue;

public class ResizeFontSize {
    public static void resize(TextView textView, final int minTextSize) {
        /*resize*/
        /// !! NO USE THIS CLASS IN "netane" because it is no useful !!
        // 引数1: リサイズしたいテキストが含まれているTextView
        // 引数2: 最小のテキストサイズ(単位:sp)
        int viewHeight = textView.getHeight();	// Viewの縦幅
        int viewWidth = textView.getWidth();	// Viewの横幅

        // 最小のテキストサイズの型 sp → px
        final float MIN_TEXT_SIZE = minTextSize * Resources.getSystem().getDisplayMetrics().scaledDensity;

        // テキストサイズ
        float textSize = textView.getTextSize();

        // Paintにテキストサイズ設定
        Paint paint = new Paint();
        paint.setTextSize(textSize);

        // テキストの縦幅取得
        FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

        // テキストの横幅取得
        float textWidth = paint.measureText(textView.getText().toString());

        // 縦幅と、横幅が収まるまでループ
        while (viewHeight < textHeight | viewWidth < textWidth)
        {
            // 調整しているテキストサイズが、定義している最小サイズ以下か。
            if (MIN_TEXT_SIZE >= textSize)
            {
                // 最小サイズ以下になる場合は最小サイズ
                textSize = MIN_TEXT_SIZE;
                break;
            }

            // テキストサイズをデクリメント
            textSize--;

            // Paintにテキストサイズ設定
            paint.setTextSize(textSize);

            // テキストの縦幅を再取得
            fm = paint.getFontMetrics();
            textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

            // テキストの横幅を再取得
            textWidth = paint.measureText(textView.getText().toString());
        }

        // テキストサイズ設定
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}

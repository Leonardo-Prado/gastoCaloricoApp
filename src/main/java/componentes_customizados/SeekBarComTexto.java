package componentes_customizados;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class SeekBarComTexto extends android.support.v7.widget.AppCompatSeekBar {
    private int offset = 0;
    private int textSize = 14;
    public SeekBarComTexto(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int thumb_x = (int) (( (double)this.getProgress()/this.getMax() ) * ((double)this.getWidth()- 10));
        float middle = (float) (this.getHeight());
        Paint paint = new Paint();
        paint.setColor(Color.MAGENTA);
        paint.setTextSize(textSize);
        c.drawText(""+(this.getProgress()+getOffset()), thumb_x, middle, paint);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}

package troop.com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.troop.filelogger.Logger;

/**
 * Created by troop on 07.12.2015.
 */
public class RotatingSeekbar extends View
{
    private String[] Values = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30,60,180".split(",");
    private int currentValue = 3;
    private Paint paint;
    private int viewWidth =0;
    private int viewHeight = 0;
    private int itemHeight = 0;
    //height of one item in pixel
    private int allItemsHeight = 0;
    private int realMin = 0;
    private int realMax = 0;
    private int currentPosToDraw = 0;
    private SeekBar.OnSeekBarChangeListener mListener;
    private int textsize = 8;
    //holds the distance from the last swipe(how faster it was how bigger is the vale) and is used as base gravity for autoscroll how fast it moves
    private int distanceInPixelFromLastSwipe = 0;
    private boolean autoscroll = false;
    private int textColor = Color.WHITE;
    boolean debug = true;
    final String TAG = RotatingSeekbar.class.getSimpleName();
    //this handels how much get added or substracted from @distanceInPixelFromLastSwipe when autoscroll = true
    final int scrollsubstract = 1;
    private Handler handler;

    final int VISIBLE_ITEMS_INVIEW = 16;

    public RotatingSeekbar(Context context) {
        super(context);
        init(context,null);
    }

    public RotatingSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RotatingSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs)
    {
        handler = new Handler();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.RIGHT);
        textsize = (int)convertDpiToPixel(textsize);
        setProgress(currentValue, false);
    }

    private void log(String msg)
    {
        if (debug)
            Logger.d(TAG, msg);
    }

    private void redraw()
    {
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        //calculates the item height depending on view height and itemscount that should be visible
        this.itemHeight = viewHeight /VISIBLE_ITEMS_INVIEW;
        //calc how big the view is when all items would be drawn
        this.allItemsHeight = itemHeight * Values.length + itemHeight;
        /*
         * calc the maximal minmal pos that could drawn,
         * we use as base the center of the view that why it can get < 0
         */
        realMin = -viewHeight/2 -itemHeight/2;
        //calc the maximal pos that could drawn
        realMax = allItemsHeight - viewHeight/2 - itemHeight*2;
        setProgress(currentValue, false);

        redraw();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint.setColor(textColor);
        paint.setTextSize(textsize);
        for(int i = 0; i< Values.length; i++)
        {

            int dif = currentValue -i;
            if (dif < 0)
                dif *= -1;
            if (dif <= VISIBLE_ITEMS_INVIEW/2) {
                paint.setAlpha(switchalpha(dif));
                paint.setStrokeWidth(1);
                int xpos = i * itemHeight + textsize + currentPosToDraw + (itemHeight / 2 - textsize / 2);
                canvas.drawLine(viewWidth - convertDpiToPixel(30), xpos - textsize / 2, viewWidth - 20, xpos - textsize / 2, paint);
                canvas.drawText(Values[i], viewWidth / 2, xpos, paint);
            }
        }
        paint.setAlpha(255);
        paint.setStrokeWidth(10);
        canvas.drawLine(viewWidth - convertDpiToPixel(20), viewHeight / 2 + itemHeight / 2, viewWidth, viewHeight / 2 + itemHeight / 2, paint);
    }

    private int switchalpha(int pos)
    {
        switch (pos)
        {
            case 8:
                return 0;
            case 7: return 31;
            case 6: return 62;
            case 5: return 93;
            case 4: return 124;
            case 3: return 155;
            case 2: return 186;
            case 1: return 217;
            case 0: return 255;
            default:return 0;
        }
    }

    private float convertDpiToPixel(int dpi)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, getResources().getDisplayMetrics());
    }

    //holds the position when user touched down
    private int startY;
    private boolean sliderMoving = false;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean throwevent = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startY = (int)event.getY();
                autoscroll = false;
                throwevent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int disy = 0;
                if (!sliderMoving)
                {
                    disy = getSignedDistance(startY, (int) event.getY());
                    if (disy > 40 || disy < -40) {
                        sliderMoving = true;
                        if (mListener != null)
                            mListener.onStartTrackingTouch(null);
                    }
                }
                if (sliderMoving)
                {
                    distanceInPixelFromLastSwipe = getSignedDistance(startY, (int) event.getY());
                    int newpos = currentPosToDraw - distanceInPixelFromLastSwipe;
                    int positivepos = newpos *-1;

                    if (positivepos < realMax && positivepos > realMin)
                    {
                        currentPosToDraw = newpos;
                        checkifCurrentValueHasChanged();
                        startY = (int) event.getY();
                    }
                }
                throwevent =sliderMoving;
                break;
            case MotionEvent.ACTION_UP:
                if (sliderMoving)
                {
                    sliderMoving = false;
                    if (mListener != null)
                        mListener.onStopTrackingTouch(null);
                    throwevent = false;
                    if ((distanceInPixelFromLastSwipe > 0 && distanceInPixelFromLastSwipe > 10) || (distanceInPixelFromLastSwipe < 0 && distanceInPixelFromLastSwipe <-10))
                    {
                        autoscroll = true;
                        handleAutoScroll();
                    }
                    else
                    {
                        setProgress(currentValue,true);
                    }
                }
                break;
        }
        redraw();
        return throwevent;
    }

    private void handleAutoScroll()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (!autoscroll)
                    return;
                int newpos = currentPosToDraw - distanceInPixelFromLastSwipe -scrollsubstract;
                int positivepos = newpos *-1;
                if (positivepos <= realMax && positivepos >= realMin)
                {
                    log("scroll pos:" + newpos +" max:" + realMax + " min:" + realMin);
                    boolean rerun = false;
                    if (distanceInPixelFromLastSwipe < 0 && distanceInPixelFromLastSwipe + scrollsubstract < 0) {
                        distanceInPixelFromLastSwipe += scrollsubstract;
                        rerun = true;
                        currentPosToDraw -= distanceInPixelFromLastSwipe;
                        checkifCurrentValueHasChanged();
                    } else if (distanceInPixelFromLastSwipe > 0 && distanceInPixelFromLastSwipe - scrollsubstract > 0) {
                        distanceInPixelFromLastSwipe -= scrollsubstract;
                        rerun = true;
                        currentPosToDraw -= distanceInPixelFromLastSwipe;
                        checkifCurrentValueHasChanged();
                    }
                    else
                    {
                        checkifCurrentValueHasChanged();
                        distanceInPixelFromLastSwipe = 0;
                        setProgress(currentValue,true);
                        rerun = false;
                    }
                    if (rerun)
                        handleAutoScroll();
                }
                else
                {
                    autoscroll = false;
                    distanceInPixelFromLastSwipe = 0;
                    if(positivepos > realMax)
                        setProgress(Values.length-1,true);
                    else if (positivepos < realMin)
                        setProgress(0,true);
                    else {
                        checkifCurrentValueHasChanged();
                        if (currentValue > Values.length -1)
                            currentValue = Values.length -1;
                        if (currentValue < 0)
                            currentValue = 0;
                        setProgress(currentValue,true);

                    }
                    log("scroll pos:" + newpos + " max:" + realMax + " min:" + realMin);
                }
                redraw();
            }
        });
    }

    private void checkifCurrentValueHasChanged() {
        int item = ((currentPosToDraw + realMin) /itemHeight);
        if (item < 0)
            item *= -1;
        if (item != currentValue)
        {
            Logger.d("RotatingSeekbar", "currentpos" + currentPosToDraw + "item " + item);
            currentValue = item;
            if (mListener != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onProgressChanged(null, currentValue, true);
                    }
                });

        }
    }

    private int getSignedDistance(int start, int current)
    {
        return start -current;
    }

    public int getProgress()
    {
        return currentValue;
    }

    public void setProgress(int progress, boolean throwevent)
    {
        //int item = ((currentPosToDraw + realMin) /itemHeight) *1;
        currentValue = progress;
        Logger.d("RotatingSeekbar", "setprogres" +progress);
        currentPosToDraw = ((progress *itemHeight + itemHeight/2 ) + realMin) * -1;
        redraw();
        if (mListener != null && throwevent)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onProgressChanged(null, currentValue, true);
                }
            });

    }
    public String GetCurrentString()
    {
        return Values[currentValue];
    }

    public void SetStringValues(String[] ar)
    {
        this.Values = ar;
        this.itemHeight = viewHeight /16;
        this.allItemsHeight = itemHeight * Values.length + itemHeight;
        realMin = -viewHeight/2 -itemHeight/2;
        realMax = allItemsHeight - viewHeight/2;
        redraw();
    }
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener mListener)
    {
        this.mListener = mListener;
    }
}

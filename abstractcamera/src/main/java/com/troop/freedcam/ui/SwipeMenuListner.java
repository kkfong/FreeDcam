package com.troop.freedcam.ui;


import android.view.MotionEvent;

/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner extends TouchHandler
{
    I_swipe swipehandler;

    public SwipeMenuListner(I_swipe swipehandler)
    {
        this.swipehandler = swipehandler;
    }

    protected void doLeftToRightSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doLeftToRightSwipe();
    }

    protected void doRightToLeftSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doRightToLeftSwipe();
    }

    protected void doTopToBottomSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doTopToBottomSwipe();
    }

    protected void doBottomToTopSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doBottomToTopSwipe();
    }

    @Override
    protected void OnClick(int x, int y) {
        swipehandler.onClick(x,y);
    }

    @Override
    protected void OnMotionEvent(MotionEvent event) {
        if (swipehandler != null)
            swipehandler.onMotionEvent(event);
    }

    public void LeftToRightSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doLeftToRightSwipe();
    }

    public void RightToLeftSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doRightToLeftSwipe();
    }

    public void TopToBottomSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doTopToBottomSwipe();
    }

    public void BottomToTopSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doBottomToTopSwipe();
    }
}

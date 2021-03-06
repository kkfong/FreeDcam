package troop.com.themesample.handler;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AbstractFocusImageHandler;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.ImageViewTouchAreaHandler;

import troop.com.themesample.R;
import troop.com.themesample.SampleThemeFragment;


/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends AbstractFocusImageHandler
{
    private AbstractCameraUiWrapper wrapper;
    protected ImageView focusImageView;
    final int crosshairShowTime = 5000;
    int disHeight;
    int disWidth;
    int marginLeft;
    int marginRight;
    int recthalf;
    protected ImageView cancelFocus;
    protected ImageView meteringArea;
    protected ImageView awbArea;
    FocusRect meteringRect;
    FocusRect awbRect;
    static final int MAX_DURATION = 3500;
    private boolean focusWasVisible = false;
    private boolean meteringWasVisible = false;
    private boolean wbWasVisible = false;



    public FocusImageHandler(View view, Fragment fragment)
    {
        super(view, fragment);
        init(view);

        recthalf = fragment.getResources().getDimensionPixelSize(R.dimen.crosshairwidth)/2;
        //focusImageView.setVisibility(View.GONE);

        cancelFocus.setVisibility(View.GONE);
        cancelFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapper.cameraHolder.CancelFocus();
                cancelFocus.setVisibility(View.GONE);
            }
        });


        meteringArea.setVisibility(View.GONE);
        if (wrapper != null)
            meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, wrapper, meteringTouch, true));

        awbArea.setOnTouchListener(new ImageViewTouchAreaHandler(awbArea, wrapper,awbTouch, true));
        awbArea.setVisibility(View.GONE);

    }

    public void HideImages(boolean hide)
    {
        if (hide)
        {
            if (focusImageView.getVisibility() == View.VISIBLE)
                focusWasVisible = true;
            else
                focusWasVisible = false;
            if (meteringArea.getVisibility() == View.VISIBLE)
                meteringWasVisible = true;
            else
                meteringWasVisible = false;
            if (awbArea.getVisibility() == View.VISIBLE)
                wbWasVisible = true;
            else
                wbWasVisible = false;
            focusImageView.setVisibility(View.GONE);
            meteringArea.setVisibility(View.GONE);
            awbArea.setVisibility(View.GONE);
        }
        else {
            if (focusWasVisible)
                focusImageView.setVisibility(View.VISIBLE);
            if (wbWasVisible)
                awbArea.setVisibility(View.VISIBLE);
            if (meteringWasVisible)
                meteringArea.setVisibility(View.VISIBLE);
        }
    }

    protected void init(View view)
    {
        focusImageView = (ImageView)view.findViewById(troop.com.themesample.R.id.imageView_Crosshair);
        cancelFocus = (ImageView)view.findViewById(troop.com.themesample.R.id.imageViewFocusClose);
        meteringArea = (ImageView)view.findViewById(troop.com.themesample.R.id.imageView_meteringarea);
        awbArea = (ImageView)view.findViewById(troop.com.themesample.R.id.imageView_awbarea);
    }

    public void SetCamerUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.wrapper = cameraUiWrapper;
        if(cameraUiWrapper instanceof CameraUiWrapper || cameraUiWrapper instanceof CameraUiWrapperApi2) {
            meteringRect = centerImageView(meteringArea);
            meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, wrapper, meteringTouch, true));
            if (wrapper.Focus.isAeMeteringSupported())
            {
                meteringArea.setVisibility(View.VISIBLE);
            }
            else {
                meteringArea.setVisibility(View.GONE);

            }
        }
        else
        {
            meteringArea.setVisibility(View.GONE);
        }
        /*if(cameraUiWrapper instanceof CameraUiWrapperApi2)
        {
            awbRect = centerImageView(awbArea);
            if(wrapper.Focus.isWbMeteringSupported())
                awbArea.setVisibility(View.VISIBLE);
            else
                awbArea.setVisibility(View.GONE);
        }
        else*/
            awbArea.setVisibility(View.GONE);
        if (wrapper.Focus != null)
            wrapper.Focus.focusEvent = this;
    }

    @Override
    public void FocusStarted(FocusRect rect)
    {

        if (!(wrapper instanceof CameraUiWrapperSony))
        {
            disWidth = wrapper.getPreviewWidth();
            disHeight = wrapper.getPreviewHeight();
            int margineleft = wrapper.getMargineLeft();
            //handler.removeCallbacksAndMessages(null);

            if (rect == null)
            {
                int halfwidth = disWidth / 2;
                int halfheight = disHeight / 2;
                rect = new FocusRect(halfwidth - recthalf, halfheight - recthalf, halfwidth + recthalf, halfheight + recthalf,halfwidth,halfheight);
            }
            final RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) focusImageView.getLayoutParams();
            mParams.leftMargin = rect.left;
            //mParams.rightMargin = x +half;
            mParams.topMargin = rect.top;

            focusImageView.post(new Runnable() {
                @Override
                public void run() {
                    focusImageView.setLayoutParams(mParams);
                    focusImageView.setBackgroundResource(troop.com.themesample.R.drawable.crosshair_circle_normal);
                    focusImageView.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(focusImageView.getContext(), R.anim.scale_focusimage);
                    focusImageView.startAnimation(anim);
                }
            });

        }
    }

    @Override
    public void FocusFinished(final boolean success)
    {
        if (!(wrapper instanceof CameraUiWrapperSony)) {
            focusImageView.post(new Runnable() {
                @Override
                public void run() {
                    if (success)
                        focusImageView.setBackgroundResource(troop.com.themesample.R.drawable.crosshair_circle_success);
                    else
                        focusImageView.setBackgroundResource(troop.com.themesample.R.drawable.crosshair_circle_failed);

                    focusImageView.setAnimation(null);
                    //handler.postDelayed(hideCrosshair, crosshairShowTime);
                }
            });
        }

    }

    @Override
    public void FocusLocked(final boolean locked)
    {
        cancelFocus.post(new Runnable() {
            @Override
            public void run() {
                if (locked)
                    cancelFocus.setVisibility(View.VISIBLE);
                else
                    cancelFocus.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void TouchToFocusSupported(boolean isSupported)
    {
        if (!isSupported)
            focusImageView.setVisibility(View.GONE);
    }

    @Override
    public void AEMeteringSupported(final boolean isSupported)
    {
        meteringArea.post(new Runnable() {
            @Override
            public void run() {
                if (isSupported)
                    meteringArea.setVisibility(View.VISIBLE);
                else
                    meteringArea.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void AWBMeteringSupported(boolean isSupported) {
        if (isSupported)
            awbArea.setVisibility(View.VISIBLE);
        else
            awbArea.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (wrapper instanceof CameraUiWrapperSony)
            wrapper.Focus.SetMotionEvent(event);
        return false;
    }


    /*private Handler handler = new Handler();
    Runnable hideCrosshair = new Runnable() {
        @Override
        public void run()
        {
            focusImageView.setBackgroundResource(R.drawable.crosshair_normal);
            focusImageView.setVisibility(View.GONE);
        }
    };*/

    ImageViewTouchAreaHandler.I_TouchListnerEvent meteringTouch = new ImageViewTouchAreaHandler.I_TouchListnerEvent() {
        @Override
        public void onAreaCHanged(FocusRect imageRect, int previewWidth, int previewHeight) {
            if (wrapper != null)
                wrapper.Focus.SetMeteringAreas(imageRect,previewWidth, previewHeight);
        }

        @Override
        public void OnAreaClick(int x, int y) {
            OnClick(x,y);
        }

        @Override
        public void OnAreaLongClick(int x, int y)
        {
            if (wrapper.camParametersHandler.ExposureLock != null && wrapper.camParametersHandler.ExposureLock.IsSupported())
            {
                wrapper.camParametersHandler.ExposureLock.SetValue("true",true);
                Vibrator v = (Vibrator) focusImageView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (v.hasVibrator())
                    v.vibrate(50);}

        }

        @Override
        public void IsMoving(boolean moving)
        {
            if (moving && wrapper.camParametersHandler.ExposureLock != null && wrapper.camParametersHandler.ExposureLock.IsSupported() && wrapper.camParametersHandler.ExposureLock.GetValue().equals("true"))
            {
                wrapper.camParametersHandler.ExposureLock.SetValue("false",true);
            }
            SampleThemeFragment sampleThemeFragment = (SampleThemeFragment)FocusImageHandler.this.fragment.getParentFragment();
            if(sampleThemeFragment != null)
                sampleThemeFragment.DisablePagerTouch(moving);
        }
    };

    ImageViewTouchAreaHandler.I_TouchListnerEvent awbTouch = new ImageViewTouchAreaHandler.I_TouchListnerEvent() {
        @Override
        public void onAreaCHanged(FocusRect imageRect, int previewWidth, int previewHeight) {
            if (wrapper != null)
                wrapper.Focus.SetAwbAreas(imageRect, previewWidth, previewHeight);
        }

        @Override
        public void OnAreaClick(int x, int y) {
            OnClick(x,y);
        }

        @Override
        public void OnAreaLongClick(int x, int y) {

        }

        @Override
        public void IsMoving(boolean moving) {

        }
    };

    /*
    This listen to clicks that happen inside awb or exposuremetering
    and translate that postion into preview coordinates for touchtofocus
     */
    public void OnClick(int x, int y)
    {
        if (wrapper == null || wrapper.Focus == null)
            return;
        disWidth = wrapper.getPreviewWidth();
        disHeight = wrapper.getPreviewHeight();
        marginLeft = wrapper.getMargineLeft();
        marginRight = wrapper.getMargineRight();
        if (x > marginLeft && x < disWidth + marginLeft ) {
            if (x < marginLeft + recthalf)
                x = marginLeft + recthalf;
            if (x > marginRight - recthalf)
                x = marginRight - recthalf;
            if (y < recthalf)
                y = recthalf;
            if (y > disHeight - recthalf)
                y = disHeight - recthalf;
            FocusRect rect = new FocusRect(x - recthalf, x + recthalf, y - recthalf, y + recthalf,x,y);
            if (wrapper.Focus != null)
                wrapper.Focus.StartTouchToFocus(rect, meteringRect, disWidth, disHeight);
        }
    }

    /*
    returns the display size depending on sdk
     */

    /*
    Centers the attached Imageview
     */
    private FocusRect centerImageView(ImageView imageview)
    {
        int width = 0;
        int height = 0;

        if(fragment == null || fragment.getActivity() == null)
            return null;
        if (Build.VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager)fragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = size.x;
                height = size.y;
            }
            else
            {
                height = size.x;
                width = size.y;
            }
        }
        else
        {
            DisplayMetrics metrics = fragment.getActivity().getResources().getDisplayMetrics();
            if (fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            }
            else
            {
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            }

        }
        imageview.setX(width/2 - recthalf);
        imageview.setY(height/2 - recthalf);

        return new FocusRect((int)imageview.getX() - recthalf, (int)imageview.getX() + recthalf, (int)imageview.getY() - recthalf, (int)imageview.getY() + recthalf,(int)imageview.getX(),(int)imageview.getY());
    }



}

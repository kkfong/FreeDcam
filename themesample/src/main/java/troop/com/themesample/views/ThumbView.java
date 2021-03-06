package troop.com.themesample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_WorkEvent;
import com.troop.freedcam.ui.FreeDPool;

import java.io.File;

import troop.com.imageviewer.BitmapHelper;

import troop.com.imageviewer.screenslide.ScreenSlideFragment;
import troop.com.themesample.R;

/**
 * Created by troop on 13.06.2015.
 */
public class ThumbView extends ImageView implements I_WorkEvent, View.OnClickListener, BitmapHelper.FileEvent
{
    private final  String TAG = ThumbView.class.getSimpleName();
    private boolean hasWork = false;
    private AbstractCameraUiWrapper cameraUiWrapper;
    private Bitmap bitmap;
    private File lastFile;
    private Bitmap mask;
    private ScreenSlideFragment.I_ThumbClick click;
    private int mImageThumbSize = 0;
    private Context context;

    public ThumbView(Context context) {
        super(context);
        this.setOnClickListener(this);
        this.setBackgroundDrawable(context.getResources().getDrawable(troop.com.themesample.R.drawable.thumbnail));
        this.context = context;
    }

    public ThumbView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setOnClickListener(this);
        this.setBackgroundDrawable(context.getResources().getDrawable(troop.com.themesample.R.drawable.thumbnail));
        this.context = context;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            mask = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.maskthumb);
            mImageThumbSize = context.getResources().getDimensionPixelSize(troop.com.themesample.R.dimen.image_thumbnails_size);
            BitmapHelper.AddFileListner(this);

            WorkHasFinished(BitmapHelper.getFiles().get(0).getFile());
        }
        catch (NullPointerException ex)
        {}
        catch (IndexOutOfBoundsException ex){}

    }

    public void INIT(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if(cameraUiWrapper != null && cameraUiWrapper.moduleHandler != null && cameraUiWrapper.moduleHandler.moduleEventHandler != null)
            cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(this);


    }

    @Override
    public String WorkHasFinished(final File filePath)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                if (!hasWork) {
                    hasWork = true;
                    Logger.d(TAG, "Load Thumb " + filePath.getName());
                    try {
                        showThumb(filePath);
                    } catch (NullPointerException ex) {
                    }

                    hasWork = false;
                }
            }
        });
        return null;
    }

    boolean firststart = true;
    private void showThumb(final File filePath)
    {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = BitmapHelper.getBitmap(filePath, true, mImageThumbSize, mImageThumbSize);
        final Bitmap drawMap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas drawc = new Canvas(drawMap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        if (bitmap != null && !bitmap.isRecycled())
            drawc.drawBitmap(bitmap, 0, 0, null);
        drawc.drawBitmap(mask, 0, 0, paint);
        //drawc.drawBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.thumbnail),0,0,null);
        paint.setXfermode(null);


        ThumbView.this.post(new Runnable() {
            @Override
            public void run() {
                ThumbView.this.setImageBitmap(drawMap);
                if (firststart == false)
                    click.newImageRecieved(filePath);
                firststart = false;
            }
        });
    }

    public void SetOnThumBlickListner(ScreenSlideFragment.I_ThumbClick click)
    {
        this.click = click;
    }

    @Override
    public void onClick(View v)
    {
        if (click != null)
            click.onThumbClick();


    }

    @Override
    public void onFileDeleted(File file) {
        WorkHasFinished(BitmapHelper.getFiles().get(0).getFile());
    }

    @Override
    public void onFileAdded(File file) {

    }
}

package com.troop.freedcam.ui.handler;

import android.util.Log;
import android.view.KeyEvent;

import com.troop.filelogger.Logger;
import com.troop.freedcam.MainActivity;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;


/**
 * Created by troop on 29.08.2014.
 */
public class HardwareKeyHandler
{
    private final MainActivity activity;
    private AbstractCameraUiWrapper cameraUiWrapper;
    private boolean longKeyPress = false;
    private static String TAG = "freedcam.HardwareKeyHandler";


    public HardwareKeyHandler(MainActivity activity)
    {
        this.activity = activity;
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public boolean OnKeyUp(int keyCode, KeyEvent event)
    {
        boolean set = false;
        longKeyPress = false;
        int appSettingsKeyShutter = 0;

        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLP))
            appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLM))
            appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.Hook) || AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
            appSettingsKeyShutter = KeyEvent.KEYCODE_HEADSETHOOK;

        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == appSettingsKeyShutter || keyCode == KeyEvent.KEYCODE_UNKNOWN)
        {
            Logger.d(TAG, "KeyUp");
            cameraUiWrapper.moduleHandler.DoWork();
        }
        //shutterbutton full pressed
        if (keyCode == KeyEvent.KEYCODE_CAMERA)
        {
            cameraUiWrapper.moduleHandler.DoWork();
        }
        // shutterbutton half pressed
        //if (keyCode == KeyEvent.KEYCODE_FOCUS)

        if (keyCode == KeyEvent.KEYCODE_BACK)
            activity.finish();
        return true;
    }

    public boolean OnKeyLongPress(int keyCode, KeyEvent event)
    {
        boolean set = false;
        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            set = true;


        }

        return set;
    }

    public boolean OnKeyDown(int keyCode, KeyEvent event)
    {
        /*if (event.isLongPress() && !longKeyPress) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                Logger.d(TAG, "LongKeyPress for Headsethook");
                longKeyPress = true;
                activity.shutterHandler.OnLongClick();

            }

        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                activity.manualMenuHandler.Decrase();
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                activity.manualMenuHandler.Incrase();
        }*/

        return true;
    }
}

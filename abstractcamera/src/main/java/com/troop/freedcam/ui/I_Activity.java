package com.troop.freedcam.ui;

import android.net.Uri;
import android.view.SurfaceView;

import java.io.File;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void SwitchCameraAPI(String Api);
    void SetTheme(String Theme);
    int[] GetScreenSize();
    void ShowHistogram(boolean enable);
    void loadImageViewerFragment(File file);
    void loadCameraUiFragment();
    void closeActivity();
    void ChooseSDCard(I_OnActivityResultCallback callback);
    public interface I_OnActivityResultCallback
    {
        void onActivityResultCallback(Uri uri);
    }
}



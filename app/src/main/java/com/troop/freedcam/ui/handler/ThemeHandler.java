package com.troop.freedcam.ui.handler;

import android.widget.LinearLayout;

import com.troop.freedcam.MainActivity;
import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.SampleThemeFragment;

/**
 * Created by Ingo on 14.03.2015.
 */
public class ThemeHandler implements I_ModuleEvent
{
    private MainActivity activity_v2;
    private LinearLayout uiLayout;
    private AbstractFragment uiFragment;

    public ThemeHandler(MainActivity activity_v2)
    {
        this.activity_v2 = activity_v2;
        uiLayout = (LinearLayout) activity_v2.findViewById(R.id.themeFragmentholder);
    }

    public AbstractFragment getCurrenttheme()
    {
        return  uiFragment;
    }


    public AbstractFragment GetThemeFragment(boolean infalte, AbstractCameraUiWrapper cameraUiWrapper)
    {
        String theme = AppSettingsManager.APPSETTINGSMANAGER.GetTheme();
        if(theme.equals("Ambient") || theme.equals("Material")|| theme.equals("Minimal") || theme.equals("Nubia") || theme.equals("Classic")) {
            theme = "Sample";
            AppSettingsManager.APPSETTINGSMANAGER.SetTheme("Sample");
        }
        if (theme.equals("Sample"))
        {
            SampleThemeFragment sampleThemeFragment = new SampleThemeFragment();
            sampleThemeFragment.SetStuff(activity_v2);
            sampleThemeFragment.SetCameraUIWrapper(cameraUiWrapper);
            uiFragment = sampleThemeFragment;
        }
        if (infalte)
            inflateFragment(uiFragment);
        return uiFragment;
    }

    private void inflateFragment(AbstractFragment fragment)
    {
        android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
        transaction.replace(R.id.themeFragmentholder, fragment, "Main");
        transaction.commitAllowingStateLoss();
    }


    @Override
    public String ModuleChanged(String module) {

        return null;
    }




}

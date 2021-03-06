package com.troop.freedcam.i_camera.parameters;

import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class ParameterExternalShutter extends AbstractModeParameter
{
    public static String VoLP = "Vol+";
    public static String VoLM = "Vol-";
    public static String Hook = "Hook";

    private String[] values = {VoLP, VoLM, Hook};

    public ParameterExternalShutter()
    {
        super(null);
    }

    public boolean IsSupported()
    {
        return true;
    }
    public void setIsSupported(boolean s)
    {

    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_EXTERNALSHUTTER, valueToSet);
    }

    public String GetValue()
    {
        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
            return "Hook";
        else
            return AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER);
    }

    public String[] GetValues() {
        return values;
    }
}

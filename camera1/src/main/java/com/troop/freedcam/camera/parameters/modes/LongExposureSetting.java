package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 27.10.2014.
 */
public class LongExposureSetting extends BaseModeParameter
{
    public LongExposureSetting(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super( handler,parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

    }

    @Override
    public String GetValue() {
        return "";
    }

    @Override
    public String[] GetValues() {
        return new String[]{"1","2","3","4","5","10", "15", "20","30","60","90","180" };
    }
}

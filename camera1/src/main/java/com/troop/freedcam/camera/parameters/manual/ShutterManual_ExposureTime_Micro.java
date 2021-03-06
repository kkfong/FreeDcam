package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_Micro extends BaseManualParameter
{
    final String TAG = ShutterManual_ExposureTime_Micro.class.getSimpleName();
    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_Micro(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues) {
        super(parameters, "", "", "", camParametersHandler,1);
        try {
            if (shuttervalues == null)
            {
                if (!parameters.get("min-exposure-time").contains("."))
                {
                    int min = Integer.parseInt(parameters.get("min-exposure-time"));
                    int max = Integer.parseInt(parameters.get("max-exposure-time"));
                    stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                }
                else
                {
                    double tmpMin = Double.parseDouble(parameters.get("min-exposure-time"))*1000000;
                    double tmpMax = Double.parseDouble(parameters.get("max-exposure-time"))*1000000;
                    int min = (int)tmpMin;
                    int max = (int)tmpMax;
                    stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                }

            }
            else
                stringvalues = shuttervalues;
            parameters.put("exposure-time", "0");
            this.isSupported = true;

        } catch (NumberFormatException ex) {
            Logger.exception(ex);
            isSupported = false;
        }
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    protected void setvalue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals("Auto"))
        {
            String shutterstring = StringUtils.FormatShutterStringToDouble(stringvalues[currentInt]);
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = StringUtils.getMicroSec(shutterstring);
            Logger.d(TAG, " StringUtils.getMicroSec"+ shutterstring);
            parameters.put("exposure-time", shutterstring);
        }
        else
        {
            parameters.put("exposure-time", "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}

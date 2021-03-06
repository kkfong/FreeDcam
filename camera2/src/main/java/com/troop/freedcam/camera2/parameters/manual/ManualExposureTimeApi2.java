package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualExposureTimeApi2 extends AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    protected BaseCameraHolderApi2 cameraHolder;
    boolean canSet = false;
    protected boolean isSupported = false;
    final String TAG = ManualExposureTimeApi2.class.getSimpleName();
    protected boolean firststart = true;
    private int onetoThirty = 0;
    private int millimax = 0;
    public ManualExposureTimeApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
        try {
            findMinMaxValue();
        }
        catch (NullPointerException ex)
        {
            this.isSupported = false;
        }
    }

    private void findMinMaxValue()
    {

        Logger.d(TAG, "max exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper());
        Logger.d(TAG, "min exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower());
        //866 975 130 = 0,8sec
        if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4) && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
            millimax = 60000000;
        else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4) && Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
            millimax = 45000000;
        else if (DeviceUtils.IS(DeviceUtils.Devices.Samsung_S6_edge_plus))
            millimax = 10000000;
        else if (DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8982_8994))
            millimax = 10000000;
        else
            millimax = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper()).intValue() / 1000;
        int millimin = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower()).intValue() / 1000;
        stringvalues = StringUtils.getSupportedShutterValues(millimin, millimax,false);
        for (int i = 0; i < stringvalues.length; i++)
            if (stringvalues[i].equals("1/30"))
                onetoThirty = i;
    }

    @Override
    public int GetValue()
    {

        return currentInt;
    }

    @Override
    public String GetStringValue()
    {

        return stringvalues[currentInt];

    }


    @Override
    public String[] getStringValues() {
        return stringvalues;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        if (valueToSet >= stringvalues.length)
            valueToSet = stringvalues.length - 1;


        currentInt = valueToSet;
        if (valueToSet > 0) {
            long val = (long) (StringUtils.getMilliSecondStringFromShutterString(stringvalues[valueToSet]) * 1000f);
            Logger.d(TAG, "ExposureTimeToSet:" + val);
            if (val > 800000000) {
                Logger.d(TAG, "ExposureTime Exceed 0,8sec for preview, set it to 0,8sec");
                val = 800000000;
            }
            //check if calced value is not bigger then max returned from cam
            if (val > millimax*1000)
                val = millimax *1000;
            if (cameraHolder == null || cameraHolder.mPreviewRequestBuilder == null)
                return;
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                        null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            } catch (NullPointerException ex) {
                Logger.exception(ex);
            }
            ThrowCurrentValueChanged(valueToSet);
        }
    }

    @Override
    public boolean IsSupported()
    {
        this.isSupported = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE) != null;
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return canSet;
    }

    //Gets thrown from AEmodeApi2
    @Override
    public void onValueChanged(String val)
    {
        if (val.equals("off"))
        {
            canSet = true;
            BackgroundIsSetSupportedChanged(true);
            if (currentInt < onetoThirty)
                SetValue(onetoThirty);
            else
                SetValue(currentInt);
        }
        else {
            canSet = false;
            BackgroundIsSetSupportedChanged(false);
        }
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    //implementation I_ModeParameterEvent END

}

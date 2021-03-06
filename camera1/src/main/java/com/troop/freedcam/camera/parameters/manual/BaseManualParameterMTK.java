package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.parameters.modes.PictureFormatHandler;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 17/04/2016.
 */
public class BaseManualParameterMTK extends AbstractManualParameter
{

    private static String TAG = StringUtils.TAG + BaseManualParameter.class.getSimpleName();
    /**
     * Holds the list of Supported parameters
     */
    HashMap<String, String> parameters;
    /*
     * The name of the current value to get like brightness
     */
    protected String value;

    /**
     * The name of the current value to get like brightness-max
     */
    protected String max_value;
    /**
     * The name of the current value to get like brightness-min
     */
    protected String  min_value;

    protected float step;


    private int default_value = 0;
    public void Set_Default_Value(int val){default_value = val; Logger.d(TAG, "set default to:" + val);}
    public int Get_Default_Value(){return default_value;}

    public void ResetToDefault()
    {
        if (isSupported)
        {
            Logger.d(TAG,"Reset Back from:" + currentInt + " to:" + default_value);
            setvalue(default_value);
            ThrowCurrentValueChanged(default_value);
        }
    }

    /**
     *
     * @param @parameters
     * @param @value
     * @param @max_value
     * @param @min_value
     * @param @camParametersHandler
     */
    public BaseManualParameterMTK(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step) {
        super(camParametersHandler);
        this.parameters = parameters;
        this.value = value;
        this.max_value = maxValue;
        this.min_value = MinValue;
        this.step = step;
        if (!this.value.equals("") && !this.max_value.equals("") && !min_value.equals(""))
        {
            if (parameters.containsKey(this.value) && parameters.containsKey(max_value) && parameters.containsKey(min_value))
            {
                if (!parameters.get(min_value).equals("") && !parameters.get(max_value).equals(""))
                {
                    stringvalues = createStringArray(Integer.parseInt(parameters.get(min_value)), Integer.parseInt(parameters.get(max_value)), step);
                    currentString = parameters.get(this.value);
                    for (int i = 0; i < stringvalues.length; i++) {
                        if (stringvalues[i].equals(currentString)) {
                            currentInt = i;
                            default_value = i;

                        }
                        this.isSupported = true;
                        this.isVisible = isSupported;
                    }

                }
            }
        }
    }
    @Override
    public boolean IsSupported()
    {
        return this.isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsVisible() {
        return super.IsVisible();
    }

    @Override
    public int GetValue()
    {
        return super.GetValue();
    }

    @Override
    protected void setvalue(int valueToset)
    {
        currentInt = valueToset;
        Logger.d(TAG, "set " + value + " to " + valueToset);
        if(stringvalues == null || stringvalues.length == 0)
            return;
        switch (valueToset)
        {
            case 0:
                parameters.put(value, "low");
                break;
            case 1:
                parameters.put(value, "middle");
                break;
            case 2:
                parameters.put(value, "high");
                break;
        }

        parameters.put(value, stringvalues[valueToset]);
        ThrowCurrentValueChanged(valueToset);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }


    public AbstractModeParameter.I_ModeParameterEvent GetPicFormatListner()
    {
        return picformatListner;
    }

    private AbstractModeParameter.I_ModeParameterEvent picformatListner = new AbstractModeParameter.I_ModeParameterEvent()
    {

        @Override
        public void onValueChanged(String val)
        {
            if (val.equals(PictureFormatHandler.CaptureMode[PictureFormatHandler.JPEG]) && BaseManualParameterMTK.this.isSupported)
            {
                isVisible = true;
                BackgroundIsSupportedChanged(true);
            }
            else {
                isVisible = false;
                BackgroundIsSupportedChanged(false);
                ResetToDefault();
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
    };

    public I_ModuleEvent GetModuleListner()
    {
        return moduleListner;
    }

    private I_ModuleEvent moduleListner =new I_ModuleEvent() {
        @Override
        public String ModuleChanged(String module)
        {
            if (module.equals(AbstractModuleHandler.MODULE_VIDEO) && isSupported)
                BackgroundIsSupportedChanged(true);
            else if (module.equals(AbstractModuleHandler.MODULE_PICTURE)
                    || module.equals(AbstractModuleHandler.MODULE_INTERVAL)
                    || module.equals(AbstractModuleHandler.MODULE_HDR))
            {
                BackgroundIsSupportedChanged(isVisible);
            }
            return null;
        }
    };
}
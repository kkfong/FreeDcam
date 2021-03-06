package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.Set;

/**
 * Created by troop on 09.04.2016.
 */
public class PreviewZoomManual extends BaseManualParameterSony
{
    private SimpleStreamSurfaceView surfaceView;
    private int zoomFactor = 1;

    public PreviewZoomManual(SimpleStreamSurfaceView surfaceView, ParameterHandlerSony parameterHandlerSony) {
        super("", "", "", parameterHandlerSony);
        this.surfaceView = surfaceView;
        this.isSupported = StringUtils.IS_L_OR_BIG();
        stringvalues = new String[] {"1","2","4","8","10","12","14","16","18","20"};
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }

    @Override
    public void SetValue(int valueToSet) {
        zoomFactor = Integer.parseInt(stringvalues[valueToSet]);
        surfaceView.PreviewZOOMFactor = zoomFactor;
    }

    @Override
    public String GetStringValue() {
        return surfaceView.PreviewZOOMFactor+"";
    }
}

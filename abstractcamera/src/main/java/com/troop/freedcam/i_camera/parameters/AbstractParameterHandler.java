package com.troop.freedcam.i_camera.parameters;

import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractParameterHandler
{
    /**
     * Holds the UI/Main Thread
     */
    protected Handler uiHandler;
    private ArrayList<I_ParametersLoaded> parametersLoadedListner;

    public AbstractManualParameter ManualBrightness;
    public AbstractManualParameter ManualEdge;
    public AbstractManualParameter ManualHue;
    public AbstractManualParameter ManualSharpness;
    public AbstractManualParameter ManualContrast;
    public AbstractManualParameter ManualSaturation;
    public AbstractManualParameter ManualExposure;
    public AbstractManualParameter ManualConvergence;
    public AbstractManualParameter ManualFocus;
    public AbstractManualParameter ManualShutter;
    public AbstractManualParameter ManualFNumber;
    public AbstractManualParameter Burst;
    public AbstractManualParameter CCT;
    public AbstractManualParameter FX;
    public AbstractManualParameter ISOManual;
    public AbstractManualParameter Zoom;
    public AbstractManualParameter Skintone;
    public AbstractManualParameter ProgramShift;
    public AbstractManualParameter PreviewZoom;

    public AbstractModeParameter ColorMode;
    public AbstractModeParameter ExposureMode;
    public AbstractModeParameter FlashMode;
    public AbstractModeParameter IsoMode;
    public AbstractModeParameter AntiBandingMode;
    public AbstractModeParameter WhiteBalanceMode;
    public AbstractModeParameter PictureSize;
    public AbstractModeParameter PictureFormat;
    public AbstractModeParameter HDRMode;
    public AbstractModeParameter JpegQuality;
    //defcomg was here
    public AbstractModeParameter GuideList;
    //done
    public AbstractModeParameter ImagePostProcessing;
    public AbstractModeParameter PreviewSize;
    public AbstractModeParameter PreviewFPS;
    public AbstractModeParameter PreviewFormat;
    public AbstractModeParameter SceneMode;
    public AbstractModeParameter FocusMode;
    public AbstractModeParameter RedEye;
    public AbstractModeParameter LensShade;
    public AbstractModeParameter ZSL;
    public AbstractModeParameter SceneDetect;
    public AbstractModeParameter Denoise;
    public AbstractModeParameter DigitalImageStabilization;
    public AbstractModeParameter VideoStabilization;
    public AbstractModeParameter MemoryColorEnhancement;
    public AbstractModeParameter SkinToneEnhancment;
    public AbstractModeParameter NightMode;
    public AbstractModeParameter NonZslManualMode;
    public AbstractModeParameter AE_Bracket;
    public AbstractModeParameter Histogram;
    public AbstractModeParameter ExposureLock;
    public AbstractModeParameter CDS_Mode;

    public AbstractModeParameter VideoProfiles;
    public AbstractModeParameter VideoProfilesG3;
    public AbstractModeParameter VideoSize;
    public AbstractModeParameter VideoHDR;
    public AbstractModeParameter VideoHighFramerateVideo;
    public AbstractModeParameter LensFilter;
    public AbstractModeParameter CameraMode;
    public AbstractModeParameter Horizont;

    //yet only seen on m9
    public AbstractModeParameter RdiMode;
    public AbstractModeParameter TnrMode;
    public AbstractModeParameter SecureMode;

    //SonyApi
    public AbstractModeParameter ContShootMode;
    public AbstractModeParameter ContShootModeSpeed;
    public AbstractModeParameter ObjectTracking;
    public AbstractModeParameter PostViewSize;
    public AbstractModeParameter Focuspeak;
    public AbstractModeParameter Module;
    public AbstractModeParameter ZoomSetting;
    //public AbstractModeParameter PreviewZoom;
    //
    public AbstractModeParameter ThemeList;
    public boolean isExposureAndWBLocked = false;
    private boolean isDngActive = false;
    public boolean IsDngActive(){ return this.isDngActive; };
    public void SetDngActive(boolean active) {this.isDngActive = active;}

    public AbstractCameraHolder cameraHolder;

    //camera2 modes
    public AbstractModeParameter EdgeMode;
    public AbstractModeParameter ColorCorrectionMode;
    public AbstractModeParameter HotPixelMode;
    public AbstractModeParameter ToneMapMode;
    public AbstractModeParameter ControlMode;

    public AbstractModeParameter oismode;

    public AbstractModeParameter SdSaveLocation;

    public LocationParameter locationParameter;

    public boolean IntervalCapture = false;
    public boolean IntervalCaptureFocusSet = false;

    public AbstractModeParameter IntervalDuration;
    public AbstractModeParameter IntervalShutterSleep;

    public AbstractModeParameter captureBurstExposures;

    public AbstractModeParameter morphoHDR;
    public AbstractModeParameter morphoHHT;

    public AbstractModeParameter aeb1;
    public AbstractModeParameter aeb2;
    public AbstractModeParameter aeb3;



    public AbstractParameterHandler(AbstractCameraHolder cameraHolder, Handler uiHandler)
    {
        this.cameraHolder = cameraHolder;
        this.uiHandler = uiHandler;
        parametersLoadedListner = new ArrayList<I_ParametersLoaded>();
        parametersLoadedListner.clear();

        GuideList = new GuideList(uiHandler);
        ThemeList = new ThemeList(uiHandler);
        locationParameter = new LocationParameter(uiHandler,cameraHolder);
        IntervalDuration = new IntervalDurationParameter(uiHandler);
        IntervalShutterSleep = new IntervalShutterSleepParameter(uiHandler);
        Horizont = new Horizont(uiHandler);
        SdSaveLocation = new SDModeParameter(uiHandler);

    }

    public abstract void SetParametersToCamera(HashMap<String, String> list);
    public abstract void LockExposureAndWhiteBalance(boolean lock);

    public void SetFocusAREA(FocusRect focusAreas, FocusRect meteringAreas){};
    public void SetMeterAREA(FocusRect meteringAreas){};

    public void SetPictureOrientation(int or){};

    public void SetEVBracket(String ev){};

    public void SetAppSettingsToParameters()
    {
        setMode(locationParameter, AppSettingsManager.SETTING_LOCATION);
        setMode(ColorMode, AppSettingsManager.SETTING_COLORMODE);
        setMode(ExposureMode, AppSettingsManager.SETTING_EXPOSUREMODE);
        setMode(FlashMode, AppSettingsManager.SETTING_FLASHMODE);
        setMode(IsoMode, AppSettingsManager.SETTING_ISOMODE);
        setMode(AntiBandingMode, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        setMode(WhiteBalanceMode, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(PictureSize, AppSettingsManager.SETTING_PICTURESIZE);
        setMode(PictureFormat, AppSettingsManager.SETTING_PICTUREFORMAT);
        setMode(oismode, AppSettingsManager.SETTING_OIS);

        setMode(JpegQuality, AppSettingsManager.SETTING_JPEGQUALITY);
        setMode(GuideList, AppSettingsManager.SETTING_GUIDE);
        setMode(ImagePostProcessing, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(SceneMode, AppSettingsManager.SETTING_SCENEMODE);
        setMode(FocusMode, AppSettingsManager.SETTING_FOCUSMODE);
        setMode(RedEye,AppSettingsManager.SETTING_REDEYE_MODE);
        setMode(LensShade,AppSettingsManager.SETTING_LENSSHADE_MODE);
        setMode(ZSL, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        setMode(SceneDetect, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        setMode(Denoise, AppSettingsManager.SETTING_DENOISE_MODE);
        setMode(DigitalImageStabilization, AppSettingsManager.SETTING_DIS_MODE);
        setMode(MemoryColorEnhancement, AppSettingsManager.SETTING_MCE_MODE);
        //setMode(SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE);
        setMode(NightMode, AppSettingsManager.SETTING_NIGHTEMODE);
        setMode(NonZslManualMode, AppSettingsManager.SETTING_NONZSLMANUALMODE);

        setMode(Histogram, AppSettingsManager.SETTING_HISTOGRAM);
        if(VideoProfiles != null)
            setMode(VideoProfiles, AppSettingsManager.SETTING_VIDEPROFILE);
        else if (VideoProfilesG3 != null)
            setMode(VideoProfilesG3, AppSettingsManager.SETTING_VIDEPROFILE);
        setMode(VideoHDR, AppSettingsManager.SETTING_VIDEOHDR);
        setMode(VideoSize, AppSettingsManager.SETTING_VIDEOSIZE);
        setMode(VideoStabilization,AppSettingsManager.SETTING_VIDEOSTABILIZATION);
        setMode(VideoHighFramerateVideo,AppSettingsManager.SETTING_HighFramerateVideo);
        setMode(WhiteBalanceMode,AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(ImagePostProcessing,AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(ColorCorrectionMode, AppSettingsManager.SETTING_COLORCORRECTION);
        setMode(EdgeMode, AppSettingsManager.SETTING_EDGE);
        setMode(HotPixelMode, AppSettingsManager.SETTING_HOTPIXEL);
        setMode(ToneMapMode, AppSettingsManager.SETTING_TONEMAP);
        setMode(ControlMode, AppSettingsManager.SETTING_CONTROLMODE);
        setMode(IntervalDuration,AppSettingsManager.SETTING_INTERVAL_DURATION);
        setMode(IntervalShutterSleep, AppSettingsManager.SETTING_INTERVAL);
        setMode(Horizont, AppSettingsManager.SETTING_HORIZONT);

        setMode(HDRMode, AppSettingsManager.SETTING_HDRMODE);
        setMode(aeb1, AppSettingsManager.SETTING_AEB1);
        setMode(aeb2, AppSettingsManager.SETTING_AEB2);
        setMode(aeb3, AppSettingsManager.SETTING_AEB3);
        setMode(captureBurstExposures, AppSettingsManager.SETTING_CAPTUREBURSTEXPOSURES);
        //setMode(AE_Bracket, AppSettingsManager.SETTING_AEBRACKET);

        setMode(morphoHDR, AppSettingsManager.SETTING_MORPHOHDR);
        setMode(morphoHHT, AppSettingsManager.SETTING_MORPHOHHT);

        //setMode(PreviewZoom, AppSettingsManager.SETTINGS_PREVIEWZOOM);


        setManualMode(ManualContrast, AppSettingsManager.MCONTRAST);
        setManualMode(ManualConvergence,AppSettingsManager.MCONVERGENCE);
        setManualMode(ManualExposure, AppSettingsManager.MEXPOSURE);
        //setManualMode(ManualFocus, AppSettingsManager.MF);
        setManualMode(ManualSharpness,AppSettingsManager.MSHARPNESS);
        setManualMode(ManualShutter, AppSettingsManager.MSHUTTERSPEED);
        setManualMode(ManualBrightness, AppSettingsManager.MBRIGHTNESS);
        //setManualMode(ISOManual, AppSettingsManager.MISO);
        setManualMode(ManualSaturation, AppSettingsManager.MSATURATION);
        //setManualMode(CCT,AppSettingsManager.MWB);


    }

    protected void setMode(AbstractModeParameter parameter, String settingsval)
    {
        if (parameter != null && parameter.IsSupported() && settingsval != null && !settingsval.equals(""))
        {
            if (AppSettingsManager.APPSETTINGSMANAGER.getString(settingsval).equals(""))
                AppSettingsManager.APPSETTINGSMANAGER.setString(settingsval, parameter.GetValue());
            else
                parameter.SetValue(AppSettingsManager.APPSETTINGSMANAGER.getString(settingsval), false);
        }
    }

    protected void setManualMode(AbstractManualParameter parameter, String settingsval)
    {
        if (parameter != null && parameter.IsSupported() && settingsval != null && !settingsval.equals(""))
        {
            if (AppSettingsManager.APPSETTINGSMANAGER.getString(settingsval).equals(""))
                AppSettingsManager.APPSETTINGSMANAGER.setString(settingsval, parameter.GetValue()+"");
            else
                parameter.SetValue(Integer.parseInt(AppSettingsManager.APPSETTINGSMANAGER.getString(settingsval)));
        }
    }

    public void AddParametersLoadedListner(I_ParametersLoaded parametersLoaded)
    {
        parametersLoadedListner.add(parametersLoaded);
    }

    public void ParametersHasLoaded()
    {
        if (parametersLoadedListner == null)
            return;
        for(int i= 0; i< parametersLoadedListner.size(); i++)
        {

            if (parametersLoadedListner.get(i) == null) {
                parametersLoadedListner.remove(i);
                i--;
            }
            else {
                final int t = i;
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (parametersLoadedListner.size()> 0 && t < parametersLoadedListner.size())
                            parametersLoadedListner.get(t).ParametersLoaded();
                    }
                });

            }
        }
    }

    public void CLEAR()
    {

        parametersLoadedListner.clear();
    }
}

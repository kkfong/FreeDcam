package com.troop.freedcam.camera.modules;


import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.IntervalModule;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler extends AbstractModuleHandler
{
    BaseCameraHolder cameraHolder;
    private static String TAG = "freedcam.ModuleHandler";

    public  ModuleHandler (AbstractCameraHolder cameraHolder)
    {
        super(cameraHolder);
        this.cameraHolder = (BaseCameraHolder) cameraHolder;
        initModules();

    }

    protected void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        if (cameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.MTK)
        {
            Logger.d(TAG, "load mtk picmodule");
            PictureModuleMTK thl5000 = new PictureModuleMTK(this.cameraHolder, moduleEventHandler);
            moduleList.put(thl5000.ModuleName(), thl5000);
            IntervalModule intervalModule = new IntervalModule(cameraHolder,moduleEventHandler,thl5000);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }
        else//else //use default pictureModule
        {
            Logger.d(TAG, "load default picmodule");
            PictureModule pictureModule = new PictureModule(this.cameraHolder, moduleEventHandler);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
            IntervalModule intervalModule = new IntervalModule(cameraHolder,moduleEventHandler,pictureModule);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }

        if (cameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG)
        {
            Logger.d(TAG, "load lg videomodule");
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(this.cameraHolder, moduleEventHandler);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else
        {
            Logger.d(TAG, "load default videomodule");
            VideoModule videoModule = new VideoModule(this.cameraHolder, moduleEventHandler);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        Logger.d(TAG, "load hdr module");
        HdrModule hdrModule = new HdrModule(this.cameraHolder, moduleEventHandler);
        moduleList.put(hdrModule.ModuleName(), hdrModule);

        StackingModule sTax = new StackingModule(this.cameraHolder, moduleEventHandler);
        moduleList.put(sTax.ModuleName(),sTax);

        //BurstModule burstModule = new BurstModule(this.cameraHolder, soundPlayer, appSettingsManager, moduleEventHandler);
        //moduleList.put(burstModule.ModuleName(), burstModule);

        //LongExposureModule longExposureModule = new LongExposureModule(this.cameraHolder, appSettingsManager, moduleEventHandler);
        //moduleList.put(longExposureModule.ModuleName(), longExposureModule);
    }

}

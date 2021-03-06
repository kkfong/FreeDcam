package com.troop.freedcam.i_camera.modules;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalModule extends AbstractModule implements AbstractModuleHandler.I_worker
{
    AbstractModule picModule;
    IntervalHandler intervalHandler;

    public IntervalModule(AbstractCameraHolder cameraHandler, ModuleEventHandler eventHandler, AbstractModule picModule) {
        super(cameraHandler, eventHandler);
        this.picModule = picModule;

        intervalHandler = new IntervalHandler(picModule);
        this.name = AbstractModuleHandler.MODULE_INTERVAL;
    }

    @Override
    public String ShortName() {
        return "Int";
    }

    @Override
    public String LongName() {
        return "Interval";
    }

    @Override
    public boolean DoWork()
    {
        if (!intervalHandler.IsWorking())
        {
            Logger.d(TAG, "StartInterval");
            intervalHandler.StartInterval();
            return true;
        } else {
            Logger.d(TAG, "Stop Interval");
            intervalHandler.CancelInterval();
            return false;
        }
    }

    @Override
    public void LoadNeededParameters() {
        picModule.SetWorkerListner(this);
    }

    @Override
    public void UnloadNeededParameters() {

    }

    @Override
    public void onWorkStarted()
    {
        Logger.d(TAG, "WorkStarted");
        workstarted();
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        Logger.d(TAG, "Work Finished");
        workfinished(finished);
        intervalHandler.DoNextInterval();
    }

    @Override
    public boolean IsWorking()
    {
        return intervalHandler.IsWorking();
    }
}

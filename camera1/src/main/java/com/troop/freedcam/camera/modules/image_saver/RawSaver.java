package com.troop.freedcam.camera.modules.image_saver;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by troop on 15.04.2015.
 */
public class RawSaver extends JpegSaver
{
    final public String fileEnding = ".bayer";
    public RawSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone) {
        super(cameraHolder, i_workeDone);
    }

    final String TAG = "RawSaver";

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");
        if (ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
        {
            ParameterHandler.ZSL.SetValue("off",true);
        }
        awaitpicture = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                cameraHolder.TakePicture(null, null, RawSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        super.onPictureTaken(data);
    }

    @Override
    public void saveBytesToFile(byte[] bytes, File fileName, boolean workdone) {
        checkFileExists(fileName);

        Logger.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (!StringUtils.IS_L_OR_BIG()
                    || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
                outStream = new FileOutputStream(fileName);
            else
            {
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(true,AppSettingsManager.APPSETTINGSMANAGER);
                DocumentFile wr = df.createFile("image/*", fileName.getName());
                outStream = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openOutputStream(wr.getUri());
            }
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        }
        Logger.d(TAG, "End Saving Bytes");
        iWorkeDone.OnWorkDone(fileName);
    }
}

package com.troop.androiddng;

import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by troop on 15.02.2015.
 */
public class RawToDng
{
    static
    {
        System.loadLibrary("RawToDng");
    }

    private static final String TAG = RawToDng.class.getSimpleName();

    private String wbct;
    private byte[] opcode2;
    private byte[] opcode3;

    private static int Calculate_rowSize(int fileSize, int height)
    {
        return fileSize/height;
    }


    private ByteBuffer nativeHandler = null;
    private static native long GetRawBytesSize(ByteBuffer nativeHandler);
    private static native int GetRawHeight(ByteBuffer nativeHandler);
    private static native void SetGPSData(ByteBuffer nativeHandler,double Altitude,float[] Latitude,float[] Longitude, String Provider, long gpsTime);
    private static native void SetThumbData(ByteBuffer nativeHandler,byte[] mThumb, int widht, int height);
    private static native void WriteDNG(ByteBuffer nativeHandler);
    private static native void Release(ByteBuffer nativeHandler);
    private static native void SetOpCode3(ByteBuffer nativeHandler, byte[] opcode);
    private static native void SetOpCode2(ByteBuffer nativeHandler, byte[] opcode);
    private static native void SetRawHeight(ByteBuffer nativeHandler,int height);
    private static native void SetModelAndMake(ByteBuffer nativeHandler,String model, String make);
    private static native void SetBayerData(ByteBuffer nativeHandler,byte[] fileBytes, String fileout);
    private static native void SetBayerDataFD(ByteBuffer nativeHandler,byte[] fileBytes, int fileout, String filename);
    private static native void SetLensData(ByteBuffer nativeHandler,byte[] fileBytes, String hasLensData);
    private static native void SetBayerInfo(ByteBuffer nativeHandler,
                                     float[] colorMatrix1,
                                     float[] colorMatrix2,
                                     float[] neutralColor,
                                            float[] fowardMatrix1,
                                            float[] fowardMatrix2,
                                            float[] reductionMatrix1,
                                            float[] reductionMatrix2,
                                            float[] noiseMatrix,
                                     int blacklevel,
                                     String bayerformat,
                                     int rowSize,
                                     String devicename,
                                     int rawType,int width,int height);

    private static native ByteBuffer Create();
    private static native void SetExifData(ByteBuffer nativeHandler,
                                           int iso,
                                           double expo,
                                           int flash,
                                           float fNum,
                                           float focalL,
                                           String imagedescription,
                                           String orientation,
                                           double exposureIndex);
   // public static native String getFilePath();

    private RawToDng()
    {
        if (nativeHandler != null) {
            Release(nativeHandler);
            nativeHandler = null;
        }
        wbct = "";
        File op2 = new File(StringUtils.GetInternalSDCARD()+ StringUtils.freedcamFolder+"opc2.bin");
        if (op2.exists())
            try {
                opcode2 = readFile(op2);
                Logger.d(TAG, "opcode2 size" + opcode2.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        File op3 = new File(StringUtils.GetInternalSDCARD()+ StringUtils.freedcamFolder+"opc3.bin");
        if (op3.exists())
            try {
                opcode3 = readFile(op3);
                Logger.d(TAG, "opcode3 size" + opcode3.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        nativeHandler = Create();
    }

    public void SetWBCT(String wbct)
    {
        this.wbct =wbct;
    }

    private float[] getWbCtMatrix(String wbct)
    {
        int wb = Integer.parseInt(wbct) / 100;
        double r,g,b;
        double tmpcol = 0;
        double colortemp = wb;
        //red

        if( colortemp <= 66 )
        {
            r = 255;
            g = colortemp-10;
            g = 99.4708025861 * Math.log(g) - 161.1195681661;
            if( colortemp <= 19)
            {
                b = 0;
            }
            else
            {
                b = colortemp -10;
                b = 138.5177312231 * Math.log(b) - 305.0447927307;
            }
        }
        else
        {
            r = colortemp - 60;
            r = 329.698727446 * Math.pow(r, -0.1332047592);
            g = colortemp-60;
            g = 288.1221695283 * Math.pow(g, -0.0755148492);
            b = 255;
        }
        Logger.d(TAG, "ColorTemp=" + colortemp + " WBCT = r:" + r + " g:" + g + " b:" + b);
        float rf,gf,bf = 0;

        rf = (float)getRGBToDouble(checkminmax((int)r))/2;
        gf = (float)getRGBToDouble(checkminmax((int)g));
        bf = (float)getRGBToDouble(checkminmax((int)b))/2;
        Logger.d(TAG, "ColorTemp=" + colortemp + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
            rf = rf / gf;
            bf = bf / gf;
            gf = 1;
        Logger.d(TAG, "ColorTemp=" + colortemp + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
        return new float[]{rf, gf,bf};
    }

    private double getRGBToDouble(int color)
    {
        double t = color;
        t = t * 3 *2;
        t = t / (255);
        t = t / 3;
        t += 1;

        return t;
    }

    private int checkminmax(int val)
    {
        if (val>255)
            return 255;
        else if(val < 0)
            return 0;
        else return val;
    }


    public static RawToDng GetInstance()
    {
        return new RawToDng();
    }

    public long GetRawSize()
    {
        return GetRawBytesSize(nativeHandler);
    }

    public void SetGPSData(double Altitude,double Latitude,double Longitude, String Provider, long gpsTime)
    {
        Logger.d(TAG,"Latitude:" + Latitude + "Longitude:" +Longitude);
        if (nativeHandler != null)
            SetGPSData(nativeHandler, Altitude, parseGpsvalue(Latitude), parseGpsvalue(Longitude), Provider, gpsTime);
    }

    public void setExifData(int iso,
                            double expo,
                            int flash,
                            float fNum,
                            float focalL,
                            String imagedescription,
                            String orientation,
                            double exposureIndex)
    {
        if (nativeHandler != null)
        SetExifData(nativeHandler, iso, expo, flash, fNum, focalL, imagedescription, orientation, exposureIndex);
    }

    private float[] parseGpsvalue(double val)
    {

        final String[] sec = Location.convert(val, Location.FORMAT_SECONDS).split(":");

        final double dd = Double.parseDouble(sec[0]);
        final double dm = Double.parseDouble(sec[1]);
        final double ds = Double.parseDouble(sec[2].replace(",","."));

        final float[] Longitudear = { (float)dd ,(float)dm,(float)ds};
        return Longitudear;
    }

    public void SetThumbData(byte[] mThumb, int widht, int height)
    {
        if (nativeHandler != null)
        {
            SetThumbData(nativeHandler,mThumb, widht,height);
        }
    }

    public void SetModelAndMake(String model, String make)
    {
        if (nativeHandler !=null)
            SetModelAndMake(nativeHandler, model, make);
    }

    public void SetBayerData(final byte[] fileBytes, String fileout) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        if (nativeHandler != null) {
            SetBayerData(nativeHandler, fileBytes, fileout);
            if (opcode2 != null)
                SetOpCode2(nativeHandler,opcode2);
            if (opcode3 != null)
                SetOpCode3(nativeHandler,opcode3);
        }
    }

    public void SetBayerDataFD(final byte[] fileBytes, ParcelFileDescriptor fileout, String filename) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        if (nativeHandler != null) {
            SetBayerDataFD(nativeHandler, fileBytes, fileout.getFd(), filename);
            if (opcode2 != null)
                SetOpCode2(nativeHandler,opcode2);
            if (opcode3 != null)
                SetOpCode3(nativeHandler,opcode3);
        }
    }

    public void SetLensData(final byte[] fileBytes, String hasLensData) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        if (nativeHandler != null)
            SetLensData(nativeHandler, fileBytes, hasLensData);
    }

    private void SetBayerInfo(float[] colorMatrix1,
                             float[] colorMatrix2,
                             float[] neutralColor,
                              float[] fowardMatrix1,
                              float[] fowardMatrix2,
                              float[] reductionMatrix1,
                              float[] reductionMatrix2,
                              float[] noise,
                             int blacklevel,
                             String bayerformat,
                             int rowSize,
                             String devicename,
                             int tight,int width,int height)
    {
        if (nativeHandler != null && wbct.equals(""))
            SetBayerInfo(nativeHandler, colorMatrix1, colorMatrix2, neutralColor, fowardMatrix1, fowardMatrix2, reductionMatrix1, reductionMatrix2, noise, blacklevel, bayerformat, rowSize, devicename, tight, width, height);
        else if (!wbct.equals(""))
            SetBayerInfo(nativeHandler, colorMatrix1, colorMatrix2,getWbCtMatrix(wbct), fowardMatrix1, fowardMatrix2, reductionMatrix1, reductionMatrix2, noise, blacklevel, bayerformat, rowSize, devicename, tight, width, height);

    }

    public void RELEASE()
    {
        if (nativeHandler !=null)
        {
            Release(nativeHandler);
            nativeHandler = null;
        }
    }

    private void setRawHeight(int height)
    {
        if (nativeHandler != null)
            SetRawHeight(nativeHandler, height);
    }

    private void AddLens()
    {
        switch (DeviceUtils.DEVICE())
        {
            case ZTE_ADV:
                //SetLensData();

        }
    }

/*    private Byte[] raw2byte()
    {

    }*/



    public void WriteDNG(DeviceUtils.Devices device)
    {
        DeviceUtils.Devices devices = device;
        if (devices != null)
        {
            DngSupportedDevices.DngProfile profile = new DngSupportedDevices().getProfile(devices, (int)GetRawSize());
            //if (profile.rowsize == 0)
                //profile.rowsize = Calculate_rowSize((int)GetRawSize(), profile.height);
            if (profile == null)
            {
                RELEASE();
                return;
            }
            SetModelAndMake(Build.MODEL, Build.MANUFACTURER);
            SetBayerInfo(profile.matrix1, profile.matrix2, profile.neutral,profile.fowardmatrix1,profile.fowardmatrix2,profile.reductionmatrix1,profile.reductionmatrix2,profile.noiseprofile,profile.blacklevel, profile.BayerPattern, profile.rowsize, Build.MODEL,profile.rawType,profile.widht,profile.height);
            WriteDNG(nativeHandler);
            RELEASE();
        }
    }

    public void WriteDngWithProfile(DngSupportedDevices.DngProfile profile)
    {
        if (profile == null)
            return;
        SetModelAndMake(Build.MODEL, Build.MANUFACTURER);
        SetBayerInfo(profile.matrix1, profile.matrix2, profile.neutral,profile.fowardmatrix1,profile.fowardmatrix2,profile.reductionmatrix1,profile.reductionmatrix2,profile.noiseprofile,profile.blacklevel, profile.BayerPattern, profile.rowsize, Build.MODEL,profile.rawType,profile.widht,profile.height);
        WriteDNG(nativeHandler);
        RELEASE();
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}

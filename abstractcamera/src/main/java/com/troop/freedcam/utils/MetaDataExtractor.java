package com.troop.freedcam.utils;

import com.troop.freedcam.ui.FreeDPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GeorgeKiarie on 2/11/2016.
 */
public class MetaDataExtractor {
    float exp;
    int iso;
    int flash;
    int isoActual;
    boolean isRoot = false;
    String Description;

    public MetaDataExtractor()
    {
        exp = 0.0f;
        iso = 0;
        flash = 0;
        isoActual = 0;
        Description = "";

    }

    public static void StatiCEXCute()
    {
        MetaDataExtractor metaDataExtractor = new MetaDataExtractor();
        metaDataExtractor.extractMeta();
        metaDataExtractor.isRoot = true;
    }


    public void extractMeta()
    {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
        {
            FreeDPool.Execute(new Runnable() {
                public void run() {
                    List<String> metadata = new ArrayList<>();
                    Process process;
                    try {
                        process = new ProcessBuilder()
                                .command("su", "-c", "logcat -d -s AEC_PORT | grep -E iso")
                                .redirectErrorStream(true)
                                .start();


                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        //StringBuilder log=new StringBuilder();
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {
                            //log.append(line);
                            metadata.add(line);
                        }


                        String[] split0 = metadata.get(metadata.size() - 1).split(",");
                        String[] split1 = split0[0].split(":");
                        String[] split2 = split1[3].split(" ");


                        exp = Float.parseFloat(split2[3]);
                        iso = Integer.parseInt(split2[5]);
                        Description = metadata.get(metadata.size() - 1);


                        process.destroy();


                        //v.setText(exposureTime+" "+ iso+" "+ flash+" "+isoActual);
                    } catch (IOException e) {
                    }
                }
            });
        }
        else
        {
            FreeDPool.Execute(new Runnable() {
                public void run() {
                    List<String> metadata = new ArrayList<>();
                    Process process;
                    try {
                        process = new ProcessBuilder()
                                .command("su", "-c", "logcat -d -s AEC_PORT | grep -E iso")
                                .redirectErrorStream(true)
                                .start();


                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        //StringBuilder log=new StringBuilder();
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {
                            //log.append(line);
                            metadata.add(line);
                        }
//OOB INDEX Check meta has a shitload
                        if (metadata.size() > 1) {
                            String[] split0 = metadata.get(metadata.size() - 1).split(",");
                            String[] split1 = split0[0].split(":");

                            exp = Float.parseFloat(split1[3]);
                            iso = Integer.parseInt(split0[1].split(":")[1]);
                            flash = Integer.parseInt(split0[2].split(":")[1]);
                            float ActualISO = Float.parseFloat(split0[5].split(":")[1]);
                            isoActual = Math.round(ActualISO * 100);
                            Description = metadata.get(metadata.size() - 1);
                        }

                        process.destroy();


                        //v.setText(exposureTime+" "+ iso+" "+ flash+" "+isoActual);
                    } catch (IOException e) {
                    }

                }
            });
        }


    }

    public static void StatiClear()
    {
        FreeDPool.Execute(new Runnable() {
            public void run() {
                try {
                    Process process = new ProcessBuilder()
                            .command("su", "-c", "logcat", "-c")
                            .redirectErrorStream(true)
                            .start();
                    process.destroy();
                } catch (IOException e) {
                }
            }
        });
    }

    public  float getExp()
    {
        return exp;
    }

    public int getIso()
    {
        return iso;
    }

    public int getFlash()
    {

        return flash;
    }
    public int getIsoActual()
    {
        return isoActual;
    }

    public String getDescription()
    {
        return Description;
    }

    public static String ShutterLooKup(int ms)
    {
        String fraction = "1/2";

        if (ms > 100000)
            fraction = String.valueOf(ms / 1000000);
        else if (ms == 500000 )
            fraction = "1/2";
        else if (ms == 250000 )
            fraction = "1/4";
        else if (ms == 125000 )
            fraction = "1/8";
        else if (ms == 62500 )
            fraction = "1/16";
        else if (ms == 33333 )
            fraction = "1/30";
        else if (ms == 16666 )
            fraction = "1/60";
        else if (ms == 8333 )
            fraction = "1/120";
        else if (ms == 4166 )
            fraction = "1/240";
        else if (ms == 2000 )
            fraction = "1/500";
        else if (ms == 1000 )
            fraction = "1/1000";
        else if (ms == 500 )
            fraction = "1/2000";
        else if (ms == 250 )
            fraction = "1/4000";
        else if (ms == 200 )
            fraction = "1/5000";
        else if (ms == 125 )
            fraction = "1/5000";


        return fraction;
    }


}

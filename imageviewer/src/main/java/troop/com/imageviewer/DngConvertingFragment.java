package troop.com.imageviewer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.troop.androiddng.DngSupportedDevices;
import com.troop.androiddng.Matrixes;
import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingFragment extends Fragment
{
    final String TAG = DngConvertingFragment.class.getSimpleName();
    View view;
    EditText editTextwidth;
    EditText editTextheight;
    EditText editTextblacklvl;
    Spinner spinnerMatrixProfile;
    Spinner spinnerColorPattern;
    Spinner spinnerrawFormat;
    Button buttonconvertToDng;
    String[] filesToConvert;
    DngSupportedDevices.DngProfile dngprofile;
    Handler handler;
    Button closeButton;

    public static final String EXTRA_FILESTOCONVERT = "extra_files_to_convert";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.SETCONTEXT(getContext());
        handler = new Handler();
        view = inflater.inflate(R.layout.dngconvertingfragment, container, false);
        this.editTextwidth = (EditText)view.findViewById(R.id.editText_width);
        this.editTextheight = (EditText)view.findViewById(R.id.editText_height);
        this.editTextblacklvl = (EditText)view.findViewById(R.id.editText_blacklevel);
        this.spinnerMatrixProfile = (Spinner)view.findViewById(R.id.spinner_MatrixProfile);
        ArrayAdapter<CharSequence> matrixadapter = ArrayAdapter.createFromResource(getContext(),
                R.array.matrixes, android.R.layout.simple_spinner_item);
        matrixadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatrixProfile.setAdapter(matrixadapter);


        this.buttonconvertToDng = (Button)view.findViewById(R.id.button_convertDng);
        buttonconvertToDng.setOnClickListener(convertToDngClick);

        this.spinnerColorPattern =(Spinner)view.findViewById(R.id.spinner_ColorPattern);
        ArrayAdapter<CharSequence> coloradapter = ArrayAdapter.createFromResource(getContext(),
                R.array.color_pattern, android.R.layout.simple_spinner_item);
        coloradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColorPattern.setAdapter(coloradapter);

        this.spinnerrawFormat = (Spinner)view.findViewById(R.id.spinner_rawFormat);
        ArrayAdapter<CharSequence> rawadapter = ArrayAdapter.createFromResource(getContext(),
                R.array.raw_format, android.R.layout.simple_spinner_item);
        rawadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerrawFormat.setAdapter(rawadapter);
        this.closeButton = (Button)view.findViewById(R.id.button_goback_from_conv);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.filesToConvert = getActivity().getIntent().getStringArrayExtra(EXTRA_FILESTOCONVERT);
        if (filesToConvert != null && filesToConvert.length > 0) {
            DeviceUtils.Devices devices = DeviceUtils.DEVICE();
            dngprofile = new DngSupportedDevices().getProfile(devices, (int) new File(filesToConvert[0]).length());
            if (dngprofile == null) {
                dngprofile = new DngSupportedDevices().GetEmptyProfile();
                Toast.makeText(getContext(), R.string.unknown_raw_add_manual_stuff, Toast.LENGTH_LONG).show();
            }
            editTextwidth.setText(dngprofile.widht + "");
            editTextheight.setText(dngprofile.height + "");
            editTextblacklvl.setText(dngprofile.blacklevel + "");

            if (dngprofile.BayerPattern.equals(DngSupportedDevices.BGGR))
                spinnerColorPattern.setSelection(0);
            else if (dngprofile.BayerPattern.equals(DngSupportedDevices.RGGB))
                spinnerColorPattern.setSelection(1);
            else if (dngprofile.BayerPattern.equals(DngSupportedDevices.GRBG))
                spinnerColorPattern.setSelection(2);
            else if (dngprofile.BayerPattern.equals(DngSupportedDevices.GBRG))
                spinnerColorPattern.setSelection(3);

            if (dngprofile.matrix1.equals(Matrixes.Nex6CCM1))
                spinnerMatrixProfile.setSelection(0);
            else
                spinnerMatrixProfile.setSelection(1);
            spinnerrawFormat.setSelection(dngprofile.rawType);
            if (dngprofile != null){
                spinnerMatrixProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                dngprofile.matrix1 = Matrixes.Nex6CCM1;
                                dngprofile.matrix2 = Matrixes.Nex6CCM2;
                                dngprofile.neutral = Matrixes.Nex6NM;
                                dngprofile.fowardmatrix1 = Matrixes.Nexus6_foward_matrix1;
                                dngprofile.fowardmatrix2 = Matrixes.Nexus6_foward_matrix2;
                                dngprofile.reductionmatrix1 = Matrixes.Nexus6_reduction_matrix1;
                                dngprofile.reductionmatrix1 = Matrixes.Nexus6_reduction_matrix2;
                                dngprofile.noiseprofile = Matrixes.Nexus6_noise_3x1_matrix;
                                break;
                            case 1:
                                dngprofile.matrix1 = Matrixes.G4CCM1;
                                dngprofile.matrix2 = Matrixes.G4CCM2;
                                dngprofile.neutral = Matrixes.G4NM;
                                dngprofile.fowardmatrix1 = Matrixes.G4_foward_matrix1;
                                dngprofile.fowardmatrix2 = Matrixes.G4_foward_matrix2;
                                dngprofile.reductionmatrix1 = Matrixes.G4_reduction_matrix1;
                                dngprofile.reductionmatrix1 = Matrixes.G4_reduction_matrix2;
                                dngprofile.noiseprofile = Matrixes.G4_noise_3x1_matrix;
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinnerColorPattern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                dngprofile.BayerPattern = DngSupportedDevices.BGGR;
                                break;
                            case 1:
                                dngprofile.BayerPattern = DngSupportedDevices.RGGB;
                                break;
                            case 2:
                                dngprofile.BayerPattern = DngSupportedDevices.GRBG;
                                break;
                            case 3:
                                dngprofile.BayerPattern = DngSupportedDevices.GBRG;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinnerrawFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        dngprofile.rawType = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }
        else {
            Toast.makeText(getContext(), R.string.no_sel_raw, Toast.LENGTH_LONG).show();
        }
    }

    private View.OnClickListener convertToDngClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (filesToConvert == null || filesToConvert.length == 0) {
                Toast.makeText(getContext(), R.string.no_sel_raw, Toast.LENGTH_LONG).show();
            }
            else {
                dngprofile.widht = Integer.parseInt(editTextwidth.getText().toString());
                dngprofile.height = Integer.parseInt(editTextheight.getText().toString());
                dngprofile.blacklevel = (Integer.parseInt(editTextblacklvl.getText().toString()));
                final ProgressDialog pr = ProgressDialog.show(getContext(), "Converting DNG", "");

                pr.setMax(filesToConvert.length);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int t = 0;
                        for (String s : filesToConvert) {
                            convertRawToDng(new File(s));
                            t++;
                            final int i = t;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    pr.setProgress(i);
                                }
                            });
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                pr.dismiss();
                            }
                        });
                    }
                }).start();

            }
        }
    };

    private void convertRawToDng(File file)
    {
        byte[] data = null;
        try {
            data = RawToDng.readFile(file);
            Logger.d("Main", "Filesize: " + data.length + " File:" + file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        }
        String out =null;
        if (file.getName().endsWith(StringUtils.FileEnding.RAW))
            out = file.getAbsolutePath().replace(StringUtils.FileEnding.RAW, StringUtils.FileEnding.DNG);
        if (file.getName().endsWith(StringUtils.FileEnding.BAYER))
            out = file.getAbsolutePath().replace(StringUtils.FileEnding.BAYER, StringUtils.FileEnding.DNG);
        RawToDng dng = RawToDng.GetInstance();
        if (!StringUtils.IS_L_OR_BIG()
                || file.canWrite())
            dng.SetBayerData(data, out);
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(true,AppSettingsManager.APPSETTINGSMANAGER);
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(StringUtils.FileEnding.JPG, StringUtils.FileEnding.DNG));
            ParcelFileDescriptor pfd = null;
            try {

                pfd = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException e) {
                Logger.exception(e);
            }
            catch (IllegalArgumentException e)
            {
                Logger.exception(e);
            }
            if (pfd != null) {
                dng.SetBayerDataFD(data, pfd, file.getName());
                try {
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pfd = null;
            }
        }
        dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
        dng.WriteDngWithProfile(dngprofile);
        data = null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(intent);
    }


}

package com.troop.freedcam.sonyapi.sonystuff;

import android.util.Log;

import com.troop.filelogger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by troop on 14.12.2014.
 */
public class JsonUtils
{
    final static String TAG = JsonUtils.class.getSimpleName();
    /**
     * Retrieve a list of APIs that are supported by the target device.
     *
     * @param replyJson
     */
    public static void loadSupportedApiList(JSONObject replyJson, Set<String> mSupportedApiSet) {
        synchronized (mSupportedApiSet) {
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("results");
                for (int i = 0; i < resultArrayJson.length(); i++) {
                    mSupportedApiSet.add(resultArrayJson.getJSONArray(i).getString(0));
                }
            } catch (JSONException e) {
                Logger.w(TAG, "loadSupportedApiList: JSON format error.");
            }
        }
    }

    public static void loadSupportedApiListFromEvent(JSONObject replyJson, Set<String> mSupportedApiSet) {
        synchronized (mSupportedApiSet) {
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("names");
                for (int i = 0; i < resultArrayJson.length(); i++) {
                    mSupportedApiSet.add(resultArrayJson.getString(i));
                }
            } catch (JSONException e) {
                Logger.w(TAG, "loadSupportedApiList: JSON format error.");
            }
        }
    }

    /**
     * Check if the specified API is available at present. This works correctly
     * only for Camera API.
     *
     * @param apiName
     * @return
     */
    public static boolean isCameraApiAvailable(String apiName, Set<String> mAvailableCameraApiSet) {
        boolean isAvailable = false;
        synchronized (mAvailableCameraApiSet) {
            isAvailable = mAvailableCameraApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Check if the specified API is supported. This is for camera and avContent
     * service API. The result of this method does not change dynamically.
     *
     * @param apiName
     * @return
     */
    public static boolean isApiSupported(String apiName, Set<String> mSupportedApiSet) {
        boolean isAvailable = false;
        synchronized (mSupportedApiSet) {
            isAvailable = mSupportedApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Retrieve a list of APIs that are available at present.
     *
     * @param replyJson
     */
    public static void loadAvailableCameraApiList(JSONObject replyJson, Set<String> mAvailableCameraApiSet) {
        synchronized (mAvailableCameraApiSet) {
            mAvailableCameraApiSet.clear();
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("result");
                JSONArray apiListJson = resultArrayJson.getJSONArray(0);
                for (int i = 0; i < apiListJson.length(); i++) {
                    mAvailableCameraApiSet.add(apiListJson.getString(i));
                }
            } catch (JSONException e) {
                Logger.w(TAG, "loadAvailableCameraApiList: JSON format error.");
            }
        }
    }

    public static String[] ConvertJSONArrayToStringArray(JSONArray array)
    {
        String[] ret = new String[array.length()];
        for (int i = 0; i < array.length(); i++)
        {
            try {
                ret[i] = array.get(i).toString();
            } catch (JSONException e) {
                Logger.exception(e);
            }
        }
        return ret;
    }

    public static int findIntInformation(JSONObject replyJson,int indexpos, String typeS, String subtype ) throws JSONException {

        int ret = -5000;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexpos)) {
            JSONObject intInformationObj = resultsObj.getJSONObject(indexpos);
            String type = intInformationObj.getString("type");
            if (typeS.equals(type)) {
                ret = intInformationObj.getInt(subtype);
            } else {
                Logger.w(TAG, "Event reply: Illegal Index " + typeS+ " "+ subtype  + " " +type);
            }
        }
        return ret;
    }

    public static boolean findBooleanInformation(JSONObject replyJson,int indexpos, String typeS, String subtype ) throws JSONException {

        boolean ret = false;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexpos)) {
            JSONObject intInformationObj = resultsObj.getJSONObject(indexpos);
            String type = intInformationObj.getString("type");
            if (typeS.equals(type)) {
                ret = intInformationObj.getBoolean(subtype);
            } else {
                Logger.w(TAG, "Event reply: Illegal Index " + typeS+ " "+ subtype  + " " +type);
            }
        }
        return ret;
    }



    public static String findStringInformation(JSONObject replyJson,int indexpos, String typeS, String subtype ) throws JSONException {
        String value = "";

        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexpos)) {
            JSONObject InformationObj = resultsObj.getJSONObject(indexpos);
            String type = InformationObj.getString("type");
            if (typeS.equals(type)) {
                value = InformationObj.getString(subtype);
            } else {
                Logger.w(TAG, "Event reply: Illegal Index " + indexpos + subtype + type);
            }
        }
        return value;
    }

    public static String[] findStringArrayInformation(JSONObject replyJson,int indexpos, String typeS, String subtype ) throws JSONException {
        ArrayList<String> values = new ArrayList<String>();

        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexpos)) {
            JSONObject InformationObj = resultsObj.getJSONObject(indexpos);
            String type = InformationObj.getString("type");
            if (typeS.equals(type))
            {
                JSONArray array = InformationObj.getJSONArray(subtype);
                for (int i = 0; i<array.length();i++)
                    values.add(array.getString(i));
            }
        }
        return values.toArray(new String[values.size()]);
    }

    /**
     * Finds and extracts an error code from reply JSON data.
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static int findErrorCode(JSONObject replyJson) throws JSONException {
        int code = 0; // 0 means no error.
        if (replyJson.has("error")) {
            JSONArray errorObj = replyJson.getJSONArray("error");
            code = errorObj.getInt(0);
        }
        return code;
    }

    /**
     * Finds and extracts a list of available APIs from reply JSON data. As for
     * getEvent v1.0, results[0] => "availableApiList"
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static List<String> findAvailableApiList(JSONObject replyJson) throws JSONException {
        List<String> availableApis = new ArrayList<String>();
        int indexOfAvailableApiList = 0;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfAvailableApiList)) {
            JSONObject availableApiListObj = resultsObj.getJSONObject(indexOfAvailableApiList);
            String type = availableApiListObj.getString("type");
            if ("availableApiList".equals(type)) {
                JSONArray apiArray = availableApiListObj.getJSONArray("names");
                for (int i = 0; i < apiArray.length(); i++) {
                    availableApis.add(apiArray.getString(i));
                }
            } else {
                Logger.w(TAG, "Event reply: Illegal Index (0: AvailableApiList) " + type);
            }
        }
        return availableApis;
    }

    /**
     * Finds and extracts a value of Camera Status from reply JSON data. As for
     * getEvent v1.0, results[1] => "cameraStatus"
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static String findCameraStatus(JSONObject replyJson) throws JSONException {
        String cameraStatus = null;
        int indexOfCameraStatus = 1;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfCameraStatus)) {
            JSONObject cameraStatusObj = resultsObj.getJSONObject(indexOfCameraStatus);
            String type = cameraStatusObj.getString("type");
            if ("cameraStatus".equals(type)) {
                cameraStatus = cameraStatusObj.getString("cameraStatus");
            } else {
                Logger.w(TAG, "Event reply: Illegal Index (1: CameraStatus) " + type);
            }
        }
        return cameraStatus;
    }

    /**
     * Finds and extracts a value of Liveview Status from reply JSON data. As
     * for getEvent v1.0, results[3] => "liveviewStatus"
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static Boolean findLiveviewStatus(JSONObject replyJson) throws JSONException {
        Boolean liveviewStatus = null;
        int indexOfLiveviewStatus = 3;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfLiveviewStatus)) {
            JSONObject liveviewStatusObj = resultsObj.getJSONObject(indexOfLiveviewStatus);
            String type = liveviewStatusObj.getString("type");
            if ("liveviewStatus".equals(type)) {
                liveviewStatus = liveviewStatusObj.getBoolean("liveviewStatus");
            } else {
                Logger.w(TAG, "Event reply: Illegal Index (3: LiveviewStatus) " + type);
            }
        }
        return liveviewStatus;
    }

    /**
     * Finds and extracts a value of Shoot Mode from reply JSON data. As for
     * getEvent v1.0, results[21] => "shootMode"
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static String findShootMode(JSONObject replyJson) throws JSONException {
        String shootMode = null;
        int indexOfShootMode = 21;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfShootMode)) {
            JSONObject shootModeObj = resultsObj.getJSONObject(indexOfShootMode);
            String type = shootModeObj.getString("type");
            if ("shootMode".equals(type)) {
                shootMode = shootModeObj.getString("currentShootMode");
            } else {
                Logger.w(TAG, "Event reply: Illegal Index (21: ShootMode) " + type);
            }
        }
        return shootMode;
    }

    /**
     * Finds and extracts a value of Zoom Information from reply JSON data. As
     * for getEvent v1.0, results[2] => "zoomInformation"
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static int findZoomInformation(JSONObject replyJson) throws JSONException {
        int zoomPosition = -1;
        int indexOfZoomInformation = 2;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfZoomInformation)) {
            JSONObject zoomInformationObj = resultsObj.getJSONObject(indexOfZoomInformation);
            String type = zoomInformationObj.getString("type");
            if ("zoomInformation".equals(type)) {
                zoomPosition = zoomInformationObj.getInt("zoomPosition");
            } else {
                Logger.w(TAG, "Event reply: Illegal Index (2: zoomInformation) " + type);
            }
        }
        return zoomPosition;
    }





    /**
     * Finds and extracts value of Storage Id from reply JSON data. As for
     * getEvent v1.0, results[10] => "storageInformation"
     *
     * @param replyJson
     * @return
     * @throws org.json.JSONException
     */
    public static String findStorageId(JSONObject replyJson) throws JSONException {
        String storageId = null;
        int indexOfStorageInfomation = 10;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfStorageInfomation)) {
            JSONArray storageInformationArray = resultsObj.getJSONArray(indexOfStorageInfomation);
            if (!storageInformationArray.isNull(0)) {
                JSONObject storageInformationObj = storageInformationArray.getJSONObject(0);
                String type = storageInformationObj.getString("type");
                if ("storageInformation".equals(type)) {
                    storageId = storageInformationObj.getString("storageID");
                } else {
                    Logger.w(TAG, "Event reply: Illegal Index (11: storageInformation) " + type);
                }
            }
        }

        return storageId;
    }
}

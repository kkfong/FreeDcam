/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.parameters.manual;

import android.content.Context;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.parameters.ParametersHandler;

public class ISOManualParameter extends BaseManualParameter {

    public ISOManualParameter(Context context, Parameters parameters, ParametersHandler parametersHandler) {
        super(context, parameters, "", "", "", parametersHandler,1);

        isSupported = true;
        key_max_value = KEYS.MIN_ISO;
        key_value = KEYS.ISO;
        key_min_value = KEYS.MAX_ISO;
        if (parameters.get(key_max_value) != null && parameters.get(key_min_value) != null) {

            if (key_min_value.equals(null)) {
                isSupported = false;
            }
            stringvalues = createStringArray(Integer.parseInt(parameters.get(key_min_value)), Integer.parseInt(parameters.get(key_max_value)), 100);
        }
        else
            isSupported = false;

        isVisible = isSupported;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetValue() {
        String tmp = parameters.get(key_value);
        if (tmp.equals(KEYS.AUTO))
            return 0;
        try {
            return Integer.parseInt(parameters.get(key_value));
        } catch (NullPointerException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        parameters.set(KEYS.ISO, stringvalues[valueToSet]);
    }

}



package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.SwipeMenuListner;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItemTheme;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsMenuFragment extends AbstractFragment implements Interfaces.I_CloseNotice, Interfaces.I_MenuItemClick, I_ParametersLoaded
{
    final String TAG = SettingsMenuFragment.class.getSimpleName();
    LinearLayout left_Holder;
    LinearLayout right_Holder;
    LeftMenuFragment leftMenuFragment;
    RightMenuFragment rightMenuFragment;
    ValuesMenuFragment valuesMenuFragment;


    final int VALUE_MENU_CLOSED = 0;
    final int VALUE_MENU_RIGHT_OPEN = 1;
    final int VALUE_MENU_LEFT_OPEN = 2;
    int value_menu_status = VALUE_MENU_CLOSED;

    UiSettingsChild currentOpendItem;

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper) {
        super.SetCameraUIWrapper(wrapper);
        Log.d(TAG,"Set Wrapper");
        if (wrapper != null && wrapper.camParametersHandler != null)
            wrapper.camParametersHandler.AddParametersLoadedListner(this);
        //if(getActivity() != null)
        //    setWrapper();
    }

    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        super.SetStuff(i_activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.settingsmenufragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        right_Holder = (LinearLayout)view.findViewById(R.id.right_holder);
        left_Holder = (LinearLayout)view.findViewById(R.id.left_holder);
        Logger.d(TAG,"onviewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG,"onResume");
        setWrapper();
    }

    private void setWrapper()
    {
        Logger.d("SettingsmenuFragment", "set CameraWrapper");
        try {
            closeValueMenu();
            loadLeftFragment();
            loadRightFragment();
        }
        catch (NullPointerException e){
            Logger.exception(e);
        }
        value_menu_status = VALUE_MENU_CLOSED;
    }

    private void loadLeftFragment()
    {
        if (leftMenuFragment == null)
            leftMenuFragment = new LeftMenuFragment();
        leftMenuFragment.SetStuff(i_activity);
        leftMenuFragment.SetCameraUIWrapper(wrapper);
        leftMenuFragment.SetMenuItemClickListner(this);
        try {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
            transaction.replace(R.id.left_holder, leftMenuFragment);
            transaction.commitAllowingStateLoss();
        }catch (NullPointerException ex)
        {}
        catch (IllegalStateException ex)
        {}
    }
    private void loadRightFragment() {
        if ( rightMenuFragment == null)
            rightMenuFragment = new RightMenuFragment();
        rightMenuFragment.SetStuff(i_activity);
        rightMenuFragment.SetCameraUIWrapper(wrapper);
        rightMenuFragment.SetMenuItemClickListner(this);
        try {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
            transaction.replace(R.id.right_holder, rightMenuFragment);
            transaction.commitAllowingStateLoss();
        }
        catch (NullPointerException ex)
        {}
        catch (IllegalStateException ex)
        {}
    }


    @Override
    public void onClose(String value)
    {
        currentOpendItem.SetValue(value);
        if (currentOpendItem instanceof MenuItemTheme)
            return;
        closeValueMenu();
    }

    private void closeValueMenu()
    {
        if (value_menu_status == VALUE_MENU_LEFT_OPEN) {
            loadLeftFragment();
        } else if (value_menu_status == VALUE_MENU_RIGHT_OPEN) {
            loadRightFragment();
        }
        currentOpendItem = null;
        value_menu_status = VALUE_MENU_CLOSED;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (currentOpendItem == item)
        {
            closeValueMenu();
            return;
        }
        currentOpendItem = item;

        valuesMenuFragment = new ValuesMenuFragment();
        if (item.GetValues() == null) {
            item.onIsSupportedChanged(false);
            value_menu_status = VALUE_MENU_CLOSED;
            if (fromLeftFragment)
                loadRightFragment();
            else
                loadLeftFragment();
            return;
        }
        valuesMenuFragment.SetMenuItem(item.GetValues(), this);

        if (fromLeftFragment)
        {
            value_menu_status = VALUE_MENU_RIGHT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
            transaction.replace(R.id.right_holder, valuesMenuFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        else
        {
            value_menu_status = VALUE_MENU_LEFT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.replace(R.id.left_holder, valuesMenuFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void ParametersLoaded() {
        setWrapper();
    }
}

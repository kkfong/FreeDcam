package troop.com.themesample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.I_Activity;

import java.io.File;

import troop.com.imageviewer.screenslide.ScreenSlideFragment;
import troop.com.themesample.subfragments.CameraUiFragment;
import troop.com.themesample.subfragments.SettingsMenuFragment;
import troop.com.views.PagingView;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    final String TAG = SampleThemeFragment.class.getSimpleName();

    private I_Activity i_activity;
    private CameraUiFragment cameraUiFragment;

    private PagingView mPager;
    private PagerAdapter mPagerAdapter;

    private SettingsMenuFragment settingsMenuFragment;
    ScreenSlideFragment screenSlideFragment;
    private boolean pagerTouchAllowed = true;

    public SampleThemeFragment()
    {
    }

    @Override
    public void SetStuff(I_Activity i_activity) {
        this.i_activity = i_activity;

    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.wrapper = wrapper;
        if (cameraUiFragment != null) {
            cameraUiFragment.SetCameraUIWrapper(wrapper);
        }
        if (settingsMenuFragment != null)
        {
            settingsMenuFragment.SetStuff(i_activity);
            settingsMenuFragment.SetCameraUIWrapper(wrapper);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, null);
        return inflater.inflate(R.layout.samplethemefragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPager = (PagingView)view.findViewById(R.id.viewPager_fragmentHolder);

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    ScreenSlideFragment.I_ThumbClick onThumbClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick() {
            mPager.setCurrentItem(2);
        }

        @Override
        public void newImageRecieved(File file) {
            if (screenSlideFragment != null)
                screenSlideFragment.addFile(file);
        }
    };

    ScreenSlideFragment.I_ThumbClick onThumbBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick()
        {
            if (mPager != null)
                mPager.setCurrentItem(1);
        }

        @Override
        public void newImageRecieved(File file) {

        }
    };


    public void DisablePagerTouch(boolean disable)
    {
        if (disable)
            mPager.EnableScroll(false);
        else
            mPager.EnableScroll(true);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {

        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);

        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
            {
                if (settingsMenuFragment == null)
                    settingsMenuFragment = new SettingsMenuFragment();
                settingsMenuFragment.SetStuff(i_activity);
                settingsMenuFragment.SetCameraUIWrapper(wrapper);
                return settingsMenuFragment;
            }
            else if (position == 2)
            {
                if (screenSlideFragment == null)
                    screenSlideFragment = new ScreenSlideFragment();
                screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                return screenSlideFragment;
            }
            else
            {
                if (cameraUiFragment == null)
                    cameraUiFragment = new CameraUiFragment();
                cameraUiFragment.SetStuff(i_activity,onThumbClick);
                cameraUiFragment.SetCameraUIWrapper(wrapper);
                return cameraUiFragment;
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }


    }

}

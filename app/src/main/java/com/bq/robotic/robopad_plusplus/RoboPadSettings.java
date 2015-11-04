package com.bq.robotic.robopad_plusplus;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bq.robotic.robopad_plusplus.utils.CustomPreferenceCategory;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class RoboPadSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private static Integer bluetoothFragmentId;
    private AppCompatDelegate mCompatDelegate;

    // Debugging
    private static final String LOG_TAG = "RoboPadSettings";


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();

    }


    /**
     * Listen to the preference changes related to the Bluetooth
     * @param sharedPreferences sharedPreferences
     * @param key key of the preference that has changed
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(RoboPadConstants.ENABLE_BLUETOOTH_KEY)) {
            enableBluetoothKeyChanged();

        } else if (key.equals(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY)) {
            wasEnablingBluetoothAllowedKeyChanged();

        } else if (key.equals(RoboPadConstants.SHOW_TIPS_KEY)){
            showTipsKeyChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isSimplePreferences(this)) {
            int padding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            ((View) getListView().getParent()).setPadding(padding, padding, padding, padding);
            getListView().setPadding(0, 0, 0, 0);
        }

        getListView().setDivider(null);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);


        /**
         * Manage the switch or checkbox state depending on the current Bluetooth state of the device
         */
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(RoboPadConstants.ENABLE_BLUETOOTH_KEY, mBluetoothAdapter.isEnabled());
            editor.commit();

            // When the device is a large tablet, the app uses the headers file to make the two
            // paned, and the getPreferenceManager() returns null, it must be managed in each fragment
            if (Build.VERSION.SDK_INT < 14 && !isXLargeTablet(this)) {
                ((CheckBoxPreference) getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY)).setChecked(mBluetoothAdapter.isEnabled());

            } else if (Build.VERSION.SDK_INT > 14 && !isXLargeTablet(this)){
                ((SwitchPreference) getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY)).setChecked(mBluetoothAdapter.isEnabled());

            }


        } else {
            getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY).setDefaultValue(false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    /**
     * The preference for enabling or disabling the Bluetooth has changed
     */
    private void enableBluetoothKeyChanged() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean enablingBluetooth = sharedPref.getBoolean(RoboPadConstants.ENABLE_BLUETOOTH_KEY, false);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(enablingBluetooth) {

            if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY, true);
            editor.commit();

        } else if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


    /**
     * When the permission for the automatic enabling and disabling of the Bluetooth for the battery
     * saving is revoked, the app disable the Bluetooth
     */
    private void wasEnablingBluetoothAllowedKeyChanged() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isBluetoothAllowed = sharedPref.getBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY, false);

        // Only disable is the permission is revoked. Don't change the bluetooth state if it is not revoked
        if(!isBluetoothAllowed) {

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter != null) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean(RoboPadConstants.ENABLE_BLUETOOTH_KEY, false);
                editor.commit();

                if (Build.VERSION.SDK_INT < 14 && !isXLargeTablet(this)) {
                    ((CheckBoxPreference) getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY)).setChecked(false);

                } else if (Build.VERSION.SDK_INT > 14 && !isXLargeTablet(this)){
                    ((SwitchPreference) getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY)).setChecked(false);

                } else if (Build.VERSION.SDK_INT > 14 && isXLargeTablet(this)) {
                    PreferenceFragment fragment = (PreferenceFragment) getFragmentManager().findFragmentById(bluetoothFragmentId);
                    Log.e(LOG_TAG, "fragment null? " + (fragment == null));
                    Log.e(LOG_TAG, "preference manager null? " + (fragment.getPreferenceManager() == null));
                    ((SwitchPreference) fragment.getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY)).setChecked(false);

                }
            }
        }
    }


    /**
     * When the user select that the tips will be shown the first time, reset each robot screen value
     * to true
     */
    private void showTipsKeyChanged() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int showTipsValue = Integer.parseInt(sharedPref.getString(RoboPadConstants.SHOW_TIPS_KEY, String.valueOf(RoboPadConstants.showTipsValues.FIRST_TIME.ordinal())));

        // If never we don't do anything
        if (showTipsValue == RoboPadConstants.showTipsValues.FIRST_TIME.ordinal()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(RoboPadConstants.POLLYWOG_FIRST_TIME_TIPS_KEY, true);
            editor.putBoolean(RoboPadConstants.BEETLE_FIRST_TIME_TIPS_KEY, true);
            editor.putBoolean(RoboPadConstants.RHINO_FIRST_TIME_TIPS_KEY, true);
            editor.putBoolean(RoboPadConstants.CRAB_FIRST_TIME_TIPS_KEY, true);
            editor.putBoolean(RoboPadConstants.GENERIC_ROBOT_FIRST_TIME_TIPS_KEY, true);
            editor.putBoolean(RoboPadConstants.SCHEDULER_MOVEMENTS_FIRST_TIME_TIPS_KEY, true);
            editor.commit();
        }
    }


    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

//        // In the simplified UI, fragments are not used at all and we instead
//        // use the older PreferenceActivity APIs.

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(root);

        // Add 'bluetooth' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new CustomPreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_bluetooth);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_bluetooth);

        // Add 'scheduler' preferences, and a corresponding header.
        fakeHeader = new CustomPreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_scheduler);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_scheduler);

        // Add 'data and sync' preferences, and a corresponding header.
        fakeHeader = new CustomPreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_help_options);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_help);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("help_options"));
    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }



    /**
     * This fragment shows bluetooth preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BluetoothPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_bluetooth);

            // TODO: think a better way rather than the static variable
            bluetoothFragmentId = getId();

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            View grandParent = (View) getView().getParent().getParent();
            grandParent.setBackgroundResource(R.color.preferences_background);

            View title = grandParent.findViewById(android.R.id.title);
            if(title != null)
                title.setVisibility(View.GONE);

            if (grandParent.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) grandParent.getLayoutParams();
                int margin = getResources().getDimensionPixelSize(R.dimen.button_press_little_padding);
                p.setMargins(0, margin, 0, margin);
                grandParent.requestLayout();
            }

            ((RoboPadSettings) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_robo_pad_sttings);

        }

        @Override
        public void onResume() {
            super.onResume();

            // Take care of the switch state
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter != null) {
                if (Build.VERSION.SDK_INT < 14) {
                    ((CheckBoxPreference) getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY))
                            .setChecked(mBluetoothAdapter.isEnabled());

                } else if (Build.VERSION.SDK_INT > 14) {
                    ((SwitchPreference) getPreferenceManager().findPreference(RoboPadConstants.ENABLE_BLUETOOTH_KEY))
                            .setChecked(mBluetoothAdapter.isEnabled());

                }
            }
        }
    }


    /**
     * This fragment shows bluetooth preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SchedulerPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_scheduler);

            // TODO: think a better way rather than the static variable
            bluetoothFragmentId = getId();

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            View grandParent = (View) getView().getParent().getParent();
            grandParent.setBackgroundResource(R.color.preferences_background);

            View title = grandParent.findViewById(android.R.id.title);
            if(title != null)
                title.setVisibility(View.GONE);

            if (grandParent.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) grandParent.getLayoutParams();
                int margin = getResources().getDimensionPixelSize(R.dimen.button_press_little_padding);
                p.setMargins(0, margin, 0, margin);
                grandParent.requestLayout();
            }

            ((RoboPadSettings) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_robo_pad_sttings);

        }
    }


    /**
     * This fragment shows help options preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TipsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_help);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("help_options"));
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            View grandParent = (View) getView().getParent().getParent();
            grandParent.setBackgroundResource(R.color.preferences_background);

            View title = grandParent.findViewById(android.R.id.title);
            if(title != null)
                title.setVisibility(View.GONE);

            if (grandParent.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) grandParent.getLayoutParams();
                int margin = getResources().getDimensionPixelSize(R.dimen.button_press_little_padding);
                p.setMargins(0, margin, 0, margin);
                grandParent.requestLayout();
            }

            ((RoboPadSettings) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_robo_pad_sttings);

        }
    }

    /**
     * Compat delegate methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        getSupportActionBar().show();
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        getSupportActionBar().hide();

        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mCompatDelegate == null) {
            mCompatDelegate = AppCompatDelegate.create(this, null);
        }
        return mCompatDelegate;
    }

    protected boolean isValidFragment(String fragmentName) {
        return BluetoothPreferenceFragment.class.getName().equals(fragmentName) ||
                SchedulerPreferenceFragment.class.getName().equals(fragmentName) ||
                TipsPreferenceFragment.class.getName().equals(fragmentName);
    }

}

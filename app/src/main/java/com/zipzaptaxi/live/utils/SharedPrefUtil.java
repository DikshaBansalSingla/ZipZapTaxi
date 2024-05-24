package com.zipzaptaxi.live.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.zipzaptaxi.live.base.AppController;


/**
 * Created by Pardeep Sharma on 14-08-2022
 */
public class SharedPrefUtil {

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String IMAGE = "profileImage";
    public static final String LOGIN_TYPE = "loginType";
    public static final String USER_ID = "userId";
    public static final String PASSWORD = "password";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String AUTH_TOKEN = "authToken";
    public static final String COUNTRY_CODE_NAME = "CountryCodeName";
    public static final String LOGIN = "login";
    public static final String BADGE = "badge";
    public static final String VENDORID = "vendorid";
    public static final String NOTIFY = "notify";
    public static final String NOTIFY_ICON = "notifyIcon";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String LOCATON = "location";

    public static final String PHONE_NUMBER = "phone_number";
    public static final String COUNTRY_CODE = "country_code";
    public static final String CITY = "city";
    public static final String IS_NOTIFICATION = "is_notification";
    public static final String ROLE = "role";
    public static final String IS_FIRST_TIME = "is_first_time";

    /**
     * Name of the preference file
     */
    private static final String APP_PREFS = "application_preferences";
    private final Context mContext;
    private static SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static SharedPrefUtil instance;

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtil(context);
        }
    }

    public static SharedPrefUtil getInstance() {
        if (instance == null) {
            instance = new SharedPrefUtil(AppController.Companion.getnstance());
        }
        return instance;
    }


    /**
     * Save a string into shared preference
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public void saveString(String key, String value) {

        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    /**
     * Save a int into shared preference
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public void saveInt(String key, int value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    /**
     * Save a boolean into shared preference
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public void saveBoolean(String key, boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or null.
     * Throws ClassCastException if there is a preference with this name that is not a String.
     */
    public String getString(String key) {
        //    mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, "");
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key          The name of the preference to retrieve.
     * @param defaultValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defaultValue.
     * Throws ClassCastException if there is a preference with this name that is not a String.
     */
    public String getString(String key, String defaultValue) {
        //    mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Retrieve a int value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or 0.
     * Throws ClassCastException if there is a preference with this name that is not a int.
     */
    public int getInt(String key) {
        // mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(key, 0);
    }

    /**
     * Retrieve a int value from the preferences.
     *
     * @param key          The name of the preference to retrieve.
     * @param defaultValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defaultValue.
     * Throws ClassCastException if there is a preference with this name that is not a int.
     */
    public int getInt(String key, int defaultValue) {
        //     mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or false.
     * Throws ClassCastException if there is a preference with this name that is not a boolean.
     */
    public boolean getBoolean(String key) {
        //     mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key          The name of the preference to retrieve.
     * @param defaultValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defaultValue.
     * Throws ClassCastException if there is a preference with this name that is not a boolean.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, defaultValue);
    }


    /**
     * save the device token.
     *
     * @param token Vslue retrieved from the firebase.
     */
    public void saveDeviceToken(String token) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(DEVICE_TOKEN, token);
        mEditor.apply();
    }


    /**
     * get the current device token
     *
     * @return Returns the last update device token got from firebase.
     */
    public String getDeviceToken() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(DEVICE_TOKEN, "");
    }


    //
    public void saveRole(String role) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(ROLE, role);
        mEditor.apply();
    }


    /**
     * get the current device token
     *
     * @return Returns the last update device token got from firebase.
     */
    public String getRole() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(ROLE, "");
    }



    /* save  is first time login */

    public void saveFirstTime(String firstTime) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(IS_FIRST_TIME, firstTime);
        mEditor.apply();
    }


    /**
     * get the is first time login *
     * @return Returns the last update device token got from firebase.
     */

    public String getFirstTime() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(IS_FIRST_TIME, "");
    }



    /**
     * save the userId of current user using the application
     *
     * @param userId this is the userId of the user
     */
    public void saveUserId(String userId) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(USER_ID, userId);
        mEditor.apply();
    }

    /**
     * get current login user's userId .
     *
     * @return Returns the last userId saved.
     */
    public String getUserId() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(USER_ID, "");
    }

    /**
     * save the latest authorization token of the user
     *
     * @param authToken authoriztion token of the user.
     */
    public void saveAuthToken(String authToken) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(AUTH_TOKEN, authToken);
        mEditor.apply();
    }

    /**
     * get the authorization of the user
     */
    public String getAuthToken() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(AUTH_TOKEN, "");
    }


/* Set COUNTRY_CODE_NAME*/
    public void saveCountryCodeName(String countryCodeName) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(COUNTRY_CODE_NAME, countryCodeName);
        mEditor.apply();
    }

    /**
     * get the COUNTRY_CODE_NAME of the user
     */
    public String getCountryCodeName() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(COUNTRY_CODE_NAME, "");
    }


    /**
     * set login state oin the device is there any user current login device
     */
    public void setLogin(boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(LOGIN, value);
        mEditor.apply();
    }

    /**
     * @return is anyuser current login into the device accordingly true/false.
     */
    public boolean isLogin() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(LOGIN, false);
    }

    /**
     * save name of the user
     */
    public void saveName(String name) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(NAME, name);
        mEditor.apply();
    }

    /**
     * get name of the user
     */
    public String getName() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(NAME, "");
    }


    public void saveLocation(String location) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(LOCATON, location);
        mEditor.apply();
    }

    /**
     * get location of the user
     */
    public String getLocaton() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(LOCATON, "");
    }



    public void savePhone(String phone) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(PHONE_NUMBER, phone);
        mEditor.apply();
    }

    /**
     * get phone of the user
     */
    public String getPhone() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(PHONE_NUMBER, "");
    }


    /**
     * save image of the user
     */
    public void saveImage(String image) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(IMAGE, image);
        mEditor.apply();
    }

    /**
     * get image of the user
     */
    public String getImage() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(IMAGE, "");
    }

    public void savePassword(String password) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(PASSWORD, password);
        mEditor.apply();
    }

    public String getPassword() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(PASSWORD, "");
    }


    public void saveEmail(String email) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(EMAIL, email);
        mEditor.apply();
    }

    public String getEmail() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(EMAIL, "");
    }

    public void saveNotifyIcon(boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(NOTIFY_ICON, value);
        mEditor.apply();
    }

    public boolean getNotifyIcon() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(NOTIFY_ICON, true);
    }


    public void saveBadge(int badge) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(BADGE, badge);
        mEditor.apply();
    }

    public void saveVendorid(int vendorid) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(VENDORID, vendorid);
        mEditor.apply();
    }

    public int getBadge() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(BADGE, 0);
    }
    public int getvednorId() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(VENDORID, 0);
    }

    public void saveNotify(int notify) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(NOTIFY, notify);
        mEditor.apply();
    }

    public int getNotify() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(NOTIFY, 0);
    }

    /**
     * Clears the shared preference file
     */
    public void clear() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        //  mSharedPreferences.edit().clear().apply();
        mEditor = mSharedPreferences.edit();
        saveLocation("");
        saveAuthToken("");
        saveUserId("");
        saveRole("");
        saveImage("");
        savePhone("");
        mEditor.apply();

    }


    /**
     * save user all data when user user login into the device
     *
     * @param userModel
     */
/*
    public void saveLoginData(UserModel userModel) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(USER_ID, String.valueOf(userModel.getId()));
        mEditor.putString(AUTH_TOKEN, userModel.getAuthorizationKey());
        mEditor.putString(NAME, userModel.getUsername());
        mEditor.putString(EMAIL, userModel.getEmail());
        mEditor.putString(IMAGE, userModel.getImage());
        mEditor.putString(LOGIN_TYPE, NORMAL);
        mEditor.putString(PHONE_NUMBER, userModel.getMobile());
        mEditor.apply();
    }
*/

    /**
     * save user all data when user user login into the device through social login
     *
     * @param userModel
     */
/*
    public void saveSocialLoginData(UserModel userModel) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(LOGIN_TYPE, SOCIAL);
        mEditor.putString(USER_ID, String.valueOf(userModel.getId()));
        mEditor.putString(AUTH_TOKEN, userModel.getAuthorizationKey());
        mEditor.putString(NAME, userModel.getName());
        mEditor.putString(EMAIL, userModel.getEmail());
        mEditor.putString(IMAGE, userModel.getImage());
        mEditor.putString(PHONE_NUMBER, userModel.getMobile());
        mEditor.apply();
    }
*/

    /**
     * clear all user's save data whenever user logout the device.
     */
    public void onLogout() {
        mEditor = mSharedPreferences.edit();
        if (!getPassword().equals("")) {
            mEditor.remove(USER_ID);
            mEditor.remove(AUTH_TOKEN);
            mEditor.remove(EMAIL);
            mEditor.remove(IMAGE);
            mEditor.remove(NOTIFY_ICON);
            mEditor.remove(NOTIFY);
            //  mEditor.remove(PHONE_NUMBER);
        } else {
            mEditor.remove(USER_ID);
            mEditor.remove(AUTH_TOKEN);
            mEditor.remove(NAME);
            mEditor.remove(EMAIL);
            mEditor.remove(IMAGE);
            mEditor.remove(NOTIFY_ICON);
            mEditor.remove(NOTIFY);
            //mEditor.remove(PHONE_NUMBER);
        }
        setLogin(false);
        mEditor.apply();
    }

    public void saveLatitude(String latitude) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(LATITUDE, latitude);
        mEditor.apply();
    }

    public void saveLongitude(String longitude) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(LONGITUDE, longitude);
        mEditor.apply();
    }

    public String getLatitude() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(LATITUDE, "");

    }

    public String getLongitude() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(LONGITUDE, "");

    }

    public void saveCountryCode(String countryCode) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(COUNTRY_CODE, countryCode);
        mEditor.apply();
    }

    public String getCountryCode() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(COUNTRY_CODE, "");

    }

    public void saveCity(String city) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(CITY, city);
        mEditor.apply();
    }

    public String getCity() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(CITY, "");

    }


    public void savePushNotificationStatus(String isNotification) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(IS_NOTIFICATION, isNotification);
        mEditor.apply();
    }

    public String getPushNotificationStatus() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(IS_NOTIFICATION, "");
    }

}

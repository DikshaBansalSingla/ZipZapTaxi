package com.zipzaptaxi.live.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import com.zipzaptaxi.live.base.AppController;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by admin on 25-09-2018.
 */

public class ValidationsClass {

    private Activity mActivity;
      private static ValidationsClass validations = null;
//    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    private static final String EMAIL_PATTERN =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x07\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01" +
                    "-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x07\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public ValidationsClass() {
    }

    public static ValidationsClass getInstance() {
        if (validations == null) {
            validations = new ValidationsClass();
        }
        return validations;
    }

    public boolean checkStringNull(String string) {
        return string == null || string.equals("null") || string.isEmpty();
    }

    public boolean isPasswordMatching(String password, String confirmPassword) {
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);

        // do your Toast("passwords are not matching");
        return matcher.matches();
    }


    public boolean checkObjectNull(Object obj) {
        return !(obj == null);
    }

    // validate first name
    public boolean isValidFirstName(String mFirstName) {
        return mFirstName.matches("^[A-Za-z]");
//        return mFirstName.matches("[A-Z][a-zA-Z]*");
    } // end method validateFirstName

    // validate last name
    public boolean isValidLastName(String mLastName) {
        return mLastName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*");
    } // end method validateLastName

    public boolean isValidPhone(String mPhone, int minLenght, int maxLenght) {
        return !(mPhone.length() < minLenght || mPhone.length() > maxLenght) && Patterns.PHONE.matcher(mPhone).matches();
    }

    public boolean isValidEmail(String mEmail) {
        if (checkStringNull(mEmail))
            return false;
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(mEmail);
        return matcher.matches();
    }



    public boolean isValidPassword(String mPassword) {
        return mPassword.matches("^.{6,}$");
    }
    public boolean isValidPhone(String mPhoneNumber) {
        return mPhoneNumber.matches("^.{10,10}$");
    }

    public boolean validatePhoneNumber(String phoneNumber) {
        // Regular expression to match a 10-digit Indian phone number starting with 6, 7, 8, or 9
        String regex = "^[6-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }

    public boolean isValidBankAccountNumber(String accountNumber) {
        // Regular expression pattern for a bank account number
        // You may need to adjust this pattern based on the specific format of bank account numbers in your region
        String bankAccountPattern = "^[0-9]{9,18}$"; // Example pattern for 9 to 18 digit account numbers

        return accountNumber.matches(bankAccountPattern);
    }

    public boolean isValidIFSC(String ifscCode) {
        // Regular expression pattern for IFSC code
        // Example pattern: ABCD0123456 (4 letters followed by 7 digits)
        String ifscPattern = "^[A-Za-z]{4}[0-9]{7}$";

        return ifscCode.matches(ifscPattern);
    }

    public boolean isValidUPI(String upiId) {
        // Regular expression pattern for UPI ID
        // Example pattern: username@upi or phone_number@upi or bankaccount@upi
        // Also, consider other common UPI domains like googlepay, paytm, phonepe, etc.
        String upiPattern = "^[a-zA-Z0-9._-]+@(upi|googlepay|paytm|phonepe|otherupi)\\.[a-zA-Z]{2,}$";

        return upiId.matches(upiPattern);
    }


    public boolean isValidTraitName(String mPassword) {
        return mPassword.matches("^.{3,}$");
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) AppController.Companion.getnstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null)
            activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public RequestBody createPartFromString(String string) {
        return RequestBody.create(
                MultipartBody.FORM, string);
    }

    public MultipartBody.Part prepareFilePart(String partName, File fileUri) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        fileUri
                );

        return MultipartBody.Part.createFormData(partName, fileUri.getName(), requestFile);
    }
    /*public MultipartBody.Part prepareFileVideoPart(String partName, File fileUri) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("video/*"),
                        fileUri
                );

        return MultipartBody.Part.createFormData(partName, fileUri.getName(), requestFile);
    }*/

    public MultipartBody.Part prepareFileVideoPart(String partName, File fileUri) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileUri);
        return MultipartBody.Part.createFormData(partName, fileUri.getName(), requestFile);
    }

    public String convertTimeStempToTime(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        //outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return outputFormat.format(cal.getTime());
    }



    /**
     * Check whether Google Play Services are available.
     * <p>
     * If not, then display dialog allowing user to update Google Play Services
     *
     * @return true if available, or false if not
     */
  /*  public boolean checkGooglePlayServicesAvailable(Activity mActivity) {
        final int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MyApplication.getInstance().getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
            return true;
        Log.e("Google Play Services", "Google Play Services not available: " + GoogleApiAvailability.getInstance().getErrorString(status));
        if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
            final Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mActivity, status, 1);
            if (errorDialog != null)
                errorDialog.show();
        }
        return false;
    }*/
}

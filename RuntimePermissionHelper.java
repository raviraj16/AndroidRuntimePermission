package com.permissiondemo.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Ravi Raj Priyadarshi on 19-09-2017.
 */

public class RuntimePermissionHelper {
    public static final int PERMISSION_PHONE_REQUEST_CODE = 101;
    private Activity activity;
    private PermissionResultCallback permissionResultCallback;

    public RuntimePermissionHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check for phone state permission
     *
     * @param permissionResultCallback
     */
    public void checkForPhoneStatePermission(PermissionResultCallback permissionResultCallback) {

        this.permissionResultCallback = permissionResultCallback;

        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        String requestablePermissions = getRequestablePermissionsIfAny(permissions);

        if (TextUtils.isEmpty(requestablePermissions)) {
            permissionResultCallback.onGranted(permissions);
        } else {
//            requestPermission(requestablePermissions.trim().split("\\s+"), PERMISSION_PHONE_REQUEST_CODE);
            ActivityCompat.requestPermissions(activity, requestablePermissions.trim().split("\\s+"), PERMISSION_PHONE_REQUEST_CODE);
        }
    }

    /**
     * Check which permission is not granted
     *
     * @param permissions string array of permissions
     * @return
     */
    private String getRequestablePermissionsIfAny(String[] permissions) {
        String requestablePermissions = "";
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                requestablePermissions += permission + " ";//Add different permissions separated with space

            }
        }
        return requestablePermissions;
    }


    private void requestPermission(String[] permissions, int reqCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {//If the user have clicked never ask again then false is returned
//            Toast.makeText(activity.getApplicationContext(), permissions[0] + " Denied.", Toast.LENGTH_LONG).show();
            if (null != permissionResultCallback)
                permissionResultCallback.onDenied(permissions);
        } else {
            ActivityCompat.requestPermissions(activity, permissions, reqCode);
        }
    }


    public interface PermissionResultCallback {
        void onGranted(String permissions[]);

        void onDenied(String permissions[]);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_PHONE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean isGranted = true;
                    int pos = 0;
                    for (int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            isGranted = false;
                            break;
                        }
                        pos++;
                    }
                    if (null != permissionResultCallback) {
                        if (isGranted)
                            permissionResultCallback.onGranted(permissions);
                        else permissionResultCallback.onDenied(new String[]{permissions[pos]});
                    }

                } else {
                    if (null != permissionResultCallback)
                        permissionResultCallback.onDenied(permissions);
                }
                break;
        }
    }
}

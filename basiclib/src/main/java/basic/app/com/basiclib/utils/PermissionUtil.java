package basic.app.com.basiclib.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

/**
 * 权限工具
 */
public class PermissionUtil {

    /**
     * 判断应用是否具有某个权限
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        if (context == null) {
            return false;
        }
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (DeviceUtil.getTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }


}

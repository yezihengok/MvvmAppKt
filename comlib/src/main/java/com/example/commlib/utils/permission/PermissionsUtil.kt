package com.example.commlib.utils.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings

/**
 * 检查权限的工具类
 * Created by bm on 17/2/7.
 */
object PermissionsUtil {
    //相机权限
    val PERMISSION_CAMERA = arrayOf(
        Manifest.permission.CAMERA
    )

    //录制视频
    val PERMISSION_VIDEO = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
    )

    //相机权限、写入权限
    val PERMISSION_CAMERA_WRITE = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //获取手机状态权限
    val PERMISSION_STATE = arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )

    //文件读取权限
    val PERMISSION_FILE = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,  // 写入权限
        Manifest.permission.READ_EXTERNAL_STORAGE //读取权限
    )

    //手机通讯录权限
    val PERMISSION_CONTACTS = arrayOf(
        Manifest.permission.READ_CONTACTS,  // 读取通讯录权限
        Manifest.permission.WRITE_CONTACTS,  //编辑通讯录权限
        Manifest.permission.GET_ACCOUNTS
    )

    //获取位置权限
    val PERMISSION_LOCATION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    //录音权限
    val PERMISSION_MICROPHONE = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    //电话操作权限
    val PERMISSION_PHONE = arrayOf( //            Manifest.permission.READ_PHONE_STATE,// 读取通讯录权限
        Manifest.permission.CALL_PHONE
    )

    //获取传感器数据权限
    val PERMISSION_SENSORS = arrayOf(
        Manifest.permission.BODY_SENSORS
    )

    //短信操作权限
    val PERMISSION_SMS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS
    )

    // 启动应用的设置
    fun startAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }
    //    /**
    //     * 显示缺失权限提示
    //     */
    //    public static void showMissingPermissionDialog(final Context context,
    //                                                   DialogInterface.OnClickListener cancelListener) {
    //        CommonAlertDialog.Builder builder = new CommonAlertDialog.Builder(context);
    //        builder.setTitle(R.string.help);
    //        builder.setMessage(R.string.string_help_text);
    //        builder.setPositiveButton(R.string.quit, cancelListener);
    //        builder.setNegativeButton(R.string.settings, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                PermissionsUtil.startAppSettings(context);
    //            }
    //        });
    //        builder.setCancelable(false);
    //        builder.show();
    //    }
    //
    //    // 判断含有全部的权限
    //    public static boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
    //        for (int grantResult : grantResults) {
    //            if (grantResult != PackageManager.PERMISSION_GRANTED) {
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    // 判断权限集合
    //    public static boolean lacksPermissions(Context context, String... permissions) {
    //        for (String permission : permissions) {
    //            if (lacksPermission(context, permission)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    // 判断是否缺少权限
    //    public static boolean lacksPermission(Context context, String permission) {
    //        return ContextCompat.checkSelfPermission(context, permission) !=
    //                PackageManager.PERMISSION_GRANTED;
    //    }
    //
    //    // 请求权限兼容低版本
    //    public static void requestPermissions(Activity aty, String[] permissions, int requestCode) {
    //        ActivityCompat.requestPermissions(aty, permissions, requestCode);
    //    }
    //
    //    // 检测权限配置
    //    public static boolean checkPermissions(Activity aty, String[] permissions, int requestCode) {
    //        if (permissions != null && permissions.length > 0) {
    //            if (PermissionsUtil.lacksPermissions(aty, permissions)) {
    //                PermissionsUtil.requestPermissions(aty, permissions, requestCode);
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    //检测权限配置,不处理结果
    //    public static boolean checkPermissionsUndo(Activity aty, String[] permissions) {
    //        return checkPermissions(aty, permissions, PERMISSION_REQUEST_CODE_UNDO);
    //    }
    //
    //    //检测权限配置,强制打开
    //    public static boolean checkPermissionsTodo(Activity aty, String[] permissions) {
    //        return checkPermissions(aty, permissions, PERMISSION_REQUEST_CODE_TODO);
    //    }
    //
    //    /**
    //     * 检测权限配置
    //     */
    //    public static boolean checkPermissions(Fragment fragment, String[] permissions, int
    //            requestCode) {
    //        if (permissions != null && permissions.length > 0) {
    //            if (PermissionsUtil.lacksPermissions(fragment.getContext(), permissions)) {
    //                fragment.requestPermissions(permissions, requestCode);
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    public static boolean checkPermissionsFile(Activity aty, int requestCode) {
    //        return checkPermissions(aty, PERMISSION_FILE, requestCode);
    //    }
    //
    //    public static boolean checkPermissionsFile(Activity aty) {
    //        return checkPermissionsUndo(aty, PERMISSION_FILE);
    //    }
    //
    //    public static boolean checkPermissionsPhoneState(Activity aty, int requestCode) {
    //        return checkPermissions(aty, PERMISSION_STATE, requestCode);
    //    }
    //
    //    public static boolean checkPermissionsPhoneState(Activity aty) {
    //        return checkPermissionsUndo(aty, PERMISSION_STATE);
    //    }
    //
    //    public static boolean checkPermissionsCameraWrite(Activity aty, int requestCode) {
    //        return checkPermissions(aty, PERMISSION_CAMERA_WRITE, requestCode);
    //    }
    //
    //
    //    public static boolean checkPermissionsCamera(Activity aty, int requestCode) {
    //        return checkPermissions(aty, PERMISSION_CAMERA, requestCode);
    //    }
    //
    //    public static boolean checkPermissionsVideo(Activity aty, int requestCode) {
    //        return checkPermissions(aty, PERMISSION_VIDEO, requestCode);
    //    }
    //
    //    public static boolean checkPermissionsVideo(Activity aty) {
    //        return checkPermissionsUndo(aty, PERMISSION_VIDEO);
    //    }
    //
    //
    //
    //    public static boolean checkPermissionsCamera(Activity aty) {
    //        return checkPermissionsUndo(aty, PERMISSION_CAMERA);
    //    }
    /**
     * API22（含）之前用于判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的，22之后用动态权限
     *
     * @return true 表示开启
     */
    fun isOPenLocation(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }
}
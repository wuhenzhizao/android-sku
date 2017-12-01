package com.wuhenzhizao.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 电话-工具
 */
public class TelephoneUtils {
    private static final String TAG = TelephoneUtils.class.getSimpleName();

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    public static ConnectivityManager getConnetManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkInfo getNetWorkInfo(Context context) {
        return getConnetManager(context).getActiveNetworkInfo();
    }

    /**
     * 网络是否激活状态
     *
     * @param context
     * @return 权限：android.Manifest.permission#ACCESS_NETWORK_STATE
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo = getNetWorkInfo(context);
        return (networkInfo != null) && (networkInfo.isAvailable() && networkInfo.isConnected());
    }

    /**
     * WIFI是否可用
     *
     * @param context
     * @return 权限：android.Manifest.permission#ACCESS_NETWORK_STATE
     */
    public static boolean isWifiEnable(Context context) {
        NetworkInfo networkInfo = getNetWorkInfo(context);
        return (networkInfo != null) && (networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 获取缓存目录大小（单个应用最大缓存限制）
     *
     * @param context
     * @return
     */
    public static int getCacheSize(Context context) {
        return 1024 * ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * 获取DEVICE
     *
     * @return
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
     * IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的
     * 其组成为：
     * 1. 前6位数(TAC)是”型号核准号码”，一般代表机型
     * 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地
     * 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号
     * 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用
     */
    public static String getIMEI(Context context) {
        String str = getTelephonyManager(context).getDeviceId();
        return TextUtils.isEmpty(str) ? "unknown" : str;
    }

    /**
     * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
     * IMSI共有15位，其结构如下：
     * MCC+MNC+MIN
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;
     * MNC:Mobile NetworkCode，移动网络码，共2位
     * 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
     * 合起来就是（也是Android手机中APN配置文件中的代码）：
     * 中国移动：46000 46002
     * 中国联通：46001
     * 中国电信：46003
     * 举例，一个典型的IMSI号码为460030912121001
     */
    public static String getIMSI(Context context) {
        String str = getTelephonyManager(context).getSubscriberId();
        return str == null ? "" : str;
    }

    /**
     * 获取手机MAC地址
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            if (info != null && !TextUtils.isEmpty(info.getMacAddress())) {
                return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00:00:00:00:00:00";
    }

    /**
     * 获取手机网络名称
     *
     * @param context
     * @return
     */
    public static String getNetWorkName(Context context) {
        return getNetworkType(context).toLowerCase();
    }

    /**
     * Returns the alphabetic name of current registered operator.
     * <p/>
     * Availability: Only when user is registered to a network. Result may be unreliable on CDMA networks (use
     * {@link TelephonyManager#getPhoneType()} to determine if on a CDMA network).
     */
    public static String getNetworkOperatorName(Context context) {
        return getTelephonyManager(context).getNetworkOperatorName();
    }

    /**
     * 获取手机当前语言
     *
     * @return
     */
    public static String getPhoneLanguage() {
        String str = Locale.getDefault().getLanguage();
        return str == null ? "" : str;
    }

    /**
     * 获取手机类型
     *
     * @return
     */
    public static String getPhoneType() {
        String str = Build.MODEL;
        if (str != null)
            str = str.replace(" ", "");
        return str.trim();
    }


    /**
     * 获取Product
     *
     * @return
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * @param context
     * @param unit    The unit to convert from. {@link TypedValue#TYPE_DIMENSION}.
     * @param value   value The value to apply the unit to.
     * @return The complex floating point value multiplied by the appropriate metrics depending on its unit.
     */
    public static int getRawSize(Context context, int unit, float value) {
        Resources resources;
        if (context == null)
            resources = Resources.getSystem();
        else
            resources = context.getResources();
        return (int) TypedValue.applyDimension(unit, value, resources.getDisplayMetrics());
    }

    /**
     * 获取手机分辨率
     *
     * @param activity
     * @return
     */
    public static String getResolution(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels + "x" + dm.heightPixels;
    }

    /**
     * 获取手机分辨率
     *
     * @param context
     * @return
     */
    public static String getResolution(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels + "x" + dm.heightPixels;
    }

    /***
     * 获取手机屏幕密度
     */
    public static String getDensityDpi(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return "" + dm.densityDpi;
    }

    /***
     * 获取手机屏幕密度
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }


    /**
     * 获取SDK版本
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getSDKVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 获取SDK版本名称
     *
     * @return
     */
    public static String getSDKVersionName() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前服务(网络)类型
     *
     * @param context
     * @return wifi/mobile/unicom/telecom
     */
    public static String getServiceName(Context context) {
        if (getNetworkType(context).equals("wifi"))
            return "wifi";
        if (isConnectChinaMobile(context))
            return "移动";
        if (isConnectChinaUnicom(context))
            return "联通";
        if (isConnectChinaTelecom(context))
            return "电信";
        return "";
    }

    public static String getNetworkName(Context context) {
        if ("wifi".equals(getNetworkType(context))) {
            return "wifi";
        } else {
            String operator = "unknown";
            if (isConnectChinaMobile(context)) {
                operator = "cmcc";
            } else if (isConnectChinaTelecom(context)) {
                operator = "ctcc";
            } else if (isConnectChinaUnicom(context)) {
                operator = "cucc";
            }
            return String.format("%s:%s", operator, getNetworkClass(context));
        }
    }

    /**
     * @param context
     * @return network class such as 2G, 3G or 4G
     */
    public static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "unknown";
        }
    }

    public static String getType() {
        return Build.TYPE;
    }

    /**
     * 获取UserAgent
     *
     * @return
     */
    public static String getUserAgent() {
        return getPhoneType();
    }

    public static String getAccessPointType(Context context) {
        NetworkInfo networkInfo = getNetWorkInfo(context);
        if (networkInfo != null && networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
            return networkInfo.getExtraInfo();
        }
        return null;
    }

    public static boolean isIpAddress(String host) {
        boolean isAllValidChars = true;
        int dotCount = 0; // .
        int colonCount = 0; // :
        for (int i = 0; i < host.length(); i++) {
            char ch = host.charAt(i);
            if (ch == ':') {
                colonCount++;
            } else if (ch == '.') {
                dotCount++;
            } else {
                char upper = Character.toUpperCase(ch);
                if ((upper >= 48 && upper <= 57) || (upper >= 65 && upper <= 70)) {
                    // is valid ip char
                } else {
                    // invalid char
                    isAllValidChars = false;
                    break;
                }
            }
        }

        if (isAllValidChars) {
            if (dotCount == 3) {
                return true;
            } else return colonCount >= 2;
        } else {
            return false;
        }
    }

    public static boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6StdAddress(String input) {
        return IPV6_STD_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6HexCompressedAddress(String input) {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6Address(String input) {
        return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
    }

    // 得到当前使用的接入点的代理地址
    public static Proxy getApnProxy(Context context) {
        ConnectivityManager manager = getConnetManager(context);
        NetworkInfo ni = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (ni.isConnected()) { // 如果有wifi连接，则选择wifi，不返回代理
            return null;
        } else {
            try {
                Cursor c = getCurrentApn(context); // 得到默认apn
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();

                    String proxy = c.getString(c.getColumnIndex("proxy")); // 得到代理
                    String port = c.getString(c.getColumnIndex("port")); // 得到断开号
                    c.close();

                    if (proxy != null && !proxy.equals("")) { // 代理地址不为空
                        if (port != null && !port.equals("")) {
                            if (isIpAddress(proxy)) {
                                return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxy, Integer.valueOf(port)));
                            } else {
                                return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy, Integer.valueOf(port)));
                            }
                        } else {
                            return null;
                        }
                    }
                } else {
                    if (c != null) {
                        c.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 得到手机上面当前默认使用的接入点
     *
     * @param context
     * @return
     */
    public static Cursor getCurrentApn(Context context) {
        return context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), null, null, null, null);
    }

    public static String getCurrentApnProxy(Context context) {
        Cursor cur = null;
        try {
            Uri uri = Uri.parse("content://telephony/carriers/preferapn");
            cur = context.getContentResolver().query(uri, null, null, null, null);
            if ((cur != null) && (cur.moveToFirst())) {
                String str = cur.getString(cur.getColumnIndex("proxy"));
                return str;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return null;
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            String typeName = networkInfo.getTypeName();
            if (!TextUtils.isEmpty(typeName) && typeName.toLowerCase().equals("wifi")) {
                return "wifi";
            }
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                return extraInfo.toLowerCase();
            }
        }
        return "wifi not available";
    }

    /**
     * 获取代理IP
     *
     * @param paramString
     * @param context
     * @return
     */
    public static String getProxyIp(String paramString, Context context) {
        if (paramString == null)
            return null;
        Cursor cur = null;
        try {
            Uri uri = Uri.parse("content://telephony/carriers");
            cur = context.getContentResolver().query(uri, null, null, null, null);
            String id;
            do {
                if (cur != null) {
                    boolean bool = cur.moveToNext();
                    if (bool)
                        ;
                } else {
                    return null;
                }
                id = cur.getString(cur.getColumnIndex("_id"));
            } while (!paramString.trim().equals(id));
            return cur.getString(cur.getColumnIndex("proxy"));
        } finally {
            if (cur != null)
                cur.close();
        }

    }

    /**
     * 获取WIFI信息
     *
     * @param context
     * @return
     */
    public static WifiInfo getWifiStatus(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
    }

    /**
     * 是否为cmwap网络
     *
     * @param context
     * @return
     */
    public static boolean isCmwap(Context context) {
        if ((!isConnectChinaMobile(context)) || (!isMobileType(context)))
            return false;
        String str = getCurrentApnProxy(context);
        if (str == null)
            return false;
        return (str.equals("10.0.0.172")) || (str.equals("010.000.000.172"));
    }

    /**
     * 是否为中国移动
     *
     * @param context
     * @return
     */
    public static boolean isConnectChinaMobile(Context context) {
        // MCC+MNC (mobile country code + mobile network code)
        String str = getTelephonyManager(context).getSimOperator();
        if (str != null)
            return (str.startsWith("46000")) || (str.startsWith("46002"));
        return false;
    }

    /**
     * 是否为中国电信
     *
     * @param context
     * @return
     */
    public static boolean isConnectChinaTelecom(Context context) {
        String str = getTelephonyManager(context).getSimOperator();
        if (str != null)
            return str.startsWith("46003");
        return false;
    }

    /**
     * 是否为中国联通
     *
     * @param context
     * @return
     */
    public static boolean isConnectChinaUnicom(Context context) {
        String str = getTelephonyManager(context).getSimOperator();
        if (str != null)
            return str.startsWith("46001");
        return false;
    }

    /**
     * 当前网络类型（Is Mobile）
     *
     * @param context
     * @return
     */
    public static boolean isMobileType(Context context) {
        NetworkInfo networkInfo = getNetWorkInfo(context);
        if (networkInfo != null) {
            return "mobile".equalsIgnoreCase(networkInfo.getTypeName());
        }
        return false;
    }

    /**
     * 手机-短-震动
     *
     * @param context
     */
    public static void shotVibratePhone(Context context) {
        Vibrator vib = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        vib.vibrate(100L);
    }

    /**
     * 获取当前版本号，升级用
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        int version = 1;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            version = pi.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取当前版本,升级用
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (info != null) {
                versionName = info.versionName;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * SDCard是否可用
     *
     * @return
     */
    public static boolean existSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        return getTelephonyManager(context).getLine1Number();
    }


    /**
     * 获取手机唯一识别码
     * UUID+设备号序列号 唯一识别码（不可变）
     **/
    public static String getPhoneUUID(Context context) {

        final TelephonyManager tm = getTelephonyManager(context);

        final String tmDevice, tmSerial, tmPhone, androidId;

        tmDevice = "" + tm.getDeviceId();

        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        String uniqueId = deviceUuid.toString();

        return uniqueId;
    }

    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    // ipv4地址
                    if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

        } catch (Exception ex) {
            return "";
        }
        return "";
    }

    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData.getString(key);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<Camera.Size> getResolutionList(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }

    public static class ResolutionComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.height!=rhs.height)
                return lhs.height-rhs.height;
            else
                return lhs.width-rhs.width;
        }

    }

    /**
     * Print telephone info.
     */
    public static String printTelephoneInfo(Context context) {
        StringBuilder sb = new StringBuilder();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = tm.getSubscriberId();
        //IMSI前面三位460是国家号码，其次的两位是运营商代号，00、02是中国移动，01是联通，03是电信。
        String providerName = null;
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                providerName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                providerName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                providerName = "中国电信";
            }
        }
        sb.append(providerName).append("  手机号：").append(tm.getLine1Number()).append(" IMSI是：").append(IMSI);
        sb.append("\nDeviceID(IMEI)       :").append(tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion:").append(tm.getDeviceSoftwareVersion());
        sb.append("\ngetLine1Number       :").append(tm.getLine1Number());
        sb.append("\nNetworkCountryIso    :").append(tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator      :").append(tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName  :").append(tm.getNetworkOperatorName());
        sb.append("\nNetworkType          :").append(tm.getNetworkType());
        sb.append("\nPhoneType            :").append(tm.getPhoneType());
        sb.append("\nSimCountryIso        :").append(tm.getSimCountryIso());
        sb.append("\nSimOperator          :").append(tm.getSimOperator());
        sb.append("\nSimOperatorName      :").append(tm.getSimOperatorName());
        sb.append("\nSimSerialNumber      :").append(tm.getSimSerialNumber());
        sb.append("\ngetSimState          :").append(tm.getSimState());
        sb.append("\nSubscriberId         :").append(tm.getSubscriberId());
        sb.append("\nVoiceMailNumber      :").append(tm.getVoiceMailNumber());

        Log.i(TAG, sb.toString());
        return sb.toString();
    }
}

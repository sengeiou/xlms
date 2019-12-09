package com.gdswlw.library.toolkit;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * UIKit
 * Created by SHZ on 2016/8/24.
 */
public class UIKit {

    private static Application context;
    private static Toast toast;

    /**
     * Must be initialized before using it
     * @param context
     */
    public static void  init(Application context){
        if(UIKit.context == null){
            if(context == null){
                throw  new IllegalArgumentException("UIKit not initialized,context is null!");
            }
            UIKit.context = context;
        }
        toast = Toast.makeText(UIKit.context, "UIKit initialized",
                Toast.LENGTH_SHORT);
    }


    /**
     * show toast
     * @param s
     */
    public static  void toastShort(@NonNull  String s) {
        checkConfig();
        toast.setDuration(Toast.LENGTH_SHORT);
        showToast(s);
    }

    /**
     * show toast
     * @param s
     */
    public static void toastLong(@NonNull String s) {
        checkConfig();
        toast.setDuration(Toast.LENGTH_LONG);
        showToast(s);
    }

    /**
     * showToast
     * @param s
     */
    private static void showToast(@NonNull  String s) {
        toast.setText(s);
        toast.show();
    }

    /**
     * Check whether or not  initialized ?
     * @return
     */
    private static boolean  checkConfig(){
        if(context == null){
            throw  new IllegalStateException("UIKit not initialized");
        }
        return true;
    }


    /**
     * Get systemService
     * @param systemServiceName
     * @return
     */
    public static Object systemService(@NonNull String systemServiceName) {
        checkConfig();
        return context.getSystemService(systemServiceName);
    }


    /**
     * openKeyboard
     * @param et
     */
    public static void openKeyboard(@NonNull EditText et) {
        InputMethodManager imm = (InputMethodManager) systemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * closeKeyboard
     * @param
     * @param
     *
     */
    public static  void closeKeyboard(@NonNull EditText et) {
        InputMethodManager imm = (InputMethodManager) systemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


    public static boolean isDebugable(){
        checkConfig();
        return  ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

    public static void eLog(@NonNull String string){
        if(string != null){
            eLog("UIKit",string);
        }

    }

    /**
     * print the error log
     * @param tag
     * @param string
     */
    public static void eLog(@NonNull String tag,@NonNull String string){
        if(isDebugable()){
            Log.e(tag,string);
        }
    }

    public static void dLog(@NonNull String string){
        dLog("UIKit",string);
    }

    /**
     * print the debug log
     * @param tag
     * @param string
     */
    public static void dLog(@NonNull String tag,@NonNull String string){
        if(isDebugable()){
            Log.d(tag,string);
        }
    }


    public static void wLog(@NonNull String string){
        wLog("UIKit",string);
    }

    /**
     * print the warming log
     * @param tag
     * @param string
     */
    public static void wLog(@NonNull String tag,@NonNull String string){
        if(isDebugable()){
            Log.w(tag,string);
        }
    }


    public static void iLog(@NonNull String string){
        iLog("UIKit",string);
    }

    /**
     * print the info log
     * @param tag
     * @param string
     */
    public static void iLog(@NonNull String tag,@NonNull String string){
        if(isDebugable()){
            Log.i(tag,string);
        }
    }

}

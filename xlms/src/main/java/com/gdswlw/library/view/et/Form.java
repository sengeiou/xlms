package com.gdswlw.library.view.et;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gdswlw.library.view.et.FormEditText.RegFormat;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Form extends LinearLayout {
    /**
     * validation callback
     */
    public interface Validation {
        /**
         * onError callback
         * @param errorText error text
         * @param edit FormEditText object
         */
        abstract void onError(String errorText, FormEditText edit);

        /**
         * onSucess callback
         * @param requestParams  Generated request parameter
         */
        abstract void onSucess(HashMap<String, Object> requestParams);
    }

    public Form(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public Form(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    private boolean isValidation = false;
    /**
     * Check whether the legal?
     * @param container
     * @param activity the activity implements the Validation interface
     * @param requestParams the http request params
     * @param onError  callback methoad
     * @throws Exception
     */
    public void Check(ViewGroup container, Activity activity,
                      HashMap<String, Object> requestParams, Method onError) throws Exception {
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount && childCount > 0; i++) {
            View view = container.getChildAt(i);
            if (view instanceof FormEditText && !((FormEditText)view).isIgnore()) {
                FormEditText fet = (FormEditText) view;
                //if content is null
                if (!fet.isNullable()) {
                    if (!checkIsInput(fet)) {
                        onError.invoke(activity, fet.getNullTips(), fet);
                        throw new IllegalArgumentException("The value is invalid");
                    }
                }
                /**
                 * check format is error
                 * if you support custom reg,ignore the regFormat attr!
                 */
                if (!isNull(fet.getRegStr())) {
                    if (!matcher(getEditText(fet), fet.getRegStr())) {
                        onError.invoke(activity, fet.getFormatTip(), fet);
                        throw new IllegalArgumentException("The value is invalid");
                    }
                } else {//through the default reg template
                    if (fet.getRegFormat() != RegFormat.ALL.value()) {
                        if (!check(fet.getRegFormat(), getEditText(fet))) {
                            onError.invoke(activity, fet.getFormatTip(), fet);
                            throw new IllegalArgumentException("The value is invalid");
                        }
                    }
                }
                isValidation = true;
                requestParams.put(fet.getFormKey(), getEditText(fet));
                //set the form param
            }else if(view instanceof FormCheckBox &&  !((FormCheckBox)view).isIgnore()){
                FormCheckBox formCheckBox = (FormCheckBox)view;
                if(formCheckBox.isChecked()){
                    if(requestParams.containsKey(formCheckBox.getFormKey())){
                        requestParams.put(formCheckBox.getFormKey(),
                        new StringBuffer(requestParams.get
                                (formCheckBox.getFormKey()).toString()).append(",").
                                append(formCheckBox.getValue()).toString());
                    }else{
                        requestParams.put(formCheckBox.getFormKey(),formCheckBox.getValue());
                    }

                }
            }else if(view instanceof FormRG){
                handleFormRG((FormRG)view,requestParams);
            }else if (view instanceof ViewGroup) {
                 Check((ViewGroup)view,activity,requestParams,onError);
            }
        }
    }

    /**
     * handle the RadioButton from RadioGroup
     * @param formRG he activity implements the Validation interface
     * @param requestParams  the http request params
     */
    private void handleFormRG(FormRG formRG,HashMap<String, Object> requestParams){
        if(!formRG.isIgnore()){
            int childCount = formRG.getChildCount();
            View view =  null;
            for (int i = 0; i < childCount && childCount > 0; i++) {
                view = formRG.getChildAt(i);
                if(view instanceof FormRB){
                    FormRB formRB = (FormRB)view;
                    if(formRB.isChecked()){
                        requestParams.put(formRG.getKey(),formRB.getValue());
                        return;
                    }
                }
            }
        }
    }

    /**
     * /validation FormEdit
     * @param activity
     * @throws Exception
     */
    public void validation(Activity activity) throws Exception {
        if (activity == null || !(activity instanceof Validation)) {
            throw new IllegalArgumentException("activity is null or activity not implements interface Form.Validation");
        }
        HashMap<String, Object> requestParams = new HashMap<>();
        Method onError = activity.getClass().getDeclaredMethod
                ("onError", String.class, FormEditText.class);
        Method onSucess = activity.getClass().getDeclaredMethod("onSucess", HashMap.class);
        Check(this,activity,requestParams,onError);
        if (isValidation && requestParams.keySet().size() > 0) {
            onSucess.invoke(activity, requestParams);
        }
    }

    /**
     * Check string whether is legal by the regular expression?
     * @param regFormat
     * @param content
     * @return
     */
    boolean check(int regFormat, String content) {
        RegFormat rf = RegFormat.valueOf(regFormat);
        if (rf == RegFormat.CHINESE) {
            return isAllChinese(content);

        } else if (rf == RegFormat.EMAIL) {
            return isEmail(content);

        } else if (rf == RegFormat.PHONE) {
            return isMobileNumber(content);
        }
        return false;
    }


    private boolean checkIsInput(EditText et) {
        if (et == null || getEditText(et).equals("")) {
            return false;
        }
        return true;
    }


    public boolean matcher(String content, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(content);
        return m.matches();
    }


    public boolean isMobileNumber(String mobiles) {
        if (mobiles == null || mobiles.trim().equals("")) {
            return false;
        }
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    public boolean isPersonName(String name) {
        if (name == null || name.trim().equals("")) {
            return false;
        }
        if (Pattern.matches("(([\u4E00-\u9FA5]{2,15}))", name)) {
            return true;
        }
        return false;
    }


    public boolean isAllChinese(String cn) {
        if (cn == null || cn.trim().equals("")) {
            return false;
        }
        if (Pattern.matches("(([\u4E00-\u9FA5]{1,10}))", cn)) {
            return true;
        }
        return false;
    }


    public String getEditText(EditText et) {
        return et == null ? "" : et.getText().toString().trim();
    }

    public boolean isNull(String string) {
        return (string == null) ? true : (string.trim().equals(""));
    }

}

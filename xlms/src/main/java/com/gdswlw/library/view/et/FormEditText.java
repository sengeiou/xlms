package com.gdswlw.library.view.et;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.xl.xyl2.R;


public class FormEditText extends android.support.v7.widget.AppCompatEditText {
	private String formatTip,nullTips,formKey,regStr;
	private boolean nullable;
	private boolean ignore = false;
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getFormatTip() {
		return formatTip;
	}

	public void setFormatTip(String formatTip) {
		this.formatTip = (formatTip == null?"String format error":formatTip);
	}

	public String getNullTips() {
		return nullTips = (nullTips == null?"String must not null":nullTips);
	}

	public void setNullTips(String nullTips) {
		this.nullTips = nullTips;
	}

	public String getFormKey() {
		return formKey;
	}

	public void setFormKey(String formKey) {
		this.formKey = (formKey == null?"noKey":formKey);
	}

	public String getRegStr() {
		return regStr;
	}

	public void setRegStr(String regStr) {
		this.regStr = regStr;
	}

	public int getRegFormat() {
		return regFormat;
	}

	public void setRegFormat(int regFormat) {
		this.regFormat = regFormat;
	}


	private int regFormat;
	public FormEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FormEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.FormAttr);
		setFormatTip(array.getString(R.styleable.FormAttr_formatTip));
		setNullTips(array.getString(R.styleable.FormAttr_nullTips));
		setFormKey(array.getString(R.styleable.FormAttr_key));
		setRegStr(array.getString(R.styleable.FormAttr_regStr));
		setNullable(array.getBoolean(R.styleable.FormAttr_nullable, true));
		setIgnore(array.getBoolean(R.styleable.FormAttr_ignore,false));
		setRegFormat(array.getInt(R.styleable.FormAttr_regFormat, RegFormat.ALL.value()));
		array.recycle();
	}

	public FormEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}


	public enum RegFormat {
		ALL(0),PHONE(1), EMAIL(2), CHINESE(3);
		private int regFormat;

		RegFormat(int regFormat) {
			this.regFormat = regFormat;
		}
		public int value() {
			return this.regFormat;
		}
		public static RegFormat valueOf(int value) {
	        switch (value) {
	        case 1:
	            return PHONE;
	        case 2:
	            return EMAIL;
	        case 3:
	            return CHINESE;
	        default:
	            return ALL;
	        }
	    }
	};

}

package com.android.ttbg.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.provider.Settings;

public class BackLightControl {

	public int getBrightNess(Context context) {

		int screenMode = -1;
		int screenBrightness = -1;
        try {

            /**
             * 获得当前屏幕亮度的模式
             * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
             * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
             */
            screenMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            Utils.Log("screenMode = " + screenMode);
            // 获得当前屏幕亮度值 0--255
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            Utils.Log("Global screenBrightness = " + screenBrightness);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        
        return screenBrightness;
    }
	
	
	public static void setBrightness(Activity context, float brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
        
        //保存当前值
        OperatingSP.setFloat(context, OperatingSP.PREFERENCE_SETTING_LIGHT_SETTING_SEEKBAR, brightness);
}
	

	
	
}

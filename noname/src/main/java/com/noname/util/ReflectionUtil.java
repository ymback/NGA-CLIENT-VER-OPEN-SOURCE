package com.noname.util;

import android.support.v7.app.AppCompatActivity;

public class ReflectionUtil {
    public static boolean actionBar_setDisplayOption(AppCompatActivity activity, int flags) {
        boolean ret = true;
         /*Method setDisplayMethod;
		 Method getActionBarMethod;
		try {//
			getActionBarMethod = activity.getClass().
			 	getMethod("getActionBar");
			Object actionBar = getActionBarMethod.invoke(activity);
			
			//setDisplayMethod= Class.forName("android.app.ActionBar")
			//		.getMethod("setDisplayOptions", int.class);
			setDisplayMethod = actionBar.getClass().getMethod("setDisplayOptions", int.class);
			 setDisplayMethod.invoke(actionBar, flags);
		} catch (Exception e){
			NLog.i(activity.getClass().getSimpleName(),"fail to set actionBar");
			ret = false;
		}*/
        activity.getSupportActionBar().setDisplayOptions(flags);
        return ret;

    }
}

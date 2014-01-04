package sp.phone.utils;

import java.lang.reflect.Method;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class ReflectionUtil {
	public static boolean actionBar_setDisplayOption(ActionBarActivity activity,
			int flags){
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
			Log.i(activity.getClass().getSimpleName(),"fail to set actionBar");
			ret = false;
		}*/
		activity.getSupportActionBar().setDisplayOptions(flags);
		return ret;
		
	}
	
	public static boolean view_setGravity(View v,
			int flags){
		boolean ret = true;
		String methodName = "setGravity";
		 Method setMethod;
		try {//
			setMethod = v.getClass().getMethod(methodName,int.class);
			 setMethod.invoke(v, flags);
		} catch (Exception e){
			Log.i(v.getClass().getSimpleName(),"fail to set gravity");
		}
		return ret;
	}
	
	public static void setShowAsAction(MenuItem item, int actionEnum){
		final String methodName = "setShowAsAction";
		 Method setMethod;
		try {//
			setMethod = MenuItem.class.getMethod(methodName,int.class);
			 setMethod.invoke(item, actionEnum);
		} catch (Exception e){
			Log.i(MenuItem.class.getSimpleName(),"fail to setShowAsAction");
		}
		
	}
	
	public static  OnItemClickListener getOnItemClickListener(Object o){
		OnItemClickListener listener=null;
		try {
			
			Method m = o.getClass().getMethod("getOnItemClickListener");
			listener= (OnItemClickListener) m.invoke(o);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return listener;
	}

}

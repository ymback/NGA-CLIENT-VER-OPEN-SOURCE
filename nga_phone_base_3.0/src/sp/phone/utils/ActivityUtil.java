package sp.phone.utils;

import sp.phone.proxy.DummyFullScreenProxy;
import sp.phone.proxy.FullScreenProxy;
import sp.phone.proxy.V19FullScreenProxy;
import sp.phone.bean.BoardHolder;
import gov.anzong.androidnga.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ActivityUtil {

	public static boolean isGreaterThan_3_0(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB;
	}
	public static boolean isGreaterThan_2_2(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.FROYO;
	}
	public static boolean isGreaterThan_2_1(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1;
	}
	public static boolean isGreaterThan_1_6(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT;
	}
	public static boolean isGreaterThan_2_3(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD;
	}
	public static boolean isGreaterThan_2_3_3(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
	}
	public static boolean isGreaterThan_4_0(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
	public static boolean islessThan_4_1(){
		return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN;
	}
	
	public static boolean isNotLessThan_4_0(){
		return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
	
	public static boolean isLessThan_3_0(){
		return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB;
	}
	
	public static boolean isMeizu(){
		return "Meizu".equalsIgnoreCase(android.os.Build.MANUFACTURER);
	}
	public static boolean isLessThan_4_4(){
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

	public static boolean isLessThan_4_3(){
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN;
    }
	static ActivityUtil instance;
	static final String TAG = ActivityUtil.class.getSimpleName();
	public static final String dialogTag = "saying"; 
	static Object lock= new Object();
	public static ActivityUtil getInstance(){
		if(instance == null){
			instance = new ActivityUtil();
		}
		return instance;//instance;
		
	}
	private ActivityUtil(){
		if(isLessThan_4_4())
            fullScreenProxy  = new DummyFullScreenProxy();
        else
            fullScreenProxy = new V19FullScreenProxy();
	}
	private final FullScreenProxy fullScreenProxy;

    public  void setFullScreen(View v)
    {
        fullScreenProxy.setFullScreen(v);
    }
    public  void setNormalScreen(View v)
    {
        fullScreenProxy.setNormalScreen(v);
    }
	
	public static void reflushLocation(Context context){
		Criteria criteria = new Criteria(); 
	    criteria.setAccuracy(Criteria.ACCURACY_LOW); 
	    criteria.setAltitudeRequired(false); 
	    criteria.setBearingRequired(false); 
	    criteria.setCostAllowed(false); 
	    criteria.setPowerRequirement(Criteria.POWER_LOW); 
	    
	    final LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE); 
	    String provider=locationManager.getBestProvider(criteria, true); 
	    Location location = null;
	    if(provider != null) { 
        	location = locationManager.getLastKnownLocation(provider); 
        } 
	    /*else{
	    	Toast.makeText(context, R.string.location_service_disabled, Toast.LENGTH_SHORT).show();
	    	return;
	    }*/
	    
	    if( location != null)
	    {
	    	updateLocation(location);
	    }else{
	    	//Toast.makeText(context, R.string.locating, Toast.LENGTH_SHORT).show();
	    	if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    		provider = LocationManager.NETWORK_PROVIDER;
	    	}
	    	else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	    	{
	    		provider = LocationManager.GPS_PROVIDER;
	    		
	    	}else{
	    		Toast.makeText(context, R.string.location_service_disabled, Toast.LENGTH_SHORT).show();
		    	return;
	    	}
	    	LocationListener listener = new LocationUpdater(locationManager,context);
	    	locationManager.requestLocationUpdates(provider, 0, 1000, listener);
	    }
	    
	}
	public static void updateLocation(Location location){
    	String uid = PhoneConfiguration.getInstance().uid;
    	if("553736".equals(uid))
    	{
    		location.setLatitude(39.905219);
    		location.setLongitude(116.39342);
    	}
    	PhoneConfiguration.getInstance().location = location;
	}
	private static final  double EARTH_RADIUS = 6378.137;
	private static double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}
	public static long distanceBetween(Location l1, String lati2, String longi2)
	{
		return distanceBetween(l1,Double.parseDouble(lati2),Double.parseDouble(longi2));
	}
	public static long distanceBetween(Location l1, double lati2, double longi2)
	{
		double radLat1 = rad(l1.getLatitude());
		double radLat2 = rad(lati2);
		double a = radLat1 - radLat2;
		double b = rad(l1.getLongitude()) - rad(longi2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		
		return Math.round(s * 1000);
	}
	
	public static void appendStaticBoard(BoardHolder boards){
		if(null == boards)
			return;
		
	}
	
	private DialogFragment df = null;

	public void noticeSaying(Context context){
		
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			notice("",str.replace(";", "-----"),context);
		} else {
			notice("", str,context);
		}
	}
	
	static public String getSaying(){
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			str = str.replace(";", "-----");
		} 
		
		return str;
		
	}
	
	public void noticeError(String error,Context context){
		HttpUtil.switchServer();
		notice(context.getString(R.string.error), error,context);
	}

	private void notice(String title, String content,Context c) {

		if(c == null)
			return;
		Log.d(TAG, "saying dialog");
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("content", content);
		synchronized (lock) {
			try{
			
			DialogFragment df = new SayingDialogFragment(); 
			df.setArguments(b);
			
			FragmentActivity fa = (FragmentActivity)c;
			FragmentManager fm = fa.getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

	       	Fragment prev = fm.findFragmentByTag(dialogTag);
	        if (prev != null) {
	            ft.remove(prev);
	        }

	        ft.commit();
			df.show(fm, dialogTag);
			this.df = df;
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));

			}
			
		}

	}

	
	public void clear(){
		synchronized (lock) {
			this.df = null;
		}
	}
	public void dismiss() {

		synchronized (lock) {
			Log.d(TAG, "trying dissmiss dialog");


			if (df != null && df.getActivity() != null) {
				Log.d(TAG, "dissmiss dialog");
				
				try{
				FragmentActivity fa = (FragmentActivity)(df.getActivity());
				FragmentManager fm = fa.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();

		        Fragment prev = fm.findFragmentByTag(dialogTag);
		        if (prev != null) {
		            ft.remove(prev);
		            
		        }

		        ft.commit();
				}catch(Exception e){
					Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
				}
		       
		        df = null;
				

			} else {
				df = null;
			}

		}
	}

	public static void dismissSaying(FragmentActivity fa){
		if(null == fa)
			return;
		FragmentManager fm = fa.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(ActivityUtil.dialogTag);
        if (prev != null) {
            ft.remove(prev);
            
        }

        ft.commit();
	}
	
	public static class SayingDialogFragment extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
		    //
			Bundle b = getArguments();
			if (b != null) {
				String title = b.getString("title");
				String content = b.getString("content");
				dialog.setTitle(title);
				if(StringUtil.isEmpty(content))
					content = ActivityUtil.getSaying();
				dialog.setMessage(content);
			}
		    
			
		    dialog.setCanceledOnTouchOutside(true);
		    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    dialog.setIndeterminate(true);
		    dialog.setCancelable(true);
		    

		    // etc...
		    this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
		    return dialog;
		}
		

		
	}

}

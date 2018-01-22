package gov.anzong.meizi.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MeiziActivityUtils {

    public static final String dialogTag = "saying";
    static final String TAG = MeiziActivityUtils.class.getSimpleName();
    private static final double EARTH_RADIUS = 6378.137;
    static MeiziActivityUtils instance;
    static Object lock = new Object();
    private DialogFragment df = null;


    private MeiziActivityUtils() {
    }

    public static void showToast(Context context, int resId) {
        if (context != null) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(Context context, String res) {
        if (context != null) {
            Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
        }
    }

    public static MeiziActivityUtils getInstance() {
        if (instance == null) {
            instance = new MeiziActivityUtils();
        }
        return instance;//instance;

    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static long distanceBetween(Location l1, String lati2, String longi2) {
        return distanceBetween(l1, Double.parseDouble(lati2), Double.parseDouble(longi2));
    }

    public static long distanceBetween(Location l1, double lati2, double longi2) {
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

    static public String getSaying() {
        String str = MeiziStringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            str = str.replace(";", "-----");
        }

        return str;

    }

    public void noticeSaying(Context context) {

        String str = MeiziStringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            notice("", str.replace(";", "-----"), context);
        } else {
            notice("", str, context);
        }
    }

    public void noticeSayingWithProgressBar(Context context) {

        String str = MeiziStringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            noticebar("", str.replace(";", "-----"), context);
        } else {
            noticebar("", str, context);
        }
    }

    public void noticeSaying(String str, Context context) {

        if (str.indexOf(";") != -1) {
            notice("", str.replace(";", "-----"), context);
        } else {
            notice("", str, context);
        }
    }

    private void notice(String title, String content, Context c) {

        if (c == null)
            return;
        Log.d(TAG, "saying dialog");
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("content", content);
        synchronized (lock) {
            try {

                DialogFragment df = new SayingDialogFragment();
                df.setArguments(b);

                FragmentActivity fa = (FragmentActivity) c;
                FragmentManager fm = fa.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(dialogTag);
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.commit();
                df.show(fm, dialogTag);
                this.df = df;
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());

            }

        }

    }

    private void noticebar(String title, String content, Context c) {

        if (c == null)
            return;
        Log.d(TAG, "saying dialog");
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("content", content);
        synchronized (lock) {
            try {

                DialogFragment df = new SayingDialogFragmentWithProgressBar();
                df.setArguments(b);

                FragmentActivity fa = (FragmentActivity) c;
                FragmentManager fm = fa.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(dialogTag);
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.commit();
                df.show(fm, dialogTag);
                this.df = df;
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());

            }

        }

    }


    public void clear() {
        synchronized (lock) {
            this.df = null;
        }
    }

    public void dismiss() {

        synchronized (lock) {
            Log.d(TAG, "trying dissmiss dialog");


            if (df != null && df.getActivity() != null) {
                Log.d(TAG, "dissmiss dialog");

                try {
                    FragmentActivity fa = (FragmentActivity) (df.getActivity());
                    FragmentManager fm = fa.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    Fragment prev = fm.findFragmentByTag(dialogTag);
                    if (prev != null) {
                        ft.remove(prev);

                    }

                    ft.commit();
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), Log.getStackTraceString(e));
                }

                df = null;


            } else {
                df = null;
            }

        }
    }

    public static class SayingDialogFragmentWithProgressBar extends DialogFragment {

        ProgressDialog dialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new ProgressDialog(getActivity());
            //
            Bundle b = getArguments();
            if (b != null) {
                String title = b.getString("title");
                String content = b.getString("content");
                dialog.setTitle(title);
                if (TextUtils.isEmpty(content))
                    content = MeiziActivityUtils.getSaying();
                dialog.setMessage(content);
            }


            dialog.setCanceledOnTouchOutside(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(true);


            // etc...
            this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
            return dialog;
        }

        public void setProgress(int i) {
            if (dialog != null) {
                dialog.setProgress(i);
            }
        }

    }

    public static class SayingDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            //
            Bundle b = getArguments();
            if (b != null) {
                String title = b.getString("title");
                String content = b.getString("content");
                dialog.setTitle(title);
                if (TextUtils.isEmpty(content))
                    content = MeiziActivityUtils.getSaying();
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

package sp.phone.rxjava;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class RxUtils {

    public static void clicks(View view, View.OnClickListener listener) {
        //避免双击
        RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        listener.onClick(view);
                    }
                });
    }
}

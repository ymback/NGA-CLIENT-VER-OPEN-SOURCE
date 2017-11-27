package sp.phone.lab.rxjava;

/**
 * Created by Justwen on 2017/11/25.
 */

public class RxEvent {

    public static final int EVENT_ARTICLE_UPDATE = 0;

    public static final int EVENT_ARTICLE_TAB_UPDATE = 1;

    public int what;

    public int arg;

    public Object obj;

    public RxEvent(int what) {
        this.what = what;
    }

    public RxEvent(int what, int arg) {
        this.what = what;
        this.arg = arg;
    }

    public RxEvent(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public RxEvent(int what, int arg, Object obj) {
        this.what = what;
        this.arg = arg;
        this.obj = obj;
    }
}

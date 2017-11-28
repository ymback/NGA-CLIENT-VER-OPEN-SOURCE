package sp.phone.mvp.model;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import sp.phone.mvp.contract.BaseContract;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseModel implements BaseContract.Model {

    private LifecycleProvider<FragmentEvent> mProvider;

    @Override
    public void detach() {
        mProvider = null;
    }

    @Override
    public void setLifecycleProvider(LifecycleProvider<FragmentEvent> provider) {
        mProvider = provider;
    }

    protected LifecycleProvider<FragmentEvent> getLifecycleProvider() {
        return mProvider;
    }
}

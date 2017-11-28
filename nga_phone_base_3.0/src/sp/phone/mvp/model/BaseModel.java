package sp.phone.mvp.model;

import com.trello.rxlifecycle2.LifecycleProvider;

import sp.phone.mvp.contract.BaseContract;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseModel implements BaseContract.Model {

    private LifecycleProvider mProvider;

    @Override
    public void detach() {
        mProvider = null;
    }

    @Override
    public void setLifecycleProvider(LifecycleProvider provider) {
        mProvider = provider;
    }

    protected LifecycleProvider getLifecycleProvider() {
        return mProvider;
    }
}

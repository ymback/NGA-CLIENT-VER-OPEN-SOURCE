package gov.anzong.androidnga.mvvm.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import gov.anzong.androidnga.http.OnHttpCallBack;
import gov.anzong.androidnga.mvvm.model.MessageListModel;
import sp.phone.http.bean.MessageListInfo;

/**
 * @author yangyihang
 */
public class MessageListViewModel extends ViewModel {

    private final MutableLiveData<MessageListInfo> mMessageListData = new MutableLiveData<>();

    private final MutableLiveData<String> mErrorData = new MutableLiveData<>();

    private final MessageListModel mMessageModel = new MessageListModel();

    public void observeMessageList(LifecycleOwner owner, Observer<MessageListInfo> observer) {
        mMessageListData.observe(owner, observer);
    }

    public void observeErrorInfo(LifecycleOwner owner, Observer<String> observer) {
        mErrorData.observe(owner, observer);
    }

    public void getMessageList(int page) {
        mMessageModel.loadPage(page, new OnHttpCallBack<MessageListInfo>() {
            @Override
            public void onError(String text) {
                mErrorData.postValue(text);
            }

            @Override
            public void onSuccess(MessageListInfo data) {
                mMessageListData.postValue(data);
            }
        });

    }
}

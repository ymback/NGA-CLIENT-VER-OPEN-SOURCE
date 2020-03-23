package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.rxjava.RxUtils;

public class LoginDialogFragment extends BaseDialogFragment {

    @BindView(R.id.wv_auth_code)
    public WebView mAuthCodeWebView;

    @BindView(R.id.et_auth_code)
    public TextInputLayout mAuthCodeEditView;

    private String mDataUrl;

    private OnAuthCodeLoadCallback mAuthCodeLoadCallback;

    @BindView(R.id.btn_update)
    public TextView mUpdateAuthCodeBtn;

    public interface OnAuthCodeLoadCallback {

        void loadAuthCodeImage(WebView webView);

        void login(String authCode);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mDataUrl = bundle.getString("data_url");

        RxBus.getInstance().register(RxEvent.class).subscribe(new BaseSubscriber<RxEvent>() {
            @Override
            public void onNext(RxEvent rxEvent) {
                if (rxEvent.what == RxEvent.EVENT_LOGIN_AUTH_CODE_UPDATE && mAuthCodeWebView != null) {
                    mAuthCodeWebView.loadUrl(String.valueOf(rxEvent.obj));
                }
            }
        });
    }

    public void setAuthCodeLoadCallback(OnAuthCodeLoadCallback authCodeLoadCallback) {
        mAuthCodeLoadCallback = authCodeLoadCallback;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login, null, false);

        ButterKnife.bind(this, view);
        mAuthCodeWebView.setBackgroundColor(Color.TRANSPARENT);
        mAuthCodeWebView.loadUrl(mDataUrl);

        RxUtils.clicks(mUpdateAuthCodeBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthCodeLoadCallback.loadAuthCodeImage(mAuthCodeWebView);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("验证码")
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuthCodeLoadCallback.login(mAuthCodeEditView.getEditText().getText().toString());

                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setView(view);

        return builder.create();
    }

}


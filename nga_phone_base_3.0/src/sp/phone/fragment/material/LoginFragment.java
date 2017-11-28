package sp.phone.fragment.material;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.adapter.UserRecycleListAdapter;
import sp.phone.mvp.contract.LoginContract;

public class LoginFragment extends MaterialCompatFragment implements View.OnClickListener,LoginContract.View {

    private EditText mPasswordView;

    private EditText mUserNameView;

    private EditText mAuthCodeView;

    private ImageView mAuthCodeImg;

    private LoginContract.Presenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mPasswordView = (EditText) rootView.findViewById(R.id.login_password_edittext);
        mUserNameView = (EditText) rootView.findViewById(R.id.login_user_edittext);
        mAuthCodeView = (EditText) rootView.findViewById(R.id.login_authcode_edittext);
        mAuthCodeImg = (ImageView) rootView.findViewById(R.id.authcode_img);
        mAuthCodeImg.setOnClickListener(this);
        rootView.findViewById(R.id.login_button).setOnClickListener(this);
        RecyclerView listView = (RecyclerView) rootView.findViewById(R.id.user_list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new UserRecycleListAdapter(getContext(),this,listView));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.loadAuthCode();
        mPresenter.start();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authcode_img:
                mPresenter.loadAuthCode();
                break;
            case R.id.login_button:
                mPresenter.login(mUserNameView.getText().toString(),mPasswordView.getText().toString(),mAuthCodeView.getText().toString());
                break;
            case R.id.user_name:
                mUserNameView.setText(((TextView) v).getText());
                mUserNameView.selectAll();
                break;
        }
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setAuthCodeImg(Bitmap bitmap) {
        mAuthCodeImg.setImageBitmap(bitmap);
    }

    @Override
    public void setAuthCodeImg(int resId) {
        mAuthCodeImg.setImageResource(resId);
    }

    @Override
    public void setAuthCode(String text) {
        mAuthCodeView.setText(text);
    }

    @Override
    public void setResult(boolean isChanged) {
        if (isChanged) {
            getActivity().setResult(Activity.RESULT_OK);
        }
    }

}

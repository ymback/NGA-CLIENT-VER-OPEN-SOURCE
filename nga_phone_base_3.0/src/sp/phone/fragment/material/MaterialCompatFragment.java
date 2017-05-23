package sp.phone.fragment.material;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import gov.anzong.androidnga.R;
import sp.phone.fragment.BaseFragment;
import sp.phone.utils.PhoneConfiguration;

public abstract class MaterialCompatFragment extends BaseFragment {

    protected PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    protected AppCompatActivity mActivity;

    private FloatingActionButton mFab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_material_compat,container,false);
        FrameLayout realContainer = (FrameLayout) rootView.findViewById(R.id.container);
        setSupportActionBar(rootView);
        initSpinner(rootView);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        View view = onCreateContainerView(inflater, realContainer,savedInstanceState);
        if (view != null){
            realContainer.addView(view);
        }
        return rootView;
    }

    protected FloatingActionButton getFloatingActionButton(){
        return mFab;
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (AppCompatActivity) context;
        super.onAttach(context);
    }

    private void setSupportActionBar(View rootView){
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (mActivity.getSupportActionBar() == null && toolbar != null) {
            mActivity.setSupportActionBar(toolbar);
        }
    }

    private void initSpinner(View rootView){
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        SpinnerAdapter adapter = getSpinnerAdapter();
        if (adapter == null){
            spinner.setVisibility(View.GONE);
        } else {
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    onSpinnerItemSelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    protected void onSpinnerItemSelected(int position){

    }

    protected SpinnerAdapter getSpinnerAdapter(){
        return null;
    }

    protected View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        return null;
    }

}

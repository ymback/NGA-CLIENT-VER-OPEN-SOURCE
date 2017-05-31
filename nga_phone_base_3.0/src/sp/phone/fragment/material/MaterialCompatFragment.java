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
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.utils.PhoneConfiguration;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public abstract class MaterialCompatFragment extends BaseFragment implements PullToRefreshAttacherOnwer {

    protected PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    protected static final String TAG = "material";

    protected AppCompatActivity mActivity;

    private FloatingActionButton mFab;

    private PullToRefreshAttacher mPullToRefreshAttacher;

    private int mLayoutId = R.layout.fragment_material_compat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(mLayoutId,container,false);
        FrameLayout realContainer = (FrameLayout) rootView.findViewById(R.id.container);
        setSupportActionBar(rootView);
        initSpinner(rootView);
        initFabButton(rootView);
        View view = onCreateContainerView(inflater, realContainer,savedInstanceState);
        if (view != null){
            realContainer.addView(view);
        }
        return rootView;
    }

    protected void setLayoutId(int layoutId){
        mLayoutId = layoutId;
    }


    protected int getContainerId(){
        return R.id.container;
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
            mActivity.getSupportActionBar().setHomeButtonEnabled(true);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    onSpinnerItemSelected((Spinner) parent,position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void initFabButton(View rootView){
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        View.OnClickListener listener = getFabClickListener();
        if (listener == null){
            mFab .setVisibility(View.GONE);
        } else {
            mFab .setVisibility(View.VISIBLE);
            mFab .setOnClickListener(listener);
        }
    }


    protected View.OnClickListener getFabClickListener(){
        return null;
    }


    protected void onSpinnerItemSelected(Spinner spinner,int position){

    }

    protected SpinnerAdapter getSpinnerAdapter(){
        return null;
    }

    @Override
    public PullToRefreshAttacher getAttacher() {
        if (mPullToRefreshAttacher == null){
            PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
            options.refreshScrollDistance = 0.3f;
            options.refreshOnUp = true;
            mPullToRefreshAttacher = PullToRefreshAttacher.get(mActivity, options);
        }
        return mPullToRefreshAttacher;
    }

    public View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return null;
    }
}

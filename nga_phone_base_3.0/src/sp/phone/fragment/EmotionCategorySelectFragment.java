package sp.phone.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import gov.anzong.androidnga.R;
import sp.phone.interfaces.EmotionCategorySelectedListener;

public class EmotionCategorySelectFragment extends NoframeDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emotion_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setBackgroundResource(R.color.shit1);
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                EmotionCategorySelectedListener father = null;
                try {
                    father = (EmotionCategorySelectedListener) getActivity();
                } catch (ClassCastException e) {
                    Log.e(EmotionCategorySelectFragment.class.getSimpleName(), e.getMessage());
                }

                if (father != null) {
                    switch (v.getId()) {

                        case R.id.emotion_basic:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_BASIC);
                            break;
                        case R.id.emotion_baozou:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_BAOZOU);
                            break;
                        case R.id.emotion_xiongmao:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_XIONGMAO);
                            break;
                        case R.id.emotion_taijun:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_TAIJUN);
                            break;
                        case R.id.emotion_ali:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_ALI);
                            break;
                        case R.id.emotion_dayanmao:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_DAYANMAO);
                            break;
                        case R.id.emotion_luoxiaohei:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_LUOXIAOHEI);
                            break;
                        case R.id.emotion_majianglian:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_MAJIANGLIAN);
                            break;
                        case R.id.emotion_zhaiyin:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_ZHAIYIN);
                            break;
                        case R.id.emotion_yangcongtou:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_YANGCONGTOU);
                            break;
                        case R.id.emotion_acniang:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_ACNIANG);
                            break;
                        case R.id.emotion_new_acniang:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_NEW_ACNIANG);
                            break;
                        case R.id.emotion_bierde:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_BIERDE);
                            break;
                        case R.id.emotion_lindabi:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_LINDABI);
                            break;
                        case R.id.emotion_quniang:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_QUNIANG);
                            break;
                        case R.id.emotion_niweihezhemediao:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_NIWEIHEZHEMEDIAO);
                            break;
                        case R.id.emotion_pst:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_PST);
                            break;
                        case R.id.emotion_dt:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_DT);
                            break;
                        case R.id.emotion_pg:
                            father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_PG);
                            break;
                        default:
                            break;
                    }
                }
            }

        };
        view.findViewById(R.id.emotion_basic).setOnClickListener(listener);
        view.findViewById(R.id.emotion_baozou).setOnClickListener(listener);
        view.findViewById(R.id.emotion_xiongmao).setOnClickListener(listener);
        view.findViewById(R.id.emotion_taijun).setOnClickListener(listener);
        view.findViewById(R.id.emotion_ali).setOnClickListener(listener);
        view.findViewById(R.id.emotion_dayanmao).setOnClickListener(listener);
        view.findViewById(R.id.emotion_luoxiaohei).setOnClickListener(listener);
        view.findViewById(R.id.emotion_majianglian).setOnClickListener(listener);
        view.findViewById(R.id.emotion_zhaiyin).setOnClickListener(listener);
        view.findViewById(R.id.emotion_yangcongtou).setOnClickListener(listener);
        view.findViewById(R.id.emotion_acniang).setOnClickListener(listener);
        view.findViewById(R.id.emotion_new_acniang).setOnClickListener(listener);
        view.findViewById(R.id.emotion_bierde).setOnClickListener(listener);
        view.findViewById(R.id.emotion_lindabi).setOnClickListener(listener);
        view.findViewById(R.id.emotion_quniang).setOnClickListener(listener);
        view.findViewById(R.id.emotion_niweihezhemediao).setOnClickListener(listener);
        view.findViewById(R.id.emotion_pst).setOnClickListener(listener);
        view.findViewById(R.id.emotion_dt).setOnClickListener(listener);
        view.findViewById(R.id.emotion_pg).setOnClickListener(listener);
        super.onViewCreated(view, savedInstanceState);
    }
}
package sp.phone.interfaces;

public interface EmotionCategorySelectedListener {
    int CATEGORY_BASIC = 0;
    int CATEGORY_BAOZOU = 1;
    int CATEGORY_XIONGMAO = 2;
    int CATEGORY_TAIJUN = 3;
    int CATEGORY_ALI = 4;
    int CATEGORY_DAYANMAO = 5;
    int CATEGORY_LUOXIAOHEI = 6;
    int CATEGORY_MAJIANGLIAN = 7;
    int CATEGORY_ZHAIYIN = 8;
    int CATEGORY_YANGCONGTOU = 9;
    int CATEGORY_ACNIANG = 10;
    int CATEGORY_NEW_ACNIANG = 11;
    int CATEGORY_BIERDE = 12;
    int CATEGORY_LINDABI = 13;
    int CATEGORY_QUNIANG = 14;
    int CATEGORY_NIWEIHEZHEMEDIAO = 15;
    int CATEGORY_PST = 16;
    int CATEGORY_DT = 17;
    int CATEGORY_PG = 18;

    void onEmotionCategorySelected(int category);
}

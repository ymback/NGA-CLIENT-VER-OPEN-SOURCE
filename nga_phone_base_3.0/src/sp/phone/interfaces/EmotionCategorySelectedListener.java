package sp.phone.interfaces;

public interface EmotionCategorySelectedListener {
	public final int CATEGORY_BASIC = 0;
	public final int CATEGORY_BAOZOU = 1;
	public final int CATEGORY_XIONGMAO = 2;
	public final int CATEGORY_TAIJUN = 3;
	public final int CATEGORY_ALI = 4;
	public final int CATEGORY_DAYANMAO = 5;
	public final int CATEGORY_LUOXIAOHEI = 6;
	public final int CATEGORY_MAJIANGLIAN= 7;
	public final int CATEGORY_ZHAIYIN = 8;
	public final int CATEGORY_YANGCONGTOU = 9;
	public final int CATEGORY_ACNIANG = 10;
	public final int CATEGORY_BIERDE= 11;
	public final int CATEGORY_LINDABI= 12;
	public final int CATEGORY_QUNIANG= 13;
	public final int CATEGORY_NIWEIHEZHEMEDIAO= 14;
	
	void onEmotionCategorySelected(int category);
}

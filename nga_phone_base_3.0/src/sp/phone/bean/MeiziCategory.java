package sp.phone.bean;


public class MeiziCategory {

    public static final MeiziCategoryItem[] ITEMS = new MeiziCategory.MeiziCategoryItem[] {
            new MeiziCategoryItem("��������:����", 10),
            new MeiziCategoryItem("��������:�Ը�", 1),
            new MeiziCategoryItem("��������:�й�", 2),
            new MeiziCategoryItem("��������:����", 3),
            new MeiziCategoryItem("��������:С����", 11),
            new MeiziCategoryItem("��������:����", 12),
            new MeiziCategoryItem("��������:����", 14),
            new MeiziCategoryItem("��������:����", 6),
            new MeiziCategoryItem("��������:������", 7),
            new MeiziCategoryItem("��������:������", 8),
            new MeiziCategoryItem("��������:������", 13),
            new MeiziCategoryItem("��ɹ:����", -2),
            new MeiziCategoryItem("��ɹ:����", -3),
            new MeiziCategoryItem("ֻ����֪��������", -1),//�ú��ӿ�����������
    };

    public static int getCount(){
    	return ITEMS.length;
    }
    
    public static class MeiziCategoryItem {

        public MeiziCategoryItem(String name, int id) {
            mName = name;
            mID = id;
        }

        public String getName() {
            return mName;
        }

        public int getID() {
            return mID;
        }

        private String mName;

        private int mID;
    }
}

package gov.anzong.meizi;


public class MeiziCategory {

    public static final MeiziCategoryItem[] ITEMS = new MeiziCategory.MeiziCategoryItem[]{
            new MeiziCategoryItem("ROSI写真", 2),
            new MeiziCategoryItem("萌爱自拍", 1),
    };

    public static int getCount() {
        return ITEMS.length;
    }

    public static class MeiziCategoryItem {

        private String mName;
        private int mID;

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
    }
}

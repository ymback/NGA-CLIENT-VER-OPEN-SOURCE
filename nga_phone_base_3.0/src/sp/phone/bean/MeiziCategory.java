package sp.phone.bean;


public class MeiziCategory {

    public static final MeiziCategoryItem[] ITEMS = new MeiziCategory.MeiziCategoryItem[] {
            new MeiziCategoryItem("豆瓣妹子:所有", 10),
            new MeiziCategoryItem("豆瓣妹子:性感", 1),
            new MeiziCategoryItem("豆瓣妹子:有沟", 2),
            new MeiziCategoryItem("豆瓣妹子:美腿", 3),
            new MeiziCategoryItem("豆瓣妹子:小清新", 11),
            new MeiziCategoryItem("豆瓣妹子:文艺", 12),
            new MeiziCategoryItem("豆瓣妹子:美臀", 14),
            new MeiziCategoryItem("豆瓣男淫:所有", 6),
            new MeiziCategoryItem("豆瓣男淫:肌肉男", 7),
            new MeiziCategoryItem("豆瓣男淫:清新男", 8),
            new MeiziCategoryItem("豆瓣男淫:文艺男", 13),
            new MeiziCategoryItem("大胆晒:最新", -2),
            new MeiziCategoryItem("大胆晒:最热", -3),
            new MeiziCategoryItem("只有神知道的世界", -1),//好孩子看不到又有了
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

package sp.phone.interfaces;

import sp.phone.bean.BoardCategory;

public interface PageCategoryOwner {

    int getCategoryCount();

    String getCategoryName(int position);

    BoardCategory getCategory(int category);

}

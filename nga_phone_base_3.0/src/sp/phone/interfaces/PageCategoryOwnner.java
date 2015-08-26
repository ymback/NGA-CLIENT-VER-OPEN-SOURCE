package sp.phone.interfaces;

import sp.phone.bean.BoardCategory;

public interface PageCategoryOwnner {
    int getCategoryCount();

    String getCategoryName(int position);

    BoardCategory getCategory(int category);

}

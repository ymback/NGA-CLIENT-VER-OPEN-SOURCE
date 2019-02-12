package sp.phone.common;

import android.content.Context;

import java.util.List;

public interface FilterKeywordsManager {
  void initialize(Context context);
  void toggleKeyword(int position);
  void addKeyword(FilterKeyword keyword);
  List<FilterKeyword> getKeywords();
  void removeKeyword(int index);
}

package sp.phone.common;

import android.content.SharedPreferences;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;

public class VersionUpgradeHelper {

    public static void upgrade() {
        upgradeBookmarkBoards();
    }

    private static void upgradeBookmarkBoards() {
        SharedPreferences sp = ContextUtils.getSharedPreferences(PreferenceKey.PERFERENCE);
        if (sp.contains(PreferenceKey.BOOKMARK_BOARD)) {
            String data = sp.getString(PreferenceKey.BOOKMARK_BOARD, "");
            sp.edit().remove(PreferenceKey.BOOKMARK_BOARD).apply();
            PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, data);
        }
    }

}

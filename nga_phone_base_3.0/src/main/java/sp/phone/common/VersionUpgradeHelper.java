package sp.phone.common;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.List;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import sp.phone.mvp.model.entity.Board;

public class VersionUpgradeHelper {

    public static void upgrade() {
        try {
            upgradeBookmarkBoards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void upgradeBookmarkBoards() {
        SharedPreferences sp = ContextUtils.getSharedPreferences(PreferenceKey.PERFERENCE);
        if (sp.contains(PreferenceKey.BOOKMARK_BOARD)) {
            String data = sp.getString(PreferenceKey.BOOKMARK_BOARD, "");
            sp.edit().remove(PreferenceKey.BOOKMARK_BOARD).apply();
            List<Board> bookmarkBoards = JSON.parseArray(data, Board.class);
            for (Board board : bookmarkBoards) {
                board.fixBoardKey();
            }
            PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, JSON.toJSONString(bookmarkBoards));
        }
    }

}

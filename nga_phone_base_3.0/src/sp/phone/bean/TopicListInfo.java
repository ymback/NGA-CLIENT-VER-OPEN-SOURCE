package sp.phone.bean;

import java.util.List;

public class TopicListInfo {
    int __ROWS;
    int __T__ROWS;
    int __SELECTED_FORUM;
    //	int __TABLE;
    boolean __SEARCHNORESULT = false;
    List<ThreadPageInfo> articleEntryList;

    public int get__ROWS() {
        return __ROWS;
    }

    public void set__ROWS(int __ROWS) {
        this.__ROWS = __ROWS;
    }

    public boolean get__SEARCHNORESULT() {
        return __SEARCHNORESULT;
    }
//	public void set__TABLE(int __TABLE) {
//		this.__TABLE = __TABLE;
//	}

    public void set__SEARCHNORESULT(boolean __SEARCHNORESULT) {//是不是没有结果
        this.__SEARCHNORESULT = __SEARCHNORESULT;
    }

    public int get__T__ROWS() {
        return __T__ROWS;
    }
//	public int get__TABLE() {
//		return __TABLE;
//	}

    public void set__T__ROWS(int __T__ROWS) {
        this.__T__ROWS = __T__ROWS;
    }

    public int get__SELECTED_FORUM() {
        return __SELECTED_FORUM;
    }

    public void set__SELECTED_FORUM(int __SELECTED_FORUM) {
        this.__SELECTED_FORUM = __SELECTED_FORUM;
    }

    public List<ThreadPageInfo> getArticleEntryList() {
        return articleEntryList;
    }

    public void setArticleEntryList(List<ThreadPageInfo> articleEntryList) {
        this.articleEntryList = articleEntryList;
    }


}

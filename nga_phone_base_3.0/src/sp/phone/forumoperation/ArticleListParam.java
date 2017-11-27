package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justwen on 2017/7/9.
 */

public class ArticleListParam implements Parcelable, Cloneable {

    public int pid;

    public int tid;

    public int authorId;

    public int page;

    public int searchPost;

    public String title;

    public ArticleListParam() {

    }

    protected ArticleListParam(Parcel in) {
        pid = in.readInt();
        tid = in.readInt();
        authorId = in.readInt();
        page = in.readInt();
        searchPost = in.readInt();
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pid);
        dest.writeInt(tid);
        dest.writeInt(authorId);
        dest.writeInt(page);
        dest.writeInt(searchPost);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArticleListParam> CREATOR = new Creator<ArticleListParam>() {
        @Override
        public ArticleListParam createFromParcel(Parcel in) {
            return new ArticleListParam(in);
        }

        @Override
        public ArticleListParam[] newArray(int size) {
            return new ArticleListParam[size];
        }
    };

    public int getPid() {
        return pid;
    }

    public int getTid() {
        return tid;
    }

    public int getAuthorId() {
        return authorId;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

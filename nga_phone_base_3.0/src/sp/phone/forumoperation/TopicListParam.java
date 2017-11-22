package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class TopicListParam implements Cloneable,Parcelable {

    public int authorId;

    public int searchPost;

    public int favor;

    public int content;

    public int fid;

    public int category;

    public String key;

    public String fidGroup;

    public String author;

    public String boardName;

    protected TopicListParam(Parcel in) {
        authorId = in.readInt();
        searchPost = in.readInt();
        favor = in.readInt();
        content = in.readInt();
        fid = in.readInt();
        category = in.readInt();
        key = in.readString();
        fidGroup = in.readString();
        author = in.readString();
        boardName = in.readString();
    }

    public TopicListParam() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(authorId);
        dest.writeInt(searchPost);
        dest.writeInt(favor);
        dest.writeInt(content);
        dest.writeInt(fid);
        dest.writeInt(category);
        dest.writeString(key);
        dest.writeString(fidGroup);
        dest.writeString(author);
        dest.writeString(boardName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TopicListParam> CREATOR = new Creator<TopicListParam>() {
        @Override
        public TopicListParam createFromParcel(Parcel in) {
            return new TopicListParam(in);
        }

        @Override
        public TopicListParam[] newArray(int size) {
            return new TopicListParam[size];
        }
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

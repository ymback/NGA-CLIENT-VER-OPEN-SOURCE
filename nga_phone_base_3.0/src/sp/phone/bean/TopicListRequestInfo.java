package sp.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class TopicListRequestInfo implements Cloneable,Parcelable {

    public int authorId;

    public int searchPost;

    public int favor;

    public int content;

    public int fid;

    public int category;

    public String key;

    public String fidGroup;

    public String author;

    public boolean searchMode;

    public String boardName;

    protected TopicListRequestInfo(Parcel in) {
        authorId = in.readInt();
        searchPost = in.readInt();
        favor = in.readInt();
        content = in.readInt();
        fid = in.readInt();
        category = in.readInt();
        key = in.readString();
        fidGroup = in.readString();
        author = in.readString();
        searchMode = in.readByte() != 0;
        boardName = in.readString();
    }

    public  TopicListRequestInfo() {
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
        dest.writeByte((byte) (searchMode ? 1 : 0));
        dest.writeString(boardName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TopicListRequestInfo> CREATOR = new Creator<TopicListRequestInfo>() {
        @Override
        public TopicListRequestInfo createFromParcel(Parcel in) {
            return new TopicListRequestInfo(in);
        }

        @Override
        public TopicListRequestInfo[] newArray(int size) {
            return new TopicListRequestInfo[size];
        }
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

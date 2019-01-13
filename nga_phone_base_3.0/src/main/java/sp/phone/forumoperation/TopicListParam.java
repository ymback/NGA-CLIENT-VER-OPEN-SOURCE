package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justwen on 2017/6/3.
 */

public class TopicListParam implements Cloneable, Parcelable {

    public int authorId;

    public int searchPost;

    public int favor;

    public int content;

    public int fid;

    public String key;

    public String fidGroup;

    public String author;

    public int recommend;

    public int twentyfour;

    public String title;

    public int stid;

    public TopicListParam() {
    }

    protected TopicListParam(Parcel in) {
        authorId = in.readInt();
        searchPost = in.readInt();
        favor = in.readInt();
        content = in.readInt();
        fid = in.readInt();
        key = in.readString();
        fidGroup = in.readString();
        author = in.readString();
        recommend = in.readInt();
        twentyfour = in.readInt();
        title = in.readString();
        stid = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(authorId);
        dest.writeInt(searchPost);
        dest.writeInt(favor);
        dest.writeInt(content);
        dest.writeInt(fid);
        dest.writeString(key);
        dest.writeString(fidGroup);
        dest.writeString(author);
        dest.writeInt(recommend);
        dest.writeInt(twentyfour);
        dest.writeString(title);
        dest.writeInt(stid);
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
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }
}

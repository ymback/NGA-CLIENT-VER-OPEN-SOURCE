package sp.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

@Deprecated
public class OldBoard implements Parcelable {

    private int category;

    private String url;

    private String name;

    private int icon;

    private int iconOld;

    public OldBoard() {

    }

    public OldBoard(String url, String name, int icon, int iconOld) {
        this.name = name;
        this.url = url;
        this.icon = icon;
        this.iconOld = iconOld;
    }

    public OldBoard(String url, String name, int icon) {
        this(url, name, icon, 0);
    }

    public OldBoard(String url, String name) {
        this(url, name, 0, 0);
    }


    protected OldBoard(Parcel in) {
        category = in.readInt();
        url = in.readString();
        name = in.readString();
        icon = in.readInt();
        iconOld = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeInt(icon);
        dest.writeInt(iconOld);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIconOld() {
        return iconOld;
    }

    public void setIconOld(int iconOld) {
        this.iconOld = iconOld;
    }

}

package sp.phone.common;

public interface ApiConstants {

    String NGA_DOMAIN  = "bbs.ngacn.cc";

    String NGA_HOST = "http://" + NGA_DOMAIN + '/';

    String NGA_NOTIFICATION = NGA_HOST + "nuke.php?__lib=noti&lite=js&__act=get_all";

    String NGA_NOTIFICATION_DELETE_ALL = NGA_HOST + "nuke.php?__lib=noti&raw=3&__act=del";

}

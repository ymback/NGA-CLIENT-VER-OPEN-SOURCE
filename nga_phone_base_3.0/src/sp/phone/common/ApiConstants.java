package sp.phone.common;

public interface ApiConstants {

    String NGA_DOMAIN  = "bbs.ngacn.cc";

    String NGA_HOST = "http://" + NGA_DOMAIN + '/';

    String NGA_NOTIFICATION = NGA_HOST + "nuke.php?__lib=noti&lite=js&__act=get_all";

    String NGA_NOTIFICATION_DELETE_ALL = NGA_HOST + "nuke.php?__lib=noti&raw=3&__act=del";

    int NGA_NOTIFICATION_TYPE_TOPIC_REPLY = 1;

    int NGA_NOTIFICATION_TYPE_REPLY_REPLY = 2;

    int NGA_NOTIFICATION_TYPE_TOPIC_COMMENT = 3;

    int NGA_NOTIFICATION_TYPE_REPLY_COMMENT = 4;

    int NGA_NOTIFICATION_TYPE_TOPIC_AT = 7;

    int NGA_NOTIFICATION_TYPE_REPLY_AT = 8;

    int NGA_NOTIFICATION_TYPE_NEW_MESSAGE = 10;

    int NGA_NOTIFICATION_TYPE_MESSAGE_REPLY = 11;

}

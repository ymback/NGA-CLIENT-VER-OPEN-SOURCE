package sp.phone.common;

public interface ApiConstants {

    String URL_BOARD_ICON = "http://img4.nga.178.com/ngabbs/nga_classic/f/app/%s.png";

    int NGA_NOTIFICATION_TYPE_TOPIC_REPLY = 1;

    int NGA_NOTIFICATION_TYPE_REPLY_REPLY = 2;

    int NGA_NOTIFICATION_TYPE_TOPIC_COMMENT = 3;

    int NGA_NOTIFICATION_TYPE_REPLY_COMMENT = 4;

    int NGA_NOTIFICATION_TYPE_TOPIC_AT = 7;

    int NGA_NOTIFICATION_TYPE_REPLY_AT = 8;

    int NGA_NOTIFICATION_TYPE_NEW_MESSAGE = 10;

    int NGA_NOTIFICATION_TYPE_MESSAGE_REPLY = 11;

    int MASK_FONT_RED = 1;

    int MASK_FONT_BLUE = 2;

    int MASK_FONT_GREEN = 4;

    int MASK_FONT_ORANGE = 8;

    int MASK_FONT_SILVER = 16;

    int MASK_FONT_BOLD = 32;

    int MASK_FONT_ITALIC = 64;

    int MASK_FONT_UNDERLINE = 128;

    // 主题被锁定 2^10
    int MASK_TYPE_LOCK = 1024;

    // 主题中有附件 2^13
    int MASK_TYPE_ATTACHMENT = 8192;

    // 合集 2^15
    int MASK_TYPE_ASSEMBLE = 32768;


}

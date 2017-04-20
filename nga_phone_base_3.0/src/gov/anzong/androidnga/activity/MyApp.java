package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.CrashHandler;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.NetUtil;
import sp.phone.bean.Board;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class MyApp extends Application implements PerferenceConstant {
    public final static int version = BuildConfig.VERSION_CODE;
    public static final int fddicon[][] = {};
    static final String RECENT = "最近访问";
    static final String ADDFID = "用户自定义";
    final private static String TAG = MyApp.class.getSimpleName();
    boolean newVersion = false;
    private PhoneConfiguration config = null;

    @Override
    public void onCreate() {
        Log.w(TAG, "app nga androind start");
        if (config == null)
            config = PhoneConfiguration.getInstance();
        loadConfig();
        initUserInfo();
        if (ActivityUtil.isGreaterThan_2_1())
            initPath();
        if (config.iconmode) {// laotubiao
            loadDefaultBoardOld();
        } else {
            loadDefaultBoard();
        }

        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());

        NetUtil.init(this);

        super.onCreate();
    }

    public BoardHolder loadDefaultBoardOld() {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        BoardHolder boards = new BoardHolder();

        int i = 0;

        SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
        String recentStr = share.getString(RECENT_BOARD, "");
        List<Board> recentList = null;
        if (!StringUtil.isEmpty(recentStr)) {
            recentList = JSON.parseArray(recentStr, Board.class);
            if (recentList != null) {
                for (int j = 0; j < recentList.size(); j++) {
                    if (recentList.get(j).getIcon() == R.drawable.pdefault) {
                        boards.add(new Board(i, recentList.get(j).getUrl(),
                                recentList.get(j).getName(), R.drawable.oldpdefault));
                    } else {
                        boards.add(recentList.get(j));
                    }
                }
            }
        }
        if (recentList != null) {
            boards.addCategoryName(i, RECENT);
            i++;
        }

        boards.add(new Board(i, "7", "议事厅", R.drawable.oldp7));
        boards.add(new Board(i, "323", "国服以外讨论", R.drawable.oldp323));
        boards.add(new Board(i, "10", "银色黎明", R.drawable.oldp10));
        boards.add(new Board(i, "230", "艾泽拉斯风纪委员会", R.drawable.oldp230));
        boards.add(new Board(i, "387", "潘大力亚之迷雾", R.drawable.oldp387));
        boards.add(new Board(i, "430", "德拉诺之王", R.drawable.oldp430));
        boards.add(new Board(i, "305", "305权贵区", R.drawable.oldpdefault));
        boards.add(new Board(i, "11", "诺森德埋骨地", R.drawable.oldpdefault));
        boards.addCategoryName(i, "综合讨论");
        i++;

        boards.add(new Board(i, "-7", "大漩涡", R.drawable.oldp354));
        boards.add(new Board(i, "-343809", "汽车俱乐部", R.drawable.oldpdefault));
        boards.add(new Board(i, "-81981", "生命之杯", R.drawable.oldpdefault));
        boards.add(new Board(i, "-576177", "影音讨论区", R.drawable.oldpdefault));
        if (currentTimeSeconds > 1464710400 && currentTimeSeconds < 1472659200) {
            boards.add(new Board(i, "497", "魔兽世界电影", R.drawable.oldpdefault));
        }//WOW MOVIE 彩蛋
        boards.add(new Board(i, "-43", "军事历史", R.drawable.oldpdefault));
        boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.oldp414));
        boards.add(new Board(i, "415", "主机游戏综合讨论", R.drawable.oldpdefault));
        boards.add(new Board(i, "427", "怪物猎人", R.drawable.oldp427));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.oldp431));
        boards.add(new Board(i, "436", "消费电子 IT新闻", R.drawable.oldpdefault));
        boards.add(new Board(i, "498", "二手交易", R.drawable.oldpdefault));
        boards.add(new Board(i, "340", "无聊图", R.drawable.oldpdefault));
        boards.add(new Board(i, "456", "冲水区", R.drawable.oldpdefault));
        boards.add(new Board(i, "-187579", "大漩涡历史博物馆", R.drawable.oldpdefault));
        boards.add(new Board(i, "485", "篮球", R.drawable.oldpdefault));
        boards.add(new Board(i, "491", "议会", R.drawable.oldpdefault));
        boards.addCategoryName(i, "大漩涡系列");
        i++;

        boards.add(new Board(i, "390", "五晨寺", R.drawable.oldp390));
        boards.add(new Board(i, "320", "黑锋要塞", R.drawable.oldp320));
        boards.add(new Board(i, "181", "铁血沙场", R.drawable.oldp181));
        boards.add(new Board(i, "182", "魔法圣堂", R.drawable.oldp182));
        boards.add(new Board(i, "183", "信仰神殿", R.drawable.oldp183));
        boards.add(new Board(i, "185", "风暴祭坛", R.drawable.oldp185));
        boards.add(new Board(i, "186", "翡翠梦境", R.drawable.oldp186));
        boards.add(new Board(i, "187", "猎手大厅", R.drawable.oldp187));
        boards.add(new Board(i, "184", "圣光之力", R.drawable.oldp184));
        boards.add(new Board(i, "188", "恶魔深渊", R.drawable.oldp188));
        boards.add(new Board(i, "189", "暗影裂口", R.drawable.oldp189));
        boards.add(new Board(i, "477", "伊利达雷", R.drawable.oldp477));
        boards.addCategoryName(i, "职业讨论区");
        i++;

        boards.add(new Board(i, "310", "精英议会", R.drawable.oldp310));
        boards.add(new Board(i, "190", "任务讨论", R.drawable.oldp190));
        boards.add(new Board(i, "213", "战争档案", R.drawable.oldp213));
        boards.add(new Board(i, "218", "副本专区", R.drawable.oldp218));
        boards.add(new Board(i, "258", "战场讨论", R.drawable.oldp258));
        boards.add(new Board(i, "272", "竞技场", R.drawable.oldp272));
        boards.add(new Board(i, "191", "地精商会", R.drawable.oldp191));
        boards.add(new Board(i, "200", "插件研究", R.drawable.oldp200));
        boards.add(new Board(i, "240", "BigFoot", R.drawable.oldp240));
        boards.add(new Board(i, "460", "大脚综合", R.drawable.oldpdefault));
        boards.add(new Board(i, "461", "大脚水区", R.drawable.oldpdefault));
        boards.add(new Board(i, "458", "大脚LOL", R.drawable.oldpdefault));
        boards.add(new Board(i, "274", "插件发布", R.drawable.oldp274));
        boards.add(new Board(i, "315", "战斗统计", R.drawable.oldp315));
        boards.add(new Board(i, "333", "DKP系统", R.drawable.oldp333));
        boards.add(new Board(i, "327", "成就讨论", R.drawable.oldp327));
        boards.add(new Board(i, "388", "幻化讨论", R.drawable.oldp388));
        boards.add(new Board(i, "411", "宠物讨论", R.drawable.oldp411));
        boards.add(new Board(i, "463", "要塞讨论", R.drawable.oldpdefault));
        boards.add(new Board(i, "255", "公会管理", R.drawable.oldp10));
        boards.add(new Board(i, "306", "人员招募", R.drawable.oldp10));
        boards.addCategoryName(i, "冒险心得");
        i++;

        boards.add(new Board(i, "264", "卡拉赞剧院", R.drawable.oldp264));
        boards.add(new Board(i, "8", "大图书馆", R.drawable.oldp8));
        boards.add(new Board(i, "102", "作家协会", R.drawable.oldp102));
        boards.add(new Board(i, "124", "壁画洞窟", R.drawable.oldpdefault));
        boards.add(new Board(i, "254", "镶金玫瑰", R.drawable.oldp254));
        boards.add(new Board(i, "355", "龟岩兄弟会", R.drawable.oldp355));
        boards.add(new Board(i, "116", "奇迹之泉", R.drawable.oldp116));
        boards.addCategoryName(i, "麦迪文之塔");
        i++;

        boards.add(new Board(i, "193", "帐号安全", R.drawable.oldp193));
        boards.add(new Board(i, "334", "PC软硬件", R.drawable.oldp334));
        boards.add(new Board(i, "201", "系统问题", R.drawable.oldp201));
        boards.add(new Board(i, "335", "网站开发", R.drawable.oldp335));
        boards.addCategoryName(i, "系统软硬件讨论");
        i++;

        boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.oldp414));
        boards.add(new Board(i, "428", "手机游戏", R.drawable.oldp428));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.oldp431));
        boards.add(new Board(i, "-452227", "口袋妖怪", R.drawable.oldp452227));
        boards.add(new Board(i, "426", "智龙迷城", R.drawable.oldp426));
        boards.add(new Board(i, "-51095", "部落冲突", R.drawable.oldp51095));
        boards.add(new Board(i, "492", "部落冲突:皇室战争", R.drawable.oldp492));
        boards.add(new Board(i, "-362960", "最终幻想14", R.drawable.oldp362960));
        boards.add(new Board(i, "-6194253", "战争雷霆", R.drawable.oldp6194253));
        boards.add(new Board(i, "427", "怪物猎人", R.drawable.oldp427));
        boards.add(new Board(i, "-47218", "地下城与勇士", R.drawable.oldp4218));
        boards.add(new Board(i, "425", "行星边际2", R.drawable.oldp425));
        boards.add(new Board(i, "422", "炉石传说", R.drawable.oldp422));
        boards.add(new Board(i, "-65653", "剑灵", R.drawable.oldp65653));
        boards.add(new Board(i, "412", "巫师之怒", R.drawable.oldp412));
        boards.add(new Board(i, "-235147", "激战2", R.drawable.oldp235147));
        boards.add(new Board(i, "442", "逆战", R.drawable.oldp442));
        boards.add(new Board(i, "-46468", "洛拉斯的战争世界", R.drawable.oldp46468));
        boards.add(new Board(i, "483", "洛拉斯的战争世界:插件", R.drawable.oldp46468));
        boards.add(new Board(i, "432", "战机世界", R.drawable.oldp432));
        boards.add(new Board(i, "441", "战舰世界", R.drawable.oldpdefault));
        boards.add(new Board(i, "321", "DotA", R.drawable.oldp321));
        boards.add(new Board(i, "375", "DotA联赛饰品", R.drawable.oldpdefault));
        boards.add(new Board(i, "-2371813", "EVE", R.drawable.oldp2371813));
        boards.add(new Board(i, "-7861121", "剑叁 ", R.drawable.oldp7861121));
        boards.add(new Board(i, "448", "剑叁同人 ", R.drawable.oldpdefault));
        boards.add(new Board(i, "-793427", "斗战神", R.drawable.oldpdefault));
        boards.add(new Board(i, "332", "战锤40K", R.drawable.oldp332));
        boards.add(new Board(i, "416", "火炬之光2", R.drawable.oldpdefault));
        boards.add(new Board(i, "406", "星际争霸2", R.drawable.oldpdefault));
        boards.add(new Board(i, "420", "MT Online", R.drawable.oldp420));
        boards.add(new Board(i, "424", "圣斗士星矢", R.drawable.oldpdefault));
        boards.add(new Board(i, "-1513130", "鲜血兄弟会", R.drawable.oldpdefault));
        boards.add(new Board(i, "433", "神雕侠侣", R.drawable.oldpdefault));
        boards.add(new Board(i, "434", "神鬼幻想", R.drawable.oldpdefault));
        boards.add(new Board(i, "435", "上古卷轴Online", R.drawable.oldp435));
        boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.oldpdefault));
        boards.add(new Board(i, "444", "刀塔传奇", R.drawable.oldp444));
        boards.add(new Board(i, "445", "迷你西游", R.drawable.oldp445));
        boards.add(new Board(i, "447", "锁链战记", R.drawable.oldpdefault));
        boards.add(new Board(i, "-532408", "沃土", R.drawable.oldp532408));
        boards.add(new Board(i, "353", "纽沃斯英雄传", R.drawable.oldpdefault));
        boards.add(new Board(i, "452", "天涯明月刀", R.drawable.oldp452));
        boards.add(new Board(i, "453", "魔力宝贝", R.drawable.oldpdefault));
        boards.add(new Board(i, "454", "神之浩劫", R.drawable.oldpdefault));
        boards.add(new Board(i, "455", "鬼武者 魂", R.drawable.oldpdefault));
        boards.add(new Board(i, "480", "百万亚瑟王", R.drawable.oldpdefault));
        boards.add(new Board(i, "481", "Minecraft", R.drawable.oldpdefault));
        boards.add(new Board(i, "482", "CS", R.drawable.oldpdefault));
        boards.add(new Board(i, "484", "热血江湖传", R.drawable.oldpdefault));
        boards.add(new Board(i, "486", "辐射", R.drawable.oldpdefault));
        boards.add(new Board(i, "487", "刀剑魔药2", R.drawable.oldpdefault));
        boards.add(new Board(i, "488", "村长打天下", R.drawable.oldpdefault));
        boards.add(new Board(i, "493", "刀塔战纪", R.drawable.oldpdefault));
        boards.add(new Board(i, "494", "魔龙之魂", R.drawable.oldpdefault));
        boards.add(new Board(i, "495", "光荣三国志系列", R.drawable.oldpdefault));
        boards.add(new Board(i, "496", "九十九姬", R.drawable.oldpdefault));
        boards.addCategoryName(i, "其他游戏");
        i++;

        boards.add(new Board(i, "318", "暗黑破坏神3", R.drawable.oldp318));
        boards.add(new Board(i, "403", "购买/安装/共享", R.drawable.oldp403));
        boards.add(new Board(i, "393", "背景故事与文艺作品", R.drawable.oldp393));
        boards.add(new Board(i, "400", "职业讨论区", R.drawable.oldp29));
        boards.add(new Board(i, "395", "野蛮人", R.drawable.oldp395));
        boards.add(new Board(i, "396", "猎魔人", R.drawable.oldp396));
        boards.add(new Board(i, "397", "武僧", R.drawable.oldp397));
        boards.add(new Board(i, "398", "巫医", R.drawable.oldp398));
        boards.add(new Board(i, "399", "魔法师", R.drawable.oldp399));
        boards.add(new Board(i, "446", "圣教军", R.drawable.oldpdefault));
        boards.addCategoryName(i, "暗黑破坏神");
        i++;

        boards.add(new Board(i, "422", "炉石传说", R.drawable.oldp422));
        boards.add(new Board(i, "429", "战术讨论", R.drawable.oldpdefault));
        boards.add(new Board(i, "450", "文章存档", R.drawable.oldpdefault));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.oldp431));
        boards.add(new Board(i, "457", "视频资料", R.drawable.oldpdefault));
        boards.add(new Board(i, "459", "守望先锋", R.drawable.oldp459));
        boards.add(new Board(i, "490", "魔兽争霸", R.drawable.oldpdefault));
        boards.addCategoryName(i, "暴雪游戏");
        i++;

        boards.add(new Board(i, "-152678", "英雄联盟", R.drawable.oldp152678));
        boards.add(new Board(i, "418", "游戏视频", R.drawable.oldpdefault));
        boards.add(new Board(i, "479", "赛事讨论", R.drawable.oldpdefault));
        boards.addCategoryName(i, "英雄联盟");
        i++;

        boards.add(new Board(i, "-447601", "二次元国家地理", R.drawable.oldp447601));
        boards.add(new Board(i, "-84", "模玩之魂", R.drawable.oldp84));
        boards.add(new Board(i, "-8725919", "小窗视界", R.drawable.oldp8725919));
        boards.add(new Board(i, "-965240", "树洞", R.drawable.oldpdefault));
        boards.add(new Board(i, "-131429", "红茶馆——小说馆", R.drawable.oldpdefault));
        boards.add(new Board(i, "-608808", "血腥厨房", R.drawable.oldpdefault));
        boards.add(new Board(i, "-469608", "影~视~秀", R.drawable.oldpdefault));
        boards.add(new Board(i, "-55912", "音乐讨论", R.drawable.oldpdefault));
        boards.add(new Board(i, "-522474", "综合体育讨论区", R.drawable.oldpdefault));
        boards.add(new Board(i, "-1068355", "晴风村", R.drawable.oldpdefault));
        boards.add(new Board(i, "-168888", "育儿版", R.drawable.oldpdefault));
        boards.add(new Board(i, "-54214", "时尚板", R.drawable.oldpdefault));
        boards.add(new Board(i, "-353371", "宠物养成", R.drawable.oldpdefault));
        boards.add(new Board(i, "-538800", "乙女向二次元", R.drawable.oldpdefault));
        boards.add(new Board(i, "-7678526", "麻将科学院", R.drawable.oldpdefault));
        boards.add(new Board(i, "-202020", "程序员职业交流", R.drawable.oldpdefault));
        boards.add(new Board(i, "-444012", "我们的骑迹", R.drawable.oldpdefault));
        boards.add(new Board(i, "-349066", "开心茶园", R.drawable.oldpdefault));
        boards.add(new Board(i, "-314508", "世界尽头的百货公司", R.drawable.oldpdefault));
        boards.add(new Board(i, "-2671", "耳机区", R.drawable.oldpdefault));
        boards.add(new Board(i, "-970841", "东方教主陈乔恩", R.drawable.oldpdefault));
        boards.add(new Board(i, "-3355501", "基腐版", R.drawable.oldpdefault));
        boards.add(new Board(i, "-403298", "怨灵图纸收藏室", R.drawable.oldpdefault));
        boards.add(new Board(i, "-3432136", "飘落的诗章", R.drawable.oldpdefault));
        boards.add(new Board(i, "-187628", "家居 装修", R.drawable.oldpdefault));
        boards.add(new Board(i, "-8627585", "牛头人酋长乐队", R.drawable.oldpdefault));
        boards.add(new Board(i, "-17100", "全民健身中心", R.drawable.oldpdefault));
        boards.addCategoryName(i, "个人版面");

        String addFidStr = share.getString(ADD_FID, "");
        List<Board> addFidList = null;
        if (!StringUtil.isEmpty(addFidStr)) {
            addFidList = JSON.parseArray(addFidStr, Board.class);
            if (addFidList != null) {
                i++;
                for (int j = 0; j < addFidList.size(); j++) {
                    boards.add(new Board(i, addFidList.get(j).getUrl(),
                            addFidList.get(j).getName(), addFidList.get(j)
                            .getIcon()));
                }
            }
        }
        if (addFidList != null) {
            boards.addCategoryName(i, ADDFID);
            // i++;
        }

        return boards;
    }

    public BoardHolder loadDefaultBoard() {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        BoardHolder boards = new BoardHolder();

        int i = 0;

        SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
        String recentStr = share.getString(RECENT_BOARD, "");
        List<Board> recentList = null;
        if (!StringUtil.isEmpty(recentStr)) {
            recentList = JSON.parseArray(recentStr, Board.class);
            if (recentList != null) {
                for (int j = 0; j < recentList.size(); j++) {
                    if (recentList.get(j).getIcon() == R.drawable.oldpdefault) {
                        boards.add(new Board(i, recentList.get(j).getUrl(),
                                recentList.get(j).getName(), R.drawable.pdefault));
                    } else {
                        boards.add(recentList.get(j));
                    }
                }
            }
        }
        if (recentList != null) {
            boards.addCategoryName(i, RECENT);
            i++;
        }

        boards.add(new Board(i, "7", "议事厅", R.drawable.p7));
        boards.add(new Board(i, "323", "国服以外讨论", R.drawable.p323));
        boards.add(new Board(i, "10", "银色黎明", R.drawable.p10));
        boards.add(new Board(i, "230", "艾泽拉斯风纪委员会", R.drawable.p230));
        boards.add(new Board(i, "387", "潘大力亚之迷雾", R.drawable.p387));
        boards.add(new Board(i, "430", "德拉诺之王", R.drawable.p430));
        boards.add(new Board(i, "305", "305权贵区", R.drawable.p305));
        boards.add(new Board(i, "11", "诺森德埋骨地", R.drawable.p11));
        boards.addCategoryName(i, "综合讨论");
        i++;

        boards.add(new Board(i, "-7", "大漩涡", R.drawable.p354));
        boards.add(new Board(i, "-343809", "汽车俱乐部", R.drawable.p343809));
        boards.add(new Board(i, "-81981", "生命之杯", R.drawable.p81981));
        boards.add(new Board(i, "-576177", "影音讨论区", R.drawable.p576177));
        if (currentTimeSeconds > 1464710400 && currentTimeSeconds < 1472659200) {
            boards.add(new Board(i, "497", "魔兽世界电影", R.drawable.p497));
        }//WOW MOVIE 彩蛋
        boards.add(new Board(i, "-43", "军事历史", R.drawable.p43));
        boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.p414));
        boards.add(new Board(i, "415", "主机游戏综合讨论", R.drawable.p415));
        boards.add(new Board(i, "427", "怪物猎人", R.drawable.p427));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
        boards.add(new Board(i, "436", "消费电子 IT新闻", R.drawable.p436));
        boards.add(new Board(i, "498", "二手交易", R.drawable.p498));
        boards.add(new Board(i, "340", "无聊图", R.drawable.p340));
        boards.add(new Board(i, "456", "冲水区", R.drawable.p456));
        boards.add(new Board(i, "-187579", "大漩涡历史博物馆", R.drawable.p187579));
        boards.add(new Board(i, "485", "篮球", R.drawable.p485));
        boards.add(new Board(i, "491", "议会", R.drawable.p491));
        boards.addCategoryName(i, "大漩涡系列");
        i++;

        boards.add(new Board(i, "390", "五晨寺", R.drawable.p390));
        boards.add(new Board(i, "320", "黑锋要塞", R.drawable.p320));
        boards.add(new Board(i, "181", "铁血沙场", R.drawable.p181));
        boards.add(new Board(i, "182", "魔法圣堂", R.drawable.p182));
        boards.add(new Board(i, "183", "信仰神殿", R.drawable.p183));
        boards.add(new Board(i, "185", "风暴祭坛", R.drawable.p185));
        boards.add(new Board(i, "186", "翡翠梦境", R.drawable.p186));
        boards.add(new Board(i, "187", "猎手大厅", R.drawable.p187));
        boards.add(new Board(i, "184", "圣光之力", R.drawable.p184));
        boards.add(new Board(i, "188", "恶魔深渊", R.drawable.p188));
        boards.add(new Board(i, "189", "暗影裂口", R.drawable.p189));
        boards.add(new Board(i, "477", "伊利达雷", R.drawable.p477));
        boards.addCategoryName(i, "职业讨论区");
        i++;

        boards.add(new Board(i, "310", "精英议会", R.drawable.p310));
        boards.add(new Board(i, "190", "任务讨论", R.drawable.p190));
        boards.add(new Board(i, "213", "战争档案", R.drawable.p213));
        boards.add(new Board(i, "218", "副本专区", R.drawable.p218));
        boards.add(new Board(i, "258", "战场讨论", R.drawable.p258));
        boards.add(new Board(i, "272", "竞技场", R.drawable.p272));
        boards.add(new Board(i, "191", "地精商会", R.drawable.p191));
        boards.add(new Board(i, "200", "插件研究", R.drawable.p200));
        boards.add(new Board(i, "240", "BigFoot", R.drawable.p240));
        boards.add(new Board(i, "460", "大脚综合", R.drawable.p460));
        boards.add(new Board(i, "461", "大脚水区", R.drawable.p461));
        boards.add(new Board(i, "458", "大脚LOL", R.drawable.p458));
        boards.add(new Board(i, "274", "插件发布", R.drawable.p274));
        boards.add(new Board(i, "315", "战斗统计", R.drawable.p315));
        boards.add(new Board(i, "333", "DKP系统", R.drawable.p333));
        boards.add(new Board(i, "327", "成就讨论", R.drawable.p327));
        boards.add(new Board(i, "388", "幻化讨论", R.drawable.p388));
        boards.add(new Board(i, "411", "宠物讨论", R.drawable.p411));
        boards.add(new Board(i, "463", "要塞讨论", R.drawable.p463));
        boards.add(new Board(i, "255", "公会管理", R.drawable.p255));
        boards.add(new Board(i, "306", "人员招募", R.drawable.p306));
        boards.addCategoryName(i, "冒险心得");
        i++;

        boards.add(new Board(i, "264", "卡拉赞剧院", R.drawable.p264));
        boards.add(new Board(i, "8", "大图书馆", R.drawable.p8));
        boards.add(new Board(i, "102", "作家协会", R.drawable.p102));
        boards.add(new Board(i, "124", "壁画洞窟", R.drawable.p124));
        boards.add(new Board(i, "254", "镶金玫瑰", R.drawable.p254));
        boards.add(new Board(i, "355", "龟岩兄弟会", R.drawable.p355));
        boards.add(new Board(i, "116", "奇迹之泉", R.drawable.p116));
        boards.addCategoryName(i, "麦迪文之塔");
        i++;

        boards.add(new Board(i, "193", "帐号安全", R.drawable.p193));
        boards.add(new Board(i, "334", "PC软硬件", R.drawable.p334));
        boards.add(new Board(i, "201", "系统问题", R.drawable.p201));
        boards.add(new Board(i, "335", "网站开发", R.drawable.p335));
        boards.addCategoryName(i, "系统软硬件讨论");
        i++;

        boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.p414));
        boards.add(new Board(i, "428", "手机游戏", R.drawable.p428));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
        boards.add(new Board(i, "-452227", "口袋妖怪", R.drawable.p452227));
        boards.add(new Board(i, "426", "智龙迷城", R.drawable.p426));
        boards.add(new Board(i, "-51095", "部落冲突", R.drawable.p51095));
        boards.add(new Board(i, "492", "部落冲突:皇室战争", R.drawable.p492));
        boards.add(new Board(i, "-362960", "最终幻想14", R.drawable.p362960));
        boards.add(new Board(i, "-6194253", "战争雷霆", R.drawable.p6194253));
        boards.add(new Board(i, "427", "怪物猎人", R.drawable.p427));
        boards.add(new Board(i, "-47218", "地下城与勇士", R.drawable.p47218));
        boards.add(new Board(i, "425", "行星边际2", R.drawable.p425));
        boards.add(new Board(i, "422", "炉石传说", R.drawable.p422));
        boards.add(new Board(i, "-65653", "剑灵", R.drawable.p65653));
        boards.add(new Board(i, "412", "巫师之怒", R.drawable.p412));
        boards.add(new Board(i, "-235147", "激战2", R.drawable.p235147));
        boards.add(new Board(i, "442", "逆战", R.drawable.p442));
        boards.add(new Board(i, "-46468", "洛拉斯的战争世界", R.drawable.p46468));
        boards.add(new Board(i, "483", "洛拉斯的战争世界:插件", R.drawable.p46468));
        boards.add(new Board(i, "432", "战机世界", R.drawable.p432));
        boards.add(new Board(i, "441", "战舰世界", R.drawable.p441));
        boards.add(new Board(i, "321", "DotA", R.drawable.p321));
        boards.add(new Board(i, "375", "DotA联赛饰品", R.drawable.p375));
        boards.add(new Board(i, "-2371813", "EVE", R.drawable.p2371813));
        boards.add(new Board(i, "-7861121", "剑叁 ", R.drawable.p7861121));
        boards.add(new Board(i, "448", "剑叁同人 ", R.drawable.p448));
        boards.add(new Board(i, "-793427", "斗战神", R.drawable.p793427));
        boards.add(new Board(i, "332", "战锤40K", R.drawable.p332));
        boards.add(new Board(i, "416", "火炬之光2", R.drawable.p416));
        boards.add(new Board(i, "406", "星际争霸2", R.drawable.p406));
        boards.add(new Board(i, "420", "MT Online", R.drawable.p420));
        boards.add(new Board(i, "424", "圣斗士星矢", R.drawable.p424));
        boards.add(new Board(i, "-1513130", "鲜血兄弟会", R.drawable.p1513130));
        boards.add(new Board(i, "433", "神雕侠侣", R.drawable.p433));
        boards.add(new Board(i, "434", "神鬼幻想", R.drawable.p434));
        boards.add(new Board(i, "435", "上古卷轴Online", R.drawable.p435));
        boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.p443));
        boards.add(new Board(i, "444", "刀塔传奇", R.drawable.p444));
        boards.add(new Board(i, "445", "迷你西游", R.drawable.p445));
        boards.add(new Board(i, "447", "锁链战记", R.drawable.p447));
        boards.add(new Board(i, "-532408", "沃土", R.drawable.p532408));
        boards.add(new Board(i, "353", "纽沃斯英雄传", R.drawable.p353));
        boards.add(new Board(i, "452", "天涯明月刀", R.drawable.p452));
        boards.add(new Board(i, "453", "魔力宝贝", R.drawable.p453));
        boards.add(new Board(i, "454", "神之浩劫", R.drawable.p454));
        boards.add(new Board(i, "455", "鬼武者 魂", R.drawable.p455));
        boards.add(new Board(i, "480", "百万亚瑟王", R.drawable.p480));
        boards.add(new Board(i, "481", "Minecraft", R.drawable.p481));
        boards.add(new Board(i, "482", "CS", R.drawable.p482));
        boards.add(new Board(i, "484", "热血江湖传", R.drawable.p484));
        boards.add(new Board(i, "486", "辐射", R.drawable.p486));
        boards.add(new Board(i, "487", "刀剑魔药2", R.drawable.p487));
        boards.add(new Board(i, "488", "村长打天下", R.drawable.p488));
        boards.add(new Board(i, "493", "刀塔战纪", R.drawable.p493));
        boards.add(new Board(i, "494", "魔龙之魂", R.drawable.p494));
        boards.add(new Board(i, "495", "光荣三国志系列", R.drawable.p495));
        boards.add(new Board(i, "496", "九十九姬", R.drawable.p496));
        boards.addCategoryName(i, "其他游戏");
        i++;

        boards.add(new Board(i, "318", "暗黑破坏神3", R.drawable.p318));
        boards.add(new Board(i, "403", "购买/安装/共享", R.drawable.p403));
        boards.add(new Board(i, "393", "背景故事与文艺作品", R.drawable.p393));
        boards.add(new Board(i, "400", "职业讨论区", R.drawable.p400));
        boards.add(new Board(i, "395", "野蛮人", R.drawable.p395));
        boards.add(new Board(i, "396", "猎魔人", R.drawable.p396));
        boards.add(new Board(i, "397", "武僧", R.drawable.p397));
        boards.add(new Board(i, "398", "巫医", R.drawable.p398));
        boards.add(new Board(i, "399", "魔法师", R.drawable.p399));
        boards.add(new Board(i, "446", "圣教军", R.drawable.p446));
        boards.addCategoryName(i, "暗黑破坏神");
        i++;

        boards.add(new Board(i, "422", "炉石传说", R.drawable.p422));
        boards.add(new Board(i, "429", "战术讨论", R.drawable.p429));
        boards.add(new Board(i, "450", "文章存档", R.drawable.p450));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
        boards.add(new Board(i, "457", "视频资料", R.drawable.p457));
        boards.add(new Board(i, "459", "守望先锋", R.drawable.p459));
        boards.add(new Board(i, "490", "魔兽争霸", R.drawable.p490));
        boards.addCategoryName(i, "暴雪游戏");
        i++;

        boards.add(new Board(i, "-152678", "英雄联盟", R.drawable.p152678));
        boards.add(new Board(i, "418", "游戏视频", R.drawable.p418));
        boards.add(new Board(i, "479", "赛事讨论", R.drawable.p152678));
        boards.addCategoryName(i, "英雄联盟");
        i++;

        boards.add(new Board(i, "-447601", "二次元国家地理", R.drawable.p447601));
        boards.add(new Board(i, "-84", "模玩之魂", R.drawable.p84));
        boards.add(new Board(i, "-8725919", "小窗视界", R.drawable.p8725919));
        boards.add(new Board(i, "-965240", "树洞", R.drawable.p965240));
        boards.add(new Board(i, "-131429", "红茶馆——小说馆", R.drawable.p131429));
        boards.add(new Board(i, "-608808", "血腥厨房", R.drawable.p608808));
        boards.add(new Board(i, "-469608", "影~视~秀", R.drawable.p469608));
        boards.add(new Board(i, "-55912", "音乐讨论", R.drawable.p55912));
        boards.add(new Board(i, "-522474", "综合体育讨论区", R.drawable.p522474));
        boards.add(new Board(i, "-1068355", "晴风村", R.drawable.p1068355));
        boards.add(new Board(i, "-168888", "育儿版", R.drawable.p168888));
        boards.add(new Board(i, "-54214", "时尚板", R.drawable.p54214));
        boards.add(new Board(i, "-353371", "宠物养成", R.drawable.p353371));
        boards.add(new Board(i, "-538800", "乙女向二次元", R.drawable.p538800));
        boards.add(new Board(i, "-7678526", "麻将科学院", R.drawable.p7678526));
        boards.add(new Board(i, "-202020", "程序员职业交流", R.drawable.p202020));
        boards.add(new Board(i, "-444012", "我们的骑迹", R.drawable.p444012));
        boards.add(new Board(i, "-349066", "开心茶园", R.drawable.p349066));
        boards.add(new Board(i, "-314508", "世界尽头的百货公司", R.drawable.p314508));
        boards.add(new Board(i, "-2671", "耳机区", R.drawable.p2671));
        boards.add(new Board(i, "-970841", "东方教主陈乔恩", R.drawable.p970841));
        boards.add(new Board(i, "-3355501", "基腐版", R.drawable.p3355501));
        boards.add(new Board(i, "-403298", "怨灵图纸收藏室", R.drawable.p403298));
        boards.add(new Board(i, "-3432136", "飘落的诗章", R.drawable.p3432136));
        boards.add(new Board(i, "-187628", "家居 装修", R.drawable.p187628));
        boards.add(new Board(i, "-8627585", "牛头人酋长乐队", R.drawable.p8627585));
        boards.add(new Board(i, "-17100", "全民健身中心", R.drawable.p395));
        boards.addCategoryName(i, "个人版面");
        // i++;

        String addFidStr = share.getString(ADD_FID, "");
        List<Board> addFidList = null;
        if (!StringUtil.isEmpty(addFidStr)) {
            addFidList = JSON.parseArray(addFidStr, Board.class);
            if (addFidList != null) {
                i++;
                for (int j = 0; j < addFidList.size(); j++) {
                    boards.add(new Board(i, addFidList.get(j).getUrl(),
                            addFidList.get(j).getName(), addFidList.get(j)
                            .getIcon()));
                }
            }
        }
        if (addFidList != null) {
            boards.addCategoryName(i, ADDFID);
            // i++;
        }

        return boards;
    }

    @TargetApi(8)
    private void initPath() {
        File baseDir = getExternalCacheDir();
        if (baseDir != null)
            HttpUtil.PATH = baseDir.getAbsolutePath();
        else
            HttpUtil.PATH = android.os.Environment
                    .getExternalStorageDirectory().getPath()
                    + "/Android/data/gov.anzong.androidnga";
        HttpUtil.PATH_AVATAR = HttpUtil.PATH + "/nga_cache";
        HttpUtil.PATH_NOMEDIA = HttpUtil.PATH + "/.nomedia";
        HttpUtil.PATH_IMAGES = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    private void initUserInfo() {

        PhoneConfiguration config = PhoneConfiguration.getInstance();

        SharedPreferences share = this.getSharedPreferences(PERFERENCE,
                MODE_PRIVATE);

        final String uid = share.getString(UID, "");
        final String cid = share.getString(CID, "");
        final String replystring = share.getString(PENDING_REPLYS, "");
        final int replytotalnum = Integer.parseInt(share.getString(
                REPLYTOTALNUM, "0"));
        final String black = share.getString(BLACK_LIST, "");
        final Set<Integer> blacklist = StringUtil.blackliststringtolisttohashset(black);
        if (!StringUtil.isEmpty(uid) && !StringUtil.isEmpty(cid)) {
            config.setUid(uid);
            config.setCid(cid);
            config.setReplyString(replystring);
            config.setReplyTotalNum(replytotalnum);
            config.blacklist = blacklist;
            String userListString = share.getString(USER_LIST, "");
            final String name = share.getString(USER_NAME, "");
            config.userName = name;
            if (StringUtil.isEmpty(userListString)) {

                addToUserList(uid, cid, name, replystring, replytotalnum, black);

            }
        }

        boolean downImgWithoutWifi = share.getBoolean(DOWNLOAD_IMG_NO_WIFI,
                false);
        config.setDownImgNoWifi(downImgWithoutWifi);
        boolean downAvatarNoWifi = share.getBoolean(DOWNLOAD_AVATAR_NO_WIFI,
                false);
        config.setDownAvatarNoWifi(downAvatarNoWifi);

        config.setDb_Cookie(share.getString(DBCOOKIE, ""));

    }

    public void addToUserList(String uid, String cid, String name,
                              String replyString, int replytotalnum, String blacklist) {
        SharedPreferences share = this.getSharedPreferences(PERFERENCE,
                MODE_PRIVATE);
        if (blacklist == null) {
            blacklist = "";
        }
        String userListString = share.getString(USER_LIST, "");

        List<User> userList = null;
        // new ArrayList<User>();
        if (StringUtil.isEmpty(userListString)) {
            userList = new ArrayList<User>();
        } else {
            userList = JSON.parseArray(userListString, User.class);
            for (User u : userList) {
                if (u.getUserId().equals(uid)) {
                    userList.remove(u);
                    break;
                }

            }
        }

        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
        user.setReplyString(replyString);
        user.setReplyTotalNum(replytotalnum);
        user.setBlackList(blacklist);
        userList.add(0, user);
        userListString = JSON.toJSONString(userList);
        share.edit().putString(UID, uid).putString(CID, cid)
                .putString(USER_NAME, name)
                .putString(PENDING_REPLYS, replyString)
                .putString(REPLYTOTALNUM, String.valueOf(replytotalnum))
                .putString(USER_LIST, userListString).putString(BLACK_LIST, blacklist).apply();
    }

    public void upgradeUserdata(String blacklist) {
        SharedPreferences share = this.getSharedPreferences(PERFERENCE, MODE_PRIVATE);

        String userListString = share.getString(USER_LIST, "");
        List<User> userList = null;
        if (StringUtil.isEmpty(userListString)) {
            userList = new ArrayList<User>();
        } else {
            userList = JSON.parseArray(userListString, User.class);
            for (User u : userList) {
                if (u.getUserId().equals(PhoneConfiguration.getInstance().uid)) {
                    addToUserList(u.getUserId(), u.getCid(), u.getNickName(), u.getReplyString(), u.getReplyTotalNum(), blacklist);
                    break;
                }

            }
        }
    }

    public void addToMeiziUserList(String uid, String sess) {
        SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
        String cookie = "uid=" + uid + "; sess=" + sess;
        share.edit().putString(DBCOOKIE, cookie).apply();
        config.setDb_Cookie(cookie);
    }

    private void loadConfig() {

        SharedPreferences share = this.getSharedPreferences(PERFERENCE,
                MODE_PRIVATE);
        if (share.getBoolean(NIGHT_MODE, false))
            ThemeManager.getInstance().setMode(1);

        ThemeManager.getInstance().screenOrentation = share.getInt(
                SCREEN_ORENTATION, ActivityInfo.SCREEN_ORIENTATION_USER);

        int version_in_config = share.getInt(VERSION, 0);
        if (version_in_config < version) {
            newVersion = true;
            Editor editor = share.edit();
            editor.putInt(VERSION, version);
            editor.putBoolean(REFRESH_AFTER_POST, false);

            String recentStr = share.getString(RECENT_BOARD, "");
            List<Board> recentList = null;
            if (!StringUtil.isEmpty(recentStr)) {
                recentList = JSON.parseArray(recentStr, Board.class);
                if (recentList != null) {
                    for (int j = 0; j < recentList.size(); j++) {
                        recentList.get(j).setIcon(R.drawable.pdefault);
                    }
                    recentStr = JSON.toJSONString(recentList);
                    editor.putString(RECENT_BOARD, recentStr);
                }
            }
            if (version_in_config < 2028) {
                editor.putString(USER_LIST, "");
            }
            editor.apply();

        }

        // refresh
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        config.setRefreshAfterPost(false);

        config.showAnimation = share.getBoolean(SHOW_ANIMATION, false);
        config.refresh_after_post_setting_mode = share.getBoolean(REFRESH_AFTERPOST_SETTING_MODE, true);
        config.useViewCache = share.getBoolean(USE_VIEW_CACHE, true);
        config.showSignature = share.getBoolean(SHOW_SIGNATURE, false);
        config.uploadLocation = share.getBoolean(UPLOAD_LOCATION, false);
        config.showStatic = share.getBoolean(SHOW_STATIC, false);
        config.showReplyButton = share.getBoolean(SHOW_REPLYBUTTON, true);
        config.showColortxt = share.getBoolean(SHOW_COLORTXT, false);
        config.showNewweiba = share.getBoolean(SHOW_NEWWEIBA, false);
        config.showLajibankuai = share.getBoolean(SHOW_LAJIBANKUAI, true);
        config.imageQuality = share.getInt(DOWNLOAD_IMG_QUALITY_NO_WIFI, 0);
        config.HandSide = share.getInt(HANDSIDE, 0);
        config.fullscreen = share.getBoolean(FULLSCREENMODE, false);
        config.kitwebview = share.getBoolean(KITWEBVIEWMODE, false);
        config.blackgunsound = share.getInt(BLACKGUN_SOUND, 0);
        config.iconmode = share.getBoolean(SHOW_ICON_MODE, false);
        config.swipeBack = share.getBoolean(SWIPEBACK, true);
        config.swipeenablePosition = share.getInt(SWIPEBACKPOSITION, 2);

        // font
        final float defTextSize = 21.0f;// new TextView(this).getTextSize();
        final int defWebSize = 16;// new
        // WebView(this).getSettings().getDefaultFontSize();

        final float textSize = share.getFloat(TEXT_SIZE, defTextSize);
        final int webSize = share.getInt(WEB_SIZE, defWebSize);
        config.setTextSize(textSize);
        config.setWebSize(webSize);

        boolean notification = share.getBoolean(ENABLE_NOTIFIACTION, true);
        boolean notificationSound = share.getBoolean(NOTIFIACTION_SOUND, true);
        config.notification = notification;
        config.notificationSound = notificationSound;

        config.nikeWidth = share.getInt(NICK_WIDTH, 100);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int flag = share.getInt(UI_FLAG, 0);
            if ((config.getUiFlag() & UI_FLAG_HA) != 0) {
                flag = flag & ~UI_FLAG_HA;
                Editor editor = share.edit();
                editor.putInt(UI_FLAG, flag);
                editor.apply();
            }
            PhoneConfiguration.getInstance().setUiFlag(flag);
        } else {
            int uiFlag = share.getInt(UI_FLAG, 0);
            config.setUiFlag(uiFlag);
        }

        // bookmarks
        String bookmarkJson = share.getString(BOOKMARKS, "");
        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
        try {
            if (!bookmarkJson.equals(""))
                bookmarks = JSON.parseArray(bookmarkJson, Bookmark.class);
        } catch (Exception e) {
            Log.e("JSON_error", Log.getStackTraceString(e));
        }
        PhoneConfiguration.getInstance().setBookmarks(bookmarks);

    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }

    public PhoneConfiguration getConfig() {
        return config;
    }

}

package sp.phone.mvp.model.convert.decoder;

import sp.phone.theme.ThemeManager;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumEmoticonDecoder implements IForumDecoder {

    // color filter css class for night mode, we invert color for certain emotion icon
    private static final String INVERT_CSS_HTML = "\n <style> .invertfilter { filter: invert(100%); </style> \n";
    private static final String CLASS_FIELD = " class=\"invertfilter\"";
    private String localClassField;
    private ThemeManager mThemeManager = ThemeManager.getInstance();

    @Override
    public String decode(String content) {
        final String acniangofubbcode[] = {"blink", "goodjob", "上", "中枪",
                "偷笑", "冷", "凌乱", "反对", "吓", "吻", "呆", "咦", "哦", "哭", "哭1",
                "哭笑", "哼", "喘", "喷", "嘲笑", "嘲笑1", "囧", "委屈", "心", "忧伤", "怒",
                "怕", "惊", "愁", "抓狂", "抠鼻", "擦汗", "无语", "晕", "汗", "瞎", "羞",
                "羡慕", "花痴", "茶", "衰", "计划通", "赞同", "闪光", "黑枪"};// (0-44)
        final String acniangappadd[] = {"-47218_5052bca81a77f.png",
                "-47218_5052bd3b4b3bd.png", "-1324875_50e597f5ce78d.png",
                "-47218_5052bcba15fcf.png", "-47218_5052bcb6e96d1.png",
                "-47218_5052bd2a0d49a.png", "-47218_5052c10aa0303.png",
                "-47218_5052bcaaacb45.png", "-1324875_50e597c090c58.png",
                "-47218_5052c104b8e27.png", "-47218_5052bc587c6f9.png",
                "-47218_5052c1076f119.png", "-47218_5052bd2497822.png",
                "-47218_5052bd2fa0790.png", "-47218_5052c0f6da079.png",
                "-47218_5052bc4cc6331.png", "-47218_5052bcf37c4c9.png",
                "-1324875_513394fbc54e1.gif", "-47218_5052bc4f51be7.png",
                "-47218_5052c1101747c.png", "-47218_5052c10d1f08c.png",
                "-47218_5052bcdd279bc.png", "-47218_5052bce27ab4d.png",
                "-47218_5052bd35aec58.png", "-47218_5052bcdfd9c69.png",
                "-47218_5052bc835856c.png", "-47218_5052bce4f2963.png",
                "-47218_5052bd330dfad.png", "-47218_5052bc7d91913.png",
                "-47218_5052c112b3b1b.png", "-47218_5052bcf0ba2db.png",
                "-47218_5052bc8638067.png", "-47218_5052bca55cb6e.png",
                "-47218_5052bc521c04b.png", "-47218_5052bca2a2f43.png",
                "-47218_5052bcad49530.png", "-47218_5052bceb823da.png",
                "-47218_5052bc80140e3.png", "-47218_5052bcb3b8944.png",
                "-1324875_50d841a63a673.png", "-47218_5052bcf68ddc2.png",
                "-1324875_50e597e9d6319.png", "-47218_5052bd27520ef.png",
                "-47218_5052bcbe35760.png", "-1324875_50e597f190a11.png"// 0-44
        };
        final String newacniangofubbcode[] = {
                "goodjob", "诶嘿", "偷笑", "怒", "笑",
                "那个…", "哦嗬嗬嗬", "舔", "鬼脸", "冷",
                "大哭", "哭", "恨", "中枪", "囧",
                "你看看你", "doge", "自戳双目", "偷吃", "冷笑",
                "壁咚", "不活了", "不明觉厉", "是在下输了", "你为猴这么",
                "干杯", "干杯2", "异议", "认真", "你已经死了",
                "你这种人…", "妮可妮可妮", "惊", "抢镜头", "yes",
                "有何贵干", "病娇", "lucky", "poi", "囧2",
                "威吓", "jojo立", "jojo立2", "jojo立3", "jojo立4",
                "jojo立5",};// (0-45)
        final String newacniangappadd[] = {"a2_02.png", "a2_05.png", "a2_03.png", "a2_04.png",
                "a2_07.png", "a2_08.png", "a2_09.png", "a2_10.png", "a2_14.png",
                "a2_16.png", "a2_15.png", "a2_17.png", "a2_21.png", "a2_23.png",
                "a2_24.png", "a2_25.png", "a2_27.png", "a2_28.png", "a2_30.png",
                "a2_31.png", "a2_32.png", "a2_33.png", "a2_36.png", "a2_51.png",
                "a2_53.png", "a2_54.png", "a2_55.png", "a2_47.png", "a2_48.png",
                "a2_45.png", "a2_49.png", "a2_18.png", "a2_19.png", "a2_52.png",
                "a2_26.png", "a2_11.png", "a2_12.png", "a2_13.png", "a2_20.png",
                "a2_22.png", "a2_42.png", "a2_37.png", "a2_38.png", "a2_39.png",
                "a2_41.png", "a2_40.png",// 0-45
        };
        final String penguinOfUBBCode[] = {
                "战斗力", "哈啤", "满分", "衰", "拒绝",
                "心", "严肃", "吃瓜", "嘣", "嘣2",
                "冻", "谢", "哭", "响指", "转身"
        };
        final String penguinAppAdd[] = {
                "pg01.png", "pg02.png", "pg03.png", "pg04.png", "pg05.png",
                "pg06.png", "pg07.png", "pg08.png", "pg09.png", "pg10.png",
                "pg11.png", "pg12.png", "pg13.png", "pg14.png", "pg15.png"
        };
        final String pstofubbcode[] = {"举手", "亲", "偷笑", "偷笑2", "偷笑3",
                "傻眼", "傻眼2", "兔子", "发光", "呆",
                "呆2", "呆3", "呕", "呵欠", "哭",
                "哭2", "哭3", "嘲笑", "基", "宅",
                "安慰", "幸福", "开心", "开心2", "开心3",
                "怀疑", "怒", "怒2", "怨", "惊吓",
                "惊吓2", "惊呆", "惊呆2", "惊呆3", "惨",
                "斜眼", "晕", "汗", "泪", "泪2",
                "泪3", "泪4", "满足", "满足2", "火星",
                "牙疼", "电击", "看戏", "眼袋", "眼镜",
                "笑而不语", "紧张", "美味", "背", "脸红",
                "脸红2", "腐", "星星眼", "谢", "醉",
                "闷", "闷2", "音乐", "黑脸", "鼻血",};// (0-64)
        final String pstappadd[] = {"pt00.png", "pt01.png", "pt02.png", "pt03.png", "pt04.png",
                "pt05.png", "pt06.png", "pt07.png", "pt08.png", "pt09.png",
                "pt10.png", "pt11.png", "pt12.png", "pt13.png", "pt14.png",
                "pt15.png", "pt16.png", "pt17.png", "pt18.png", "pt19.png",
                "pt20.png", "pt21.png", "pt22.png", "pt23.png", "pt24.png",
                "pt25.png", "pt26.png", "pt27.png", "pt28.png", "pt29.png",
                "pt30.png", "pt31.png", "pt32.png", "pt33.png", "pt34.png",
                "pt35.png", "pt36.png", "pt37.png", "pt38.png", "pt39.png",
                "pt40.png", "pt41.png", "pt42.png", "pt43.png", "pt44.png",
                "pt45.png", "pt46.png", "pt47.png", "pt48.png", "pt49.png",
                "pt50.png", "pt51.png", "pt52.png", "pt53.png", "pt54.png",
                "pt55.png", "pt56.png", "pt57.png", "pt58.png", "pt59.png",
                "pt60.png", "pt61.png", "pt62.png", "pt63.png", "pt64.png",};

        final String dtofubbcode[] = {
                "ROLL", "上", "傲娇", "叉出去", "发光",
                "呵欠", "哭", "啃古头", "嘲笑", "心",
                "怒", "怒2", "怨", "惊", "惊2",
                "无语", "星星眼", "星星眼2", "晕", "注意",
                "注意2", "泪", "泪2", "烧", "笑",
                "笑2", "笑3", "脸红", "药", "衰",
                "鄙视", "闲", "黑脸",//0-32
        };
        final String dtappadd[] = {
                "dt01.png", "dt02.png", "dt03.png", "dt04.png", "dt05.png",
                "dt06.png", "dt07.png", "dt08.png", "dt09.png",
                "dt10.png", "dt11.png", "dt12.png", "dt13.png", "dt14.png",
                "dt15.png", "dt16.png", "dt17.png", "dt18.png", "dt19.png",
                "dt20.png", "dt21.png", "dt22.png", "dt23.png", "dt24.png",
                "dt25.png", "dt26.png", "dt27.png", "dt28.png", "dt29.png",
                "dt30.png", "dt31.png", "dt32.png", "dt33.png",//0-32
        };

        // If it is night mode we attach class field to invert color.
        if (mThemeManager.isNightMode()) {
            localClassField = CLASS_FIELD;
        } else {
            localClassField = "";
        }
        for (int i = 0; i < 45; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:ac:" + acniangofubbcode[i]
                    + "]", "<img src='file:///android_asset/acniang/"
                    + acniangappadd[i] + "'" + localClassField + ">");
        }
        for (int i = 0; i < 46; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:a2:" + newacniangofubbcode[i]
                    + "]", "<img src='file:///android_asset/newacniang/"
                    + newacniangappadd[i] + "'" + localClassField + ">");

        }
        for (int i = 0; i < penguinOfUBBCode.length; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:pg:" + penguinOfUBBCode[i]
                    + "]", "<img src='file:///android_asset/pg/"
                    + penguinAppAdd[i] + "' width=" + 60 + " height=" + 60 + ">");
        }
        for (int i = 0; i < 65; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:pst:" + pstofubbcode[i]
                    + "]", "<img src='file:///android_asset/pst/"
                    + pstappadd[i] + "'>");
        }
        for (int i = 0; i < 33; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:dt:" + dtofubbcode[i]
                    + "]", "<img src='file:///android_asset/dt/"
                    + dtappadd[i] + "'" + localClassField + ">");
        }
        content = INVERT_CSS_HTML + content;
        return content;
    }
}

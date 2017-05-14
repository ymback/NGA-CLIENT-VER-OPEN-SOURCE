package sp.phone.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.Utils;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.bean.StringFindResult;

@SuppressLint("SimpleDateFormat")
public class StringUtil {
    public final static String key = "asdfasdf";
    final static String tips = "花几十秒看完,你会有新收获\n"
            + "由于新增黑名单功能,低于2.0.28版本的APP更新后会重置登录信息\n"
            + "更新后每次打开看到这个窗口的,重启手机\n"
            + "发现bug,删除app,再更新到最新版,还有问题私信[@竹井詩織里],号已经被CCQ了\n"
            + "签到/短消息/历史被喷/匿名版/URL读取看帖/添加版面等在侧边栏,设置里选项都看下\n"
            + "主题列表长按楼层可以看头像签名、用户信息、屏蔽用户等,还有投票,自己的个人信息界面可以改头像和签名,收藏列表长按可删收藏\n"
            + "看不到的选项按菜单键或竖排三个点的按钮,很多功能都在里面,比如分享啊啥的\n"
            + "内置播放器独立为播放器APP,若未安装请到侧边栏关于中下载或PLAY商店搜索BambooPlayer安装\n"
            + "彩蛋还有,但不知道在哪了\n"
            + "客户端吐槽QQ群:172503242,欢迎加入捡肥皂\n"
            + "查找更新请使用侧边栏关于中的新版校验功能\n"
            + "PS:我已经叛变了,IOS的没有第三方客户端,还有健健";
    private static final String lesserNukeStyle = "<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>";
    private static final String styleAlignRight = "<div style='text-align:right' >";
    private static final String styleAlignLeft = "<div style='text-align:left' >";
    private static final String styleAlignCenter = "<div style='text-align:center' >";
    private static final String styleColor = "<span style='color:$1' >";
    private static final String collapseStart = "<div style='border:1px solid #888' >";
    private static final String ignoreCaseTag = "(?i)";
    private static final String endDiv = "</div>";
    private static final String[] SAYING = {
            "战争打响，真理阵亡。;埃斯库罗斯",
            "让子弹先走。 ",
            "人类不结束战争，战争就会结束人类。;约翰·F·肯尼迪",
            "战争无法决定谁是正确的，只能决定谁是存活的一方。;罗素 ",
            "没有海军陆战队的军舰就像没有扣子的衣服。 ;美国海军上将 大卫·D·波特",
            "传媒是我们主要的思想武器。;赫鲁晓夫",
            "不管你喜不喜欢，历史都站在我们这边，而你们将被我们埋葬！;赫鲁晓夫",
            "敌人在的射程内，彼此彼此。;步兵日记 ",
            "一枚战斧巡航导弹的造价：90万美元。 ",
            "一架F-22猛禽战斗机的造价：1.35亿美元",
            "一架F-117A“夜鹰”隐形战斗机的造价：1.22亿美元 ",
            "一架B-2轰炸机的造价：22亿美元",
            "只要有人类存在，就会有战争 。;爱因斯坦 ",
            "对着敌人瞄准。;美国 火箭发射器上的使用说明 ",
            "任何诚实的军事指挥官都会承认他在动用军队时犯下过错误。;罗伯特·麦克纳马拉",
            "你可以用暴力获得权利，但你会如坐针毡。;叶利钦 ",
            "世上最致命的武器是陆战队和他的枪！;美国将军 约翰 J. 珀欣",
            "站在理想一边的人不能称为恐怖分子。;阿拉法特",
            "没有比挨了枪子还安然无恙更爽的了。 ;丘吉尔",
            "对于没经历过战争的人来说，战争当然是轻松愉快的。;伊拉斯谟 ",
            "友军伤害——不友好。",
            "就像士兵们结束战争一样，外交官往往是开始战争的关键。",
            "科技在被应用之前，都是道德中立的。用之善则善，用之恶则恶。;威廉·吉布森",
            "老家伙们宣战，上前线的却是小伙子们。;赫伯特·胡佛 第三十一位美国总统",
            "战场上的指挥官总是对的，屁股后面的司令部总是错的，除非举出反例。;鲍威尔 美国前国务卿",
            "自由并不是免费的，但是美国海军陆战队会帮你出大头。",
            "我不知道第三次世界大战人类会用什么武器，但第四次世界大战会是棍棒和石头。 ;爱因斯坦",
            "你总是知道该做什么，但难的是去做。",
            "知己知彼，百战不殆。;孙子",
            "几乎所有人都能矗立在逆境中，但是如果你想知道一个人的真实一面，请给他力量。;林肯",
            "如果我们不能向国家证明我们的理想更有价值，我们最好重新检视我们的推理,罗伯特·麦克纳马拉",
            "自由之树必须用暴君和爱国者的鲜血一遍又一遍的洗刷;托马斯·杰斐逊 (美国政治家, 第三任总统, indpdc宣言的起草人)",
            "如果机翼飞得比机身还快，那八成是架直升机——所以这玩意不安全。",
            "5秒的引信只烧3秒;步兵日记",
            "如果进攻太过顺利，那么恭喜你上当了。;士兵日记 ",
            "没有任何作战计划在与敌人遭遇后还有效。;鲍威尔",
            "当保险丝被拔掉后，手雷先生就不再是我们的朋友了。;美国陆军训练提示",
            "人有生死，国有兴亡，而意志长存。;肯尼迪",
            "一枚标枪反坦克导弹的造价：8万美元 ",
            "以道作人主，不以兵强于天下",
            "如果你记不住，66式(XD)就是冲着你来的 ",
            "只有两种人了解陆战队：陆战队和它的敌人，其他人都在扯二手淡。",
            "我身边的陆战队员越多，我越Happy 。;美国陆军克拉克将军 ",
            "别忘了，你的武器是由出价最低的竞标商制造的。 ",
            "记住要透过现象看本质。不要因为害怕真相的肮脏而退缩。",
            "嘿，看开点，他们也许没子弹了。;士兵日记",
            "这个世界不会接受专政与支配。;戈尔巴乔夫 ",
            "暴君们总会有些微不足道的美德，他们会支持法律，然后摧毁它。;伏尔泰 ",
            "英雄不见得比别人更勇敢，但他们多坚持了5分钟。;里根",
            "最后，很幸运，我们曾“如此”接近核战争，但避免了。;罗伯特·麦克纳马拉",
            "有些人活了一辈子，一直希望干些什么大事，但陆战队员们没有那个问题。 ;里根",
            "一般来讲，直接降落在刚刚被轰炸过的区域是不明智的。 ;美国空军中将",
            "我们之所以能够在床上睡安稳觉，是因为大兵们正在为我们站岗。;乔治·奥韦尔",
            "如果你一开始没搞定，赶快呼叫空中支援。 ",
            "曳光弹照亮的不光是敌人。;美国陆军条例",
            "团队协作很重要，它能让别人替你挨枪子。",
            "只有和平才会获得最终的胜利。 ;爱默生",
            "在一个恐怖主义可能拥有科技的世界里，如果我们不采取行动，将会十分后悔。;康多莉扎·赖斯 (美国第66任国务卿) ",
            "兵者，诡道也。;孙子",
            "人类的可靠性和核武器，这对不稳定的组合会毁灭许多国家。;罗伯特·麦克纳马拉",
            "在战争中，输赢、生死只是一念之差。 ;道格拉斯·麦克阿瑟将军",
            "你不能说文明没有进步——至少在每次战争中，他们都换种新方法来干掉你。",
            "在你明白核武器怎么用之前，这玩意就能毁灭国家了。;罗伯特·麦克纳马拉",
            "指挥的家伙不配当英雄，真正的英雄在战斗中诞生。",
            "任何合格的士兵都应该反对战争，同时，也有着值得为之战斗的东西。",
            "不想打胜仗就别去送死。 ",
            "难知如阴，动如雷震。;孙子",
            "衷心想参加战争的人，肯定没真正体验过。;拉里 瑞福斯",
            "说“笔强于剑”的人肯定没见过自动武器。;道格拉斯·麦克阿瑟将军",
            "邪恶的得逞依靠善良的无为 ;埃德蒙·伯克",
            "If a man has done his best, what else is there?;乔治 S. 巴顿将军",
            "手雷的爆炸半径总是比你的跳跃距离多一点。",
            "暴君们一边夸耀他如何爱民，一边在残害百姓。",
            "每个暴君都相信自由——他自己的自由。;阿尔伯特·哈伯德(美国作家)",
            "相对于战争结束来说，我们更希望所有的战争本就没有爆发。;富兰克林·D·罗斯福",
            "成功不是终点，失败也不是终结，只有勇气才是永恒。;温斯顿·丘吉尔 ",
            "没有必胜的决心，战争必败无疑。;道格拉斯·麦克阿瑟 ",
            "所有的战争都是内战，因为所有的人类都是同胞。;弗朗索瓦·费奈隆 ",
            "在战争中，第二名是没有奖赏的。;奥玛·布莱德利将军 ",
            "好动与不满足是进步的第一必需品。",
            "time is money",
            "不论多么师出有名，也决不能因此误以为战争是无罪的。;厄尼斯．海明威",
            "为已死的人哀悼是愚蠢的，我们反而应该感谢上帝曾经赐予他生命。;乔治．巴顿将军",
            "战争的目的不是要你为国牺牲，而是要让该死的敌人为他的国家牺牲。;乔治．巴顿将军",
            "一死则百了 － 没有人，就不会有战争。;约瑟夫．斯大林",
            "一个人的死亡是天大的不幸，而数百万人的死亡则只是简单的统计数字。;约瑟夫．斯大林",
            "幸好战争是如此地丑恶，否则我们恐怕会爱上它。;罗伯特．李",
            "我自己都忍不住开始鼓掌直到我意识到自己是桑德兰的主席。 ;Peter Reid,在博格坎普对桑德兰的比赛中入球后",
            "我告诉我儿子Josh “霍华德·威尔金森希望爸爸为英格兰队比赛。”他把这件事告诉了我的女儿Olivia，然后他们的眼中都含着泪水问我‘那是不是说明你不再为阿森纳踢球了？;Lee Dixon ",

            "战士会愿意为了一小块勋章而奋战到底。;拿破仑．波拿巴特",
            "害怕战败的人一定会战败。;拿破仑．波拿巴特",
            "绝不可与同一敌人对峙太久，否则他会学会你所有的战术。;拿破仑．波拿巴特",
            "不怀念苏联的人没心没肺，想重回苏联的人无头无脑。;普京",
            "...和......表示的含义是不同的。",
            "闭嘴！我们正在讨论民主。",
            "某男：你说这个世界上有没有男的有两个蛋蛋？",
            "1024",
            "你懂的！",
            "YSLM",
            "5楼:people don't want face,sky down no enemy.",
            "1楼:no 废死,who's your 爹地.",
            "Your brain has two parts:the left&the right .Your left brain has nothing right,and your right brain has nothing left.",
            "现在找一个又傻又善良又漂亮,身材又好有钱又肯倒贴的女人怎么这么难?", "8楼:美国的乡下人英语都这么好，难怪美国这么强大 ",
            "星际争霸2:目田之翼", "星际争霸2:折翼的天使 ", "鸟德怒吼:那个贼,不要一直交易我烤鹌鹑",
            "国服最新笑话:DK坦没拿盾,被踢了",
            "楼主：帮忙给我即将出生的孩子取个好听的名字。回帖：陈不悔。楼主：大哥,我姓王。回帖：我姓陈。",
            "悦来客栈是古代最大的连锁客栈。", "超级巨毒，解药，暗器都产自西域。",
            "平时朝夕相处的人，只要穿上夜行衣，再蒙个面纱，对方就不认识了。",
            "没用的小角色用的武功名字有很强的文学性和动物性，就是不大好用。", "长着超长白发+胡子的绝对是旷世高人，和他要拉好关系。",
            "英雄配一把好兵器，好到从不用去保养修理。",
            "在乱箭中，英雄要是不想死，就决不会死；万一中了箭，那也是因为一旁有大恶人挟持其亲人导致英雄分心。",
            "一定要象征性的打几下，才出绝招，并喷着口水大叫：去死吧！！",
            "使出必杀技要做很花哨的动作，还要做上一两分钟，但敌人决不会乘机偷袭，尽管这是个好机会……",
            "高手都无视万有引力，到处乱飞且飞得飞快。不过要是赶远路，却会骑马。",
            "大侠套餐：2斤熟牛肉+上等女儿红。(悦来客栈长期供应......)",
            "好人从不下毒，坏人从不不下毒；但好人从不下毒却老被诬陷下毒，坏人从不不下毒却没人怀疑他。",
            "大侠想显示自己的修为，往往会捡起一根树枝将不知天高地厚的小角色打败，后来悦来客栈开始供应树枝……",
            "在一条笔直的街道被人追杀，尽管有很多事要做，但弄翻两旁的小摊是最重要的！",
            "好人用暗器是形式所逼，多才多艺，一击必中；坏人用暗器是卑鄙无耻，旁门左道，扔死了都扔不中……",
            "坏人千心万苦扔中了，还会被好人忍着巨痛放倒，并喷着口水大叫：卑鄙！", "会有绝世佳人救起中暗器的英雄，日不久也生情……",
            "当时社会治安不好，人人佩带危险器械……", "菜市场杀猪的绝对是一胖子！！！！！",
            "绝世神兵被麻布一层一层裹紧，绝世神人也被麻布一层一层裹紧……",
            "主角一生坎坷或是一帆风顺，一生坎坷的会坎坷到死，一帆风顺的从不买票……",
            "所有人都很有钱，铜板很少出现，一张一张的银票比草纸还便宜。", " (悦来客栈的)店小二知识渊博，有问(+钱)必答！",
            "少林寺就1个方丈(老和尚那种8算)和1个徒弟厉害，其他都很菜。",
            "单挑时， “ 正义 ” 一方支撑不住了，就会喊人帮忙： “ 对付这种魔头，不用和他讲什么江湖道义，大家一起上！ ”",
            "少林图书馆经常失窃……", "一个人喝完闷酒一定会下暴雨。", "团体组合流行：四大?#，四大%￥，四大*(……",
            "拔剑时，有时会有剑气，有时会拔不出来……", "朝廷的大将军是坨屎，公公才是高手。",
            "妓院都是怡红院(我怀疑是悦来集团的子公司……)。", "美女到处都是，这是最郁闷的…… ", "不要因为仅仅是意见不同就点举报",
            "所有汉字乱码的,把接入点从wap改成net!!!", "178,准时发货,绝不坑爹!", "178,准时坑爹,绝不发货!",
            "彩虹体累计已招致禁言210天次以上,自重!", "寻找NGA客户端开源版更新请在Google Play商店或酷安搜索并安装",
            "内置播放器独立为播放器APP,若未安装请到侧边栏关于中下载或PLAY商店搜索BambooPlayer安装",
    };

    /**
     * 验证是否是邮箱
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email))
            return false;
        String pattern1 = "^([a-z0-9A-Z]+[-_|\\.]?)+[a-z0-9A-Z_]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(pattern1);
        Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否是 "" 或者 null
     */
    public static boolean isEmpty(String str) {
        if (str != null && !"".equals(str)) {
            return false;
        } else {
            return true;
        }
    }

    /* 给候总客户端乱码加适配 */
    public static String unescape(String src) {
        if (isEmpty(src))
            return "";
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        String patternStr = "[A-Fa-f0-9]{4}";
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (pos > src.length() - 3) {
                    tmp.append(src.substring(pos, src.length()));
                    lastPos = pos + 3;
                } else {
                    if (src.charAt(pos + 1) == 'u') {
                        try {
                            if (Pattern.matches(patternStr,
                                    src.substring(pos + 2, pos + 6))) {
                                ch = (char) Integer.parseInt(
                                        src.substring(pos + 2, pos + 6), 16);
                                tmp.append(ch);
                                lastPos = pos + 6;
                            } else {
                                tmp.append(src.substring(pos, pos + 3));
                                lastPos = pos + 3;
                            }
                        } catch (Exception e) {
                            tmp.append(src.substring(pos, pos + 3));
                            lastPos = pos + 3;
                        }

                    } else {
                        try {
                            ch = (char) Integer.parseInt(
                                    src.substring(pos + 1, pos + 3), 16);
                            tmp.append(ch);
                            lastPos = pos + 3;
                        } catch (Exception e) {
                            tmp.append(src.substring(pos, pos + 3));
                            lastPos = pos + 3;
                        }
                    }
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static boolean isEmpty(StringBuffer str) {
        if (str != null && !"".equals(str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * yy-M-dd hh:mm
     */
    public static Long sDateToLong(String sDate) {
        DateFormat df = new SimpleDateFormat("yy-M-dd hh:mm");
        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static boolean isNumer(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static Long parseLong(String str) {
        if (str == null) {
            return null;
        } else {
            if (str.equals("")) {
                return 0l;
            } else {
                return Long.parseLong(str);
            }
        }
    }

    public static Long sDateToLong(String sDate, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String encodeUrl(final String s, final String charset) {

		/*
         * try { return java.net.URLEncoder.encode(s,charset); // this not work
		 * in android 4.4 if a english char is followed //by a Chinese character
		 *
		 * } catch (UnsupportedEncodingException e) {
		 *
		 * return ""; }
		 */
        String ret = UriEncoderWithCharset.encode(s, null, charset);
        // Log.i("111111", s+"----->"+ret);
        return ret;
    }

    public static String parseHTML(String s) {
        // 转换字体
        if (s.indexOf("[quote]") != -1) {
            s = s.replace("[quote]", "");
            s = s.replace("[/quote]", "</font><font color='#1d2a63' size='10'>");

            s = s.replace("[b]", "<font color='red' size='1'>");
            s = s.replace("[/b]", "</font>");
            s = s.replace("<br/><br/>", "<br/>");
            s = s.replace("<br/><br/>", "<br/>");

            s = s.replace("[/pid]", "<font color='blue' size='2'>");
            s = s + "</font>";
        } else {
            s = "<font color='#1d2a63' size='10'>" + s;
            s = s + "</font>";
        }
        // 转换 表情

        s = s.replaceAll("(\\[s:\\d\\])", "<img src='$1'>");
        return s;
    }

    public static String decodealbum(String s, String quotediv) {
        int startpos = s.indexOf("[album="), endpos = s.indexOf("[/album]") + 8;
        String sup = "", sdown = "", salbum = "", stemp = "", stitle = "";
        while (startpos < endpos && startpos >= 0) {
            sup = s.substring(0, startpos);
            if (endpos >= 0)
                sdown = s.substring(endpos, s.length());
            salbum = s.substring(startpos, endpos);
            stitle = salbum.replaceAll("(?i)" + "\\[album=(.*?)\\](.*?)\\[/album\\]", "$1");
            stemp = salbum.replaceAll("(?i)" + "\\[album=(.*?)\\](.*?)\\[/album\\]", "$2");
            if (stemp.startsWith("<br/>")) {
                stemp = "[img]" + stemp.substring(5) + "[/img]";
            }
            stitle = "相册列表:" + stitle + "<br/>";
            stemp = stemp.replaceAll("<br/>", "[/img]<br/><br/>[img]");
            stemp = "<br/>" + quotediv + stitle + "<br/>" + stemp + "</div>";
            s = sup + stemp + sdown;
            startpos = s.indexOf("[album=");
            endpos = s.indexOf("[/album]") + 8;
        }
        return s;
    }

    public static String decodeForumTag(String s, boolean showImage,
                                        int imageQuality, HashSet<String> imageURLSet) {
        if (StringUtil.isEmpty(s))
            return "";
        // s = StringUtil.unEscapeHtml(s);
        String quoteStyle = "<div style='background:#E8E8E8;border:1px solid #888' >";
        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT)
            quoteStyle = "<div style='background:#000000;border:1px solid #888' >";

        final String styleLeft = "<div style='float:left' >";
        final String styleRight = "<div style='float:right' >";
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
                "冻", "谢", "哭", "响指", "转身"};
        final String penguinAppAdd[] = {"pg01.png", "pg02.png", "pg03.png", "pg04.png",
                "pg05.png", "pg06.png", "pg07.png", "pg08.png", "pg09.png",
                "pg10.png", "pg11.png", "pg12.png", "pg13.png", "pg14.png",
                "pg15.png"};
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
        s = decodealbum(s, quoteStyle);
        s = s.replaceAll(ignoreCaseTag + "&amp;", "&");
        s = s.replaceAll(ignoreCaseTag + "\\[l\\]", styleLeft);
        s = s.replaceAll(ignoreCaseTag + "\\[/l\\]", endDiv);
        // s = s.replaceAll("\\[L\\]", styleLeft);
        // s = s.replaceAll("\\[/L\\]", endDiv);

        s = s.replaceAll(ignoreCaseTag + "\\[r\\]", styleRight);
        s = s.replaceAll(ignoreCaseTag + "\\[/r\\]", endDiv);
        // s = s.replaceAll("\\[R\\]", styleRight);
        // s = s.replaceAll("\\[/R\\]", endDiv);

        s = s.replaceAll(ignoreCaseTag + "\\[align=right\\]", styleAlignRight);
        s = s.replaceAll(ignoreCaseTag + "\\[align=left\\]", styleAlignLeft);
        s = s.replaceAll(ignoreCaseTag + "\\[align=center\\]", styleAlignCenter);
        s = s.replaceAll(ignoreCaseTag + "\\[/align\\]", endDiv);

        s = s.replaceAll(
                ignoreCaseTag
                        + "\\[b\\]Reply to \\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\] (.+?)\\[/b\\]",
                "[quote]Reply to [b]<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[Reply]</a> $4[/b][/quote]");

        s = s.replaceAll(
                ignoreCaseTag + "\\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[Reply]</a>");

        // 某些帖子会导致这个方法卡住, 暂时不清楚原因, 和这个方法的作用.... by elrond
        /*s = s.replaceAll(
                ignoreCaseTag + "={3,}((^=){0,}(.*?){0,}(^=){0,})={3,}",
                "<h4 style='font-weight: bold;border-bottom: 1px solid #AAA;clear: both;margin-bottom: 0px;'>$1</h4>");*/

        s = s.replaceAll(ignoreCaseTag + "\\[quote\\]", quoteStyle);
        s = s.replaceAll(ignoreCaseTag + "\\[/quote\\]", endDiv);

        s = s.replaceAll(ignoreCaseTag + "\\[code\\]", quoteStyle + "Code:");
        s = s.replaceAll(ignoreCaseTag + "\\[code(.+?)\\]", quoteStyle);
        s = s.replaceAll(ignoreCaseTag + "\\[/code\\]", endDiv);
        // reply
        // s = s.replaceAll(
        // ignoreCaseTag +"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
        // s = s.replaceAll(
        // ignoreCaseTag +"\\[pid=\\d+,\\d+,\\d\\]Reply\\[/pid\\]", "Reply");

        // topic
        s = s.replaceAll(ignoreCaseTag + "\\[tid=\\d+\\]Topic\\[/pid\\]",
                "Topic");
        s = s.replaceAll(ignoreCaseTag + "\\[tid=?(\\d{0,50})\\]Topic\\[/tid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;'>[Topic]</a>");
        // reply
        // s =
        // s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
        // , "Reply to Reply <b>$1</b>");
        // 转换 tag
        // [b]
        s = s.replaceAll(ignoreCaseTag + "\\[b\\]", "<b>");
        s = s.replaceAll(ignoreCaseTag + "\\[/b\\]", "</b>"/* "</font>" */);

        // item
        s = s.replaceAll(ignoreCaseTag + "\\[item\\]", "<b>");
        s = s.replaceAll(ignoreCaseTag + "\\[/item\\]", "</b>");

        s = s.replaceAll(ignoreCaseTag + "\\[u\\]", "<u>");
        s = s.replaceAll(ignoreCaseTag + "\\[/u\\]", "</u>");

        s = s.replaceAll(ignoreCaseTag + "\\[s:(\\d+)\\]",
                "<img src='file:///android_asset/a$1.gif'>");
        for (int i = 0; i < 45; i++) {
            s = s.replaceAll(ignoreCaseTag + "\\[s:ac:" + acniangofubbcode[i]
                    + "\\]", "<img src='file:///android_asset/acniang/"
                    + acniangappadd[i] + "'>");
        }
        for (int i = 0; i < 46; i++) {
            s = s.replaceAll(ignoreCaseTag + "\\[s:a2:" + newacniangofubbcode[i]
                    + "\\]", "<img src='file:///android_asset/newacniang/"
                    + newacniangappadd[i] + "'>");
        }
        for (int i = 0; i < penguinOfUBBCode.length; i++) {
            s = s.replaceAll(ignoreCaseTag + "\\[s:pg:" + penguinOfUBBCode[i]
                    + "\\]", "<img src='file:///android_asset/pg/"
                    + penguinAppAdd[i] + "'>");
        }
        for (int i = 0; i < 65; i++) {
            s = s.replaceAll(ignoreCaseTag + "\\[s:pst:" + pstofubbcode[i]
                    + "\\]", "<img src='file:///android_asset/pst/"
                    + pstappadd[i] + "'>");
        }
        for (int i = 0; i < 33; i++) {
            s = s.replaceAll(ignoreCaseTag + "\\[s:dt:" + dtofubbcode[i]
                    + "\\]", "<img src='file:///android_asset/dt/"
                    + dtappadd[i] + "'>");
        }
        s = s.replace(ignoreCaseTag + "<br/><br/>", "<br/>");
        // [url][/url]
        s = s.replaceAll(
                ignoreCaseTag + "\\[url\\]([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"$1\">$1</a>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[url=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"$1\">$2</a>");
        s = s.replaceAll(ignoreCaseTag
                + "\\[uid=?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        s = s.replaceAll(
                ignoreCaseTag + "Post by\\s{0,}([^\\[\\s]{1,})\\s{0,}\\(",
                "Post by <a href='" + Utils.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;'>[$1]</a> (");
        s = s.replaceAll(
                ignoreCaseTag + "\\[@(.{2,20}?)\\]",
                "<a href='" + Utils.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;'>[@$1]</a>");
        s = s.replaceAll(ignoreCaseTag
                + "\\[uid=-?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[hip\\](.+?)\\[\\/hip\\]",
                "$1");
        s = s.replaceAll(ignoreCaseTag + "\\[tid=?(\\d{0,50})\\](.+?)\\[/tid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;'>[$2]</a>");
        s = s.replaceAll(
                ignoreCaseTag
                        + "\\[pid=(.+?)\\]\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[Reply]</a>");
        s = s.replaceAll(
                ignoreCaseTag
                        + "\\[pid=(.+?)\\](.+?)\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[$2]</a>");
        // flash
        s = s.replaceAll(
                ignoreCaseTag + "\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
                "<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");
        // color

        // s = s.replaceAll("\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
        // ,"<b style=\"color:$1\">$2</b>");
        s = s.replaceAll(ignoreCaseTag + "\\[color=([^\\[|\\]]+)\\]",
                styleColor);
        s = s.replaceAll(ignoreCaseTag + "\\[/color\\]", "</span>");

        // lessernuke
        s = s.replaceAll("\\[lessernuke\\]", lesserNukeStyle);
        s = s.replaceAll("\\[/lessernuke\\]", endDiv);

        s = s.replaceAll(
                "\\[table\\]",
                "<div><table cellspacing='0px' style='border:1px solid #aaa;width:99.9%;'><tbody>");
        s = s.replaceAll("\\[/table\\]", "</tbody></table></div>");
        s = s.replaceAll("\\[tr\\]", "<tr>");
        s = s.replaceAll("\\[/tr\\]", "<tr>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td(\\d+)\\]",
                "<td style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' rowspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td colspan='$1' rowspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' colspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$1' colspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$3' colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$2' colspan='$3'  style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");


        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan=(\\d+)\\]",
                "<td colspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan=(\\d+)\\]",
                "<td rowspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        s = s.replaceAll("\\[td\\]", "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        s = s.replaceAll("\\[/td\\]", "<td>");
        // [i][/i]
        s = s.replaceAll(ignoreCaseTag + "\\[i\\]",
                "<i style=\"font-style:italic\">");
        s = s.replaceAll(ignoreCaseTag + "\\[/i\\]", "</i>");
        // [del][/del]
        s = s.replaceAll(ignoreCaseTag + "\\[del\\]", "<del class=\"gray\">");
        s = s.replaceAll(ignoreCaseTag + "\\[/del\\]", "</del>");

        s = s.replaceAll(ignoreCaseTag + "\\[font=([^\\[|\\]]+)\\]",
                "<span style=\"font-family:$1\">");
        s = s.replaceAll(ignoreCaseTag + "\\[/font\\]", "</span>");

        // collapse
        s = s.replaceAll(ignoreCaseTag
                        + "\\[collapse([^\\[|\\]])*\\](([\\d|\\D])+?)\\[/collapse\\]",
                collapseStart + "$2" + endDiv);

        // size
        s = s.replaceAll(ignoreCaseTag + "\\[size=(\\d+)%\\]",
                "<span style=\"font-size:$1%;line-height:$1%\">");
        s = s.replaceAll(ignoreCaseTag + "\\[/size\\]", "</span>");

        // [img]./ddd.jpg[/img]
        // if(showImage){
        s = s.replaceAll(ignoreCaseTag
                        + "\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]",
                "<a href='http://" + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments$1'><img src='http://"
                        + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments$1' style= 'max-width:100%' ></a>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]",
                "<a href='$1'><img src='$1' style= 'max-width:100%' ></a>");

        s = s.replaceAll(ignoreCaseTag
                        + "\\[list\\](.+?)\\[/list\\]",
                "<ul>$1</ul>");
        s = s.replaceAll(ignoreCaseTag
                        + "\\[\\*\\](.+?)<br/>",
                "<li>$1</li>");

        Pattern p = Pattern
                .compile("<img src='(http\\S+)' style= 'max-width:100%' >");
        Matcher m = p.matcher(s);
        try {
            while (m.find()) {
                String s0 = m.group();
                String s1 = m.group(1);
                String path = ExtensionEmotionAdapter.getPathByURI(s1);
                if (path != null) {

                    String newImgBlock = "<img src='"
                            + "file:///android_asset/" + path
                            + "' style= 'max-width:100%' >";
                    s = s.replace(s0, newImgBlock);
                } else if (!showImage) {
                    path = "ic_offline_image.png";
                    String newImgBlock = "<img src='"
                            + "file:///android_asset/" + path
                            + "' style= 'max-width:100%' >";
                    s = s.replace(s0, newImgBlock);
                } else {

                    String newImgBlock = "<img src='"
                            + buildOptimizedImageURL(s1, imageQuality)
                            + "' style= 'max-width:100%' >";
                    s = s.replace(s0, newImgBlock);
                    int t = s1.indexOf(HttpUtil.NGA_ATTACHMENT_HOST);
                    if (t != -1 && imageURLSet != null) {
                        imageURLSet.add(s1.substring(t
                                + HttpUtil.NGA_ATTACHMENT_HOST.length() + 13)); // this
                        // is
                        // the
                        // length
                        // from
                        // HOST/attachments/^
                    }
                }
            }
        } catch (Exception e) {
        }
        return s;
    }

    public static String buildOptimizedImageURL(String url, int imageQuality) {
        String encodedURL = null;
        try {
            encodedURL = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
        String r = url;
        switch (imageQuality) {
            case 1:
                r = "http://ngac.sinaapp.com/imgapi/getimg.php?url=" + encodedURL
                        + "&size=small";
                break;
            case 2:
                r = "http://ngac.sinaapp.com/imgapi/getimg.php?url=" + encodedURL
                        + "&size=medium";
                break;
            case 3:
                r = "http://ngac.sinaapp.com/imgapi/getimg.php?url=" + encodedURL
                        + "&size=large";
                break;
        }
        return r;
    }

    public static String removeBrTag(String s) {
        s = s.replaceAll("<br/><br/>", "\n");
        s = s.replaceAll("<br/>", "\n");
        return s;
    }

    public static String getSaying() {
        Random random = new Random();
        int num = random.nextInt(SAYING.length);
        return SAYING[num];
    }

    public static String unEscapeHtml(String s) {
        String ret = "";
        ret = StringHelper.unescapeHTML(s);
        return ret;
    }

    public static String buildThreadURLByTid(String tid) {
        return "/read.php?tid=" + tid;
    }

    public static int getNowPageNum(String link) {
        // link: http://bbs.ngacn.cc/thread.php?fid=7&page=1&rss=1&OOXX=
        int ret = 1;
        if (link.indexOf("\n") != -1) {
            link = link.substring(0, link.length() - 1);
        }
        if (link.indexOf("&") == -1) {
            return ret;
        } else {
            try {
                ret = Integer.parseInt(link.substring(
                        link.indexOf("page=") + 5, link.length()));
            } catch (Exception E) {

            }
        }
        return ret;
    }

    public static String getTips() {
        return tips;
    }

    public static StringFindResult getStringBetween(String data,
                                                    int begPosition, String startStr, String endStr) {
        StringFindResult ret = new StringFindResult();
        do {
            if (isEmpty(data) || begPosition < 0
                    || data.length() <= begPosition || isEmpty(startStr)
                    || isEmpty(startStr))
                break;

            int start = data.indexOf(startStr, begPosition);
            if (start == -1)
                break;

            start += startStr.length();
            int end = data.indexOf(endStr, start);
            if (end == -1)
                end = data.length();
            ret.result = data.substring(start, end);
            ret.position = end + endStr.length();

        } while (false);

        return ret;
    }

    public static int getUrlParameter(String url, String paraName) {
        if (StringUtil.isEmpty(url)) {
            return 0;
        }
        final String pattern = paraName + "=";
        int start = url.indexOf(pattern);
        if (start == -1)
            return 0;
        start += pattern.length();
        int end = url.indexOf("&", start);
        if (end == -1)
            end = url.length();
        String value = url.substring(start, end);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            Log.e("getUrlParameter", "invalid url:" + url);
        }

        return ret;
    }


    public static Set<Integer> blackliststringtolisttohashset(String s) {
        if (StringUtil.isEmpty(s)) {
            Set<Integer> sset = new HashSet<Integer>();
            return sset;
        }
        if (s.startsWith("[") && s.endsWith("]") && s.length() > 2) {
            s = s.replace("[", "").replace("]", "").replace(" ", "");
            String[] sarray = s.split(",");
            List<String> sliststring = Arrays.asList(sarray);
            List<Integer> slistint = new ArrayList<Integer>(sliststring.size());
            for (String myString : sliststring) {
                slistint.add(Integer.parseInt(myString));
            }
            Set<Integer> sset = new HashSet<Integer>(slistint);
            return sset;
        } else {
            Set<Integer> sset = new HashSet<Integer>();
            return sset;
        }
    }

    public static String TimeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
        return date;
    }

    /* 乐视解析用 */
    public static String letu_(long s) {
        long key = 773625421;
        long value = let_(s, key % 13);
        value = value ^ key;
        value = let_(value, key % 17);
        return String.valueOf(value);
    }

    public static long let_(long value, long key) {
        long i = 0l;
        while (i < key) {
            System.out.println((value & 1) << 31);
            value = 2147483647 & value >> 1 | -((value & 1) << 31);
            i++;
        }
        return value;
    }
}

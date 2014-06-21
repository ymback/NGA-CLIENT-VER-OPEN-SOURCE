package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.util.Log;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApp extends Application implements PerferenceConstant {
	final private static String TAG = MyApp.class.getSimpleName();
	public final static int version = 2027;
	private PhoneConfiguration config = null;
	boolean newVersion = false;
	static final String RECENT = "�������";
	static final String ADDFID = "�û��Զ���";
	public static final int fddicon[][] = {};

	@Override
	public void onCreate() {
		Log.w(TAG, "app nga androind start");
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		if (config == null)
			config = PhoneConfiguration.getInstance();
		initUserInfo();
		loadConfig();
		if (ActivityUtil.isGreaterThan_2_1())
			initPath();
		initImageLoader();
		if (config.iconmode == true) {// laotubiao
			loadDefaultBoardOld();
		} else {
			loadDefaultBoard();
		}
		super.onCreate();
	}

	public void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading)
				.showImageForEmptyUri(R.drawable.loading_null)
				.showImageOnFail(R.drawable.loading_fail).cacheInMemory(true)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();
		int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass() / 3;
		if (ActivityUtil.isGreaterThan_2_1()) {
			File baseDir = getExternalCacheDir();
			File sdCardDir;
			if (baseDir != null)
				sdCardDir = new File(baseDir.getAbsolutePath()
						+ "/dbmeizi_cache");
			else
				sdCardDir = new File(
						android.os.Environment.getExternalStorageDirectory()
								.getPath()
								+ "/Android/data/gov.anzong.androidnga/cache/dbmeizi_cache");
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getApplicationContext())
					.denyCacheImageMultipleSizesInMemory()
					.defaultDisplayImageOptions(options)
					.memoryCache(new LruMemoryCache(MEM_CACHE_SIZE))
					.memoryCacheSize(MEM_CACHE_SIZE)
					.discCache(new UnlimitedDiscCache(sdCardDir)).build();
			ImageLoader.getInstance().init(config);
		} else {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getApplicationContext())
					.denyCacheImageMultipleSizesInMemory()
					.defaultDisplayImageOptions(options)
					.memoryCache(new LruMemoryCache(MEM_CACHE_SIZE))
					.memoryCacheSize(MEM_CACHE_SIZE).writeDebugLogs().build();
			ImageLoader.getInstance().init(config);
		}
	}


	public BoardHolder loadDefaultBoardOld() {

		BoardHolder boards = new BoardHolder();

		int i = 0;

		SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
		String recentStr = share.getString(RECENT_BOARD, "");
		List<Board> recentList = null;
		if (!StringUtil.isEmpty(recentStr)) {
			recentList = JSON.parseArray(recentStr, Board.class);
			if (recentList != null) {
				for (int j = 0; j < recentList.size(); j++) {
					if(recentList.get(j).getIcon()==R.drawable.pdefault){
						boards.add(new Board(i,recentList.get(j).getUrl(),
								recentList.get(j).getName(), R.drawable.oldpdefault));
					}else{
						boards.add(recentList.get(j));
					}
				}
			}
		}
		if (recentList != null) {
			boards.addCategoryName(i, RECENT);
			i++;
		}

		boards.add(new Board(i, "7", "������", R.drawable.oldp7));
		boards.add(new Board(i, "323", "������������", R.drawable.oldp323));
		boards.add(new Board(i, "10", "��ɫ����", R.drawable.oldp10));
		boards.add(new Board(i, "230", "������˹���ίԱ��", R.drawable.oldp230));
		boards.add(new Board(i, "387", "�˴�����֮����", R.drawable.oldp387));
		boards.add(new Board(i, "430", "����ŵ֮��", R.drawable.oldp430));
		boards.add(new Board(i, "305", "305Ȩ����", R.drawable.oldpdefault));
		boards.add(new Board(i, "11", "ŵɭ����ǵ�", R.drawable.oldpdefault));
		boards.addCategoryName(i, "�ۺ�����");
		i++;

		boards.add(new Board(i, "-7", "������", R.drawable.oldp354));
		boards.add(new Board(i, "-343809", "�������ֲ�", R.drawable.oldpdefault));
		boards.add(new Board(i, "-81981", "����֮��", R.drawable.oldpdefault));
		boards.add(new Board(i, "-576177", "Ӱ��������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-43", "������ʷ", R.drawable.oldpdefault));
		boards.add(new Board(i, "414", "��Ϸ�ۺ�����", R.drawable.oldp414));
		boards.add(new Board(i, "415", "������Ϸ�ۺ�����", R.drawable.oldpdefault));
		boards.add(new Board(i, "427", "��������", R.drawable.oldp427));
		boards.add(new Board(i, "431", "�籩Ӣ��", R.drawable.oldp431));
		boards.add(new Board(i, "436", "���ѵ��� IT����", R.drawable.oldpdefault));
		boards.add(new Board(i, "-187579", "��������ʷ�����", R.drawable.oldpdefault));
		boards.addCategoryName(i, "������ϵ��");
		i++;

		boards.add(new Board(i, "390", "�峿��", R.drawable.oldp390));
		boards.add(new Board(i, "320", "�ڷ�Ҫ��", R.drawable.oldp320));
		boards.add(new Board(i, "181", "��Ѫɳ��", R.drawable.oldp181));
		boards.add(new Board(i, "182", "ħ��ʥ��", R.drawable.oldp182));
		boards.add(new Board(i, "183", "�������", R.drawable.oldp183));
		boards.add(new Board(i, "185", "�籩��̳", R.drawable.oldp185));
		boards.add(new Board(i, "186", "����ξ�", R.drawable.oldp186));
		boards.add(new Board(i, "187", "���ִ���", R.drawable.oldp187));
		boards.add(new Board(i, "184", "ʥ��֮��", R.drawable.oldp184));
		boards.add(new Board(i, "188", "��ħ��Ԩ", R.drawable.oldp188));
		boards.add(new Board(i, "189", "��Ӱ�ѿ�", R.drawable.oldp189));
		boards.addCategoryName(i, "ְҵ������");
		i++;

		boards.add(new Board(i, "310", "��Ӣ���", R.drawable.oldp310));
		boards.add(new Board(i, "190", "��������", R.drawable.oldp190));
		boards.add(new Board(i, "213", "ս������", R.drawable.oldp213));
		boards.add(new Board(i, "218", "����ר��", R.drawable.oldp218));
		boards.add(new Board(i, "258", "ս������", R.drawable.oldp258));
		boards.add(new Board(i, "272", "������", R.drawable.oldp272));
		boards.add(new Board(i, "191", "�ؾ��̻�", R.drawable.oldp191));
		boards.add(new Board(i, "200", "����о�", R.drawable.oldp200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.oldp240));
		boards.add(new Board(i, "274", "�������", R.drawable.oldp274));
		boards.add(new Board(i, "315", "ս��ͳ��", R.drawable.oldp315));
		boards.add(new Board(i, "333", "DKPϵͳ", R.drawable.oldp333));
		boards.add(new Board(i, "327", "�ɾ�����", R.drawable.oldp327));
		boards.add(new Board(i, "388", "�û�����", R.drawable.oldp388));
		boards.add(new Board(i, "411", "��������", R.drawable.oldp411));
		boards.add(new Board(i, "255", "�������", R.drawable.oldp10));
		boards.add(new Board(i, "306", "��Ա��ļ", R.drawable.oldp10));
		boards.addCategoryName(i, "ð���ĵ�");
		i++;

		boards.add(new Board(i, "264", "�����޾�Ժ", R.drawable.oldp264));
		boards.add(new Board(i, "8", "��ͼ���", R.drawable.oldp8));
		boards.add(new Board(i, "102", "����Э��", R.drawable.oldp102));
		boards.add(new Board(i, "124", "�ڻ�����", R.drawable.oldpdefault));
		boards.add(new Board(i, "254", "���õ��", R.drawable.oldp254));
		boards.add(new Board(i, "355", "�����ֵܻ�", R.drawable.oldp355));
		boards.add(new Board(i, "116", "�漣֮Ȫ", R.drawable.oldp116));
		boards.addCategoryName(i, "�����֮��");
		i++;

		boards.add(new Board(i, "193", "�ʺŰ�ȫ", R.drawable.oldp193));
		boards.add(new Board(i, "334", "PC��Ӳ��", R.drawable.oldp334));
		boards.add(new Board(i, "201", "ϵͳ����", R.drawable.oldp201));
		boards.add(new Board(i, "335", "��վ����", R.drawable.oldp335));
		boards.addCategoryName(i, "ϵͳ��Ӳ������");
		i++;

		boards.add(new Board(i, "414", "��Ϸ�ۺ�����", R.drawable.oldp414));
		boards.add(new Board(i, "428", "�ֻ���Ϸ", R.drawable.oldp428));
		boards.add(new Board(i, "431", "�籩Ӣ��", R.drawable.oldp431));
		boards.add(new Board(i, "-452227", "�ڴ�����", R.drawable.oldpdefault));
		boards.add(new Board(i, "426", "�����Գ�", R.drawable.oldp426));
		boards.add(new Board(i, "-51095", "����ս��", R.drawable.oldpdefault));
		boards.add(new Board(i, "-362960", "���ջ���14", R.drawable.oldp362960));
		boards.add(new Board(i, "-6194253", "ս������", R.drawable.oldp6194253));
		boards.add(new Board(i, "427", "��������", R.drawable.oldp427));
		boards.add(new Board(i, "-47218", "���³�����ʿ", R.drawable.oldpdefault));
		boards.add(new Board(i, "425", "���Ǳ߼�2", R.drawable.oldp425));
		boards.add(new Board(i, "422", "¯ʯ��˵", R.drawable.oldp422));
		boards.add(new Board(i, "-65653", "����", R.drawable.oldp65653));
		boards.add(new Board(i, "412", "��ʦ֮ŭ", R.drawable.oldp412));
		boards.add(new Board(i, "-235147", "��ս2", R.drawable.oldp235147));
		boards.add(new Board(i, "442", "��ս", R.drawable.oldp442));
		boards.add(new Board(i, "-46468", "̹������", R.drawable.oldp46468));
		boards.add(new Board(i, "432", "ս������", R.drawable.oldp432));
		boards.add(new Board(i, "441", "ս������", R.drawable.oldpdefault));
		boards.add(new Board(i, "321", "DotA", R.drawable.oldp321));
		boards.add(new Board(i, "375", "DotA������Ʒ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-2371813", "EVE", R.drawable.oldp2371813));
		boards.add(new Board(i, "-7861121", "���� ", R.drawable.oldp7861121));
		boards.add(new Board(i, "448", "����ͬ�� ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-793427", "��ս��", R.drawable.oldpdefault));
		boards.add(new Board(i, "332", "ս��40K", R.drawable.oldp332));
		boards.add(new Board(i, "416", "���֮��2", R.drawable.oldpdefault));
		boards.add(new Board(i, "406", "�Ǽ�����2", R.drawable.oldpdefault));
		boards.add(new Board(i, "420", "MT Online", R.drawable.oldp420));
		boards.add(new Board(i, "424", "ʥ��ʿ��ʸ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-1513130", "��Ѫ�ֵܻ�", R.drawable.oldpdefault));
		boards.add(new Board(i, "433", "�������", R.drawable.oldpdefault));
		boards.add(new Board(i, "434", "������", R.drawable.oldpdefault));
		boards.add(new Board(i, "435", "�Ϲž���Online", R.drawable.oldp435));
		boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.oldpdefault));
		boards.add(new Board(i, "444", "��������", R.drawable.oldp444));
		boards.add(new Board(i, "445", "��������", R.drawable.oldp445));
		boards.add(new Board(i, "447", "����ս��", R.drawable.oldpdefault));
		boards.add(new Board(i, "-532408", "����", R.drawable.oldpdefault));
		boards.add(new Board(i, "353", "Ŧ��˹Ӣ�۴�", R.drawable.oldpdefault));
		boards.add(new Board(i, "452", "�������µ�", R.drawable.oldpdefault));
		boards.addCategoryName(i, "������Ϸ");
		i++;

		boards.add(new Board(i, "318", "�����ƻ���3", R.drawable.oldp318));
		boards.add(new Board(i, "403", "����/��װ/����", R.drawable.oldp403));
		boards.add(new Board(i, "393", "����������������Ʒ", R.drawable.oldp393));
		boards.add(new Board(i, "400", "ְҵ������", R.drawable.oldp29));
		boards.add(new Board(i, "395", "Ұ����", R.drawable.oldp395));
		boards.add(new Board(i, "396", "��ħ��", R.drawable.oldp396));
		boards.add(new Board(i, "397", "��ɮ", R.drawable.oldp397));
		boards.add(new Board(i, "398", "��ҽ", R.drawable.oldp398));
		boards.add(new Board(i, "399", "ħ��ʦ", R.drawable.oldp399));
		boards.add(new Board(i, "446", "ʥ�̾�", R.drawable.oldpdefault));
		boards.addCategoryName(i, "�����ƻ���");
		i++;

		boards.add(new Board(i, "422", "¯ʯ��˵", R.drawable.oldp422));
		boards.add(new Board(i, "429", "ս������", R.drawable.oldpdefault));
		boards.add(new Board(i, "450", "���´浵", R.drawable.oldpdefault));
		boards.addCategoryName(i, "¯ʯ��˵");
		i++;

		boards.add(new Board(i, "-152678", "Ӣ������", R.drawable.oldp152678));
		boards.add(new Board(i, "418", "��Ϸ��Ƶ", R.drawable.oldpdefault));
		boards.addCategoryName(i, "Ӣ������");
		i++;

		boards.add(new Board(i, "-447601", "����Ԫ���ҵ���", R.drawable.oldp447601));
		boards.add(new Board(i, "-84", "ģ��֮��", R.drawable.oldp84));
		boards.add(new Board(i, "-8725919", "С���ӽ�", R.drawable.oldp8725919));
		boards.add(new Board(i, "-965240", "����", R.drawable.oldpdefault));
		boards.add(new Board(i, "-131429", "���ݡ���С˵��", R.drawable.oldpdefault));
		boards.add(new Board(i, "-608808", "Ѫ�ȳ���", R.drawable.oldpdefault));
		boards.add(new Board(i, "-469608", "Ӱ~��~��", R.drawable.oldpdefault));
		boards.add(new Board(i, "-55912", "��������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-522474", "�ۺ�����������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-1068355", "����", R.drawable.oldpdefault));
		boards.add(new Board(i, "-168888", "������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-54214", "ʱ�а�", R.drawable.oldpdefault));
		boards.add(new Board(i, "-353371", "��������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-538800", "��Ů�����Ԫ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-7678526", "�齫��ѧԺ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-202020", "����Աְҵ����", R.drawable.oldpdefault));
		boards.add(new Board(i, "-444012", "���ǵ��Ｃ", R.drawable.oldpdefault));
		boards.add(new Board(i, "-349066", "���Ĳ�԰", R.drawable.oldpdefault));
		boards.add(new Board(i, "-314508", "���羡ͷ�İٻ���˾", R.drawable.oldpdefault));
		boards.add(new Board(i, "-2671", "������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-970841", "�����������Ƕ�", R.drawable.oldpdefault));
		boards.add(new Board(i, "-3355501", "������", R.drawable.oldpdefault));
		boards.add(new Board(i, "-403298", "Թ��ͼֽ�ղ���", R.drawable.oldpdefault));
		boards.add(new Board(i, "-3432136", "Ʈ���ʫ��", R.drawable.oldpdefault));
		boards.add(new Board(i, "-187628", "�Ҿ� װ��", R.drawable.oldpdefault));
		boards.addCategoryName(i, "���˰���");

		SharedPreferences shareaddFid = getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
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

		BoardHolder boards = new BoardHolder();

		int i = 0;

		SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
		String recentStr = share.getString(RECENT_BOARD, "");
		List<Board> recentList = null;
		if (!StringUtil.isEmpty(recentStr)) {
			recentList = JSON.parseArray(recentStr, Board.class);
			if (recentList != null) {
				for (int j = 0; j < recentList.size(); j++) {
					if(recentList.get(j).getIcon()==R.drawable.oldpdefault){
						boards.add(new Board(i,recentList.get(j).getUrl(),
								recentList.get(j).getName(), R.drawable.pdefault));
					}else{
						boards.add(recentList.get(j));
					}
				}
			}
		}
		if (recentList != null) {
			boards.addCategoryName(i, RECENT);
			i++;
		}

		boards.add(new Board(i, "7", "������", R.drawable.p7));
		boards.add(new Board(i, "323", "������������", R.drawable.p323));
		boards.add(new Board(i, "10", "��ɫ����", R.drawable.p10));
		boards.add(new Board(i, "230", "������˹���ίԱ��", R.drawable.p230));
		boards.add(new Board(i, "387", "�˴�����֮����", R.drawable.p387));
		boards.add(new Board(i, "430", "����ŵ֮��", R.drawable.p430));
		boards.add(new Board(i, "305", "305Ȩ����", R.drawable.p305));
		boards.add(new Board(i, "11", "ŵɭ����ǵ�", R.drawable.p11));
		boards.addCategoryName(i, "�ۺ�����");
		i++;

		boards.add(new Board(i, "-7", "������", R.drawable.p354));
		boards.add(new Board(i, "-343809", "�������ֲ�", R.drawable.p343809));
		boards.add(new Board(i, "-81981", "����֮��", R.drawable.p81981));
		boards.add(new Board(i, "-576177", "Ӱ��������", R.drawable.p576177));
		boards.add(new Board(i, "-43", "������ʷ", R.drawable.p43));
		boards.add(new Board(i, "414", "��Ϸ�ۺ�����", R.drawable.p414));
		boards.add(new Board(i, "415", "������Ϸ�ۺ�����", R.drawable.p415));
		boards.add(new Board(i, "427", "��������", R.drawable.p427));
		boards.add(new Board(i, "431", "�籩Ӣ��", R.drawable.p431));
		boards.add(new Board(i, "436", "���ѵ��� IT����", R.drawable.p436));
		boards.add(new Board(i, "-187579", "��������ʷ�����", R.drawable.p187579));
		boards.addCategoryName(i, "������ϵ��");
		i++;

		boards.add(new Board(i, "390", "�峿��", R.drawable.p390));
		boards.add(new Board(i, "320", "�ڷ�Ҫ��", R.drawable.p320));
		boards.add(new Board(i, "181", "��Ѫɳ��", R.drawable.p181));
		boards.add(new Board(i, "182", "ħ��ʥ��", R.drawable.p182));
		boards.add(new Board(i, "183", "�������", R.drawable.p183));
		boards.add(new Board(i, "185", "�籩��̳", R.drawable.p185));
		boards.add(new Board(i, "186", "����ξ�", R.drawable.p186));
		boards.add(new Board(i, "187", "���ִ���", R.drawable.p187));
		boards.add(new Board(i, "184", "ʥ��֮��", R.drawable.p184));
		boards.add(new Board(i, "188", "��ħ��Ԩ", R.drawable.p188));
		boards.add(new Board(i, "189", "��Ӱ�ѿ�", R.drawable.p189));
		boards.addCategoryName(i, "ְҵ������");
		i++;

		boards.add(new Board(i, "310", "��Ӣ���", R.drawable.p310));
		boards.add(new Board(i, "190", "��������", R.drawable.p190));
		boards.add(new Board(i, "213", "ս������", R.drawable.p213));
		boards.add(new Board(i, "218", "����ר��", R.drawable.p218));
		boards.add(new Board(i, "258", "ս������", R.drawable.p258));
		boards.add(new Board(i, "272", "������", R.drawable.p272));
		boards.add(new Board(i, "191", "�ؾ��̻�", R.drawable.p191));
		boards.add(new Board(i, "200", "����о�", R.drawable.p200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.p240));
		boards.add(new Board(i, "274", "�������", R.drawable.p274));
		boards.add(new Board(i, "315", "ս��ͳ��", R.drawable.p315));
		boards.add(new Board(i, "333", "DKPϵͳ", R.drawable.p333));
		boards.add(new Board(i, "327", "�ɾ�����", R.drawable.p327));
		boards.add(new Board(i, "388", "�û�����", R.drawable.p388));
		boards.add(new Board(i, "411", "��������", R.drawable.p411));
		boards.add(new Board(i, "255", "�������", R.drawable.p255));
		boards.add(new Board(i, "306", "��Ա��ļ", R.drawable.p306));
		boards.addCategoryName(i, "ð���ĵ�");
		i++;

		boards.add(new Board(i, "264", "�����޾�Ժ", R.drawable.p264));
		boards.add(new Board(i, "8", "��ͼ���", R.drawable.p8));
		boards.add(new Board(i, "102", "����Э��", R.drawable.p102));
		boards.add(new Board(i, "124", "�ڻ�����", R.drawable.p124));
		boards.add(new Board(i, "254", "���õ��", R.drawable.p254));
		boards.add(new Board(i, "355", "�����ֵܻ�", R.drawable.p355));
		boards.add(new Board(i, "116", "�漣֮Ȫ", R.drawable.p116));
		boards.addCategoryName(i, "�����֮��");
		i++;

		boards.add(new Board(i, "193", "�ʺŰ�ȫ", R.drawable.p193));
		boards.add(new Board(i, "334", "PC��Ӳ��", R.drawable.p334));
		boards.add(new Board(i, "201", "ϵͳ����", R.drawable.p201));
		boards.add(new Board(i, "335", "��վ����", R.drawable.p335));
		boards.addCategoryName(i, "ϵͳ��Ӳ������");
		i++;

		boards.add(new Board(i, "414", "��Ϸ�ۺ�����", R.drawable.p414));
		boards.add(new Board(i, "428", "�ֻ���Ϸ", R.drawable.p428));
		boards.add(new Board(i, "431", "�籩Ӣ��", R.drawable.p431));
		boards.add(new Board(i, "-452227", "�ڴ�����", R.drawable.p452227));
		boards.add(new Board(i, "426", "�����Գ�", R.drawable.p426));
		boards.add(new Board(i, "-51095", "����ս��", R.drawable.p51095));
		boards.add(new Board(i, "-362960", "���ջ���14", R.drawable.p362960));
		boards.add(new Board(i, "-6194253", "ս������", R.drawable.p6194253));
		boards.add(new Board(i, "427", "��������", R.drawable.p427));
		boards.add(new Board(i, "-47218", "���³�����ʿ", R.drawable.p47218));
		boards.add(new Board(i, "425", "���Ǳ߼�2", R.drawable.p425));
		boards.add(new Board(i, "422", "¯ʯ��˵", R.drawable.p422));
		boards.add(new Board(i, "-65653", "����", R.drawable.p65653));
		boards.add(new Board(i, "412", "��ʦ֮ŭ", R.drawable.p412));
		boards.add(new Board(i, "-235147", "��ս2", R.drawable.p235147));
		boards.add(new Board(i, "442", "��ս", R.drawable.p442));
		boards.add(new Board(i, "-46468", "̹������", R.drawable.p46468));
		boards.add(new Board(i, "432", "ս������", R.drawable.p432));
		boards.add(new Board(i, "441", "ս������", R.drawable.p441));
		boards.add(new Board(i, "321", "DotA", R.drawable.p321));
		boards.add(new Board(i, "375", "DotA������Ʒ", R.drawable.p375));
		boards.add(new Board(i, "-2371813", "EVE", R.drawable.p2371813));
		boards.add(new Board(i, "-7861121", "���� ", R.drawable.p7861121));
		boards.add(new Board(i, "448", "����ͬ�� ", R.drawable.p448));
		boards.add(new Board(i, "-793427", "��ս��", R.drawable.p793427));
		boards.add(new Board(i, "332", "ս��40K", R.drawable.p332));
		boards.add(new Board(i, "416", "���֮��2", R.drawable.p416));
		boards.add(new Board(i, "406", "�Ǽ�����2", R.drawable.p406));
		boards.add(new Board(i, "420", "MT Online", R.drawable.p420));
		boards.add(new Board(i, "424", "ʥ��ʿ��ʸ", R.drawable.p424));
		boards.add(new Board(i, "-1513130", "��Ѫ�ֵܻ�", R.drawable.p1513130));
		boards.add(new Board(i, "433", "�������", R.drawable.p433));
		boards.add(new Board(i, "434", "������", R.drawable.p434));
		boards.add(new Board(i, "435", "�Ϲž���Online", R.drawable.p435));
		boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.p443));
		boards.add(new Board(i, "444", "��������", R.drawable.p444));
		boards.add(new Board(i, "445", "��������", R.drawable.p445));
		boards.add(new Board(i, "447", "����ս��", R.drawable.p447));
		boards.add(new Board(i, "-532408", "����", R.drawable.p532408));
		boards.add(new Board(i, "353", "Ŧ��˹Ӣ�۴�", R.drawable.p353));
		boards.add(new Board(i, "452", "�������µ�", R.drawable.p452));
		boards.addCategoryName(i, "������Ϸ");
		i++;

		boards.add(new Board(i, "318", "�����ƻ���3", R.drawable.p318));
		boards.add(new Board(i, "403", "����/��װ/����", R.drawable.p403));
		boards.add(new Board(i, "393", "����������������Ʒ", R.drawable.p393));
		boards.add(new Board(i, "400", "ְҵ������", R.drawable.p400));
		boards.add(new Board(i, "395", "Ұ����", R.drawable.p395));
		boards.add(new Board(i, "396", "��ħ��", R.drawable.p396));
		boards.add(new Board(i, "397", "��ɮ", R.drawable.p397));
		boards.add(new Board(i, "398", "��ҽ", R.drawable.p398));
		boards.add(new Board(i, "399", "ħ��ʦ", R.drawable.p399));
		boards.add(new Board(i, "446", "ʥ�̾�", R.drawable.p446));
		boards.addCategoryName(i, "�����ƻ���");
		i++;

		boards.add(new Board(i, "422", "¯ʯ��˵", R.drawable.p422));
		boards.add(new Board(i, "429", "ս������", R.drawable.p429));
		boards.add(new Board(i, "450", "���´浵", R.drawable.p450));
		boards.addCategoryName(i, "¯ʯ��˵");
		i++;

		boards.add(new Board(i, "-152678", "Ӣ������", R.drawable.p152678));
		boards.add(new Board(i, "418", "��Ϸ��Ƶ", R.drawable.p418));
		boards.addCategoryName(i, "Ӣ������");
		i++;

		boards.add(new Board(i, "-447601", "����Ԫ���ҵ���", R.drawable.p447601));
		boards.add(new Board(i, "-84", "ģ��֮��", R.drawable.p84));
		boards.add(new Board(i, "-8725919", "С���ӽ�", R.drawable.p8725919));
		boards.add(new Board(i, "-965240", "����", R.drawable.p965240));
		boards.add(new Board(i, "-131429", "���ݡ���С˵��", R.drawable.p131429));
		boards.add(new Board(i, "-608808", "Ѫ�ȳ���", R.drawable.p608808));
		boards.add(new Board(i, "-469608", "Ӱ~��~��", R.drawable.p469608));
		boards.add(new Board(i, "-55912", "��������", R.drawable.p55912));
		boards.add(new Board(i, "-522474", "�ۺ�����������", R.drawable.p522474));
		boards.add(new Board(i, "-1068355", "����", R.drawable.p1068355));
		boards.add(new Board(i, "-168888", "������", R.drawable.p168888));
		boards.add(new Board(i, "-54214", "ʱ�а�", R.drawable.p54214));
		boards.add(new Board(i, "-353371", "��������", R.drawable.p353371));
		boards.add(new Board(i, "-538800", "��Ů�����Ԫ", R.drawable.p538800));
		boards.add(new Board(i, "-7678526", "�齫��ѧԺ", R.drawable.p7678526));
		boards.add(new Board(i, "-202020", "����Աְҵ����", R.drawable.p202020));
		boards.add(new Board(i, "-444012", "���ǵ��Ｃ", R.drawable.p444012));
		boards.add(new Board(i, "-349066", "���Ĳ�԰", R.drawable.p349066));
		boards.add(new Board(i, "-314508", "���羡ͷ�İٻ���˾", R.drawable.p314508));
		boards.add(new Board(i, "-2671", "������", R.drawable.p2671));
		boards.add(new Board(i, "-970841", "�����������Ƕ�", R.drawable.p970841));
		boards.add(new Board(i, "-3355501", "������", R.drawable.p3355501));
		boards.add(new Board(i, "-403298", "Թ��ͼֽ�ղ���", R.drawable.p403298));
		boards.add(new Board(i, "-3432136", "Ʈ���ʫ��", R.drawable.p3432136));
		boards.add(new Board(i, "-187628", "�Ҿ� װ��", R.drawable.p187628));
		boards.addCategoryName(i, "���˰���");
		// i++;

		SharedPreferences shareaddFid = getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
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
		if (!StringUtil.isEmpty(uid) && !StringUtil.isEmpty(cid)) {
			config.setUid(uid);
			config.setCid(cid);
			config.setReplyString(replystring);
			config.setReplyTotalNum(replytotalnum);
			String userListString = share.getString(USER_LIST, "");
			final String name = share.getString(USER_NAME, "");
			config.userName = name;
			if (StringUtil.isEmpty(userListString)) {

				addToUserList(uid, cid, name, replystring, replytotalnum);

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
			String replyString, int replytotalnum) {
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);

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
		userList.add(0, user);

		userListString = JSON.toJSONString(userList);
		share.edit().putString(UID, uid).putString(CID, cid)
				.putString(USER_NAME, name)
				.putString(PENDING_REPLYS, replyString)
				.putString(REPLYTOTALNUM, String.valueOf(replytotalnum))
				.putString(USER_LIST, userListString).commit();
	}

	public void addToMeiziUserList(String uid, String sess) {
		SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
		String cookie = "uid=" + uid + "; sess=" + sess;
		share.edit().putString(DBCOOKIE, cookie).commit();
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

			editor.commit();

		}

		// refresh
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		config.setRefreshAfterPost(share.getBoolean(REFRESH_AFTER_POST, false));
		config.setRefreshAfterPost(false);

		config.showAnimation = share.getBoolean(SHOW_ANIMATION, false);
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
		config.playMode = share.getInt(PLAY_MODE, 0);
		config.blackgunsound = share.getInt(BLACKGUN_SOUND, 0);
		config.iconmode = share.getBoolean(SHOW_ICON_MODE, false);

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

		int uiFlag = share.getInt(UI_FLAG, 0);
		config.setUiFlag(uiFlag);

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

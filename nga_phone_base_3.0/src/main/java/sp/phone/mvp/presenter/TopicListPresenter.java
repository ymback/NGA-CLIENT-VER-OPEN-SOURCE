package sp.phone.mvp.presenter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import gov.anzong.androidnga.base.util.PermissionUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.common.util.FileUtils;
import gov.anzong.androidnga.common.util.LogUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.mvp.contract.TopicListContract;
import sp.phone.mvp.model.BoardModel;
import sp.phone.mvp.model.TopicListModel;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.param.ParamKey;
import sp.phone.param.TopicListParam;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.ui.fragment.TopicCacheFragment;
import sp.phone.ui.fragment.TopicSearchFragment;
import sp.phone.util.ARouterUtils;

/**
 * @author Justwen
 * @date 2017/6/3
 */

public class TopicListPresenter extends BasePresenter<TopicSearchFragment, TopicListModel> implements TopicListContract.Presenter {

    // Following variables are for the 24 hour hot topic feature
    // How many pages we query for twenty four hour hot topic
    protected final int twentyFourPageCount = 5;
    // How many total topics we want to show
    protected final int twentyFourTopicCount = 50;
    protected int pageQueriedCounter = 0;
    protected int twentyFourCurPos = 0;
    protected TopicListInfo twentyFourList = new TopicListInfo();
    protected TopicListInfo twentyFourCurList = new TopicListInfo();

    private TopicListParam mRequestParam;

    private OnHttpCallBack<TopicListInfo> mCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.showToast(text);
                mBaseView.hideLoadingView();
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            mBaseView.clearData();
            mBaseView.scrollTo(0);
            setData(data);
            mBaseView.hideLoadingView();
        }
    };

    private OnHttpCallBack<TopicListInfo> mNextPageCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.setNextPageEnabled(false);
                mBaseView.showToast(text);
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            setData(data);
        }
    };

    /* callback for the twenty four hour hot topic list */
    private OnHttpCallBack<TopicListInfo> mTwentyFourCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.setNextPageEnabled(false);
                mBaseView.showToast(text);
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            /* Concatenate the pages */
            twentyFourList.getThreadPageList().addAll(data.getThreadPageList());
            pageQueriedCounter++;

            if (pageQueriedCounter == twentyFourPageCount) {
                twentyFourCurPos = 0;
                List<ThreadPageInfo> threadPageList = twentyFourList.getThreadPageList();
                if (DeviceUtils.isGreaterEqual_7_0()) {
                    threadPageList.removeIf(item -> (data.curTime - item.getPostDate() > 24 * 60 * 60));
                } else {
                    final Iterator<ThreadPageInfo> each = threadPageList.iterator();
                    while (each.hasNext()) {
                        ThreadPageInfo item = each.next();
                        if (data.curTime - item.getPostDate() > 24 * 60 * 60) {
                            each.remove();
                        }
                    }
                }

                if (threadPageList.size() > twentyFourTopicCount) {
                    threadPageList.subList(twentyFourTopicCount, threadPageList.size());
                }
                Collections.sort(twentyFourList.getThreadPageList(), (o1, o2) -> Integer.compare(o2.getReplies(), o1.getReplies()));
                // We list 20 topics each time
                int endPos = twentyFourCurPos + 20 > twentyFourList.getThreadPageList().size() ?
                        twentyFourList.getThreadPageList().size() : (twentyFourCurPos + 20);
                twentyFourCurList.setThreadPageList(twentyFourList.getThreadPageList().subList(0, endPos));
                twentyFourCurPos = endPos;
                setData(twentyFourCurList);
                mBaseView.hideLoadingView();
            }
        }
    };

    public TopicListPresenter() {
    }

    public TopicListPresenter(TopicListParam requestParam) {
        mRequestParam = requestParam;
    }

    private void setData(TopicListInfo result) {
        mBaseView.setRefreshing(false);
        mBaseView.setData(result);
    }

    @Override
    protected TopicListModel onCreateModel() {
        return new TopicListModel();
    }

    @Override
    public void removeTopic(ThreadPageInfo info, final int position) {
        mBaseModel.removeTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                if (isAttached()) {
                    mBaseView.showToast(text);
                }
            }

            @Override
            public void onSuccess(String data) {
                if (isAttached()) {
                    mBaseView.showToast(data);
                    mBaseView.removeTopic(info);
                }
            }
        });
    }

    @Override
    public void removeCacheTopic(ThreadPageInfo info) {
        mBaseModel.removeCacheTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                if (isAttached()) {
                    ToastUtils.showToast("删除失败！");
                }
            }

            @Override
            public void onSuccess(String data) {
                if (isAttached()) {
                    ThreadUtils.runOnMainThread(() -> {
                        ToastUtils.showToast("删除成功！");
                        mBaseView.removeTopic(info);
                    });

                }
            }
        });

    }

    @Override
    public void loadPage(int page, TopicListParam requestInfo) {
        mBaseView.setRefreshing(true);
        if (requestInfo.twentyfour == 1) {
            // preload pages
            twentyFourList.getThreadPageList().clear();
            pageQueriedCounter = 0;
            mBaseView.clearData();
            mBaseView.scrollTo(0);
            mBaseModel.loadTwentyFourList(requestInfo, mTwentyFourCallBack, twentyFourPageCount);
        } else {
            mBaseModel.loadTopicList(page, requestInfo, mCallBack);
        }
    }

    @Override
    public void loadCachePage() {
        mBaseModel.loadCache(mCallBack);
    }

    @Override
    public void loadNextPage(int page, TopicListParam requestInfo) {
        mBaseView.setRefreshing(true);
        if (requestInfo.twentyfour == 1) {
            int endPos = twentyFourCurPos + 20 > twentyFourList.getThreadPageList().size() ?
                    twentyFourList.getThreadPageList().size() : (twentyFourCurPos + 20);
            twentyFourCurList.setThreadPageList(twentyFourList.getThreadPageList().subList(0, endPos));
            twentyFourCurPos = endPos;
            setData(twentyFourCurList);
        } else {
            mBaseModel.loadTopicList(page, requestInfo, mNextPageCallBack);
        }
    }

    @Override
    public boolean isBookmarkBoard(int fid, int stid) {
        return BoardModel.getInstance().isBookmark(fid, stid);
    }

    @Override
    public void addBookmarkBoard(int fid, int stid, String boardName) {
        BoardModel.getInstance().addBookmark(fid, stid, boardName);
    }

    @Override
    public void addBookmarkBoard(Board board) {
        BoardModel.getInstance().addBookmark(board);
    }

    @Override
    public void removeBookmarkBoard(int fid, int stid) {
        BoardModel.getInstance().removeBookmark(fid, stid);
    }

    @Override
    public void startArticleActivity(String tid, String title) {
        ARouterUtils.build(ARouterConstants.ACTIVITY_TOPIC_CONTENT)
                .withInt(ParamKey.KEY_TID, Integer.parseInt(tid))
                .withString(ParamKey.KEY_TITLE, title)
                .navigation(ContextUtils.getContext());
    }

    @Override
    public void onViewCreated() {
        if (mRequestParam != null && mRequestParam.loadCache) {
            loadCachePage();
        } else {
            loadPage(1, mRequestParam);
        }
    }

    @Override
    public void exportCacheTopic() {
        PermissionUtils.requestAsync(mBaseView, new BaseSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    String srcDir = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/";

                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String dateStr = dateFormat.format(new Date(System.currentTimeMillis()));
                    String destDir = Environment.getExternalStorageDirectory() + File.separator
                            + BuildConfig.APPLICATION_ID + File.separator + "cache/cache_" + dateStr + ".zip";

                    if (FileUtils.zipFiles(srcDir, destDir)) {
                        ToastUtils.success("导出成功至" + destDir);
                    } else {
                        ToastUtils.error("导出失败");
                    }
                } else {
                    ToastUtils.warn("无存储权限，无法导出！");
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void showFileChooser() {
        PermissionUtils.request(mBaseView, new BaseSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        mBaseView.startActivityForResult(intent, TopicCacheFragment.REQUEST_IMPORT_CACHE);
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.warn("系统不支持导入");
                    }
                } else {
                    ToastUtils.warn("无存储权限，无法导入！");
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @Override
    public void importCacheTopic(Uri uri) {
        Context context = ContextUtils.getContext();
        if (!checkCacheZipFile(context, uri)) {
            ToastUtils.error("选择非法文件");
            return;
        }
        ContentResolver cr = context.getContentResolver();
        String destDir = context.getFilesDir().getAbsolutePath();
        File tempZipFile = new File(destDir , "temp.zip");
        try {
            InputStream is = cr.openInputStream(uri);
            if (is == null) {
                return;
            }
            org.apache.commons.io.FileUtils.copyInputStreamToFile(is, tempZipFile);
            FileUtils.unzip(tempZipFile.getAbsolutePath(), destDir);
            loadCachePage();
            ToastUtils.success("导入成功！！");
        } catch (Exception e) {
            LogUtils.print(e);
        }
        tempZipFile.delete();
    }

    private boolean checkCacheZipFile(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();
        String contentType = cr.getType(uri);
        return contentType != null && contentType.contains("zip");
    }
}

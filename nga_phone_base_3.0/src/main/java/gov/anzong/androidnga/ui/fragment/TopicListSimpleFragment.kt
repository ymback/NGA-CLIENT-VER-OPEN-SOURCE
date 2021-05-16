package gov.anzong.androidnga.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gov.anzong.androidnga.R
import sp.phone.mvp.model.entity.ThreadPageInfo
import sp.phone.ui.adapter.BaseAppendableAdapter
import sp.phone.ui.adapter.ReplyListAdapter
import sp.phone.util.StringUtils

/**
 * 提供了带ActionBar主题列表Fragment
 */
class TopicListSimpleFragment() : TopicListBaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topic_list_simple, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    override fun createAdapter(): BaseAppendableAdapter<ThreadPageInfo, *> {
        return if (mRequestParam!!.searchPost > 0) {
            ReplyListAdapter(context)
        } else {
            super.createAdapter()
        }
    }

    private fun setTitle() {
        if (!StringUtils.isEmpty(mRequestParam!!.key)) {
            if (mRequestParam!!.content == 1) {
                if (!StringUtils.isEmpty(mRequestParam!!.fidGroup)) {
                    setTitle("搜索全站(包含正文):" + mRequestParam!!.key)
                } else {
                    setTitle("搜索(包含正文):" + mRequestParam!!.key)
                }
            } else {
                if (!StringUtils.isEmpty(mRequestParam!!.fidGroup)) {
                    setTitle("搜索全站:" + mRequestParam!!.key)
                } else {
                    setTitle("搜索:" + mRequestParam!!.key)
                }
            }
        } else if (!StringUtils.isEmpty(mRequestParam!!.author)) {
            if (mRequestParam!!.searchPost > 0) {
                val title = "搜索" + mRequestParam!!.author + "的回复"
                setTitle(title)
            } else {
                val title = "搜索" + mRequestParam!!.author + "的主题"
                setTitle(title)
            }
        } else if (mRequestParam!!.recommend == 1) {
            setTitle(mRequestParam!!.title + " - 精华区")
        } else if (mRequestParam!!.twentyfour == 1) {
            setTitle(mRequestParam!!.title + " - 24小时热帖")
        } else if (!TextUtils.isEmpty(mRequestParam!!.title)) {
            setTitle(mRequestParam!!.title)
        }
    }

}
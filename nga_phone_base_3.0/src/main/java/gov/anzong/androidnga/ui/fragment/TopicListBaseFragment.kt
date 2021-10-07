package gov.anzong.androidnga.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import gov.anzong.androidnga.R
import gov.anzong.androidnga.base.util.ToastUtils
import gov.anzong.androidnga.base.widget.DividerItemDecorationEx
import sp.phone.mvp.model.entity.ThreadPageInfo
import sp.phone.mvp.presenter.TopicListPresenter
import sp.phone.param.ParamKey
import sp.phone.param.TopicListParam
import sp.phone.ui.adapter.BaseAppendableAdapter
import sp.phone.ui.adapter.TopicListAdapter
import sp.phone.ui.fragment.TopicListFragment
import sp.phone.view.RecyclerViewEx

/**
 * 主题列表Base实现类，提供了列表解析、下拉刷新、上拉加载更多功能
 */
open class TopicListBaseFragment : BaseFragment(R.layout.fragment_topic_list_base), View.OnClickListener {

    protected lateinit var mRefreshLayout: SwipeRefreshLayout;

    protected lateinit var mListView: RecyclerViewEx;

    protected var mRequestParam: TopicListParam? = null

    protected lateinit var mPresenter: TopicListPresenter

    protected lateinit var mAdapter: BaseAppendableAdapter<ThreadPageInfo, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestParam = arguments!!.getParcelable(ParamKey.KEY_PARAM)
        mPresenter = onCreatePresenter()
        lifecycle.addObserver(mPresenter)
        initState()
    }

    private fun initState() {
        mPresenter.isRefreshing.observe(this, Observer {
            mRefreshLayout.isRefreshing = it
        })
        mPresenter.errorMsg.observe(this, Observer {
            ToastUtils.error(it)
        })
        mPresenter.firstTopicList.observe(this, Observer {
            if (it == null) {
                setData(null, false)
            } else{
                mListView.scrollToPosition(0)
                setData(it.threadPageList, false)
            }
        })

        mPresenter.nextTopicList.observe(this, Observer {
            it?.let { setData(it.threadPageList, true) }
        })
    }

    private fun setData(data: MutableList<ThreadPageInfo>?, append: Boolean) {
        if (!append) {
            mAdapter.clear()
        }
        mAdapter.setData(data)
        mRefreshLayout.isRefreshing = false
    }

    protected open fun onCreatePresenter(): TopicListPresenter {
        val viewModelProvider = ViewModelProvider(this)
        val topicListPresenter = viewModelProvider[TopicListPresenter::class.java]
        topicListPresenter.setRequestParam(mRequestParam)
        return topicListPresenter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRefreshLayout = view.findViewById(R.id.swipe_refresh)
        mRefreshLayout.setOnRefreshListener { mPresenter.loadPage(1, mRequestParam) }

        mAdapter = createAdapter()
        mAdapter.setOnClickListener(this)
        mListView = view.findViewById(R.id.list)
        mListView.layoutManager = LinearLayoutManager(context)
        mListView.adapter = mAdapter
        val padding = resources.getDimension(R.dimen.topic_list_item_padding)
        mListView.addItemDecoration(DividerItemDecorationEx(view.context, padding.toInt(), DividerItemDecoration.VERTICAL))
        mListView.setOnNextPageLoadListener {
            if (!mRefreshLayout.isRefreshing) {
                mPresenter.loadNextPage(mAdapter.nextPage, mRequestParam)
            }
        }
    }

    open fun createAdapter(): BaseAppendableAdapter<ThreadPageInfo, *> {
        return TopicListAdapter(context)
    }

    override fun onClick(v: View?) {
        TopicListFragment.handleClickEvent(context, v?.tag as ThreadPageInfo?, mRequestParam)
    }


}
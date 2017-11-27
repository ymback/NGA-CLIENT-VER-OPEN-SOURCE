package sp.phone.fragment.material;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.ParamKey;

/**
 * Created by Justwen on 2017/11/22.
 */

public class ArticleListReplyFragment extends ArticleListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.article_list_option_menu_reply,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_show_whole_thread) {
            Intent intentThis = new Intent();
            intentThis.putExtra("tab", "1");
            intentThis.putExtra("tid", mRequestParam.tid);
            intentThis.putExtra(ParamKey.KEY_TITLE,getActivity().getTitle());
            intentThis.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
            startActivity(intentThis);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;

public class PullToRefreshAttacher extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher {

    public static PullToRefreshAttacher get(Activity activity) {
        return get(activity, new Options());
    }

    public static PullToRefreshAttacher get(Activity activity, Options options) {
        return new PullToRefreshAttacher(activity, options);
    }

    protected PullToRefreshAttacher(Activity activity, Options options) {
        super(activity, options);
    }

    @Override
    protected EnvironmentDelegate createDefaultEnvironmentDelegate() {
        return new AbcEnvironmentDelegate();
    }

    @Override
    protected HeaderTransformer createDefaultHeaderTransformer() {
        return new AbcDefaultHeaderTransformer();
    }

    public static class AbcEnvironmentDelegate extends EnvironmentDelegate {
        /**
         * @return Context which should be used for inflating the header layout
         */
        public Context getContextForInflater(Activity activity) {
            if (activity instanceof ActionBarActivity) {
                return ((ActionBarActivity) activity).getSupportActionBar().getThemedContext();
            }
            return super.getContextForInflater(activity);
        }
    }
}

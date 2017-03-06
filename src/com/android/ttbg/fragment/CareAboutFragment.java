package com.android.ttbg.fragment;

import android.util.Log;
import android.view.View;

import com.android.ttbg.R;



/**
 * 作者：哇牛Aaron
 * 作者简书文章地址: http://www.jianshu.com/users/07a8b5386866/latest_articles
 * 时间: 2016/10/24
 * 功能描述: 关心页面Fragment
 */
public class CareAboutFragment extends BaseFragment {
    private static final String TAG = CareAboutFragment.class.getSimpleName();

    @Override
    protected View initView() {
        Log.e(TAG, "CareAboutFragment  initView...");
        return View.inflate(mContext, R.layout.fragment_care_about, null);
    }

    @Override
    protected void initData() {
        Log.e(TAG, "CareAboutFragment  initData...");
        super.initData();
    }

	@Override
	protected String getPageName() {
		return CareAboutFragment.class.getName();
	}
}

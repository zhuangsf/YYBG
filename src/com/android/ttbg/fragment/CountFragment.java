package com.android.ttbg.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.ttbg.*;
import com.android.ttbg.adapter.GoodsRecommendAdapter;
import com.android.ttbg.adapter.HotRecommendAdapter;
import com.android.ttbg.adapter.NewestGoodsAdapter;
import com.android.ttbg.util.Urls;
import com.android.ttbg.view.AddPopWindow;
import com.android.ttbg.view.GoodsProperty;
import com.android.ttbg.view.NoScroolGridView;
import com.android.ttbg.view.PullToRefreshLayout;



public class CountFragment extends BaseFragment {
    private static final String TAG = CountFragment.class.getSimpleName();
	private View CountFragment;
	private PullToRefreshLayout ptrl;
    @Override
    protected View initView() {
    	CountFragment = View.inflate(mContext, R.layout.fragment_account, null);
		if (CountFragment != null) {
			View account_record = (View)CountFragment.findViewById(R.id.account_record);  
			
			account_record.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), HistoryActivity.class);
						startActivity(intent);
					}
				});
			
			
		   View account_obtain_goods = (View)CountFragment.findViewById(R.id.account_obtain_goods);  
		   account_obtain_goods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ObtainGoodsActivity.class);
					startActivity(intent);
				}
			});
		   
		   View account_help = (View)CountFragment.findViewById(R.id.account_help);  
		   account_help.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), WebViewActivity.class);
			        intent.putExtra(Urls.URL_TITLE, "帮助与反馈");
			        intent.putExtra(Urls.URL_CONTENT, Urls.URL_HELP);
					startActivity(intent);
				}
			});
		   ImageView btn_account_message = (ImageView)CountFragment.findViewById(R.id.btn_account_message);  
		   btn_account_message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), MessageCenterActivity.class);
					startActivity(intent);
				}
			});
		   
		   ImageView btn_account_setting = (ImageView)CountFragment.findViewById(R.id.btn_account_setting);  
		   btn_account_setting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), SettingActivity.class);
					startActivity(intent);
				}
			});
		   
		   
		}
		return CountFragment;
    }



	@Override
    protected void initData() {
        Log.e(TAG, "CountFragment  initData...");
        super.initData();
    }

	@Override
	protected String getPageName() {
		return CountFragment.class.getName();
	}
}

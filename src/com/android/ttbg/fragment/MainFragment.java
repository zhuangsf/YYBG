package com.android.ttbg.fragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.android.ttbg.R;
import com.android.ttbg.SearchActivity;
import com.android.ttbg.adapter.GoodsRecommendAdapter;
import com.android.ttbg.json.JsonControl;
import com.android.ttbg.tools.AsyncImageLoader;
import com.android.ttbg.util.OperatingSP;
import com.android.ttbg.util.TimerUtil;
import com.android.ttbg.util.Utils;
import com.android.ttbg.view.AddPopWindow;
import com.android.ttbg.view.CountDownTimerTextView;
import com.android.ttbg.view.GoodsProperty;
import com.android.ttbg.view.NoScroolGridView;
import com.finddreams.adbanner.ImagePagerAdapter;
import com.finddreams.bannerview.CircleFlowIndicator;
import com.finddreams.bannerview.ViewFlow;

//todo


// 还需要写一个在当前界面打开网络的接口


public class MainFragment extends BaseFragment implements ViewFactory,OnClickListener{
    private static final String TAG = MainFragment.class.getSimpleName();
    private View mainFragmentView;
    private AsyncImageLoader imageLoader;
    
	private ViewFlow mViewFlow;     //广告页控件
	private CircleFlowIndicator mFlowIndicator;   //指示点点
	private ArrayList<String> imageUrlList = new ArrayList<String>();
	ArrayList<String> linkUrlArray= new ArrayList<String>();
    
	ArrayList<GoodsProperty> hashMapList = new ArrayList<GoodsProperty>();   //用于显示最新揭晓的数据
	private int currentShowNewestID = 0;    //用于指示当前刷到哪个最新揭晓的数据
	
	ArrayList<GoodsProperty> hashMapListRecommend = new ArrayList<GoodsProperty>();
	
    private ImageView count1_image;
    private ImageView count2_image;
    private ImageView count3_image;
    private ImageView count4_image;
    private ArrayList<ImageView> countImages = new ArrayList<ImageView>();
    private TextView count1_time;
    private TextView count2_time;
    private TextView count3_time;
    private TextView count4_time;
    private ArrayList<TextView> countTimes = new ArrayList<TextView>();
    private ArrayList<CountDownTimerTextView> countDownTimers = new ArrayList<CountDownTimerTextView>();
    
    private TextSwitcher switcher;
    
    private TextView tv_home_new_arrivals_title;
    private TextView tv_home_new_arrivals_content;
    private ImageView iv_home_new_arrivals_pic;
    private TextView tv_home_new_arrivals_title1;
    private TextView tv_home_new_arrivals_content1;
    private ImageView iv_home_new_arrivals_pic1;
    private TextView tv_home_new_arrivals_title2;
    private TextView tv_home_new_arrivals_content2;
    private ImageView iv_home_new_arrivals_pic2;
    private View home_new_arrivals;
    private View home_new_arrivals1;
    private View home_new_arrivals2;
    
    private View searchButton;
    private PopupMenu popupMenu;
    
    private static final int MSG_TEST_SWITCHER_TEST=0;
    private static final int MSG_JSON_TYPE_NEWEST_UPDATE=1;
    private ImageView add_menus;
    private ImageView iv_home_to_check_more;
    
    private GoodsRecommendAdapter goodsRecommendAdapter;
    
    private Handler mHandler= new Handler()
    {
   		@Override
   		public void handleMessage(Message msg) {
   			switch (msg.what) {
   			case MSG_TEST_SWITCHER_TEST:
   			{
   				GoodsProperty item;
   				
   				
   				if(hashMapList.size() == 0)
   				{
   					switcher.setVisibility(View.INVISIBLE);
   					
   	   				Message new_msg = new Message();
   	   				new_msg.what = MSG_TEST_SWITCHER_TEST;
   	   				mHandler.sendMessageDelayed(new_msg, 2000);
   					
   					return;
   				}
   				
   				switcher.setVisibility(View.VISIBLE);
   				if(currentShowNewestID >= hashMapList.size())
   		    	{
   					currentShowNewestID = 0;
   		    	}
   				item = hashMapList.get(currentShowNewestID);
   				if(hashMapList.size() > 0 && switcher != null)
   				{
   		            int fstart,fend;
   		            
   		            String hintText = "恭喜"+item.getUsername()+"获得"+item.getGoodsTitle();
   		            fstart="恭喜".length();  
   		            fend=("恭喜"+item.getUsername()).length(); 
   		            SpannableStringBuilder style=new SpannableStringBuilder(hintText);     
   		            style.setSpan(new ForegroundColorSpan(0xffaaaaaa),fstart,fend,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);   
   		            fstart = ("恭喜"+item.getUsername()+"获得").length();
   		            fend = hintText.length();
   		            style.setSpan(new ForegroundColorSpan(0xffaaaaaa),fstart,fend,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);   
   					switcher.setText(style);  
   					
   					currentShowNewestID ++;
   				}
   				

   				
   				Message new_msg = new Message();
   				new_msg.what = MSG_TEST_SWITCHER_TEST;
   				mHandler.sendMessageDelayed(new_msg, 2000);
   			}
   			break;
   			
       		case MSG_JSON_TYPE_NEWEST_UPDATE:
       		{
       			Utils.Log("MSG_JSON_TYPE_NEWEST_UPDATE ");
       			new Thread(runnableNewest).start();	
       		}
    			break;
   			case JsonControl.GET_SUCCESS_MSG:
   			{
            	JSONObject jsonObject=(JSONObject)msg.obj;
            	Utils.Log("GET_SUCCESS_MSG jsonObject:"+jsonObject+" msg.arg1 = "+msg.arg1);
            	if(jsonObject == null)
            	{
            		return;
            	}
        		JSONObject result;
        		switch(msg.arg1)
        		{
        		case JsonControl.JSON_TYPE_BANNER:
        		{
    				try {
    					result = new JSONObject(jsonObject.toString());
    	        		String bSuccess = result.optString("success","");
    	        		
    	        		Utils.Log("getJson JSON_TYPE_BANNER bSuccess "+bSuccess);
    	        		
    	        		
    	        		//这个变量实际没什么用,图片下载已经用本地缓存来判断,如果文件名改变,会自动下载,否则用缓存的图片
    	        		String lastTime = result.optString("lasttime","");
    	        		
    	        		Utils.Log("getJson lastTime "+lastTime);
    	        		JSONArray banners = result.getJSONArray("banners");
    	        		int len = banners.length();
    	        		
    	        		imageUrlList.clear();
    	        		linkUrlArray.clear();
    	        		
    	        		if(len == 0)
    	        		{
    	        			return;
    	        		}
    	        		
    	        		
    	        		for(int i =0;i<len;i++){
    	        		JSONObject obj = banners.getJSONObject(i);
    	        		
    	        		String title = obj.getString("title");
    	        		String link = obj.getString("link");
    	        		String img = obj.getString("img");
    	        		
    	        		imageUrlList.add(img);
    	        		linkUrlArray.add(link);
    	        		Utils.Log("getJson banners["+i+"].title = "+title);
    	        		Utils.Log("getJson banners["+i+"].link = "+link);
    	        		Utils.Log("getJson banners["+i+"].img = "+img);
    	      
    	        		}
    	        		
    	        		//放在这里,如果没网络,那么会显示为空
    	        		if(mViewFlow != null)
    	        		{
	    	        		mViewFlow.setAdapter(new ImagePagerAdapter(mContext, imageUrlList,
	    	        				linkUrlArray).setInfiniteLoop(true));
	    	        		mViewFlow.setmSideBuffer(imageUrlList.size()); // 实际图片张数，
	    	        		mViewFlow.startAutoFlowTimer(); // 启动自动播放
    	        		}
    				} catch (JSONException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
        		}
        			break;
        		case JsonControl.JSON_TYPE_NEWEST:
        		{
        			try {
        			result = new JSONObject(jsonObject.toString());
        			Utils.Log("JsonControl.JSON_TYPE_NEWEST  result = "+result);
        			
        			if(result.toString().contains("连接不成功"))
        			{
    	        		Message msg1 = new Message();
    	        		msg1.what = MSG_JSON_TYPE_NEWEST_UPDATE;
    	        		mHandler.sendMessageDelayed(msg1, 15000);  //15秒刷新一下数组
        				return;
        			}
        			
        			String bSuccess = result.optString("success","");
        			Utils.Log("getJson JSON_TYPE_NEWEST bSuccess = "+bSuccess);
        			
        			int count = Integer.parseInt(result.optString("count","0"));
        			Utils.Log("getJson JSON_TYPE_NEWEST count = "+count);

        			JSONArray shoplists = result.getJSONArray("shoplists");
	        		int len = shoplists.length();
	        		Utils.Log("getJson JSON_TYPE_NEWEST len = "+len);
	        		if(len == 0)
	        		{
	        			return;
	        		}
	        		
	        		hashMapList.clear();
	        		
	        		
	        		//后续点击,把这个goodsItem传进去,就可以用于直接显示,不用再重新读网络
	        		for(int i =0;i<len;i++){
	        			
    	        		JSONObject obj = shoplists.getJSONObject(i);
    	        		Utils.Log("getJson hashMapList["+i+"] = "+obj.toString());

    	        		String id = obj.getString("id");
    	        		String sid = obj.getString("sid");
    	        		String title = obj.getString("title");
    	        		String title2 = obj.getString("title2");
    	        		String qishu = obj.getString("qishu");
    	        		String money = obj.getString("money");    	        		
    	        		String yunjiage = obj.getString("yunjiage");
    	        		String thumb = obj.getString("thumb");
    	        		String q_end_time = obj.getString("q_end_time");
    	        		String q_uid = obj.getString("q_uid");
    	        		String username = obj.getString("username");
    	        		String userphoto = obj.getString("userphoto");
    	        		String q_buynum = obj.getString("q_buynum");
    	        		String q_user_code = obj.getString("q_user_code");
    	        		
    	        		GoodsProperty goodsItem = new GoodsProperty();
    	        		goodsItem.setId(id);
    	        		goodsItem.setSid(sid);
    	        		goodsItem.setTitle(title);
    	        		goodsItem.setTitle2(title2);
    	        		goodsItem.setQishu(qishu);
    	        		goodsItem.setMoney(money);
    	        		goodsItem.setYunjiage(yunjiage);
    	        		goodsItem.setThumb(thumb);
    	        		goodsItem.setQ_end_time(q_end_time);
    	        		goodsItem.setQ_uid(q_uid);
    	        		goodsItem.setUsername(username);
    	        		goodsItem.setUserphoto(userphoto);
    	        		goodsItem.setQ_buynum(q_buynum);
    	        		goodsItem.setQ_user_code(q_user_code);
    	                hashMapList.add(goodsItem);
    	      
    	        		}
	        		
	        		Utils.Log("hashMapList size = "+hashMapList.size());
	        		//重新读取数据,归零
	        		currentShowNewestID = 0; 
	            	
	        		Message msg1 = new Message();
	        		msg1.what = MSG_JSON_TYPE_NEWEST_UPDATE;
	        		mHandler.sendMessageDelayed(msg1, 15000);  //15秒刷新一下数组
	        		Utils.Log("start send sendMessageDelayed(msg1, 15000)");
	        		CountDownPageReflash();
        			} catch (JSONException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
        		}
        		break;
        		case JsonControl.JSON_TYPE_ARRIVALS:
        		{
        			try {
        			result = new JSONObject(jsonObject.toString());
        			Utils.Log("JsonControl.JSON_TYPE_ARRIVALS  result = "+result);
        			
        			if(result.toString().contains("连接不成功"))
        			{
        				return;
        			}
        			
        			String bSuccess = result.optString("success","");
        			Utils.Log("getJson JSON_TYPE_ARRIVALS bSuccess = "+bSuccess);

        			JSONArray shoplists = result.getJSONArray("shoplists");
	        		int len = shoplists.length();
	        		Utils.Log("getJson JSON_TYPE_ARRIVALS len = "+len);
	        		if(len <3)
	        		{
	        			//小于3,不对,返回
	        			return;
	        		}
	        		
	        		
	        		//后续点击,把这个goodsItem传进去,就可以用于直接显示,不用再重新读网络
	        		for(int i =0;i<len;i++){
	        			
    	        		JSONObject obj = shoplists.getJSONObject(i);
    	        		Utils.Log("getJson hashMapList["+i+"] = "+obj.toString());

    	        		String id = obj.getString("id");
    	        		String sid = obj.getString("sid");
    	        		String cateid = obj.getString("cateid");
    	        		String title = obj.getString("title");
    	        		String title2 = obj.getString("title2");
    	        		String qishu = obj.getString("qishu");
    	        		String money = obj.getString("money");    	        		
    	        		String yunjiage = obj.getString("yunjiage");
    	        		String thumb = obj.getString("thumb");
    	        		String brandid = obj.getString("brandid");
    	        		String brandname = obj.getString("brandname");
    	        		String zongrenshu = obj.getString("zongrenshu");
    	        		String canyurenshu = obj.getString("canyurenshu");
    	        		String shenyurenshu = obj.getString("shenyurenshu");
    	        		
    	        		if(i == 0)
    	        		{
    	        	         tv_home_new_arrivals_title.setText(title);
    	        	         tv_home_new_arrivals_content.setText(title2);
	        	    		if(imageLoader != null && iv_home_new_arrivals_pic != null)
	        	    		{
	        	    			imageLoader.downloadImage(JsonControl.FILE_HEAD+thumb, iv_home_new_arrivals_pic);
	        	    		}
    	        	         
    	        		}else if(i == 1)
    	        		{
   	        	         tv_home_new_arrivals_title1.setText(title);
   	        	         tv_home_new_arrivals_content1.setText(title2);
	        	    		if(imageLoader != null && iv_home_new_arrivals_pic1 != null)
	        	    		{
	        	    			imageLoader.downloadImage(JsonControl.FILE_HEAD+thumb, iv_home_new_arrivals_pic1);
	        	    		}
    	        		}else if(i == 2)
    	        		{
   	        	         tv_home_new_arrivals_title2.setText(title);
   	        	         tv_home_new_arrivals_content2.setText(title2);
	        	    		if(imageLoader != null && iv_home_new_arrivals_pic2 != null)
	        	    		{
	        	    			imageLoader.downloadImage(JsonControl.FILE_HEAD+thumb, iv_home_new_arrivals_pic2);
	        	    		}
    	        		}
    	        		
    	        		}
        			
        			
        			} catch (JSONException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
        		}
        		break;
        		case JsonControl.JSON_TYPE_RECOMMAND:
        		{
        			try {
            			result = new JSONObject(jsonObject.toString());
            			Utils.Log("JsonControl.JSON_TYPE_RECOMMAND  result = "+result);
            			
            			if(result.toString().contains("连接不成功"))
            			{
            				return;
            			}
            			
            			JSONArray shoplists = result.getJSONArray("shoplists");
    	        		int len = shoplists.length();
    	        		Utils.Log("getJson JSON_TYPE_RECOMMAND len = "+len);
    	        		
    	        		if(len == 0)
    	        		{
    	        			return;
    	        		}
    	        		hashMapListRecommend.clear();
    	        		for(int i =0;i<len;i++){
    	        			
        	        		JSONObject obj = shoplists.getJSONObject(i);
        	        		Utils.Log("getJson hashMapList["+i+"] = "+obj.toString());

        	        		String id = obj.getString("id");
        	        		String sid = obj.getString("sid");
        	        		String cateid = obj.getString("cateid");
        	        		String title = obj.getString("title");
        	        		String title2 = obj.getString("title2");
        	        		String qishu = obj.getString("qishu");
        	        		String money = obj.getString("money");    	        		
        	        		String yunjiage = obj.getString("yunjiage");
        	        		String thumb = obj.getString("thumb");
        	        		String brandid = obj.getString("brandid");
        	        		String brandname = obj.getString("brandname");
        	        		String zongrenshu = obj.getString("zongrenshu");
        	        		String canyurenshu = obj.getString("canyurenshu");
        	        		String shenyurenshu = obj.getString("shenyurenshu");
        	        		
    	                    GoodsProperty goodsRecommandItem = new GoodsProperty();
    	                    goodsRecommandItem.setGoodsItemUrl(mContext, title, JsonControl.FILE_HEAD+thumb, Integer.parseInt(canyurenshu), Integer.parseInt(zongrenshu),Integer.parseInt(shenyurenshu),"价值:¥ "+money);
    	                    hashMapListRecommend.add(goodsRecommandItem);
    	        		}
    	        		
    	        		goodsRecommendAdapter.setData(hashMapListRecommend);
    	        		goodsRecommendAdapter.notifyDataSetChanged();
    	        		
            			
        			}catch (JSONException e) {
         					// TODO Auto-generated catch block
         					e.printStackTrace();
         				}
        		}
        		break;
        		default:
        			break;
        		}


            	
   			}
   			break;
   		  }
   			
   		}
   	};
   	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = new AsyncImageLoader(mContext);  
    }
   	
   	
    @Override
    protected View initView() {
        //Log.e(TAG, "首页页面Fragment页面被初始化了...");
        mainFragmentView = View.inflate(mContext, R.layout.fragment_main, null);
        
        if(mainFragmentView != null)
        {
        	initSearchView(mainFragmentView); 
        	initADPager(mainFragmentView);      //初始化页眉广告条
        	initNewestSwitcher(mainFragmentView);
        	initCountDownPage(mainFragmentView);
        	initNewArrivals(mainFragmentView);
        	initGoodsItems(mainFragmentView);

        }
        iv_home_to_check_more = (ImageView)mainFragmentView.findViewById(R.id.iv_home_to_check_more);
        return mainFragmentView;
    }

    
    public ImageView getCheckMoreButton()
    {
    	if(iv_home_to_check_more != null)
    	{
    		return iv_home_to_check_more;
    	}
    	else
    	{
    		return null;
    	}
	}


	private void initNewArrivals(View v) {
		// TODO Auto-generated method stub
         tv_home_new_arrivals_title = (TextView)v.findViewById(R.id.tv_home_new_arrivals_title);
         tv_home_new_arrivals_content = (TextView)v.findViewById(R.id.tv_home_new_arrivals_content);
         iv_home_new_arrivals_pic = (ImageView)v.findViewById(R.id.iv_home_new_arrivals_pic);
         tv_home_new_arrivals_title1 = (TextView)v.findViewById(R.id.iv_home_new_arrivals_small_title1);
         tv_home_new_arrivals_content1 = (TextView)v.findViewById(R.id.iv_home_new_arrivals_small_content1);
         iv_home_new_arrivals_pic1 = (ImageView)v.findViewById(R.id.iv_home_new_arrivals_small_pic1);
         tv_home_new_arrivals_title2 = (TextView)v.findViewById(R.id.iv_home_new_arrivals_small_title2);
         tv_home_new_arrivals_content2 = (TextView)v.findViewById(R.id.iv_home_new_arrivals_small_content2);
         iv_home_new_arrivals_pic2 = (ImageView)v.findViewById(R.id.iv_home_new_arrivals_small_pic2);
         
         //还有3个view的点击事件
         home_new_arrivals = (View)v.findViewById(R.id.home_new_arrivals);
         home_new_arrivals1 = (View)v.findViewById(R.id.home_new_arrivals1);
         home_new_arrivals2 = (View)v.findViewById(R.id.home_new_arrivals2);
         
 		new Thread(runnableArrivals).start();		
         
	}


	private void initSearchView(View v) {
		// TODO Auto-generated method stub
    	LinearLayout searchButton = (LinearLayout)v.findViewById(R.id.title_search);  
		searchButton.setOnClickListener(this);
		
		
		 add_menus = (ImageView) v.findViewById(R.id.add_menus);
		 add_menus.setOnClickListener(this);
	}

    
	@Override
	public void onClick(View v) {
       if (v.getId() == R.id.title_search) {
			Intent intent = new Intent(mContext, SearchActivity.class);
			startActivity(intent);
		}
       else if(v.getId() == R.id.add_menus)
       {
			AddPopWindow addPopWindow = new AddPopWindow(getActivity());
			addPopWindow.showPopupWindow(add_menus);
       }
	}
    
	private void initNewestSwitcher(View v) {
    	switcher = (TextSwitcher) v.findViewById(R.id.ts_newest_info);  
    	switcher.setFactory(this); 
    	
    	switcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));  
        // 设置切出动画  
    	switcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_up));  
    	
    	
    	if(Utils.WTF)
    	{
    		View layout_home_newest_switcher = (View) v.findViewById(R.id.layout_home_newest_switcher);  
    		layout_home_newest_switcher.setVisibility(View.GONE);
    		
    	}
    	
		Message msg = new Message();
		msg.what = MSG_TEST_SWITCHER_TEST;
		mHandler.sendMessageDelayed(msg, 2000);
		
		new Thread(runnableNewest).start();		
		
    }
    
	
	//最新揭晓
	Runnable runnableNewest = new Runnable(){
		  @Override
		  public void run() {
		    //
		    // TODO: http request.
		    //
			JsonControl.httpGet(JsonControl.HOME_PAGE+"apps/ajax/getLotteryList/0/0/10/1", mHandler,JsonControl.JSON_TYPE_NEWEST);
		  }
	};
	
	//新品推荐
	Runnable runnableArrivals = new Runnable(){
		  @Override
		  public void run() {
		    //
		    // TODO: http request.
		    //
			JsonControl.httpGet(JsonControl.HOME_PAGE+"apps/ajax/getShopList/0/90/0/3/0", mHandler,JsonControl.JSON_TYPE_ARRIVALS);
		  }
	};
	
	//猜你喜欢
	Runnable runnableRecommend = new Runnable(){
		  @Override
		  public void run() {
		    //
		    // TODO: http request.
		    //
			JsonControl.httpGet(JsonControl.HOME_PAGE+"apps/ajax/getShopList/0/91/0/10/0", mHandler,JsonControl.JSON_TYPE_RECOMMAND);
		  }
	};
	
    @Override  
    public void onStop()
    {
    	super.onStop();
    	mHandler.removeCallbacksAndMessages(null);
    }
    
    @Override  
    public View makeView() {   
        TextView textView = new TextView(mContext);   
        textView.setSingleLine(true);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView.setTextColor(0xff666666);  
        textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        return textView;   
    }  
    
    private void initGoodsItems(View v) {
    	NoScroolGridView gridView = (NoScroolGridView) v.findViewById(R.id.gridview_recommend);
    	gridView.setFocusable(false); 
/*    	List<GoodsProperty> hashMapList = new ArrayList<GoodsProperty>();
        //测试数据
        for (int i = 0; i < 8; i++) {

            GoodsProperty goodsRecommandItem = new GoodsProperty();
            goodsRecommandItem.setGoodsRecommandItem(mContext, "测试测    "+i+"   试测试", null, i*10, i*100, i*90,"价值:¥ 888.88");
            hashMapListRecommend.add(goodsRecommandItem);

        }*/

       goodsRecommendAdapter = new GoodsRecommendAdapter(mContext);

       gridView.setAdapter(goodsRecommendAdapter);
       new Thread(runnableRecommend).start();	
    }
    
    
    private void CountDownPageReflash() {
    	
    	//hashMapList
    	
    	if(Utils.WTF)
    	{
    		return;
    	}
    	
    	
    	for(int i = 0;i < 4;i++)
    	{
    		if(hashMapList.size() < i + 1)
    		{
    			//界面就不处理了,这边返回是为了防止数组越界
    			return;
    		}
    		
    		Drawable drawable;	
    		if(imageLoader != null)
    		{
    			imageLoader.downloadImage(JsonControl.FILE_HEAD+hashMapList.get(i).getThumb(), countImages.get(i));
    		}
    		
    		long timeUnixNow =  System.currentTimeMillis();    //服务器也是用的这个接口,来获取unix时间
    		//1490327446.815   这个是服务端读出来的值,string类型
    		//1491407081699  这个是timeUnixNow的值
    		
    		//1079635     1036800     42835
    		
    		long countDownTimeLeft =hashMapList.get(i).getQ_end_time() + 180 - (timeUnixNow/1000);
    		
    		Utils.Log("开奖时间: "+hashMapList.get(i).getQ_end_time()+" + 180 - "+(timeUnixNow/1000)+" = "+countDownTimeLeft);
    		
    		if(countDownTimeLeft < 0)
    		{
    			//已经过了开奖后的3分钟
    			countTimes.get(i).setText("正在开奖");
    			continue;
    		}
    		
    		if(countDownTimers.get(i) != null)
    		{
    			countDownTimers.get(i).setTextView(countTimes.get(i));
    			countDownTimers.get(i).setCountDownTime(countDownTimeLeft*1000);
    			countDownTimers.get(i).start();
    		}
    	}
    	

    }
    
    private void initCountDownPage(View v) {
    	
    	
    	if(Utils.WTF)
    	{
    		View ll_count_donw_view = (View) v.findViewById(R.id.ll_count_donw_view);
    		ll_count_donw_view.setVisibility(View.GONE);
    		return;
    	}
    	
    	
        count1_image = (ImageView) v.findViewById(R.id.count1_image);
        count2_image = (ImageView) v.findViewById(R.id.count2_image);
        count3_image = (ImageView) v.findViewById(R.id.count3_image);
        count4_image = (ImageView) v.findViewById(R.id.count4_image);
        countImages.add(count1_image);
        countImages.add(count2_image);
        countImages.add(count3_image);
        countImages.add(count4_image);
        count1_time = (TextView) v.findViewById(R.id.count1_time);
        count2_time = (TextView) v.findViewById(R.id.count2_time);
        count3_time = (TextView) v.findViewById(R.id.count3_time);
        count4_time = (TextView) v.findViewById(R.id.count4_time);
        countTimes.add(count1_time);
        countTimes.add(count2_time);
        countTimes.add(count3_time);
        countTimes.add(count4_time);
        CountDownTimerTextView countDowntimer1 = new CountDownTimerTextView(180000, count1_time);
        CountDownTimerTextView countDowntimer2 = new CountDownTimerTextView(180000, count2_time);
        CountDownTimerTextView countDowntimer3 = new CountDownTimerTextView(180000, count3_time);
        CountDownTimerTextView countDowntimer4 = new CountDownTimerTextView(180000, count4_time);
        countDownTimers.add(countDowntimer1);
        countDownTimers.add(countDowntimer2);
        countDownTimers.add(countDowntimer3);
        countDownTimers.add(countDowntimer4);
        CountDownPageReflash();
     

    }

    private void initADPager(View v) {

		mViewFlow = (ViewFlow) v.findViewById(R.id.viewflow);
		mFlowIndicator = (CircleFlowIndicator) v.findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mFlowIndicator);
		mViewFlow.setTimeSpan(4500);
		mViewFlow.setSelection(imageUrlList.size() * 1000); // 设置初始位置

        new Thread(new Runnable() {
			@Override
			public void run() {
				//读取服务器广告条数据
				JsonControl.httpGet(JsonControl.HOME_PAGE+"apps/ajax/getBanner", mHandler,JsonControl.JSON_TYPE_BANNER);
			}
		}).start();
		
    }    

    
    @Override
    protected void initData() {
        super.initData();
    }
    

    
   
    
    
    @Override
	protected String getPageName() {
		return MainFragment.class.getName();
	}
}


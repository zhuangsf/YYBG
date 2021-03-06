package com.android.ttbg;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;

import com.android.ttbg.pickerviewbean.GetJsonDataUtil;
import com.android.ttbg.pickerviewbean.JsonBean;
import com.android.ttbg.util.BackLightControl;
import com.android.ttbg.util.FileUtil;
import com.android.ttbg.util.OperatingSP;
import com.android.ttbg.util.Utils;
import com.android.ttbg.view.SelectPicPopupWindow;
import com.android.ttbg.view.SelectSexPopupWindow;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingEditActivity extends ActivityPack {
	
    private ImageView  title_back;
    private LinearLayout mainLayout;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
	private static final int REQUESTCODE_PICK = 0; // 相册选图标记
	private static final int REQUESTCODE_TAKE = 1; // 相机拍照标记
	private static final int REQUESTCODE_CUTTING = 2; // 图片裁切标记
	private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
	private SelectSexPopupWindow menuWindow_sex; // 自定义的头像编辑弹出框
	private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
	private static final String IMAGE_FILE_NAME_CROP = "avatarImage_crop.jpg";// 头像文件名称
	private String urlpath; // 图片本地路径
	
	private ImageView userHead;
	private TextView tv_user_name;
	private TextView tv_setting_item_tips7; //QQ
	private TextView tv_setting_item_tips8; //现居地
	private TextView tv_setting_item_tips9;//家乡
	private TextView tv_setting_signature;//个人签名
	
	private TextView tv_setting_item_tips5;  //显示性别的信息
	private TextView tv_setting_item_tips6;//显示生日
	
	
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_edit);
		
		
		 title_back = (ImageView)findViewById(R.id.title_back);
		 title_back.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
		        	finish();
		        }  
		  }); 
		 
		 
		 
		 mainLayout = (LinearLayout) findViewById(R.id.main_layout);
		 View item_edit_icon = (View)findViewById(R.id.item_edit_icon);
		 item_edit_icon.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
					menuWindow = new SelectPicPopupWindow(SettingEditActivity.this, itemsOnClick);
					menuWindow.showAtLocation(mainLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		        }  
		  }); 
		 //用户头像
		 userHead = (ImageView) findViewById(R.id.iv_setting_item_icon);
		 
		 
		 View item_setting_edit = (View)findViewById(R.id.item_edit_name);
		  //昵称的值
		 tv_user_name = (TextView)findViewById(R.id.tv_setting_item_tips);
		 tv_user_name.setText(OperatingSP.getUserName(SettingEditActivity.this));
		 item_setting_edit.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
					Intent intent = new Intent(SettingEditActivity.this, SettingNameActivity.class);
					startActivity(intent);
		        }  
		  }); 

 
		 
		 //收货地址
		 View item_edit_receive_address = (View)findViewById(R.id.item_edit_receive_address);
		 item_edit_receive_address.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
					Intent intent = new Intent(SettingEditActivity.this, AddressManagerActivity.class);
					startActivity(intent);
		        }  
		  }); 
		 
		 
		 //性别信息编辑
		 View item_edit_sex = (View)findViewById(R.id.item_edit_sex);
		 tv_setting_item_tips5 = (TextView)findViewById(R.id.tv_setting_item_tips5);
		 tv_setting_item_tips5.setText( OperatingSP.getString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_SEX,OperatingSP.PREFERENCE_SETTING_SEX_DEFAULT));
		
		 item_edit_sex.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
					menuWindow_sex = new SelectSexPopupWindow(SettingEditActivity.this, itemsOnClick);
					menuWindow_sex.showAtLocation(mainLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		        }  
		  }); 
		 
		 
		//生日信息编辑
		 View item_edit_birthday = (View)findViewById(R.id.item_edit_birthday);
		 tv_setting_item_tips6 = (TextView)findViewById(R.id.tv_setting_item_tips6);
		 tv_setting_item_tips6.setText( OperatingSP.getString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_BIRTHDAY,OperatingSP.PREFERENCE_SETTING_BIRTHDAY_DEFAULT));
		
		 item_edit_birthday.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
		        	if(OperatingSP.getString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_BIRTHDAY,OperatingSP.PREFERENCE_SETTING_BIRTHDAY_DEFAULT).equals(OperatingSP.PREFERENCE_SETTING_BIRTHDAY_DEFAULT))
		        	{
		        		ShowTimePickerView();
		        	}
		        	else
		        	{
		        		Toast.makeText(SettingEditActivity.this, "一年内只能修改一次", 2000).show();
		        	}
		        }  
		  }); 
		 
		 
		 //QQ
		 View item_edit_qq = (View)findViewById(R.id.item_edit_qq);
		 tv_setting_item_tips7 = (TextView)findViewById(R.id.tv_setting_item_tips7);
		 tv_setting_item_tips7.setText(OperatingSP.getString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_QQ  ,OperatingSP.PREFERENCE_SETTING_QQ_DEFAULT));
		 item_edit_qq.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
					Intent intent = new Intent(SettingEditActivity.this, SettingQQActivity.class);
					startActivity(intent);
		        }  
		  }); 
		 
			//现居地
		 mHandler.sendEmptyMessage(MSG_LOAD_DATA);//开始初始化城市信息
		 View item_edit_live_address = (View)findViewById(R.id.item_edit_live_address);
		 tv_setting_item_tips8 = (TextView)findViewById(R.id.tv_setting_item_tips8);
		 tv_setting_item_tips8.setText(OperatingSP.getString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_LIVE,OperatingSP.PREFERENCE_SETTING_LIVE_DEFAULT));
		 item_edit_live_address.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
		        	
		        	
		        	if (!isLoaded){
	                    Toast.makeText(SettingEditActivity.this,"数据暂未解析成功，请等待",Toast.LENGTH_SHORT).show();
	                    return;
	                }
		        	
		            OptionsPickerView  pvOptions = new OptionsPickerView.Builder(SettingEditActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
		                @Override
		                public void onOptionsSelect(int options1, int options2, int options3, View v) {
		                    //返回的分别是三个级别的选中位置
		                    String tx = options1Items.get(options1).getPickerViewText()+" "+options2Items.get(options1).get(options2);

		                    //Toast.makeText(AddressEditActivity.this,tx,Toast.LENGTH_SHORT).show();
		                    
		                    OperatingSP.setString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_LIVE,tx);
		                    tv_setting_item_tips8.setText(tx);
		                }
		            })
		            .setTitleText("现居地")
		                    .setSubmitColor(0xffff7700)
		                    .setCancelColor(0xff999999)
		                    .setDividerColor(Color.BLACK)
		                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
		                    .setContentTextSize(18)
		                    .setOutSideCancelable(true)// default is true
		                    .build();

		            //pvOptions.setPicker(options1Items);//一级选择器
		            pvOptions.setPicker(options1Items, options2Items);//二级选择器
		           //pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
		            pvOptions.show();
		        }  
		  }); 
		 
		 //家乡
		 View item_edit_home = (View)findViewById(R.id.item_edit_home);
		 tv_setting_item_tips9 = (TextView)findViewById(R.id.tv_setting_item_tips9);
		 tv_setting_item_tips9.setText(OperatingSP.getString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_HOME,OperatingSP.PREFERENCE_SETTING_HOME_DEFAULT));
		 item_edit_home.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
		        	
		        	
		        	if (!isLoaded){
	                    Toast.makeText(SettingEditActivity.this,"数据暂未解析成功，请等待",Toast.LENGTH_SHORT).show();
	                    return;
	                }
		        	
		            OptionsPickerView  pvOptions = new OptionsPickerView.Builder(SettingEditActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
		                @Override
		                public void onOptionsSelect(int options1, int options2, int options3, View v) {
		                    //返回的分别是三个级别的选中位置
		                    String tx = options1Items.get(options1).getPickerViewText()+" "+options2Items.get(options1).get(options2);

		                    //Toast.makeText(AddressEditActivity.this,tx,Toast.LENGTH_SHORT).show();
		                    
		                    OperatingSP.setString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_HOME,tx);
		                    tv_setting_item_tips9.setText(tx);
		                }
		            })
		            .setTitleText("家乡")
		                    .setSubmitColor(0xffff7700)
		                    .setCancelColor(0xff999999)
		                    .setDividerColor(Color.BLACK)
		                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
		                    .setContentTextSize(18)
		                    .setOutSideCancelable(true)// default is true
		                    .build();

		            //pvOptions.setPicker(options1Items);//一级选择器
		            pvOptions.setPicker(options1Items, options2Items);//二级选择器
		           //pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
		            pvOptions.show();
		        }  
		  }); 
		 
		 //个性签名
		 View layout_item_edit_signature = (View)findViewById(R.id.layout_item_edit_signature);
		 tv_setting_signature = (TextView)findViewById(R.id.tv_setting_signature);
		 tv_setting_signature.setText(OperatingSP.getString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_SIGNATURE  ,OperatingSP.PREFERENCE_SETTING_SIGNATURE_DEFAULT));
		 layout_item_edit_signature.setOnClickListener(new View.OnClickListener() {  
		        public void onClick(View v) {  
					Intent intent = new Intent(SettingEditActivity.this, SettingSignature.class);
					startActivity(intent);
		        }  
		  });  
		 
	}
	
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread==null){//如果已创建就不再重新创建子线程了

                     //   Toast.makeText(AddressEditActivity.this,"开始解析数据",Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                   // Toast.makeText(AddressEditActivity.this,"解析数据成功",Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                   // Toast.makeText(AddressEditActivity.this,"解析数据失败",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    
    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
            }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }
	
    private void ShowTimePickerView() {// 弹出选择器

    	TimePickerView  timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v) {//选中事件回调
             String dataString = getTime(date);
    		 tv_setting_item_tips6.setText(dataString);
    		 OperatingSP.setString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_BIRTHDAY,dataString);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("保存")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText(" ")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(0xffff7700)
                .setCancelColor(0xff999999)
                .setTitleBgColor(0xFFffffff)//标题背景颜色 Night mode
                .setBgColor(0xFFffffff)//滚轮背景颜色 Night mode
                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的 label 文字，false 则每项 item 全部都带有 label。
                //.isDialog(true)//是否显示为对话框样式
                .build();
    	timePickerView.show();
    	
    	
    }
	
	
	@Override
    public void onResume() {
        super.onResume();
        
        updateData();

    }
	private void updateData()
	{
		if(tv_user_name != null)
		{
			String user_name = OperatingSP.getUserName(SettingEditActivity.this);
			tv_user_name.setText(user_name);
		}
		if(tv_setting_item_tips7 != null)
		{
			String qq = OperatingSP.getString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_QQ  ,OperatingSP.PREFERENCE_SETTING_QQ_DEFAULT);
			tv_setting_item_tips7.setText(qq);
		}
		if(tv_setting_signature != null)
		{
			String signature = OperatingSP.getString(SettingEditActivity.this, OperatingSP.PREFERENCE_SETTING_SIGNATURE,OperatingSP.PREFERENCE_SETTING_SIGNATURE_DEFAULT);
			tv_setting_signature.setText(signature);
		}
	}
	

	private void updateUserHead(Drawable drawable)
	{
		if(userHead != null)
		{
			userHead.setImageDrawable(drawable);
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_PICK:// 直接从相册获取
			try {
				startPhotoZoom(data.getData());
			} catch (NullPointerException e) {
				e.printStackTrace();// 用户点击取消操作
			}
			break;
		case REQUESTCODE_TAKE:// 调用相机拍照
			startPhotoZoom(getTakePicSaveUri());
			break;
		case REQUESTCODE_CUTTING:// 取得裁剪后的图片
			try {
				setPicToView(data);
			} catch (Exception e) {
				e.printStackTrace();// 用户点击取消操作
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);// 去黑边
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		// the return data true may waste logs of mem
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getCropPicSaveUri());
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		Context context = SettingEditActivity.this;
		if (extras != null) {
			// 取得SDCard图片路径做显示
			// Bitmap photo = extras.getParcelable("data");
			Bitmap photo = getBitmapFromUri(getCropPicSaveUri(), context);
			Drawable drawable = new BitmapDrawable(null, photo);
			urlpath = FileUtil.saveFile(context, Utils.getInternelStoragePath(SettingEditActivity.this), IMAGE_FILE_NAME, photo);
			OperatingSP.saveUserImage(context,urlpath);
			// 更新头像
			updateUserHead(drawable);
			
			
			//上传到服务器的代码,暂时还未处理
/*			try {
				// send to server
				new Thread(new Runnable() {
					@Override
					public void run() {
						SharedPreferences p = Utils.getSharedPpreference(getActivity());
						final String phone = p.getString(Utils.SHARE_PREFERENCE_CUP_PHONE, "");
						final String accountid = p.getString(Utils.SHARE_PREFERENCE_CUP_ACCOUNTID, "");

						// http://121.199.75.79:8280//user/updateProfile.do
						if (!TextUtils.isEmpty(urlpath)) {
//							Utils.httpPostFile(Utils.URL_PATH + "/user/updateProfile.do", urlpath, mHandler, accountid, phone);
						}
					}
				}).start();
			} catch (Exception ee) {
				Utils.Log("httpPut error:" + ee);
				ee.printStackTrace();
			}*/

		}
	}

	public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
			bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// save and edit pic uri can not be same or it will 0byte
	private Uri getTakePicSaveUri() {
		String filePath = Utils.getInternelStoragePath(SettingEditActivity.this);
		Utils.Log("getTakePicSaveUri filePath = " + filePath);
		File file = new File(filePath);
		Utils.Log("getTakePicSaveUri file = " + file);
		
		if(file != null)
		{
			Utils.Log("getTakePicSaveUri file.exists() = " + file.exists());
		}
		
		if (file != null && !file.exists()) {
			boolean createSuccess = file.mkdirs();
			Utils.Log("getTakePicSaveUri createSuccess  = " + createSuccess);
		}

		return Uri.fromFile(new File(filePath, IMAGE_FILE_NAME));
	}
	private Uri getCropPicSaveUri() {

		String filePath = Utils.getInternelStoragePath(SettingEditActivity.this);
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}

		return Uri.fromFile(new File(filePath, IMAGE_FILE_NAME_CROP));
	}
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(menuWindow != null)
			{
				menuWindow.dismiss();
			}
			if(menuWindow_sex != null)
			{
				menuWindow_sex.dismiss();
			}
			
					
			switch (v.getId()) {
			// 拍照
			case R.id.takePhotoBtn:
				Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 下面这句指定调用相机拍照后的照片存储的路径
				takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTakePicSaveUri());
				startActivityForResult(takeIntent, REQUESTCODE_TAKE);
				
				
			//	getTakePicSaveUri();
				break;
			// 相册选择图片
			case R.id.pickPhotoBtn:
				Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
				// 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
				pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(pickIntent, REQUESTCODE_PICK);
				break;
			case R.id.sex_man:
				tv_setting_item_tips5.setText("男");
				OperatingSP.setString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_SEX,"男");
				break;
			case R.id.sex_women:
				tv_setting_item_tips5.setText("女");
				OperatingSP.setString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_SEX,"女");
				break;
			case R.id.sex_unknow:
				tv_setting_item_tips5.setText("保密");
				OperatingSP.setString(SettingEditActivity.this,OperatingSP.PREFERENCE_SETTING_SEX,"保密");
				break;
			default:
				break;
			}
		}
	};
	
    public static String getTime(Date date)  
    {  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        return format.format(date);  
    }  

}
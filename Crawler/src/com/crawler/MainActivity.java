package com.crawler;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.topnews.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crawler.adapter.NewsFragmentPagerAdapter;
import com.crawler.app.AppApplication;
import com.crawler.bean.ChannelItem;
import com.crawler.bean.ChannelManage;
import com.crawler.fragment.NewsFragment;
import com.crawler.tool.BaseTools;
import com.crawler.tool.Constants;
import com.crawler.view.ColumnHorizontalScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
/**
 * ��android�߷�ϵ�У�����ͷ�� --�����Ķ���  
 * author:RA
 * blog : http://blog.csdn.net/vipzjyno1/
 */
public class MainActivity extends FragmentActivity {
	
	LinearLayout mRadioGroup_content;
	
	RelativeLayout rl_column;
	private ViewPager mViewPager;
	
	private PullToRefreshListView mPullRefreshListView;
	/** �û�ѡ������ŷ����б�*/
	private ArrayList<ChannelItem> userChannelList=new ArrayList<ChannelItem>();
	/** ��ǰѡ�е���Ŀ*/
	private int columnSelectIndex = 0;
	private int mScreenWidth = 0;
	/** Item��� */
	private int mItemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	
	protected SlidingMenu side_drawer;
	
	/** ����CODE */
	public final static int CHANNELREQUEST = 1;
	private final static String menu_name_first = "��������20ҳ";
	private final static String menu_name_second = "��������50ҳ";
	private final static String menu_name_third = "��������100ҳ";
	/** �������ص�RESULTCODE */
	public final static int CHANNELRESULT = 10;
	private static ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mScreenWidth = BaseTools.getWindowsWidth(this);
		mItemWidth = mScreenWidth / 3;
		initView();
		
	}
	/** ��ʼ��layout�ؼ�*/
	private void initView() {
		
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		
		rl_column = (RelativeLayout) findViewById(R.id.rl_column);
		
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		
		setChangelView();
	}
	/** 
	 *  ����Ŀ����仯ʱ�����
	 * */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}
	/** ��ȡColumn��Ŀ ���ݡ�����ѡ�������*/
	private void initColumnData() {
		userChannelList = ((ArrayList<ChannelItem>)ChannelManage.getManage(AppApplication.getApp().getSQLHelper()).getUserChannel());
	}

	/** 
	 *  ��ʼ��Column��Ŀ��
	 * */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count =  userChannelList.size();
		
		for(int i = 0; i< count; i++){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth , LayoutParams.WRAP_CONTENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			params.weight = 1;
			TextView columnTextView = new TextView(this);
			columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
		
			columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setLayoutParams(params);
			columnTextView.setPadding(5, 5, 5, 5);
			columnTextView.setId(i);
			columnTextView.setText(userChannelList.get(i).getName());
			columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
			if(columnSelectIndex == i){
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
			          for(int i = 0;i < mRadioGroup_content.getChildCount();i++){
				          View localView = mRadioGroup_content.getChildAt(i);
				          if (localView != v)
				        	  localView.setSelected(false);
				          else{
				        	  localView.setSelected(true);
				        	  mViewPager.setCurrentItem(i);
				          }
			          }
			          Toast.makeText(getApplicationContext(), userChannelList.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
				}
			});
			mRadioGroup_content.addView(columnTextView, i ,params);
		}
	}
	/** 
	 *  ѡ���Column�����Tab
	 * */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			//mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		//�ж��Ƿ�ѡ��
		for (int j = 0; j <  mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}
	/** 
	 *  ��ʼ��Fragment
	 * */
	private void initFragment() {
		fragments.clear();//���
		int count =  userChannelList.size();
		for(int i = 0; i< count;i++){
			Bundle data = new Bundle();
    		data.putString("text", userChannelList.get(i).getName());
    		data.putInt("id", userChannelList.get(i).getId());
			NewsFragment newfragment = new NewsFragment();
			newfragment.setArguments(data);
			fragments.add(newfragment);
		}
		NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);

		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}
	/** 
	 *  ViewPager�л���������
	 * */
	public OnPageChangeListener pageListener= new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);
		menu.add(menu_name_first);
		menu.add(menu_name_second);
		menu.add(menu_name_third);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		//������
		//dialog = ProgressDialog.show(MainActivity.this, "�������ؽ���","90%", true, true);
		//int PROGRESS_DIALOG = 0;
		dialog = new ProgressDialog(MainActivity.this);
		//dialog.setMessage("Loading");
		
		//dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//dialog.g
		//dialog.setTitle("�������ؽ���");
		dialog.setMessage("0%");
		dialog.setIndeterminate(false);
		//dialog.set
		//dialog.setMax(500);
		//dialog.setProgress(250);
		dialog.setCancelable(true);
		//dialog.setSecondaryProgress(50);
		dialog.show();
		//dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//showDialog(PROGRESS_DIALOG);
		Log.d("MenuItem", String.valueOf(item.getItemId()));
		
		switch(item.getItemId()) {
		case 0:
			new Thread() {
				public void run() {
					Constants.getOfflineData(20);
				}
			}.start();
			Log.d("getitemid","here");
			break;
		case 1:
			new Thread() {
				public void run() {
					Constants.getOfflineData(50);
				}
			}.start();
			break;
		case 2:
			new Thread() {
				public void run() {
					Constants.getOfflineData(100);
				}
			}.start();
			break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private long mExitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(side_drawer.isMenuShowing() ||side_drawer.isSecondaryMenuShowing()){
//				side_drawer.showContent();
//			}else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "�ڰ�һ���˳�",
							Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					finish();
				}
			//}
			return true;
		}
		//����MENU��ť����¼����������κβ���
		//if (keyCode == KeyEvent.KEYCODE_MENU) {
		//	return true;
		//}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case CHANNELREQUEST:
			if(resultCode == CHANNELRESULT){
				setChangelView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public final static Handler handle = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("handlerMessage", String.valueOf(msg.what));
			if (msg.what == -1) {
				dialog.setMessage("�����������");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				dialog.dismiss();
			} else {
				dialog.setMessage(String.valueOf(msg.what) + "%");
			}
			
			if (msg.what == 100) {
				dialog.setMessage("�������");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			    dialog.dismiss();
			}
			//switch(msg.what) {
			//
			//	dialog.dismiss();
			//	break;
			//}
		}
	};
	
}

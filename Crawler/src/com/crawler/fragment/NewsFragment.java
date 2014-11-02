package com.crawler.fragment;

import java.util.ArrayList;
import java.util.Iterator;

import com.topnews.R;

import java.util.Arrays;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import java.util.LinkedList;

import android.os.AsyncTask;

import com.crawler.DetailsActivity;
import com.crawler.adapter.NewsAdapter;
import com.crawler.app.AppApplication;
import com.crawler.bean.CommentManage;
import com.crawler.bean.ContentManage;
import com.crawler.bean.NewsEntity;
import com.crawler.tool.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class NewsFragment extends Fragment {
	private final static String TAG = "NewsFragment";
	
	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	
	Activity activity;
	LinkedList<NewsEntity> newsList = new LinkedList<NewsEntity>();
	
	
	private LinkedList<String> mListItems;
	NewsAdapter mAdapter;
	private ArrayAdapter<String> mAdapterRefresh;
	String text;
	private int channel_id;
	private long last_timestamp;
	ImageView detail_loading;
	private PullToRefreshListView mPullRefreshListView;
	ListView actualListView;
	public final static int SET_NEWSLIST = 0;
	//Toast提示框
	private RelativeLayout notify_view;
	private TextView notify_view_text;
	ContentManage contentManger = ContentManage.getManage(AppApplication.getApp().getSQLHelper());
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		channel_id = args != null ? args.getInt("id", 0) : 0;
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}
	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.d("setUserVisibleHint","setUserVisibleHint");
		Log.d("channel_id",String.valueOf(channel_id));
		Log.d("isVisibleToUser",String.valueOf(isVisibleToUser));
		if (isVisibleToUser) {
			//fragment可见时加载数据
			if(newsList !=null && newsList.size() !=0){
				handler.obtainMessage(SET_NEWSLIST).sendToTarget();
			}else{
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
					}
				}).start();
			}
		}else{
			//fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//setContentView(R.layout.pull_to_refresh_list);
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
		View viewRefresh = LayoutInflater.from(getActivity()).inflate(R.layout.pull_to_refresh_list, null);
		
		mPullRefreshListView = (PullToRefreshListView) viewRefresh.findViewById(R.id.pull_refresh_list);
		
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				Log.d("mPullRefreshListView", "mPullRefreshListView = " + mPullRefreshListView.hasPullFromTop());
				
				new GetDataTask().execute();
				
			}
		});
		actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setDividerHeight(0);

		mAdapterRefresh = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mListItems);
		
		// You can also just use setListAdapter(mAdapter)
		//actualListView.setAdapter(mAdapterRefresh);
		//TextView item_textview = (TextView)view.findViewById(R.id.item_textview);
		//detail_loading = (ImageView)view.findViewById(R.id.detail_loading);
		//Toast提示框
		notify_view = (RelativeLayout)viewRefresh.findViewById(R.id.notify_view);
		notify_view_text = (TextView)view.findViewById(R.id.notify_view_text);
		//item_textview.setText(text);
		return viewRefresh;
	}
    
	private void initData() {
		newsList = Constants.getNewsList(0, channel_id);
		
		Log.d("newsListsize", String.valueOf(newsList.size()));
		if (newsList !=null && newsList.size() != 0) {
			Log.d("initData", "没有从网络上获取信息");
			contentManger.deleteContent(channel_id);
			contentManger.saveContent(newsList);
			last_timestamp = newsList.getLast().getPublishTime();
		} else {
			Log.d("initDataother", "从本地获取信息");
			//2409904180为了得到最新的数据
			newsList = (LinkedList<NewsEntity>)contentManger.getNews(Long.valueOf("2409904180"), channel_id);
			if (newsList !=null && newsList.size() != 0) {
				last_timestamp = newsList.getLast().getPublishTime();
			}
		}
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_NEWSLIST:
				Log.d("SET_NEWSLIST", "SET_NEWSLIST");
				if(mAdapter == null){
					mAdapter = new NewsAdapter(activity, newsList);
				}
				actualListView.setAdapter(mAdapter);
				//actualListView.setOnScrollListener(mAdapter);
				actualListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Log.d("WebView", "WebView");
								Intent intent = new Intent(activity,
										DetailsActivity.class);

								intent.putExtra("news",
										mAdapter.getItem(position-1));
								startActivity(intent);
								activity.overridePendingTransition(
										R.anim.slide_in_right,
										R.anim.slide_out_left);

							}
						});
				
				if(channel_id == 1){
					initNotify();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	
	/* 初始化通知栏目*/
	private void initNotify() {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), 10));
				//notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d("onDestroyView", "channel_id = " + channel_id);
		mAdapter = null;
	}
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "channel_id = " + channel_id);
	}
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			
			// Simulates a background job.
			String[] result = {};
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			
			if (mPullRefreshListView.hasPullFromTop()) {
				//mListItems.addFirst("Added first");
				LinkedList<NewsEntity> newsAddList = Constants.getNewsList(0, channel_id);
				for(int index = 0; index < newsAddList.size(); index++) {
					Log.d("newsAddList", String.valueOf(newsAddList.get(index).getPublishTime()));
				}
				if (newsAddList.size() != 0) {
					newsList.clear();
					Iterator<NewsEntity> it = newsAddList.iterator();
					while(it.hasNext()) {
						newsList.addLast(it.next());
						//Log.d("top_log_publishtime", String.valueOf(newsAddList.get(index).getPublishTime()));
					
					}
					last_timestamp = newsAddList.getLast().getPublishTime();
					contentManger.deleteContent(channel_id);
					contentManger.saveContent(newsList);
					//mAdapter.notifyDataSetInvalidated();
					//Log.d("top_log_publishtime", String.valueOf(last_timestamp));
				} else {
					// 网络问题
					Log.d("网络问题", "网络问题");
					newsAddList = (LinkedList<NewsEntity>)contentManger.getNews(last_timestamp, channel_id);
					for(int index = 0; index < newsAddList.size(); index++) {
						Log.d("newsAddList", String.valueOf(newsAddList.get(index).getPublishTime()));
					}
					if (newsAddList.size() != 0) {
						newsList.clear();
						Iterator<NewsEntity> it = newsAddList.iterator();
						while(it.hasNext()) {
							newsList.addLast(it.next());
							//Log.d("top_log_publishtime", String.valueOf(newsAddList.get(index).getPublishTime()));
						
						}
						last_timestamp = newsAddList.getLast().getPublishTime();
						
					}
				}
			} else {
				//mListItems.addLast("Added last");
				LinkedList<NewsEntity> newsAddList = Constants.getNewsList(last_timestamp, channel_id);
				//Log.d("newsAddList.toString()", newsAddList.toString());
				if (newsAddList.size() != 0) {
					Iterator<NewsEntity> it = newsAddList.iterator();
					while(it.hasNext()) {
					
						newsList.addLast(it.next());
						//Log.d("bottom_log_publishtime", String.valueOf(newsAddList.get(index).getPublishTime()));
					
					}
					last_timestamp = newsAddList.getLast().getPublishTime();
					contentManger.deleteContent(channel_id);
					contentManger.saveContent(newsList);
				} else {
					// 网络问题
					Log.d("网络问题", "向下滑动从本地获取");
					newsAddList = (LinkedList<NewsEntity>)contentManger.getNews(last_timestamp, channel_id);
					Log.d("newsAddList.toString()", newsAddList.toString());
					for(int index = 0; index < newsAddList.size(); index++) {
						Log.d("网络问题", String.valueOf(newsAddList.get(index).getPublishTime()));
					}
					if (newsAddList.size() != 0) {
						Iterator<NewsEntity> it = newsAddList.iterator();
						while(it.hasNext()) {
						
							newsList.addLast(it.next());
							//Log.d("bottom_log_publishtime", String.valueOf(newsAddList.get(index).getPublishTime()));
						
						}
						last_timestamp = newsAddList.getLast().getPublishTime();
						Log.d("最后时间戳", String.valueOf(last_timestamp));
					}
				}
			}
			//mAdapterRefresh.notifyDataSetChanged();
			mAdapter.notifyDataSetInvalidated();
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
}

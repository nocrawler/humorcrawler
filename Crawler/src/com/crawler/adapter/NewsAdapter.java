package com.crawler.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;

import com.crawler.DetailsActivity;
import com.crawler.bean.NewsEntity;
import com.crawler.tool.Constants;
import com.crawler.tool.DateTools;
import com.crawler.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.topnews.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;

public class NewsAdapter extends BaseAdapter implements SectionIndexer, OnScrollListener{
	LinkedList<NewsEntity> newsList;
	Activity activity;
	
	ViewHolder mHolder;
	View view;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	
	/** 弹出的更多选择框  */
	
	public NewsAdapter(Activity activity, LinkedList<NewsEntity> newsList) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		initDateHead();
	}
	
	private List<Integer> mPositions;
	private List<String> mSections;
	
	private void initDateHead() {
		mSections = new ArrayList<String>();
		mPositions= new ArrayList<Integer>();
		for(int i = 0; i <newsList.size();i++){
			if(i == 0){
				mSections.add(DateTools.getSection(String.valueOf(newsList.get(i).getPublishTime())));
				mPositions.add(i);
				continue;
			}
			if(i != newsList.size()){
				if(!newsList.get(i).getPublishTime().equals(newsList.get(i - 1).getPublishTime())){
					mSections.add(DateTools.getSection(String.valueOf(newsList.get(i).getPublishTime())));
					mPositions.add(i);
				}
			}
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public NewsEntity getItem(int position) {
		// TODO Auto-generated method stub
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item, null);
			mHolder = new ViewHolder();
			mHolder.item_layout = (LinearLayout)view.findViewById(R.id.item_layout);
			mHolder.item_title = (TextView)view.findViewById(R.id.item_title);
			mHolder.item_source = (TextView)view.findViewById(R.id.item_source);
			mHolder.btn_like = (ToggleButton)view.findViewById(R.id.btn_like);
			mHolder.likes = (TextView)view.findViewById(R.id.likes);
			mHolder.btn_like.setTag(R.id.tag_first, mHolder.likes);
			//mHolder.comment_name = (TextView)view.findViewById(R.id.comment_name);
			mHolder.comment_count = (TextView)view.findViewById(R.id.comment_count);
			mHolder.publish_time = (TextView)view.findViewById(R.id.publish_time);
			mHolder.item_image_0 = (ImageView)view.findViewById(R.id.item_image_0);
			mHolder.item_image_1 = (ImageView)view.findViewById(R.id.item_image_1);
			mHolder.item_image_2 = (ImageView)view.findViewById(R.id.item_image_2);
			
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		//获取position对应的数据
		NewsEntity news = getItem(position);
		mHolder.item_title.setText(news.getTitle());
		mHolder.item_source.setText(news.getSource());
		
		mHolder.likes.setText(String.valueOf(news.getLikes()));
		mHolder.newsId = news.getNewsId();
		mHolder.btn_like.setTag(R.id.tag_second, mHolder.newsId);
		//mHolder.comment_name.setText("评论");
		mHolder.comment_count.setText(String.valueOf(news.getCommentNum()));
		
		mHolder.publish_time.setText(DateTools.getStrTime_ymd_hms(String.valueOf(news.getPublishTime())));
		List<String> imgUrlList = news.getPicList();
		
		if(imgUrlList !=null && imgUrlList.size() !=0){
			
			for (int index = 0; index < imgUrlList.size(); index++) {
				switch (index) {
				case 0:
					imageLoader.displayImage(imgUrlList.get(0),
							mHolder.item_image_0, options);
					break;
				case 1:
					imageLoader.displayImage(imgUrlList.get(1),
							mHolder.item_image_1, options);
					break;
				case 2:
					imageLoader.displayImage(imgUrlList.get(2),
							mHolder.item_image_2, options);
					break;
				default:
					break;
				}
			}
			
		}else{
			
		}
		
		if(!TextUtils.isEmpty(news.getComment())){
			
		}else{
			
		}
		
		int section = getSectionForPosition(position);
		if (getPositionForSection(section) == position) {
			
		} else {
			
		}

		mHolder.btn_like.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(buttonView.isChecked()) {
					
					Integer likes = Integer.parseInt(((TextView)buttonView.getTag(R.id.tag_first)).getText().toString());
				    
					((TextView)buttonView.getTag(R.id.tag_first)).setText(String.valueOf(likes + 1));
					
					new GetDataTask().execute((Integer)buttonView.getTag(R.id.tag_second), 1);
					// 传到服务器
					Log.d("isChecked", ((TextView)buttonView.getTag(R.id.tag_first)).getText().toString());
				} else {
							// mHolder.likes.setText("1024");
							Integer likes = Integer
									.parseInt(((TextView) buttonView.getTag(R.id.tag_first))
											.getText().toString());

							((TextView) buttonView.getTag(R.id.tag_first)).setText(String
									.valueOf(likes - 1));
					// 传到服务器
					
					new GetDataTask().execute((Integer)buttonView.getTag(R.id.tag_second), -1);
					Log.d("isnotChecked", ((TextView)buttonView.getTag(R.id.tag_first)).getText().toString());
					
				}
				
			}
		}
		);
			
		return view;
	}
	
	
	
	static class ViewHolder {
		LinearLayout item_layout;
		//title
		TextView item_title;
		//图片源
		TextView item_source;
		ToggleButton btn_like;
		TextView likes;
		Integer newsId;
		//评论名字
		//TextView comment_name;
		//评论数量
		TextView comment_count;
		//发布时间
		TextView publish_time;
		
		ImageView right_image;
		//3张图片布局
		LinearLayout item_image_layout; //3张图片时候的布局
		ImageView item_image_0;
		ImageView item_image_1;
		ImageView item_image_2;
		//大图的图片的话布局
		ImageView large_image;
		//pop按钮
		ImageView popicon;
		//评论布局
		RelativeLayout comment_layout;
		TextView comment_content;
		//paddingview
		View right_padding_view;
		
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}
	//滑动监听
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return mSections.toArray();
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
			return -1;
		}
		return mPositions.get(sectionIndex);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}
	
	private class GetDataTask extends AsyncTask<Integer, Void, String[]> {

		@Override
		protected String [] doInBackground(Integer... params) {
			
		 Log.d("param0", String.valueOf(params[0]));
		 Log.d("param1", String.valueOf(params[1]));
	     Constants.postLikesCount((int)params[0], params[1]);
	     
	     String[] result = {};
		 return result;
	     
	     
		}
	}
}

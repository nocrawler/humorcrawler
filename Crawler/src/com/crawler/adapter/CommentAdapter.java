package com.crawler.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;

import com.crawler.bean.CommentEntity;
import com.crawler.bean.NewsEntity;
import com.crawler.tool.Constants;
import com.crawler.tool.DateTools;
import com.crawler.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.topnews.R;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ToggleButton;

public class CommentAdapter extends BaseAdapter implements SectionIndexer,
		OnScrollListener {
	LinkedList<CommentEntity> commentList;
	Activity activity;

	ViewHolder mHolder;
	View view;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	NewsEntity news;

	/** 弹出的更多选择框 */

	public CommentAdapter(Activity activity, NewsEntity news,
			LinkedList<CommentEntity> commentList) {
		this.activity = activity;
		this.commentList = commentList;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		initDateHead();
		this.news = news;
	}

	private List<Integer> mPositions;
	private List<String> mSections;

	private void initDateHead() {
		mSections = new ArrayList<String>();
		mPositions = new ArrayList<Integer>();
		for (int i = 0; i < commentList.size(); i++) {
			if (i == 0) {
				mSections.add(DateTools.getSection(String.valueOf(commentList
						.get(i).getPublishTime())));
				mPositions.add(i);
				continue;
			}
			if (i != commentList.size()) {
				if (!commentList.get(i).getPublishTime()
						.equals(commentList.get(i - 1).getPublishTime())) {
					mSections.add(DateTools.getSection(String
							.valueOf(commentList.get(i).getPublishTime())));
					mPositions.add(i);
				}
			}
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentList == null ? 0 : commentList.size();
	}

	@Override
	public CommentEntity getItem(int position) {
		// TODO Auto-generated method stub
		if (commentList != null && commentList.size() != 0) {
			return commentList.get(position);
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
			view = inflater.inflate(R.layout.list_comment_item, null);
			mHolder = new ViewHolder();
			mHolder.order = (TextView) view
					.findViewById(R.id.comment_item_order);
			mHolder.position = (TextView) view
					.findViewById(R.id.comment_item_position);
			mHolder.content = (TextView) view
					.findViewById(R.id.comment_item_content);
			mHolder.btn_like = (ToggleButton) view
					.findViewById(R.id.comment_item_btn_like);
			mHolder.publish_time = (TextView) view
					.findViewById(R.id.comment_item_publish_time);
			mHolder.likes = (TextView) view
					.findViewById(R.id.comment_item_likes);
			mHolder.btn_like.setTag(R.id.tag_comment_first, mHolder.likes);
			
			view.setTag(mHolder);
			Log.d("tag", "tag");
		} else {
			mHolder = (ViewHolder) view.getTag();
			Log.d("tag2", "tag2");
			Log.d("tag2", String.valueOf(position));
		}
		// 获取position对应的数据
		CommentEntity comment = getItem(position);
		mHolder.order = (TextView) view.findViewById(R.id.comment_item_order);
		mHolder.position = (TextView) view
				.findViewById(R.id.comment_item_position);
		mHolder.content = (TextView) view
				.findViewById(R.id.comment_item_content);
		mHolder.btn_like = (ToggleButton) view
				.findViewById(R.id.comment_item_btn_like);
		mHolder.publish_time = (TextView) view
				.findViewById(R.id.comment_item_publish_time);
		mHolder.likes = (TextView) view.findViewById(R.id.comment_item_likes);
		mHolder.commentId = comment.getCommentId();
		mHolder.btn_like.setTag(R.id.tag_comment_second, mHolder.commentId);
		mHolder.order.setText(String.valueOf(comment.getOrder()) + "楼");
		mHolder.position.setText(String.valueOf(comment.getPosition()));
		mHolder.content.setText(comment.getContent());
		// mHolder.publish_time.setText(String.valueOf(comment.getPublishTime()));
		mHolder.likes.setText(String.valueOf(String.valueOf(comment.getLikesNum())));
		mHolder.publish_time.setText(DateTools.getStrTime_ymd_hms(String
				.valueOf(comment.getPublishTime())));

		if (!TextUtils.isEmpty(news.getComment())) {

		} else {

		}

		int section = getSectionForPosition(position);
		if (getPositionForSection(section) == position) {

		} else {

		}

		mHolder.btn_like
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView.isChecked()) {

							Integer likes = Integer
									.parseInt(((TextView) buttonView.getTag(R.id.tag_comment_first))
											.getText().toString());

							((TextView) buttonView.getTag(R.id.tag_comment_first)).setText(String
									.valueOf(likes + 1));
							new GetDataTask().execute(0, (Integer)buttonView.getTag(R.id.tag_comment_second), 1);
							Log.d("isnotChecked", ((TextView) buttonView
									.getTag(R.id.tag_comment_first)).getText().toString());
						} else {
							// mHolder.likes.setText("1024");
							Integer likes = Integer
									.parseInt(((TextView) buttonView.getTag(R.id.tag_comment_first))
											.getText().toString());

							((TextView) buttonView.getTag(R.id.tag_comment_first)).setText(String
									.valueOf(likes - 1));
							new GetDataTask().execute(0, (Integer)buttonView.getTag(R.id.tag_comment_second), -1);
							Log.d("isnotChecked", ((TextView) buttonView
									.getTag(R.id.tag_comment_first)).getText().toString());

						}

					}
				});

		return view;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// mHolder.likes.setText("2048");
			// mHolder.likes.invalidate();
			Log.d("thread", mHolder.likes.getText().toString());
		}
	};

	public class RefreshTextView extends Thread {
		@Override
		public void run() {
			Message message = new Message();
			mHandler.sendMessage(message);
		}
	}

	static class ViewHolder {

		// title
		TextView order;
		TextView content;
		TextView position;

		Integer commentId;
		ToggleButton btn_like;
		TextView publish_time;
		TextView likes;

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	// 滑动监听
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
		
		protected String[] doInBackground(Integer... params) {
			
			if (params[0] == 0) {
				Constants.postCommentLikesCount((int) params[1], params[2]);
			} else {
				Constants.postLikesCount((int) params[1], params[2]);
			}

			String[] result = {};
			return result;

		}
	}
}

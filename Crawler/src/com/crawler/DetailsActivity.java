package com.crawler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.crawler.adapter.CommentAdapter;
import com.crawler.adapter.NewsAdapter;
import com.crawler.app.AppApplication;
import com.crawler.base.BaseActivity;
import com.crawler.bean.ChannelItem;
import com.crawler.bean.ChannelManage;
import com.crawler.bean.CommentEntity;
import com.crawler.bean.CommentManage;
import com.crawler.bean.NewsEntity;
import com.crawler.tool.Constants;
import com.crawler.tool.DateTools;
import com.crawler.tool.Options;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.topnews.R;

public class DetailsActivity extends BaseActivity {

	private NewsEntity news;
	private PullToRefreshListView mPullRefreshListView;

	// 评论列表
	private LinkedList<CommentEntity> commentList = new LinkedList<CommentEntity>();
	// 当前段子ID
	private int newsId;
	// 当前评论ID
	private int lastOrder;
	private long last_timestamp;
	private ListView actualListView;
	private CommentAdapter mAdapter;
	public final static int SET_COMMENTLIST = 0;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	Activity activity;
	View viewContent;
	EditText input_comment;
	CommentManage commentManger = CommentManage.getManage(AppApplication.getApp().getSQLHelper());
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		View viewDetail = LayoutInflater.from(this).inflate(
				R.layout.list_item_detail, null);
		setContentView(viewDetail);
		ViewGroup pull_refresh_layout = (ViewGroup) findViewById(R.id.pull_refresh_comment);
		LayoutInflater.from(this).inflate(
				R.layout.pull_to_refresh_list_comment, pull_refresh_layout);


		mPullRefreshListView = (PullToRefreshListView) viewDetail
				.findViewById(R.id.pull_refresh_list_comment);

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				Log.d("mPullRefreshListView", "mPullRefreshListView = "
						+ mPullRefreshListView.hasPullFromTop());

				new GetDataTask().execute();

			}
		});
		actualListView = mPullRefreshListView.getRefreshableView();
		actualListView.setDividerHeight(2);

		options = Options.getListOptions();

		/* 获取传递过来的数据 */
		news = (NewsEntity) getIntent().getSerializableExtra("news");
		activity = this;
		// newsId = news.getNewsId();
		newsId = 60009;
		commentList = Constants.getCommentList(0, newsId, 0);
		//if (commentList.size() != 0) {
		//}
		if (commentList != null && commentList.size() != 0) {
			last_timestamp = commentList.getLast().getPublishTime();
			commentManger.deleteComment(newsId);
			commentManger.saveComment(commentList);
			handler.obtainMessage(SET_COMMENTLIST).sendToTarget();
		} else {
			commentList = (LinkedList<CommentEntity>)commentManger.getComment(0, newsId, 0);
			if (commentList != null && commentList.size() != 0) {
				last_timestamp = commentList.getLast().getPublishTime();
			}
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
					handler.obtainMessage(SET_COMMENTLIST).sendToTarget();
				}
			}).start();
		}
		initView();
		initCommentInput();
	}

	private void initCommentInput() {

		input_comment = (EditText) findViewById(R.id.input_comment);
		Button button_comment = (Button) findViewById(R.id.button_comment);
		button_comment.setTag(input_comment);
		button_comment.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				new GetInputDataTask().execute(String.valueOf(newsId),
						((EditText) v.getTag()).getText().toString());
			}

		});

	}

	private void initView() {

		// TODO Auto-generated method stub
		viewContent = LayoutInflater.from(this).inflate(
				R.layout.list_item_detail_content, null);
		TextView item_title = (TextView) viewContent
				.findViewById(R.id.item_title);
		TextView item_source = (TextView) viewContent
				.findViewById(R.id.item_source);
		ToggleButton btn_like = (ToggleButton) viewContent
				.findViewById(R.id.btn_like);
		TextView likes = (TextView) viewContent.findViewById(R.id.likes);
		btn_like.setTag(R.id.tag_detail_first, likes);
		
		TextView publish_time = (TextView) viewContent
				.findViewById(R.id.publish_time);
		ImageView item_image_0 = (ImageView) viewContent
				.findViewById(R.id.item_image_0);
		ImageView item_image_1 = (ImageView) viewContent
				.findViewById(R.id.item_image_1);
		ImageView item_image_2 = (ImageView) viewContent
				.findViewById(R.id.item_image_2);

		// 获取position对应的数据
		item_title.setText(news.getTitle());
		item_source.setText(news.getSource());

		likes.setText(String.valueOf(news.getLikes()));
		//comment_count.setText(String.valueOf(news.getCommentNum()));
		btn_like.setTag(R.id.tag_detail_second, newsId);
		publish_time.setText(DateTools.getStrTime_ymd_hms(String.valueOf(news
				.getPublishTime())));
		List<String> imgUrlList = news.getPicList();

		if (imgUrlList != null && imgUrlList.size() != 0) {

			for (int index = 0; index < imgUrlList.size(); index++) {
				switch (index) {
				case 0:
					imageLoader.displayImage(imgUrlList.get(0), item_image_0,
							options);
					break;
				case 1:
					imageLoader.displayImage(imgUrlList.get(1), item_image_1,
							options);
					break;
				case 2:
					imageLoader.displayImage(imgUrlList.get(2), item_image_2,
							options);
					break;
				default:
					break;
				}
			}

		} else {

		}

		btn_like.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {

					Integer likes = Integer.parseInt(((TextView) buttonView
							.getTag(R.id.tag_detail_first)).getText().toString());

					((TextView) buttonView.getTag(R.id.tag_detail_first)).setText(String
							.valueOf(likes + 1));

					new GetCommentDataTask().execute(1,
							(Integer) buttonView.getTag(R.id.tag_detail_second), 1);
					Log.d("isnotChecked", ((TextView) buttonView.getTag(R.id.tag_detail_first))
							.getText().toString());
				} else {
					// likes.setText("1024");
					Integer likes = Integer.parseInt(((TextView) buttonView
							.getTag(R.id.tag_detail_first)).getText().toString());

					((TextView) buttonView.getTag(R.id.tag_detail_first)).setText(String
							.valueOf(likes - 1));
					new GetCommentDataTask().execute(1,
							(Integer) buttonView.getTag(R.id.tag_detail_second), -1);
					Log.d("isnotChecked", ((TextView) buttonView.getTag(R.id.tag_detail_first))
							.getText().toString());

				}

			}
		});

		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_COMMENTLIST:
				Log.d("SET_COMMENTLIST", "SET_COMMENTLIST");
				if (mAdapter == null) {
					mAdapter = new CommentAdapter(activity, news, commentList);
					Log.d("mAdapter", "mAdapter");
				}
				actualListView.addHeaderView(viewContent);
				actualListView.setAdapter(mAdapter);
				// actualListView.setOnScrollListener(mAdapter);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};


	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {

			// Simulates a background job.
			String[] result = {};
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {

			// mListItems.addLast("Added last");
			lastOrder = commentList.size();
			LinkedList<CommentEntity> commentAddList = Constants
					.getCommentList(last_timestamp, newsId, lastOrder);
			// Log.d("newsAddList.toString()", newsAddList.toString());
			if (commentAddList.size() != 0) {
				Iterator<CommentEntity> it = commentAddList.iterator();
				while (it.hasNext()) {

					commentList.addLast(it.next());
					// Log.d("bottom_log_publishtime",
					// String.valueOf(newsAddList.get(index).getPublishTime()));

				}
				last_timestamp = commentAddList.getLast().getPublishTime();
				commentManger.deleteComment(newsId);
				commentManger.saveComment(commentList);
			} else {
		        // 网络问题
				commentAddList = (LinkedList<CommentEntity>)commentManger.getComment(last_timestamp, newsId, lastOrder);
				// Log.d("newsAddList.toString()", newsAddList.toString());
				if (commentAddList.size() != 0) {
					Iterator<CommentEntity> it = commentAddList.iterator();
					while (it.hasNext()) {

						commentList.addLast(it.next());
						// Log.d("bottom_log_publishtime",
						// String.valueOf(newsAddList.get(index).getPublishTime()));

					}
					last_timestamp = commentAddList.getLast().getPublishTime();
					
			}
			}
			
			//mAdapter.notifyDataSetInvalidated();
			mAdapter.notifyDataSetInvalidated();
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}

	}

	private class GetCommentDataTask extends AsyncTask<Integer, Void, String[]> {

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
		private class GetInputDataTask extends
				AsyncTask<String, Void, String[]> {

			@Override
			protected String[] doInBackground(String... params) {
				int newsId = Integer.valueOf(params[0]);
				String comment = params[1];
				// 获取IP
				// ip = Integer.valueOf(params[0]);
				String ip = "122.84.163.128";
				if (Constants.postCommentContent(newsId, comment, ip) == true) {
					//String str = "";
					//Log.d("inputcontent", "恢复TEXT");
					
					
					//input_comment.invalidate();
				}

				String[] result = {};
				return result;

			}
		}
	

}

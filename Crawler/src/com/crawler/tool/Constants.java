package com.crawler.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import android.os.Build;

import com.crawler.MainActivity;
import com.crawler.app.AppApplication;
import com.crawler.bean.CityEntity;
import com.crawler.bean.CommentEntity;
import com.crawler.bean.CommentManage;
import com.crawler.bean.ContentManage;
import com.crawler.bean.NewsEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.topnews.bean.NewsClassify;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class Constants {

	public static LinkedList<NewsEntity> getNewsList(long last_timestamp,
			int channel_id) {
		LinkedList<NewsEntity> newsListPage = new LinkedList<NewsEntity>();
		String urlNameString = newsUrl + last_timestamp + "&channel_id="
				+ channel_id;
		Log.d("getURL", urlNameString);
		String newsListJson = getData(urlNameString);
		if (newsListJson == "") {
			return newsListPage;
		}
		
		try {
			
			JSONObject newsListJsonObject = new JSONObject(newsListJson);
			for (int index = 0; (index < page_num) && (index < newsListJsonObject.length()); index++) {
				NewsEntity news = new NewsEntity();
				JSONObject newsListJsonItem = newsListJsonObject
						.getJSONObject(String.valueOf(index));
				news.setSource(newsListJsonItem.getString("source"));
				news.setNewsId(Integer.valueOf(newsListJsonItem.getString("newsid")));
				news.setCommentNum(Integer.valueOf(newsListJsonItem.getString("commentnum")));
				news.setLikes(Integer.valueOf(newsListJsonItem.getString("likes")));
				Log.d("source", newsListJsonItem.getString("source"));
				news.setTitle(newsListJsonItem.getString("content"));
				Log.d("content", newsListJsonItem.getString("content"));
				news.setPublishTime(Long.valueOf(newsListJsonItem.getString("publishtime")));
				news.setChannelId(Integer.valueOf(newsListJsonItem.getString("channelid")));
				//Log.d("setPublishTime", news.getPublishTime());
				// picurllist
				JSONArray newsListJsonItemPic = newsListJsonItem
						.getJSONArray("pic_urllist");
				
				List<String> url_list = new ArrayList<String>();
				for (int picIndex = 0; (picIndex < newsListJsonItemPic.length()) && (picIndex < pic_max); picIndex++) {
					url_list.add(newsListJsonItemPic.getString(picIndex));					
					newsListJsonItemPic.getString(picIndex);
				}
				news.setPicList(url_list);
				newsListPage.addLast(news);
				
			}
			// Log.d("newsListJsonArray",newsListJsonArray );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return newsListPage;
			//e.printStackTrace();
		}
		
		return newsListPage;
	}

	public static LinkedList<CommentEntity> getCommentList(long last_timestamp,
			int newsId, int lastOrder) {
		int order = lastOrder;
		LinkedList<CommentEntity> commentListPage = new LinkedList<CommentEntity>();
		String urlNameString = commentUrl + last_timestamp + "&news_id="
				+ newsId;
		Log.d("getURL", urlNameString);
		String commentListJson = getData(urlNameString);
		if (commentListJson == "") {
			return commentListPage;
		}
		
		try {
			
			JSONObject commentListJsonObject = new JSONObject(commentListJson);
			for (int index = 0; (index < page_num) && (index < commentListJsonObject.length()); index++) {
				CommentEntity comment = new CommentEntity();
				JSONObject commentListJsonItem = commentListJsonObject
						.getJSONObject(String.valueOf(index));
				comment.setLikesNum(Integer.valueOf(commentListJsonItem.getString("likes")));
				comment.setCommentId(Integer.valueOf(commentListJsonItem.getString("commentid")));
				
				comment.setContent(commentListJsonItem.getString("content"));
				comment.setNewsId(Integer.valueOf(commentListJsonItem.getString("newsid")));
				comment.setPosition(commentListJsonItem.getString("position"));
				comment.setPublishTime(Long.valueOf(commentListJsonItem.getString("publishtime")));
				//Log.d("source", commentListJsonItem.getString("source"));
				order++;
				comment.setOrder(order);
				
				
				commentListPage.addLast(comment);
				
			}
			// Log.d("newsListJsonArray",newsListJsonArray );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return commentListPage;
		}
		
		return commentListPage;
	}
	
	public static boolean postLikesCount(int newsId, int likes) {
		
		String  key = Md5(private_key + "newsId" + String.valueOf(newsId));
		
		String urlNameString = likesCountUrl + newsId + "&likes="
				+ likes + "&key=" + key;
		Log.d("postLikesCount", urlNameString);
		//String commentListJson = getData(urlNameString);
		
		if (getData(urlNameString).equals("success")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean postCommentLikesCount(int commentId, int likes) {
		
		String  key = Md5(private_key + "commentId" + String.valueOf(commentId));
		String urlNameString = commentLikesCountUrl + commentId + "&likes="
				+ likes + "&key=" + key;
		
		Log.d("postCommentLikesCount", urlNameString);
		//String commentListJson = getData(urlNameString);
		
		if (getData(urlNameString).equals("success")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean postCommentContent(int newsId, String comment, String ip) {
		String commentUrlencode;
		try {
			commentUrlencode = URLEncoder.encode(comment, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return false;
			//e.printStackTrace();
		}
		String  key = Md5(private_key + "newsId" + String.valueOf(newsId));
		String urlNameString = commentContentUrl + newsId + "&comment=" 
		+ commentUrlencode + "&ip=" + ip + "&key=" + key;
		Log.d("postCommentContent", urlNameString);
		//String commentListJson = getData(urlNameString);
		
		Log.d("getData(urlNameString)", getData(urlNameString));
		if (getData(urlNameString).equals("success")) {
			Log.d("postCommentContent", "success");
			return true;
		} else {
			Log.d("postCommentContent", "failed");
			return false;
			
		}
	}
	
	private static String getData(String urlNameString) {

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		String listJson = "";
		BufferedReader in = null;
		try {
			
			URL realUrl = new URL(urlNameString);
			// �򿪺�URL֮�������
			URLConnection connection = realUrl.openConnection();
			// ����ͨ�õ���������
			connection.setConnectTimeout(4000);
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// ����ʵ�ʵ�����
			connection.connect();
			// ��ȡ������Ӧͷ�ֶ�
			// Map<String, List<String>> map = connection.getHeaderFields();
			// �������е���Ӧͷ�ֶ�
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// ���� BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				listJson += line;
			}
		} catch (Exception e) {
			System.out.println("����GET��������쳣��" + e);
			
			//e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				//e2.printStackTrace();
			}
		}

		//Log.d("newsListJson", newsListJson);
		Log.d("listJson", listJson);
		return listJson;
	}

	/** ÿ��ˢ�µ���Ŀ���� */
	private final static int page_num = 10;
	/** ����ͼƬ���� */
	private final static int pic_max = 3;
	private final static String private_key = "59aleor10&jgibnr9la-qla[#";
	private final static String url = "http://192.168.3.104:8888";
	private final static String newsUrl = url + "/data/news?last_timestamp=";
	private final static String commentUrl = url + "/data/comment?last_timestamp=";
	
	//ͳ��ϲ����
	private final static String likesCountUrl = url + "/data/likescount?news_id=";
	
	//ͳ�Ƹ����۵�ϲ����
	private final static String commentLikesCountUrl = url + "/data/commentlikescount?comment_id=";
	
	//�ύ����
	private final static String commentContentUrl = url + "/data/commentcontent?news_id=";

	private final static CommentManage commentManger = CommentManage.getManage(AppApplication.getApp().getSQLHelper());
	private final static ContentManage contentManger = ContentManage.getManage(AppApplication.getApp().getSQLHelper());
	
	private final static ImageLoader imageLoader = ImageLoader.getInstance();
	private final static int channel_num = 3;
	
	private static String Md5(String plainText) {
		
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			
			Log.d("result: ", buf.toString());// 32λ�ļ���
			Log.d("result: ", buf.toString().substring(8, 24));// 16λ�ļ���
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return buf.toString();
		
	}
	
	// ������������
	public static boolean getOfflineData(int max) {

		
		// �����������
		commentManger.deleteAllComment();
		contentManger.deleteAllContent();
		LinkedList<NewsEntity> newsList;
		LinkedList<CommentEntity> commentList;
		DisplayImageOptions options = Options.getOfflineOptions();
		Bitmap bitmap = null;
		for (int channel_id = 1; channel_id < channel_num + 1; channel_id++) {
			long last_timestamp_news = Long.valueOf("2409904180");

			Log.d("max", String.valueOf(max));
			for (int index_page = 0; index_page < max + 1; index_page++) {
				newsList = getNewsList(last_timestamp_news, channel_id);
				
				int percent = ((((channel_id-1) * max) + index_page) * 100) / (channel_num * max);
				//dialog.setMessage(String.valueOf(percent) + "%");
				Log.d("percent", String.valueOf(percent));
				Log.d("index_page", String.valueOf(index_page));
				
				contentManger.saveContent(newsList);
				if (newsList.size() != 0) {
					last_timestamp_news = newsList.getLast().getPublishTime();
					Message msg = MainActivity.handle.obtainMessage();
					msg.what = percent;
					MainActivity.handle.sendMessage(msg);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				} else {
					Message msg = MainActivity.handle.obtainMessage();
					msg.what = -1;
					MainActivity.handle.sendMessage(msg);
					break;
				}
                
				for (int index_news = 0; index_news < newsList.size(); index_news++) {
					// ����ͼƬ
					
					Log.d("loadimage", "����ͼƬ");
					List<String> imgUrlList = newsList.get(index_news)
							.getPicList();

					if (imgUrlList != null && imgUrlList.size() != 0) {

						for (int index = 0; index < imgUrlList.size(); index++) {
							switch (index) {
							case 0:
								bitmap = imageLoader.loadImageSync(imgUrlList.get(0), options);
								break;
							case 1:
								bitmap = imageLoader.loadImageSync(imgUrlList.get(1), options);
								break;
							case 2:
								bitmap = imageLoader.loadImageSync(imgUrlList.get(2), options);
								break;
							default:
								break;
							}
							bitmap.recycle();
							bitmap = null;
						}

						// ��ȡ��Ӧ������
						commentList = getCommentList(0, newsList
								.get(index_news).getNewsId(), 0);
						commentManger.saveComment(commentList);
					}
				}
			}

		}
		
		return true;
	}
}



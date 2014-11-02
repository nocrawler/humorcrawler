package com.crawler.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.crawler.dao.ContentDao;
import com.crawler.db.SQLHelper;

import android.database.SQLException;
import android.util.Log;

public class ContentManage {
	public static ContentManage contentManage;
	/**
	 * 默认的用户选择频道列表
	 * */
	public static List<NewsEntity> NewsEntity;
	private final static String limit = "10";
	private ContentDao contentDao;

	private ContentManage(SQLHelper paramDBHelper) throws SQLException {
		if (contentDao == null)
			contentDao = new ContentDao(paramDBHelper.getContext());
		return;
	}

	/**
	 * 
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static ContentManage getManage(SQLHelper dbHelper)throws SQLException {
		if (contentManage == null)
			contentManage = new ContentManage(dbHelper);
		return contentManage;
	}

	/**
	 * 清除所有的评论
	 */
	public void deleteAllContent() {
		contentDao.clearFeedTable();
	}
	
	/**
	 * 清除某个内容的所有评论
	 */
	public void deleteContent(int channel_id) {
		contentDao.deleteCache(SQLHelper.CHANNEL_ID + "= ?", new String[] {String.valueOf(channel_id)});
	}
	
	/**
	 * 获取评论列表
	 * @return 
	 */
	public List<NewsEntity> getNews(long last_timestamp, int channel_id) {
		
		LinkedList<NewsEntity> contentList = new LinkedList<NewsEntity>();
		Object cacheList;
		if (channel_id != 1) {
		    cacheList = contentDao.listCache(SQLHelper.CHANNEL_ID + "= ? and " + SQLHelper.PUBLISHTIME + "< ?" ,new String[] { String.valueOf(channel_id), String.valueOf(last_timestamp)}, SQLHelper.PUBLISHTIME  + " desc", limit);
		} else {
			cacheList = contentDao.listCache(SQLHelper.PUBLISHTIME + "< ?" ,new String[] {String.valueOf(last_timestamp)}, SQLHelper.PUBLISHTIME  + " desc", limit);
		}
		if (cacheList != null && !((List<Map<String, String>>) cacheList).isEmpty()) {
			List<Map<String, String>> maplist = (List<Map<String, String>>) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				NewsEntity navigate = new NewsEntity();
				String [] picUrl = maplist.get(i).get(SQLHelper.PICURL).split(";");
				List<String> url_list = new ArrayList<String>();
				for (int picIndex = 0; (picIndex < picUrl.length); picIndex++) {
					url_list.add(picUrl[picIndex]);
				}
				
				navigate.setNewsId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setSource(maplist.get(i).get(SQLHelper.SOURCE));
				navigate.setTitle(maplist.get(i).get(SQLHelper.CONTENT));
				navigate.setLikes(Integer.valueOf(maplist.get(i).get(SQLHelper.LIKES)));
				navigate.setPicList(url_list);
				navigate.setChannelId(Integer.valueOf(maplist.get(i).get(SQLHelper.CHANNEL_ID)));
				navigate.setCommentNum(Integer.valueOf(maplist.get(i).get(SQLHelper.COMMENT_NUM)));
				navigate.setPublishTime(Long.valueOf(maplist.get(i).get(SQLHelper.PUBLISHTIME)));
				contentList.addLast(navigate);
			}
		}
		return contentList;
	}
	/**
	 * 保存评论
	 * @param 
	 */
	public void saveContent(List<NewsEntity> newsList) {
		for (int i = 0; i < newsList.size(); i++) {
			contentDao.addCache(newsList.get(i));
		}
	}
	
}

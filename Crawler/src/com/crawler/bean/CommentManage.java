package com.crawler.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.crawler.dao.CommentDao;
import com.crawler.db.SQLHelper;

import android.database.SQLException;
import android.util.Log;

public class CommentManage {
	public static CommentManage commentManage;
	/**
	 * Ĭ�ϵ��û�ѡ��Ƶ���б�
	 * */
	public static List<CommentEntity> commentEntity;
	private final static String limit = "10";
	private CommentDao commentDao;

	private CommentManage(SQLHelper paramDBHelper) throws SQLException {
		if (commentDao == null)
			commentDao = new CommentDao(paramDBHelper.getContext());
		return;
	}

	/**
	 * 
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static CommentManage getManage(SQLHelper dbHelper)throws SQLException {
		if (commentManage == null)
			commentManage = new CommentManage(dbHelper);
		return commentManage;
	}

	/**
	 * ������е�����
	 */
	public void deleteAllComment() {
		commentDao.clearFeedTable();
	}
	
	/**
	 * ���ĳ�����ݵ���������
	 */
	public void deleteComment(int newsId) {
		commentDao.deleteCache(SQLHelper.NEWS_ID + "= ?", new String[] {String.valueOf(newsId)});
	}
	
	/**
	 * ��ȡ�����б�
	 * @return 
	 */
	public List<CommentEntity> getComment(long last_timestamp, int news_id, int lastorder) {
		
		LinkedList<CommentEntity> commentList = new LinkedList<CommentEntity>();
		Object cacheList = commentDao.listCache(SQLHelper.NEWS_ID + "= ? and " + SQLHelper.PUBLISHTIME + "> ?" ,new String[] { String.valueOf(news_id), String.valueOf(last_timestamp)}, SQLHelper.PUBLISHTIME, limit);
		int order = lastorder;
		if (cacheList != null && !((List<Map<String, String>>) cacheList).isEmpty()) {
			List<Map<String, String>> maplist = (List<Map<String, String>>) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				CommentEntity navigate = new CommentEntity();
				navigate.setCommentId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setContent(maplist.get(i).get(SQLHelper.CONTENT));
				navigate.setPosition(maplist.get(i).get(SQLHelper.POSITION));
				navigate.setLikesNum(Integer.valueOf(maplist.get(i).get(SQLHelper.LIKES)));
				navigate.setPublishTime(Integer.valueOf(maplist.get(i).get(SQLHelper.PUBLISHTIME)));
				order++;
				navigate.setOrder(order);
				commentList.addLast(navigate);
			}
		}
		return commentList;
	}
	/**
	 * ��������
	 * @param 
	 */
	public void saveComment(List<CommentEntity> commentList) {
		for (int i = 0; i < commentList.size(); i++) {
			commentDao.addCache(commentList.get(i));
		}
	}
	
}

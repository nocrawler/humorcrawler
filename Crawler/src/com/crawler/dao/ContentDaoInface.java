package com.crawler.dao;

import java.util.List;
import java.util.Map;

import com.crawler.bean.NewsEntity;

import android.content.ContentValues;

public interface ContentDaoInface {
	public boolean addCache(NewsEntity item);

	public boolean deleteCache(String whereClause, String[] whereArgs);

	public boolean updateCache(ContentValues values, String whereClause,
			String[] whereArgs);

	public Map<String, String> viewCache(String selection,
			String[] selectionArgs);

	public List<Map<String, String>> listCache(String selection,
			String[] selectionArgs, String orderby, String limit);

	public void clearFeedTable();
}

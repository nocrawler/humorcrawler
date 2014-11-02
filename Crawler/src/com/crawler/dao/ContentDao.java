package com.crawler.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crawler.bean.NewsEntity;
import com.crawler.db.SQLHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContentDao implements ContentDaoInface {
	private SQLHelper helper = null;

	public ContentDao(Context context) {
		helper = new SQLHelper(context);
	}

	@Override
	public boolean addCache(NewsEntity item) {
		// TODO Auto-generated method stub
		boolean flag = false;
		SQLiteDatabase database = null;
		long id = -1;
		try {
			
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String picUrl = "";
			for (int picIndex = 0; (picIndex < item.getPicList().size()); picIndex++) {
				picUrl = item.getPicList().get(picIndex) + ";";					
			}
			Log.d("db_id", String.valueOf(item.getNewsId()));
			Log.d("db_source", item.getSource());
			Log.d("db_picurl", picUrl);
			Log.d("db_channel_id", String.valueOf(item.getChannelId()));
			Log.d("db_likes", String.valueOf(item.getLikes()));
			Log.d("db_comment_num", String.valueOf(item.getCommentNum()));
			Log.d("db_content", item.getTitle());
			Log.d("db_publishtime", String.valueOf(item.getPublishTime()));
			
			values.put("id", item.getNewsId());
			values.put("source", item.getSource());
			values.put("picurl", picUrl);
			values.put("channel_id", item.getChannelId());
			values.put("likes", item.getLikes());
			values.put("content", item.getTitle());
			values.put("comment_num", item.getCommentNum());
			values.put("publishtime", item.getPublishTime());
			
			id = database.insert(SQLHelper.CONTENT_TABLE, null, values);
			flag = (id != -1 ? true : false);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	@Override
	public boolean deleteCache(String whereClause, String[] whereArgs) {
		// TODO Auto-generated method stub
		boolean flag = false;
		SQLiteDatabase database = null;
		int count = 0;
		try {
			database = helper.getWritableDatabase();
			count = database.delete(SQLHelper.CONTENT_TABLE, whereClause, whereArgs);
			flag = (count > 0 ? true : false);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	@Override
	public boolean updateCache(ContentValues values, String whereClause,
			String[] whereArgs) {
		// TODO Auto-generated method stub
		boolean flag = false;
		SQLiteDatabase database = null;
		int count = 0;
		try {
			database = helper.getWritableDatabase();
			count = database.update(SQLHelper.CONTENT_TABLE, values, whereClause, whereArgs);
			flag = (count > 0 ? true : false);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	@Override
	public Map<String, String> viewCache(String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase database = null;
		Cursor cursor = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			database = helper.getReadableDatabase();
			cursor = database.query(true, SQLHelper.CONTENT_TABLE, null, selection,
					selectionArgs, null, null, null, null);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				for (int i = 0; i < cols_len; i++) {
					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return map;
	}

	@Override
	public List<Map<String, String>> listCache(String selection,String[] selectionArgs, String orderby, String limit) {
		// TODO Auto-generated method stub
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = helper.getReadableDatabase();
			cursor = database.query(false, SQLHelper.CONTENT_TABLE, null, selection,selectionArgs, null, null, orderby, limit);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < cols_len; i++) {

					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
				list.add(map);
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return list;
	}

	@Override
	public void clearFeedTable() {
		String sql = "DELETE FROM " + SQLHelper.CONTENT_TABLE + ";";
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(sql);
		revertSeq();
	}

	private void revertSeq() {
		String sql = "update sqlite_sequence set seq=0 where name='"
				+ SQLHelper.CONTENT_TABLE + "'";
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(sql);
	}

}

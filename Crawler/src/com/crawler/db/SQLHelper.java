package com.crawler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "database.db";// ���ݿ�����
	public static final int VERSION = 4;
	
	public static final String TABLE_CHANNEL = "channel";//���ݱ�
	public static final String COMMENT_TABLE = "comment_table";//���ݱ� 
	public static final String CONTENT_TABLE = "content_table";//���ݱ� 

	public static final String ID = "id";//
	public static final String NAME = "name";
	public static final String ORDERID = "orderId";
	public static final String SELECTED = "selected";
	
	//comment_table
	
	public static final String NEWS_ID = "news_id";
	public static final String CONTENT = "content";
	public static final String POSITION = "position";
	public static final String LIKES = "likes";
	public static final String PUBLISHTIME = "publishtime";
	
	// content_table
	
	public static final String SOURCE = "source";
	public static final String PICURL = "picurl";
	public static final String CHANNEL_ID = "channel_id";
	public static final String COMMENT_NUM = "comment_num";
	
	
	private Context context;
	public SQLHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}

	public Context getContext(){
		return context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//String sql = "drop table if exists " + TABLE_CHANNEL;
		//db.execSQL(sql);
		//sql = "drop table if exists " + CONTENT_TABLE;
		//db.execSQL(sql);
		//sql = "drop table if exists " + COMMENT_TABLE;
		//db.execSQL(sql);
		
		// TODO �������ݿ�󣬶����ݿ�Ĳ���
		String sql = "create table if not exists "+TABLE_CHANNEL +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ID + " INTEGER , " +
				NAME + " TEXT , " +
				ORDERID + " INTEGER , " +
				SELECTED + " SELECTED)";
		db.execSQL(sql);
		
		//������comment_table
		sql = "create table if not exists "+COMMENT_TABLE +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ID + " INTEGER , " +
				NEWS_ID + " INTEGER , " +
				CONTENT + " TEXT , " +
				POSITION + " TEXT , " +
				LIKES + " INTEGER , " +
				PUBLISHTIME + " INTEGER)";
		db.execSQL(sql);
		
		
		//������content_table
		sql = "create table if not exists "+CONTENT_TABLE +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ID + " INTEGER , " +
				SOURCE + " TEXT , " +
				PICURL + " TEXT , " +
				CHANNEL_ID + " TEXT , " +
				CONTENT + " TEXT , " +
				COMMENT_NUM + " INTEGER , " +
				LIKES + " INTEGER , " +
				PUBLISHTIME + " INTEGER)";
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO �������ݿ�汾�Ĳ���
		onCreate(db);
	}

}

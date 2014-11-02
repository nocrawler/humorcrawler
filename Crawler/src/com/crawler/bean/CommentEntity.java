package com.crawler.bean;

import java.io.Serializable;
import java.util.List;

public class CommentEntity implements Serializable {
	
	/** 喜欢数量 */
	private Integer likesNum;
	/** 评论ID **/
	private Integer commentId;
	/** 内容ID **/
	private Integer newsId;
	/** 评论内容 */
	private String content;
	/** 用户位置 */
	private String position;
	/** 发布时间 */
	private Long publishTime;
	/**楼层**/
	private Integer order;
	

	public Integer getLikesNum() {
		return likesNum;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}
	
	public Integer getNewsId() {
		return newsId;
	}

	public void setLikesNum(Integer likesNum) {
		this.likesNum = likesNum;
	}
	
	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	public Long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}

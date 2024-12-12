package com.event.cia103g1springboot.bb.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "bulletinBoard")
public class BBVO implements java.io.Serializable{

	private static final long serialVersionUID = 1L;

	private Integer msgid;

	private Byte msgtype;

	private String msgtitle;

	private String msgcon;

	private Byte poststat;

	private Timestamp posttime;

	@Column(name = "isPinned", nullable = false)
	private Boolean isPinned = false;

	public BBVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BBVO(Integer msgid, Byte msgtype, String msgtitle, String msgcon, Byte poststat, Timestamp posttime,
			Boolean isPinned) {
		super();
		this.msgid = msgid;
		this.msgtype = msgtype;
		this.msgtitle = msgtitle;
		this.msgcon = msgcon;
		this.poststat = poststat;
		this.posttime = posttime;
		this.isPinned = isPinned;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "msgId", updatable = false)
	public Integer getMsgid() {
		return this.msgid;
	}
	public void setMsgid(Integer msgid) {
		this.msgid = msgid;
	}

	@Column(name = "msgType")
	@NotNull(message="公告類型: 請勿空白")
	@Min(value = 0, message = "公告類型值無效")
	@Max(value = 3, message = "公告類型值無效")
	public Byte getMsgtype() {
		return this.msgtype;
	}
	public void setMsgtype(Byte msgtype) {
		this.msgtype = msgtype;
	}

	@Column(name = "msgTitle")
	@NotEmpty(message="公告標題: 請勿空白")
	public String getMsgtitle() {
		return this.msgtitle;
	}
	public void setMsgtitle(String msgtitle) {
		this.msgtitle = msgtitle;
	}

	@Column(name = "msgCon")
	@NotEmpty(message="公告內容: 請勿空白")
	public String getMsgcon() {
		return this.msgcon;
	}
	public void setMsgcon(String msgcon) {
		this.msgcon = msgcon;
	}

	@Column(name = "postStat")
	@NotNull(message="發布狀態: 請勿空白")
	@Min(value = 0, message = "發布狀態只能為發佈或未發佈")
	@Max(value = 1, message = "發布狀態只能為發佈或未發佈")
	public Byte getPoststat() {
		return this.poststat;
	}
	public void setPoststat(Byte poststat) {
		this.poststat = poststat;
	}

	@Column(name = "postTime")
	public Timestamp getPosttime() {
		return this.posttime;
	}
	public void setPosttime(Timestamp posttime) {
		this.posttime = posttime;
	}

	public Boolean getIsPinned() {
	    return isPinned;
	}

	public void setIsPinned(Boolean isPinned) {
	    this.isPinned = isPinned;
	}


}

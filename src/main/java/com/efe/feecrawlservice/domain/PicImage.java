//package com.efe.feecrawlservice.domain;
//
//import java.io.Serializable;
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
///**
// * 图片实体类
// * 
// * @author liutianlong
// *
// */
//@Getter
//@Setter
//@NoArgsConstructor
////@Entity
////@Table(name="pic_recover_images")
//public class PicImage implements Serializable {
//	private static final long serialVersionUID = 1L;
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
//    @SequenceGenerator(sequenceName = "pic_images_seq", allocationSize = 1, name = "CUST_SEQ")
//	private Long id;
//	// 圖片名称
//	private String name;
//	// 生成的HTTP路径
//	private String url;
//	// 扩展名
//	private String extension;
//	// 存储的绝对路径
//	@Column(name = "absolute_path")
//	private String absolutePath;
//	// 存储的相对路径
//	@Column(name = "relative_path")
//	private String relativePath;
//	// 上传时间
//	@Column(name = "upload_date")
//	private Date uploadDate;
//	// 上传人ID
//	@Column(name = "user_id")
//	private String userId = "21";
//	// 上传人名称
//	@Column(name = "user_name")
//	private String userName = "系统管理员";
//	// 备注
//	private String remark = "从原图库文件目录恢复";
//	// 缩放文件的绝对路径
//	@Column(name = "small_absolute_path")
//	private String smallAbsolutePath;
//	// 缩放文件的相对路径
//	@Column(name = "small_relative_path")
//	private String smallRelativePath;
//	// 账号ID
//	@Column(name = "account_id")
//	private String accountId;
//	// 缩放版的URL
//	@Column(name = "small_url")
//	private String smallUrl;
//	// 账号名称
//	@Column(name = "account_name")
//	private String accountName;
//	// 平台
//	private String platform;
//	// 宽（单位：像素）
//	private Integer width;
//	// 高（单位：像素）
//	private Integer height;
//	// 大小（单位：字节）
//	private Long length;
//	// 状态(0: 不可用,1:可用)
//	private Integer status = 1;
//	// 市场（国内市场、海外市场）
//	private String market = "OVERSEAS";
//
//	public static class Status {
//		public static final int AVAILABLE = 1; // 可用
//		public static final int UNAVAILABLE = 0; // 不可用
//	}
//
//}

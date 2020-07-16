package com.efe.feecrawlservice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * amazon费用爬取任务
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "amazon_fee_task")
@org.hibernate.annotations.Table(appliesTo = "amazon_fee_task",comment = "amazon费用采集任务表")
@Getter
@Setter
@NoArgsConstructor
public class FeeCrawlTask extends SeriEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "account_id",columnDefinition = "varchar(50) default '' comment '账号ID'")
	private String accountId;
	@Column(name = "account_name",columnDefinition = "varchar(100) default '' comment '账号名称'")
	private String accountName;
	@Column(name= "market_id",columnDefinition = "varchar(50) default '' comment '站点ID'")
	private String marketId;
	@Column(name= "market_name",columnDefinition = "varchar(100) default '' comment '站点名称'")
	private String marketName;
	@Column(columnDefinition = "int(2) comment '状态(0:未处理,1:处理中,2:处理失败,3:失败异常)'")
	private Integer status = 0;
	@Column(columnDefinition = "text comment '信息'")
	private String message;
	@Column(name= "create_time",columnDefinition = "datetime comment '创建时间'")
	private Date createTime;
	@Column(name= "driver_type",columnDefinition = "varchar(50) comment '驱动类型(chrome,firefox等等)'")
	private String driverType;
	
	
	public static class Status{
		public static final int UN_PROCESS = 0;          // 未处理 
		public static final int PROCESSING = 1;          // 处理中 
		public static final int PROCESSED = 2;           // 处理结束
		public static final int PROCESS_EXCEPTION = 3;   // 处理异常
		
	}
}

package com.efe.feecrawlservice.domain;

import java.math.BigDecimal;
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
 * amazon费用爬取下载的txt文件解析后的数据明细
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "amazon_fee_txt_detail")
@org.hibernate.annotations.Table(appliesTo = "amazon_fee_txt_detail",comment = "amazon费用采集TXT文件数据明细")
@Getter
@Setter
@NoArgsConstructor
public class FeeCrawlTxtDetail extends SeriEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="task_id",columnDefinition = "comment '任务ID'")
	private Long taskId;
	@Column(name = "account_id",columnDefinition = "varchar(50) default '' comment '账号ID'")
	private String accountId;
	@Column(name = "account_name",columnDefinition = "varchar(100) default '' comment '账号名称'")
	private String accountName;
	@Column(name= "market_id",columnDefinition = "varchar(50) default '' comment '站点ID'")
	private String marketId;
	@Column(name= "market_name",columnDefinition = "varchar(100) default '' comment '站点名称'")
	private String marketName;
	@Column(name= "create_time",columnDefinition = "datetime comment '创建时间'")
	private Date createTime;
	
	@Column(name= "data_type",columnDefinition = "varchar(100) default '' comment '数据类型'")
	private String dataType;
	@Column(name= "date_range",columnDefinition = "varchar(100) default '' comment '时间区间'")
	private String dateRange;
	@Column(name= "trans_date",columnDefinition = "varchar(100) default '' comment '交易日期'")
	private String transDate;
	@Column(name= "order_no",columnDefinition = "varchar(100) default '' comment '订单编号'")
	private String orderNo;
	@Column(columnDefinition = "varchar(100) default '' comment 'SKU'")
	private String sku;
	@Column(name= "trans_type",columnDefinition = "varchar(100) default '' comment '交易类型'")
	private String transType;
	@Column(name= "pay_type",columnDefinition = "varchar(100) default '' comment '付款类型'")
	private String payType; 
	@Column(name= "pay_detail",columnDefinition = "varchar(100) default '' comment '付款详情'")
	private String payDetail;
	@Column(columnDefinition = "varchar(100) default '' comment '金额'")
	private String amount;
	@Column(name= "amount_num",columnDefinition = "decimal(12,2) comment '金额(数值)'")
	private BigDecimal amountNum;
	@Column(columnDefinition = "varchar(100) default '' comment '货币类型'")
	private String currency;
	@Column(columnDefinition = "int comment '数量'")
	private Integer count;
	@Column(name= "product_name",columnDefinition = "varchar(1000) default '' comment '商品名称'")
	private String productName;
	@Column(name= "file_name",columnDefinition = "varchar(100) default '' comment '文件名称'")
	private String fileName;
	@Column(name= "file_path",columnDefinition = "varchar(500) default '' comment '文件存放路径（绝对路径）'")
	private String filePath;
	
}

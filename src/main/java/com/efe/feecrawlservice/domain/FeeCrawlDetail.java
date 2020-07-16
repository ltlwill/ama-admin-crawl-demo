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
 * amazon费用爬取数据明细
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "amazon_fee_detail")
@org.hibernate.annotations.Table(appliesTo = "amazon_fee_detail",comment = "amazon费用采集数据明细")
@Getter
@Setter
@NoArgsConstructor
public class FeeCrawlDetail extends SeriEntity{

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
	@Column(name= "trans_type",columnDefinition = "varchar(100) default '' comment '交易类型'")
	private String transType;
	@Column(name= "order_no",columnDefinition = "varchar(100) default '' comment '订单编号'")
	private String orderNo;
	@Column(name= "product_name",columnDefinition = "varchar(1000) default '' comment '商品名称'")
	private String productName;
	@Column(name= "product_price",columnDefinition = "varchar(100) default '' comment '商品价格总额'")
	private String productPrice;
	@Column(name= "product_price_num",columnDefinition = "decimal(12,2) comment '商品价格总额（数字）'")
	private BigDecimal productPriceNum;
	@Column(name= "promotion_fee",columnDefinition = "varchar(100) default '' comment '促销返点总额'")
	private String promotionFee;
	@Column(name= "promotion_fee_num",columnDefinition = "decimal(12,2) comment '促销返点总额（数字）'")
	private BigDecimal promotionFeeNum;
	@Column(name= "amazon_fee",columnDefinition = "varchar(100) default '' comment '亚马逊所收费用'")
	private String amazonFee;
	@Column(name= "amazon_fee_num",columnDefinition = "decimal(12,2) comment '亚马逊所收费用（数字）'")
	private BigDecimal amazonFeeNum;
	@Column(name= "other_fee",columnDefinition = "varchar(100) default '' comment '其他'")
	private String otherFee;
	@Column(name= "other_fee_num",columnDefinition = "decimal(12,2) comment '其他（数字）'")
	private BigDecimal otherFeeNum;
	@Column(name= "total_income",columnDefinition = "varchar(100) default '' comment '总计'")
	private String totalIncome;
	@Column(name= "total_income_num",columnDefinition = "decimal(12,2) comment '总计（数字）'")
	private BigDecimal totalIncomeNum;
}

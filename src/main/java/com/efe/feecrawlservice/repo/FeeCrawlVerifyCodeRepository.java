package com.efe.feecrawlservice.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.efe.feecrawlservice.domain.FeeCrawlVerifyCode;

public interface FeeCrawlVerifyCodeRepository extends JpaRepository<FeeCrawlVerifyCode, Long>,
		JpaSpecificationExecutor<FeeCrawlVerifyCode> {
//	@Query(value="select * from amazon_fee_verify_code a where a.account_Id = ?1 and a.market_Id = ?2 and a.deviceid = ?3 order by a.createTime desc limit 1",nativeQuery=true)
//	@Query(value="select * from amazon_fee_verify_code a where a.account_Id = ?1 and a.market_Id = ?2 and a.deviceid = ?3 order by a.createTime desc",nativeQuery=true)
	@Query(value="select a from FeeCrawlVerifyCode a where a.accountId = ?1 and a.marketId = ?2 and a.deviceid = ?3 and a.status = 0 order by a.createTime desc") 
	List<FeeCrawlVerifyCode> getNewestValidByParams(String accountId,String marketId,String deviceid);
}

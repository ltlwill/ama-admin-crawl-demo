package com.efe.feecrawlservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.efe.feecrawlservice.domain.FeeCrawlDetail;
import com.efe.feecrawlservice.domain.FeeCrawlTxtDetail;

public interface FeeCrawlTxtDetailRepository
		extends JpaRepository<FeeCrawlTxtDetail, Long>, JpaSpecificationExecutor<FeeCrawlTxtDetail> {

}

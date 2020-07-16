package com.efe.feecrawlservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.efe.feecrawlservice.domain.FeeCrawlDetail;

public interface FeeCrawlDetailRepository
		extends JpaRepository<FeeCrawlDetail, Long>, JpaSpecificationExecutor<FeeCrawlDetail> {

}

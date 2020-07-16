package com.efe.feecrawlservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.efe.feecrawlservice.domain.FeeCrawlTask;

public interface FeeCrawlTaskRepository
		extends JpaRepository<FeeCrawlTask, Long>, JpaSpecificationExecutor<FeeCrawlTask> {

}

package com.wumin.core.dao;

import com.wumin.core.entity.Annex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnexDao extends JpaRepository<Annex, String>, JpaSpecificationExecutor<Annex> {

	Annex findByPath(String path);
	
}

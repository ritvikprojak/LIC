package com.projak.lic.repository;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.jpa.repository.query.Jpa21Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projak.lic.dao.Vendor;


	public interface VendorRepository extends JpaRepository<Vendor,String>{

		 @Query(value = "select * from Vendor where user_id = ?1", nativeQuery = true)
		 public Vendor fetchDetails(String user_id);

		 
		
		
		
	}
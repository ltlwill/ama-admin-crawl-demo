//package com.efe.feecrawlservice.domain;
//
//import java.io.Serializable;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@SuppressWarnings("serial")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
////@Entity
////@Table(name="pic_recover_images_log")
//public class RecoverLog implements Serializable{
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
//    @SequenceGenerator(sequenceName = "pic_recover_images_log_seq", allocationSize = 1, name = "CUST_SEQ")
//	private Long id;
//	
//	@Column(name = "file_name")
//	private String fileName;
//	
//	private String message;
//
//}

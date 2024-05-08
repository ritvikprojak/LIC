package com.projak.lic.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projak.lic.dao.Vendor;
import com.projak.lic.repository.VendorRepository;
import com.projak.lic.service.LicService;

@RestController
@CrossOrigin
@RequestMapping("/laps")
public class LicController 
{
	private static final Logger logger = LoggerFactory.getLogger(LicController.class);
	
	@Autowired
	private LicService licService;
	
	@Autowired
	private VendorRepository repository;
	
	
	@GetMapping(value = "/demo")
	public ResponseEntity<String> getEmployeeDetails()
	{     
		
		logger.info("Inside demo of LicController Controller ");
		return ResponseEntity.ok().body("{\"status\" : \"UP\"}");
	}
	
	@GetMapping(value = "/getEmpDtl" , produces = "application/json")
	public ResponseEntity<String> getEmployeeDetails(@RequestParam(value = "srNo") String srNo)
	{
		logger.info("Inside getEmpDtl method of LicController Controller ");
		String empdetails = licService.getEmployeeDetails(srNo);
		logger.info("Response of getEmpDtl method of LicController Controller :"+empdetails);
		return ResponseEntity.ok().body(empdetails.toString());	
		
	}
	
	@GetMapping(value = "/getLocDtl" , produces = "application/json")
	public ResponseEntity<String> getEmployeeLocations(@RequestParam(value = "srNo") String srNo, @RequestParam(value="filter") String filter)
	{
		logger.info("Inside getLocDtl method of LicController Controller ");
		String empLocations = licService.getEmployeeLocationLocations(srNo, filter);
		logger.info("Response of getLocDtl method of LicController Controller :"+empLocations);
		return ResponseEntity.ok().body(empLocations.toString());	
	}
	
	@GetMapping(value = "/getLoanDtl", produces = "application/json")
	public ResponseEntity<String> getLoanDetails(@RequestParam(value = "number") String number, @RequestParam(value = "filter") String filter)
	{
		logger.info("Inside getLoanDtl method of LicController Controller ");
		String loanDetails = licService.getLoanDetails(number, filter);
		logger.info("Response of getLoanDtl method of LicController Controller :"+loanDetails);
		return ResponseEntity.ok().body(loanDetails.toString());
	}
	
	@GetMapping(value = "/getUserDetails", produces = "application/json")
	public ResponseEntity<String> getUserDetails(@RequestParam(value = "srNo") String srNo)
	{
		logger.info("Inside getUserDetails method of LicController Controller ");
		String userDetails = licService.getUserDetails(srNo);
		logger.info("Response of getUserDetails method of LicController Controller :"+userDetails);
		return ResponseEntity.ok().body(userDetails.toString());
	}
	
	@GetMapping(value = "/getDocList", produces = "application/json")
	public ResponseEntity<String> getDocList()
	{
		logger.info("Inside getDocList method of LicController Controller ");
		String docList = licService.getDocList();
		logger.info("Response of getUserDetails method of LicController Controller :"+docList);
		return ResponseEntity.ok().body(docList.toString());
	}
	
	  @GetMapping({"/vendors"})
	  public ResponseEntity<Vendor> getVendorDetails(@RequestParam(value = "user_id") String user_id) {
	    System.out.println(user_id);
	    Vendor vendors = this.repository.fetchDetails(user_id);
	    if(vendors != null ) {
	    if (vendors.getSUPERVISOR_ID() == null || vendors.getSUPERVISOR_ID().isEmpty()) {
	    	vendors.setTYPE("S");
	    	
	    }else  {
	    	
	    	vendors.setTYPE("O");	
	    }
	    }else {
	    	 return ResponseEntity.ok().body(null);	
	    }
	    	
	    return ResponseEntity.ok().body(vendors);
	   
	  }

}

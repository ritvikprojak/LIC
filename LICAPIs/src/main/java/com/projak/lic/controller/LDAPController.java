package com.projak.lic.controller;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projak.lic.exception.APIErrorResponse;
import com.projak.lic.exception.APIResponse;
import com.projak.lic.service.LDAPService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/lic")
public class LDAPController {

	private static final Logger logger = LoggerFactory.getLogger(LDAPController.class);
	
   @Autowired
   LDAPService service;
   
   @PostMapping(value = "/addUserToGroupAndRole", produces = "application/json")
	public final ResponseEntity<?> addUserToGroupAndRole(@RequestParam(name = "userName") String userName, @RequestParam(name = "groupName") String groupName, 
			@RequestParam(name = "roleName") String roleName) {
		
		  //return new ResponseEntity<>(ldapservice.addUserToGroup(userName, groupName), HttpStatus.OK);
		
		String addUser = null;
		try 
		{			
			logger.info("Inside try block of addUserToGroupAndRole of LDAP Controller ");
			addUser = service.addUserToGroupAndRole(userName, groupName, roleName);
			logger.info("response of addUserToGroupAndRole method :"+addUser);
			if(addUser.equalsIgnoreCase("user added") || addUser.equalsIgnoreCase("user Added Succesfully")) {
				APIResponse response = new APIResponse("Success", addUser);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else {
			APIResponse response = new APIResponse("Fail", addUser);
			return new ResponseEntity<>(response, HttpStatus.OK);
			}
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			logger.debug("Exception caught in addUserToGroupAndRole of LDAP Controller :",e.getMessage());

			APIErrorResponse response = new APIErrorResponse(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
					"Exception caught in addUserToGroupAndRole of LDAP controller", e.getMessage());

			return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
					
		}
		
		
   }
	
	
	
	@PostMapping(value = "/removeUserFromGroupAndRole", produces = "application/json")
	public final ResponseEntity<?> removeUserFromGroupAndRole(@RequestParam(name = "userName") String userName, @RequestParam(name = "groupName") String groupName,
			@RequestParam(name= "roleName") String roleName) 
	{

		try {
		  logger.info("Inside removeUserFromGroupAndRole of LDAP Controller ");
		  String UserFromRole = service.removeUserFromRole(userName, groupName, roleName);
		  logger.info("response of removeUserFromGroupAndRole method :"+ UserFromRole);
		  if(UserFromRole.equalsIgnoreCase("user removed successfully")) {
				APIResponse response = new APIResponse("Success", UserFromRole);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else {
			APIResponse response = new APIResponse("Fail", UserFromRole);
			return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.debug("Exception caught in removeUserFromGroupAndRole of LDAP Controller :",e.getMessage());

			APIErrorResponse response = new APIErrorResponse(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
					"Exception caught in removeUserFromGroupAndRole of LDAP controller", e.getMessage());

			return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
		}
	}


}
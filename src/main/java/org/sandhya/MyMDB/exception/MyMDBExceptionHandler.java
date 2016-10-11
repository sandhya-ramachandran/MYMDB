package org.sandhya.MyMDB.exception;
import java.util.HashMap;
import java.util.Map;

import org.sandhya.MyMDB.util.MyMDBConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
public class MyMDBExceptionHandler {
	
		
		public ResponseEntity<Map<String,Object>> handleExceptionAsEntity(Exception e) {
			Map<String,Object> responseMap = new HashMap<String,Object>();
			responseMap.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_FAILURE);
			try{
				throw(e);
			}catch(DataAccessException dae){
				//All MYSQL related issues
				responseMap.put(MyMDBConstants.JSON_KEY_HTTP_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
				responseMap.put(MyMDBConstants.JSON_KEY_ERROR_MESSAGE, "Internal Server Errror.Data Access Failure");
				return  new ResponseEntity<Map<String,Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			catch(MyMDBBadRequestException ce){
				responseMap.put(MyMDBConstants.JSON_KEY_HTTP_STATUS_CODE, HttpStatus.BAD_REQUEST);
				responseMap.put(MyMDBConstants.JSON_KEY_ERROR_MESSAGE, ce.getMessage());
				return  new ResponseEntity<Map<String,Object>>(responseMap, HttpStatus.BAD_REQUEST);
			}
			catch( Exception ex){
				responseMap.put(MyMDBConstants.JSON_KEY_HTTP_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
				responseMap.put(MyMDBConstants.JSON_KEY_ERROR_MESSAGE, "internal");
				return  new ResponseEntity<Map<String,Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

}

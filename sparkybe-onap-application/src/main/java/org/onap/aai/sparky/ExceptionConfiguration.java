/**
 * ============LICENSE_START=======================================================
 * SPARKY (AAI UI service)
 * ================================================================================
 * Copyright � 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright � 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.sparky;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.aai.sparky.exception.GenericServiceException;
import org.onap.aai.sparky.exception.ProxyErrorDetails;
import org.onap.aai.sparky.exception.ProxyServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@RestController
public class ExceptionConfiguration extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ProxyServiceException.class)
  public final ResponseEntity<ProxyErrorDetails> handleProxyException(ProxyServiceException ex, WebRequest request) {
	  Date curDate = new Date();
      SimpleDateFormat format = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	  String DateToStr = format.format(curDate);
	  ProxyErrorDetails errorDetails = new ProxyErrorDetails(
	    		DateToStr, ex.getMessage(),request.getDescription(false),"REQUEST PROCESSING ERROR","500");   
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(GenericServiceException.class)
  public final ResponseEntity<ProxyErrorDetails> handleGenericException(GenericServiceException ex, WebRequest request) {
	  Date curDate = new Date();
      SimpleDateFormat format = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	  String DateToStr = format.format(curDate);
	  String[] msg = ex.getMessage().split("resultCode:");
	  int statusCode = Integer.valueOf(msg[1]);
	  HttpStatus hs = HttpStatus.valueOf(statusCode);
	  Matcher m = Pattern.compile("\\[\"(.*?)\"\\,").matcher(msg[0]);
	  
	  String message="";
	  while(m.find()) {
		  message=m.group(0);
	  }
	  message=message.replaceAll("\\[\\\"", "").replaceAll("\"\\,","");
	  ProxyErrorDetails errorDetails = new ProxyErrorDetails(
    		DateToStr,message,request.getDescription(false),hs.name().toString().toUpperCase(),msg[1]);    
    return new ResponseEntity<>(errorDetails, hs);
  }
}
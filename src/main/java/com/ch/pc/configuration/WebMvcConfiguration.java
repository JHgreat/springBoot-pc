package com.ch.pc.configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ch.pc.service.ExpiredReservation;
import com.ch.pc.service.SessionChk;
@Configuration
public class WebMvcConfiguration  implements WebMvcConfigurer{
	// sessionCheck
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SessionChk())
			.excludePathPatterns()
			.addPathPatterns("/**/updateForm.do","/**/mypageForm.do","/**/passChkForm.do","/**/pcMainForm.do","/**/reservationForm.do");
		registry.addInterceptor(ExpiredReservation())
			.excludePathPatterns()
			.addPathPatterns("/**/*.do");
	}
	 @Bean
	 public ExpiredReservation ExpiredReservation(){
	    	return new ExpiredReservation();
	    }
	
}
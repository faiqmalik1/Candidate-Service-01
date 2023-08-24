package com.voting.system.usercandidate.feignController;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientInterceptorConfig {

  @Bean
  public RequestInterceptor bearerTokenInterceptor() {
    return new RequestInterceptor() {
      @Override
      public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
          String token = getHeaderValue(requestAttributes.getRequest());
          if (token != null && !token.isEmpty()) {
            template.header("Authorization", "Bearer " + token);
          }
        }
      }
    };
  }

  private String getHeaderValue(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null) {
      token = token.replace("Bearer ", token);
      return token;
    }
    return null;
  }
}

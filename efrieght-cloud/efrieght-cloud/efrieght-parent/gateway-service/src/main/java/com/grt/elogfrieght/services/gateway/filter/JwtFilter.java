/**
 * 
 */
package com.grt.elogfrieght.services.gateway.filter;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.grt.elogfrieght.services.gateway.util.JwtTokenUtil;

import reactor.core.publisher.Mono;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

	public JwtFilter() {
		super(Config.class);
	}

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			log.info("First pre filter {}" , exchange.getRequest());
			if (exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
				String accesstoken = token.replace("Bearer ", "");
				log.info("token : {} " , token);
				if (jwtTokenUtil.validateToken(accesstoken)) {
					if(exchange.getRequest().getCookies().containsKey("refeshToken")) {
						Optional<HttpCookie> refreshCookie = exchange.getRequest().getCookies().get("refeshToken").stream().findFirst();
						if (refreshCookie.isPresent()) {
							HttpCookie httpCookie = refreshCookie.get();
							String refreshToken = httpCookie.getValue();
							if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
								
							} else {
								throw new RuntimeException("session Expired login again");
							}
						}else {
							throw new RuntimeException("session Expired login again");
						}
					}
				}
//				chain.filter(exchange);
			}
			log.info("First post filter");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			}));
		};
	}

	public static class Config {

	}
}

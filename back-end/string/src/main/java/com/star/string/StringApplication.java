package com.star.string;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.util.TimeZone;

@SpringBootApplication
public class StringApplication {

	@PostConstruct
	// 设置时区为utc
	void started(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	private  static final Logger LOG= LoggerFactory.getLogger(StringApplication.class);
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(StringApplication.class);
		Environment env = app.run(args).getEnvironment();
		LOG.info(("启动成功！"));
		LOG.info(("地址：\thttp://127.0.0.1:{}"),env.getProperty("server.port"));
	}

}

package com.fss.pos.scheduler;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.StaticStore;

@Configuration
@Component
public class PosTaskScheduler {

	@Bean(name = "posScheduledThreadPool")
	public ScheduledThreadPoolExecutor getExecutor() {
		ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(5);
		s.setMaximumPoolSize(10);
		s.setThreadFactory(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "posEventThread");
			}
		});
		return s;
	}

	@Autowired
	private ScheduledThreadPoolExecutor posScheduledThreadPool;

	@Autowired
	private ApplicationContext appContext;

	@PostConstruct
	public void init() {
		Set<String> schemaSet = StaticStore.schemaMap.keySet();
		for (String msp : schemaSet) {
			ReversalTask rt = (ReversalTask) appContext.getBean("reversalTask",
					msp);
			posScheduledThreadPool.scheduleAtFixedRate(rt, 0, 10,
					TimeUnit.SECONDS);
		}
	}

	@PreDestroy
	public void destroy() {
		posScheduledThreadPool.shutdown();
	}

}

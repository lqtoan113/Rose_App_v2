package com.rose;

import com.rose.services.impl.OrderServiceImpl;
import com.rose.services.impl.SmsServiceImpl;
import com.rose.threads.AutomationAcceptOrderThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationListener;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableElasticsearchRepositories
@EnableCaching
public class RoseStoreApplication {
    public static void main(String[] args){
        SpringApplication.run(RoseStoreApplication.class, args);

    }
    @Component
    public static class ApplicationStartup
            implements ApplicationListener<ApplicationReadyEvent> {

        @Autowired
        @Qualifier("myThreadPool")
        private ThreadPoolTaskExecutor executor;
        @Autowired private OrderServiceImpl orderService;
        @Autowired private SmsServiceImpl smsService;

        /**
         * This event is executed as late as conceivably possible to indicate that
         * the application is ready to service requests.
         */
        @Override
        public void onApplicationEvent(final ApplicationReadyEvent event) {
            executor.execute(new AutomationAcceptOrderThread(smsService, orderService));
//            Runtime rt = Runtime.getRuntime();
//            try {
//                rt.exec("cmd /c start chrome.exe http://localhost:8080/shop");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return;
        }
    }

}

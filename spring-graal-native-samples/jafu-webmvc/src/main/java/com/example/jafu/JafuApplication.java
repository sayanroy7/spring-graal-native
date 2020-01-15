package com.example.jafu;

import com.example.jafu.handler.JafuApplicationRestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.context.MessageSourceInitializer;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.ServletWebServerApplicationContextWithoutSpel;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctionDsl;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

public class JafuApplication {

	private final ApplicationContextInitializer<GenericApplicationContext> initializer;

	private ServerProperties serverProperties = new ServerProperties();

	private HttpProperties httpProperties = new HttpProperties();

	private WebMvcProperties webMvcProperties = new WebMvcProperties();

	private ResourceProperties resourceProperties = new ResourceProperties();

	protected JafuApplication() {
		this.initializer = context -> {
			new MessageSourceInitializer().initialize(context);
			context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(ObjectMapper.class.getSimpleName(), context)
					, ObjectMapper.class
					, new ObjectMapper());
			/*context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(MappingJackson2HttpMessageConverter.class.getName(), context)
					, MappingJackson2HttpMessageConverter.class
					, new MappingJackson2HttpMessageConverter());*/
			context.registerBean(CommandLineRunner.class, () -> args -> System.out.println("jafu running!"));
			/*context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(JafuApplicationController.class.getName(), context), RouterFunction.class, () ->
				RouterFunctions.route().GET("/", request -> ServerResponse.ok().body("Hello")).build());*/

			JafuApplicationRestHandler handler = new JafuApplicationRestHandler();
			context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl.class.getName(), context), RouterFunction.class, () ->
					route()
							.GET("/text", accept(TEXT_PLAIN), handler::getTextResponse)
							.GET("/json", accept(APPLICATION_JSON), handler::getMessageJsonResponse)
							.build());






			serverProperties.setPort(8080);
			new StringConverterInitializer().initialize(context);
			new ResourceConverterInitializer().initialize(context);
			new JacksonJsonConverterInitializer().initialize(context);
			new ServletWebServerInitializer(serverProperties, httpProperties, webMvcProperties, resourceProperties).initialize(context);
		};
	}

	public ConfigurableApplicationContext run() {
		return run("", new String[0]);
	}

	public ConfigurableApplicationContext run(String profiles) {
		return run(profiles, new String[0]);
	}

	public ConfigurableApplicationContext run(String[] args) {
		return run("", args);
	}

	public ConfigurableApplicationContext run(String profiles, String[] args) {
		SpringApplication app = new SpringApplication(JafuApplication.class) {
			@Override
			protected void load(ApplicationContext context, Object[] sources) {
				// We don't want the annotation bean definition reader
			}
		};
		initializeWebApplicationContext(app);
		if (!profiles.isEmpty()) {
			app.setAdditionalProfiles(Arrays.stream(profiles.split(",")).map(it -> it.trim()).toArray(String[]::new));
		}
		app.addInitializers(this.initializer);
		System.setProperty("spring.backgroundpreinitializer.ignore", "true");
		return app.run(args);
	}

	protected void initializeWebApplicationContext(SpringApplication app) {
		app.setWebApplicationType(WebApplicationType.SERVLET);
		app.setApplicationContextClass(ServletWebServerApplicationContextWithoutSpel.class);
	}

	public static void main(String[] args) throws InterruptedException {
		new JafuApplication().run(args);
		Thread.currentThread().join(); // To be able to measure memory consumption
	}
	
}

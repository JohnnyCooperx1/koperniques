package server.configuration;

import com.alibaba.fastjson.support.jaxrs.FastJsonProvider;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import server.mapper.JSONObjectMapper;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"server.dao","server.controller"})
//@ImportResource({"WEB-INF/spring-security.xml"})
public class AppConfig extends WebMvcConfigurerAdapter{



    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
    }
    @Bean
    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(jacksonObjectMapper());
        return converter;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return builder;
    }

    @Bean
    public JSONObjectMapper jacksonObjectMapper() {
        JSONObjectMapper j = new JSONObjectMapper();

        j.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        j.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        j.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        j.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        j.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
        return j;
    }

    @Bean
    public ResourceBundleViewResolver viewResolver() {
        ResourceBundleViewResolver resolver = new ResourceBundleViewResolver();
        resolver.setBasename("views");
        resolver.setOrder(0);
        return resolver;
    }

    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setDefinitions("/WEB-INF/tiles.xml");
        return tilesConfigurer;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource r = new ReloadableResourceBundleMessageSource();
        r.setBasename("classpath:messages");
        r.setFallbackToSystemLocale(false);
        r.setDefaultEncoding("UTF-8");
        return r;
    }

    @Bean
    public PropertiesFactoryBean sqlQueries() {
        PropertiesFactoryBean p = new PropertiesFactoryBean();
        p.setSingleton(true);
        p.setLocation(new ClassPathResource("psql_queries.xml"));
        return p;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver p = new CommonsMultipartResolver();
        p.setMaxUploadSize(2147483648L);
        return p;
    }

    @Bean
    public FastJsonProvider jsonProvider() {
        return new FastJsonProvider();
    }

    @Bean
    public MappingJackson2JsonView jsonResolver() {
        MappingJackson2JsonView m = new MappingJackson2JsonView();
        m.setPrefixJson(true);
        m.setObjectMapper(jacksonObjectMapper());
        return m;
    }
    @Bean
    public DataSource dataSource() {
        DataSource dataSource = null;
        try {
            Context initialContext = new InitialContext();
            Context environmentContext = (Context) initialContext.lookup("java:comp/env");
            dataSource = (DataSource) environmentContext.lookup("jdbc/Kopernic");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public SessionFactory sessionFactory() {

        DataSource dataSource = dataSource();

        LocalSessionFactoryBean result = new LocalSessionFactoryBean();
        result.setDataSource(dataSource);
        result.setPackagesToScan("server.model");

        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
        properties.setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
        properties.setProperty("hibernate.connection.release_mode", "auto");
        properties.setProperty("hibernate.connection.oracle.jdbc.ReadTimeout", "9999");
        properties.setProperty("hibernate.transaction.auto_close_session", "true");

        result.setHibernateProperties(properties);

        try {
            result.afterPropertiesSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.getObject();
    }
//    @Bean
//    public ServerEndpointExporter endpointExporter() {
//        return new ServerEndpointExporter();
//    }






}


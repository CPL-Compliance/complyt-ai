//package com.complyt.config;
//
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.reactivestreams.client.MongoClient;
//import com.mongodb.reactivestreams.client.MongoClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
//import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
//
//@Configuration
//@EnableReactiveMongoRepositories
//public class MongoReactiveApplicationConfig extends AbstractReactiveMongoConfiguration {
//
//    @Override
//    protected String getDatabaseName() {
//        return "complyt";
//    }
//
//    @Bean
//    public MongoClient reactiveMongoClient() {
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://complyt_app:d4GVyPYBMEDfJgKp@cluster0.tlaie.mongodb.net/complyt");
//        MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
//
//        return MongoClients.create(mongoClientSettings);
//    }
//
//    @Bean
//    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient reactiveMongoClient) {
//        return new ReactiveMongoTemplate(reactiveMongoClient, getDatabaseName());
//    }
//}

package com.danscottjones.kotlinwebflux.config

import com.danscottjones.kotlinwebflux.repository.UserAccountRepository
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories



@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = arrayOf(UserAccountRepository::class))
@Import(EmbeddedMongoAutoConfiguration::class)
class MongoConfig {

//    @Value("\${MONGO_URL}")
//    lateinit var mongoUrl: String
//
//    override fun reactiveMongoClient(): MongoClient {
//        return MongoClients.create(mongoUrl)
//    }
//
//    override fun getDatabaseName() = "trainer-track"

}
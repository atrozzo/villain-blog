package com.villains;


import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
//@Configuration
public class DbConfigUnitTest {  //extends DbConfigDev {
/*

    @Override
    public MongoDbFactory mongoDbFactory() throws Exception {
        MongodForTestsFactory factory = MongodForTestsFactory.with(Version.Main.V3_1);
        MongoClient client = factory.newMongo();
        DB db = client.getDB("test-" + UUID.randomUUID());
        DBCollection col = db.createCollection(super.getDatabaseName(), new BasicDBObject());
        System.out.println("messages collection was created in test database" + col);

        return new SimpleMongoDbFactory(client, super.getDatabaseName());
    }*/
}

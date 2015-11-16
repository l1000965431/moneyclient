/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class ExampleDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.dragoneye.wjjt.dao");

//        addProject(schema);
//        addProjectImage(schema);
//        addInvestedProject(schema);
        addInvestRecord(schema);
        addEarningRecord(schema);
        addMessageBoxItem(schema);
        new DaoGenerator().generateAll(schema, "../money/app/src/main/java/");
    }

    private static void addInvestRecord(Schema schema){
        Entity investRecord = schema.addEntity("InvestRecord");
        investRecord.addStringProperty("id").primaryKey();
        investRecord.addBooleanProperty("isRead");
    }

    private static void addEarningRecord(Schema schema){
        Entity earningRecord = schema.addEntity("EarningRecord");
        earningRecord.addLongProperty("id").primaryKey();
        earningRecord.addBooleanProperty("isRead");
    }

    private static void addMessageBoxItem(Schema schema){
        Entity messageBoxItem = schema.addEntity("MessageBoxItem");
        messageBoxItem.addIdProperty();
        messageBoxItem.addStringProperty("messageJson");
        messageBoxItem.addBooleanProperty("isRead");
    }

//    private static void addProject(Schema schema){
//        Entity projectEntity = schema.addEntity("Project");
//        projectEntity.addIdProperty();
//        projectEntity.addIntProperty("status");
//        projectEntity.addIntProperty("stage");
//        projectEntity.addStringProperty("summary");
//        projectEntity.addStringProperty("introduce");
//    }
//
//    private static void addProjectImage(Schema schema){
//        Entity projectImageEntity = schema.addEntity("ProjectImage");
//        projectImageEntity.addIdProperty();
//        projectImageEntity.addLongProperty("projectId");
//        projectImageEntity.addStringProperty("imageUrl");
//    }
//
//    private static void addInvestedProject(Schema schema){
//        Entity investedProjectEntity = schema.addEntity("InvestedProject");
//        investedProjectEntity.addIdProperty();
//        investedProjectEntity.addLongProperty("projectId");
//        investedProjectEntity.addFloatProperty("price");
//    }

//    private static void addNote(Schema schema) {
//        Entity note = schema.addEntity("Note");
//        note.addIdProperty();
//        note.addStringProperty("text").notNull();
//        note.addStringProperty("comment");
//        note.addDateProperty("date");
//    }

//    private static void addCustomerOrder(Schema schema) {
//        Entity customer = schema.addEntity("Customer");
//        customer.addIdProperty();
//        customer.addStringProperty("name").notNull();
//
//        Entity order = schema.addEntity("Order");
//        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
//        order.addIdProperty();
//        Property orderDate = order.addDateProperty("date").getProperty();
//        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
//        order.addToOne(customer, customerId);
//
//        ToMany customerToOrders = customer.addToMany(order, customerId);
//        customerToOrders.setName("orders");
//        customerToOrders.orderAsc(orderDate);
//    }

}

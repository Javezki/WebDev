package com.lftech.sqlapi.database;

public interface HandleCallback {

    Object apply(DatabaseTemplate databaseTemplate) throws Exception;

}
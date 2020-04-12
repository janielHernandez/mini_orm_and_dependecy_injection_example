package com.company.orm;

import com.company.beans.Person;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface EntityManager<T> {

    void persist(T entity) throws SQLException, IllegalAccessException;

    T find(Class<T> tClass, Object l) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}

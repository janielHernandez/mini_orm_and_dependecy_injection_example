package com.company.orm;

import com.company.annotations.Inject;
import com.company.utils.ColumnField;
import com.company.utils.MetaModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicLong;

public  class ManagedEntityManager<T> implements EntityManager<T> {

    private AtomicLong atomicLong = new AtomicLong(0L);

    @Inject
    Connection connection;

    @Override
    public void persist(T entity) throws SQLException, IllegalAccessException {
        MetaModel metaModel = MetaModel.of(entity.getClass());
        String  sql = metaModel.getInsertRequest();

        try(PreparedStatement statement = prepareStatementWith(sql).andParameters(entity);) {
            statement.executeUpdate();
        }

    }

    @Override
    public T find(Class<T> tClass, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MetaModel metaModel = MetaModel.of( tClass );
        String  sql = metaModel.getSelectRequest();

        try(PreparedStatement statement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
            ResultSet resultSet = statement.executeQuery();)
        {
            return buildInstanceFrom(tClass, resultSet);
        }
    }

    private T buildInstanceFrom(Class<T> tClass, ResultSet resultSet)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        MetaModel metaModel = MetaModel.of( tClass );
        T entity = tClass.getConstructor().newInstance();
        Field primaryKeyField = metaModel.getPrimaryKey().getField();
        String primaryKeyName = metaModel.getPrimaryKey().getName();
        Class<?> keyFieldType = primaryKeyField.getType();

        resultSet.next();
        if (keyFieldType == Long.class){
            long primaryKey = resultSet.getInt(primaryKeyName);
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(entity, primaryKey);
        }

        for (ColumnField column: metaModel.getColumns() ) {
            Field field = column.getField();
            field.setAccessible( true );
            Class<?> columnType = field.getType();
            String columnName = field.getName();

            if (columnType == Integer.class){
                var value = resultSet.getInt(columnName);
                field.set(entity, value);
            }else if(columnType == String.class){
                var value = resultSet.getString(columnName);
                field.set(entity, value);
            }

        }

        return entity;


    }

    private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {

        PreparedStatement statement = connection.prepareStatement( sql );
        return new PreparedStatementWrapper(statement);
    }


    private class PreparedStatementWrapper{
        private PreparedStatement statement;

        public PreparedStatementWrapper(PreparedStatement statement) {
            this.statement = statement;
        }

        public PreparedStatement andParameters(T entity) throws SQLException, IllegalAccessException {

            MetaModel metaModel = MetaModel.of(entity.getClass());
            Class<?> primaryKey = metaModel.getPrimaryKey().getType();
            if (primaryKey == Long.class ){
                long id = atomicLong.incrementAndGet();
                statement.setLong(1, id );
                Field field = metaModel.getPrimaryKey().getField();
                field.setAccessible(true);
                field.set(entity, id);
            }
            
            for(int i = 0; i<metaModel.getColumns().size(); i++){
                ColumnField columnField = metaModel.getColumns().get( i );
                Class<?> type = columnField.getType();
                Field field = columnField.getField();
                field.setAccessible( true );
                Object value = field.get(entity);
                if(type == Integer.class)
                    statement.setInt(i+2, (Integer)value );
                else if(type == String.class)
                    statement.setString(i+2, (String) value );
            }

            return statement;
        }

        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {

            if (primaryKey.getClass() == Long.class )
                statement.setLong(1, (Long) primaryKey);
            return statement;
        }
    }
}

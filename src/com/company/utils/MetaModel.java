package com.company.utils;

import com.company.annotations.Column;
import com.company.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaModel {


    private Class<?> clss;

    public static  MetaModel of(Class<?> clss){

        return new MetaModel(clss);
    }

    public MetaModel(Class<?> clss) {
        this.clss = clss;
    }

    public PrimaryKeyField getPrimaryKey() {

        Field[] fields = clss.getDeclaredFields();

        for (Field f: fields){
            PrimaryKey primaryKey = f.getAnnotation(PrimaryKey.class);
            if (primaryKey != null){
                PrimaryKeyField primaryKeyField = new PrimaryKeyField(f);
                return primaryKeyField;
            }
        }
        throw new IllegalArgumentException("No primary key found in class " + clss.getSimpleName());

    }

    public List<ColumnField> getColumns() {

        List<ColumnField> columnFields = new ArrayList<>();

        Field[] fields = clss.getDeclaredFields();

        for (Field f: fields){
            Column column = f.getAnnotation(Column.class);
            if (column != null){
                ColumnField columnField = new ColumnField(f);
                columnFields.add(columnField);
            }
        }
        return columnFields;
    }

    public String getInsertRequest() {
        String id = this.getPrimaryKey().getName();
        List<String> columns = this.getColumns().stream().map(ColumnField::getName).collect(Collectors.toList());
        columns.add(0, id);
        String columnElements = String.join(", ", columns);

        int numberColumn = columns.size();
        String questionMark = IntStream.range(0, numberColumn)
                .mapToObj(index->"?")
                .collect(Collectors.joining(", "));

        return "insert into " + this.clss.getSimpleName() + " (" + columnElements + ") values ( "
                + questionMark + " );";

    }

    public String getSelectRequest() {
        String id = this.getPrimaryKey().getName();
        List<String> columns = this.getColumns().stream().map(ColumnField::getName).collect(Collectors.toList());
        columns.add(0, id);
        String columnElements = String.join(", ", columns);

        return "select "+ columnElements + " from " + this.clss.getSimpleName() + " where "+id + " = ?;";
    }
}

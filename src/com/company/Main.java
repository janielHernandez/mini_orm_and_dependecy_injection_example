package com.company;

import com.company.beanManager.BeanManager;
import com.company.beans.Person;
import com.company.orm.EntityManager;
import com.company.orm.ManagedEntityManager;
import com.company.utils.ColumnField;
import com.company.utils.MetaModel;
import com.company.utils.PrimaryKeyField;
import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        BeanManager beanManager = BeanManager.getInstance();
        EntityManager<Person> em = beanManager.getInstance( ManagedEntityManager.class );

//        Person janiel = Person.of("janiel", 25);
//        Person claudia = Person.of("claudia", 24);

//        em.persist(janiel);
//
//        em.persist(claudia);

        Person jani = em.find(Person.class, 1L);

        System.out.println( jani );


    }
}

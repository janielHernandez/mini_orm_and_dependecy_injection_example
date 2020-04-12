package com.company.providers;

import com.company.annotations.Provides;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider {


    @Provides
    public Connection buildConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:/home/janielcito/IdeaProjects/reflection_api/db_test/hibernate","sa", ""
        );
    }

}

package service

import groovy.sql.Sql

import java.sql.DriverManager

/**
 * Created by naga on 2014/06/29.
 */
class MasterService {

    def createSql() {

    }

    def findTableNames() {
        System.out.println("finding...")
        ConnectCondition condition = readCondition()

        def sql = Sql.newInstance(
                condition.url,
                condition.user,
                condition.password,
                condition.driver)
        def results = sql.rows('SELECT relname AS tableName FROM pg_stat_user_tables;')
//        results.each { System.out.println(it.toString()) }
        return results.collect{ it.tablename.toString() }
    }

    def readCondition() {

        def config = new ConfigSlurper().parse(new File('config.groovy').toURI().toURL())
        ConnectCondition condition = new ConnectCondition();
        condition.with {
            url = config.db.url
            user = config.db.user
            password = config.db.password
            driver = config.db.driver
        }

        System.out.println(condition.toString())
        return condition
    }
}

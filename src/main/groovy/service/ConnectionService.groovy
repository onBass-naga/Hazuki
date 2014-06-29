package service

import groovy.transform.Canonical

import java.sql.DriverManager

/**
 * Created by naga on 2014/06/28.
 */
class ConnectionService {

    def testConnection(ConnectCondition condition) {

        def conn
        try {
            Class.forName(condition.driver)
            conn = DriverManager.getConnection(
                    condition.url,
                    condition.user,
                    condition.password
            )

            saveCondition(condition)

            new Result(canConnect: true, message: '接続成功')

        } catch (Exception e) {
            new Result(canConnect: false, message: "${e.class.name}:${e.message}")
        } finally {
            if (conn) { conn.close() }
        }
    }

    def saveCondition(ConnectCondition condition) {

        def config = new ConfigSlurper().parse('config.groovy')
//        config.with {
            config.db.url = condition.url
            config.db.user = condition.user
            config.db.password = condition.password
            config.db.driver = condition.driver
//        }

        new File('config.groovy').withWriter { writer ->
            config.writeTo(writer)
        }
    }
}

@Canonical
class ConnectCondition {
    def url
    def user
    def password
    def driver
}

class Result {
    boolean canConnect
    String message
}

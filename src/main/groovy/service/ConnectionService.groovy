package service

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

            new Result(canConnect: true, message: '接続成功')

        } catch (Exception e) {
            new Result(canConnect: false, message: "${e.class.name}:${e.message}")
        }
//        } finally {
//            if (conn) { conn.close() }
//        }
    }
}

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

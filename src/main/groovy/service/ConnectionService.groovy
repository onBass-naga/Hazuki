package service

import java.sql.DriverManager

/**
 * Created by naga on 2014/06/28.
 */
class ConnectionService {

    def testConnection(ConnectCondition condition) {

        try {
            Class.forName(condition.driver)
            DriverManager.getConnection(
                    condition.url,
                    condition.user,
                    condition.password
            )

            new Result(canConnect: true, message: '接続成功')

        } catch (e) {
            new Result(canConnect: false, message: "${e.class.name}:${e.message}" )
        }
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

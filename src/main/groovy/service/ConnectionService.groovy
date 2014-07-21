package service

import groovy.transform.Canonical
import groovy.xml.MarkupBuilder

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
        } finally {
            if (conn) { conn.close() }
        }
    }

    def saveCondition(List connections) {

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.database(){
            connections.each {
                connection(name: it.name, url: it.url, user: it.user,
                        password: it.password, driver: it.driver)
            }
        }
        new File('config.xml').write(writer.toString(), 'utf-8')
    }

    def load() {

        def xml = new File('config.xml').getText('utf-8')
        def database = new XmlSlurper().parseText(xml)

        return database.connection.collect {
            return new ConnectCondition(name: it.@name, url: it.@url, user: it.@user,
                    password: it.@password, driver: it.@driver)
        }
    }
}

@Canonical
class ConnectCondition {
    def name
    def url
    def user
    def password
    def driver
}

class Result {
    boolean canConnect
    String message
}

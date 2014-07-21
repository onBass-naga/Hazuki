package service

import groovy.sql.Sql
import org.dbunit.IDatabaseTester
import org.dbunit.JdbcDatabaseTester
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.database.QueryDataSet
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.operation.DatabaseOperation

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by naga on 2014/06/29.
 */
class MasterService {

    def export(tableNames, directory, condition) {

        IDatabaseTester databaseTester = new JdbcDatabaseTester(
                condition.driver as String,
                condition.url as String,
                condition.user as String,
                condition.password as String)

        for (String tableName : tableNames) {
            QueryDataSet queryDataSet = new QueryDataSet(databaseTester.getConnection())
            queryDataSet.addTable(tableName)

            File backupDataFile = new File(directory as String, "${tableName}.xml")
            FileOutputStream fileOutputStream = new FileOutputStream(backupDataFile)
            FlatXmlDataSet.write(queryDataSet, fileOutputStream)
            fileOutputStream.close()
        }
    }

    def importToDB(files, condition) {
        files.each { System.out.println(it.toString()) }
        Class.forName(condition.driver);
        Connection conn = DriverManager.getConnection(
                condition.url as String,
                condition.user as String,
                condition.password as String);
        conn.setAutoCommit(false);

        IDatabaseConnection con = new DatabaseConnection(conn)
        files.each{ File file ->
            IDataSet dataSet = new FlatXmlDataSet(new FileReader(file))
            DatabaseOperation.CLEAN_INSERT.execute(con, dataSet);

            System.out.println(file.name + " done!")
        }
        con.connection.commit();
    }

    def findTableNames(condition) {

        def sql = Sql.newInstance(
                condition.url,
                condition.user,
                condition.password,
                condition.driver)
        def results = sql.rows('SELECT relname AS tableName FROM pg_stat_user_tables order by relname;')
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

        return condition
    }

    def export() {

    }
}

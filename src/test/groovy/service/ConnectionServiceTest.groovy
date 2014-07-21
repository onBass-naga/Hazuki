package service

import org.junit.Test

/**
 * Created by naga on 2014/07/12.
 */
class ConnectionServiceTest {

    ConnectionService sut = new ConnectionService()

    @Test
    def void load() {
        sut.load().each { System.out.println(it.toString()) }
    }
}

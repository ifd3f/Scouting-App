package com.ironpanthers.scouting.desktop.io.server

import com.ironpanthers.scouting.common.*
import com.ironpanthers.scouting.desktop.ioExecutor
import com.ironpanthers.scouting.io.server.DatabaseBackend
import org.apache.log4j.PropertyConfigurator
import org.slf4j.LoggerFactory
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class SQLiteBackend(private val url: String) : DatabaseBackend {

    private lateinit var conn: Connection
    private val log = LoggerFactory.getLogger(javaClass)

    override fun initialize() {
        log.info("Initializing connection to {}", url)
        conn = DriverManager.getConnection(url)
    }

    override fun close() {
        log.info("Closing")
        conn.close()
    }

    override fun getCompetitionDescription(id: Int, cb: (CompetitionDescription) -> Unit) {
        ioExecutor.execute {
            val stm = conn.prepareStatement(STM_GET_COMP_DESC)
            stm.setInt(1, id)
            stm.setInt(2, id)

            log.debug("executing query {}", stm)

            val results = stm.executeQuery()
            results.next()
            val date = results.getDate(1)
            val gameDef = results.getString(2)

            log.debug("date={} gameDef={}", date, gameDef)

            val matchMap = mutableMapOf<Int, TempMatchDesc>()

            while (results.next()) {
                val matchId = results.getInt(1)
                val matchNum = results.getInt(2)
                val color = results.getString(3)
                val team = results.getInt(4)

                log.trace("matchId={} matchNum={} color={} team={}", matchId, matchNum, color, team)

                val desc = matchMap.getOrPut(matchId) { TempMatchDesc(matchNum) }
                desc.alliances[color]!!.add(team)
            }

            log.debug("matchMap={}", matchMap)

            val matches = matchMap.map { (id, desc) ->
                MatchDescription(id, desc.number, desc.alliances["RED"]!!, desc.alliances["BLUE"]!!)
            }
            cb(CompetitionDescription(id, date, gameDef, matches))
        }
    }

    override fun updateRobotPerformance(rp: MatchRobot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

private class TempMatchDesc(val number: Int) {
    val alliances: Map<String, MutableList<Int>> = mutableMapOf(
            "RED" to mutableListOf(),
            "BLUE" to mutableListOf()
    )
}

fun main(args: Array<String>) {
    PropertyConfigurator.configure("log4j.properties")
    val path = File(".", "test.sqlite3").canonicalPath
    val b = SQLiteBackend("jdbc:sqlite:$path")
    b.initialize()
    b.getCompetitionDescription(1) {
        println(it)
        b.close()
    }
}
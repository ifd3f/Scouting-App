package com.ironpanthers.scouting.common

import com.ironpanthers.scouting.util.KeyCombo


const val AUTO = 0x1
const val TELEOP = 0x2
const val ENDGAME_ONLY = 0x4
const val ENDGAME = TELEOP or ENDGAME_ONLY
const val ANY = AUTO or TELEOP or ENDGAME_ONLY

enum class TeamColor {
    RED, BLUE
}

enum class MatchResult {
    RED_VICTORY, BLUE_VICTORY, DRAW, OTHER
}

data class RobotEventDef(val id: String, val name: String, val stage: Int, val keyCombo: KeyCombo, val maxTimes: Int = -1, val icon: String? = null) {
    fun createEventInstance(team: Int): RobotEvent {
        return RobotEvent(id, team)
    }
}

data class RobotEndState(val id: String, val name: String, val icon: String? = null)

data class RobotEvent(val id: String, val team: Int) {
    val time: Long = System.currentTimeMillis()
    val extras: MutableMap<String, String> = mutableMapOf()
}

data class Team(val number: Int, val name: String)

data class RobotPerformance(val team: Int, val start: Long, val events: List<RobotEvent>, val endState: String)

data class Alliance(val teams: List<RobotPerformance>) {

}

data class Match(val red: Alliance, val blue: Alliance) {
    val teams: List<RobotPerformance> by lazy { red.teams + blue.teams }
}

fun main(args: Array<String>) {

}
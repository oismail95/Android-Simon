package com.example.simon

import java.io.Serializable

data class GameSetup constructor(var seq: Int, var timeRound: Long, var buttonSpeed: Int): Serializable {

}
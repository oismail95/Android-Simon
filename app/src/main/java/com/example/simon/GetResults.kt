package com.example.simon

import java.io.Serializable

data class GetResults constructor(var points: Int, var round: Int) : Serializable {
}
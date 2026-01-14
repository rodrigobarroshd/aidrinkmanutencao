package com.smartchip.aidrink.android.mqtt

//data class MqttPayload(
//    val command: String,
//
//    val coupler: Int? = null,
//    val pressure_coupler: Int? = null,
//    val pulse: Int? = null,
//
//    val hour: Int? = null,
//    val duration: Int? = null,
//
//    val system: String? = null
//)

data class MqttPayload(
    val command: String,
    val volume: Int? = null,
    val foam: Int? = null,
    val pressure_coupler: Float? = null,
    val pulse: Float? = null,
    val hour: Int? = null,
    val duration: Int? = null,
    val system: String? = null,
    val beverator: String? = null,
    val order: String? = null,
    val keg: String? = null,
    val coupler: Int? = null,
    val customer: String? = null
)

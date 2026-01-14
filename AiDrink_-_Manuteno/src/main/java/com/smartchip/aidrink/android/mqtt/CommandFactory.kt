package com.smartchip.aidrink.android.mqtt

object CommandFactory {

    fun unlock() = MqttPayload(
        command = "unlock"
    )

    fun dispenserCup() = MqttPayload(
        command = "dispenserCup"
    )

    fun reboot() = MqttPayload(
        command = "shell",
        system = "sudo reboot"
    )

    fun shutdown() = MqttPayload(
        command = "shell",
        system = "sudo shutdown now"
    )

    fun updateTime() = MqttPayload(
        command = "shell",
        system = "ntpd ntp.ubuntu.com"
    )

    fun getPressure(coupler: Int) = MqttPayload(
        command = "getPressure",
        coupler = coupler
    )

    fun setPressure(
        coupler: Int,
        pressureCoupler: Float
    ) = MqttPayload(
        command = "setPressure",
        coupler = coupler,
        pressure_coupler = pressureCoupler
    )

    fun getCalibratePulse(coupler: Int) = MqttPayload(
        command = "getCalibratePulse",
        coupler = coupler
    )

    fun setCalibratePulse(
        coupler: Int,
        pulse: Float
    ) = MqttPayload(
        command = "setCalibratePulse",
        coupler = coupler,
        pulse = pulse
    )

    fun setFreezerDown(
        hour: Int,
        duration: Int
    ) = MqttPayload(
        command = "setFreezerDown",
        hour = hour,
        duration = duration
    )
    fun serve(
        volume: Int,
        foam: Int,
        beverator: String,
        orderId: String,
        kegId: String,
        coupler: Int,
        customer: String
    ) = MqttPayload(
        command = "serve",
        volume = volume,
        foam = foam,
        beverator = beverator,
        order = orderId,
        keg = kegId,
        coupler = coupler,
        customer = "app_manutencao"
    )
}

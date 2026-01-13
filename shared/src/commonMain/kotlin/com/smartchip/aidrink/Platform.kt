package com.smartchip.aidrink

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
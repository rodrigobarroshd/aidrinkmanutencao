package com.smartchip.aidrink.android.ui.navigation


import com.smartchip.aidrink.android.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: Int,   // Alterado para Int
    val unselectedIcon: Int  // Alterado para Int
) {
    object Config : BottomNavItem(
        route = "home",
        label = "Config",
        selectedIcon = R.drawable.handyman_24px,
        unselectedIcon = R.drawable.handyman_24px
    )

    object Serve : BottomNavItem(
        route = "home2",
        label = "Serve",
        selectedIcon = R.drawable.water_full_24px,
        unselectedIcon = R.drawable.water_full_24px
    )

    object Message : BottomNavItem(
        route = "messages",
        label = "messages",
        selectedIcon = R.drawable.chat_24px,
        unselectedIcon = R.drawable.chat_24px
    )
}

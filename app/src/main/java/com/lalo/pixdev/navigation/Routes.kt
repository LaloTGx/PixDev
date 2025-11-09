package com.lalo.pixdev.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val REQUIREMENTS = "requirements/{projectId}"

    fun requirements(projectId: String) = "requirements/$projectId"
}
package com.lalo.pixdev.viewmodel.model

import java.util.UUID
import androidx.annotation.StringRes
import com.lalo.pixdev.R

data class Requirement(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val nombre: String,
    val prioridad: String,
    val fechaLimite: Long = System.currentTimeMillis(),
    val completado: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)

enum class Prioridad(@StringRes val titleResId: Int) {
    OPTIONAL(R.string.priority_optional),
    MANDATORY(R.string.priority_mandatory),
    ESSENTIAL(R.string.priority_essential)
}

enum class RequirementSortType(@StringRes val titleResId: Int) {
    DEFAULT_ORDER(R.string.req_sort_default_order),
    NAME_ASCENDING(R.string.req_sort_name_ascending),
    NAME_DESCENDING(R.string.req_sort_name_descending),
    PRIORITY_ASCENDING(R.string.req_sort_priority_ascending),
    PRIORITY_DESCENDING(R.string.req_sort_priority_descending)
}
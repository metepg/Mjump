package com.metepg.finder

sealed class InputState(private val value: Int) {
    object ADD_CHAR1_TO_SEARCH: InputState(1)
    object ADD_CHAR2_TO_SEARCH: InputState(1)
    object SHOW_MARKS: InputState(2)

    override fun toString() = value.toString()
}
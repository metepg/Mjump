package com.metepg.finder

sealed class InputState(private val value: Int) {
    object WAIT_SEARCH_CHAR1: InputState(0)
    object WAIT_SEARCH_CHAR2: InputState(1)
    object WAIT_KEY: InputState(2)

    override fun toString() = value.toString()

}
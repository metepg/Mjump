package com.metepg.finder

sealed class InputState(private val value: Int) {
    object AddChar1ToSearch: InputState(1)
    object AddChar2ToSearch: InputState(1)
    object ShowMarks: InputState(2)

    override fun toString() = value.toString()
}
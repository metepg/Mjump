package com.metepg

sealed class JumpMode(private val value: Int) {
    object CHAR1: JumpMode(0)
    object CHAR2: JumpMode(1)
    object WORD0: JumpMode(2)
    object WORD1: JumpMode(3)
    object LINE: JumpMode(4)

    override fun toString() = value.toString()
}
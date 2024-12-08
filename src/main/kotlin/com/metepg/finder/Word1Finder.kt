package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Word1Finder : Finder {
    private lateinit var state: InputState

    override fun start(e: Editor): List<MarksCanvas.Mark>? {
        state = InputState.WAIT_SEARCH_CHAR1
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return when (state) {
            InputState.WAIT_SEARCH_CHAR1 -> {
                val pattern = Regex("(?i)\\b${Regex.escape(c.toString())}")
                state = InputState.WAIT_KEY
                return e.project.getMarksFromAllEditors(pattern)
            }
            InputState.WAIT_KEY -> advanceMarks(c, lastMarks)
            else -> throw IllegalStateException("Unexpected state: $state")
        }
    }
}
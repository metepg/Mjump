package com.werfad.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.werfad.MarksCanvas
import com.werfad.utils.getMarksFromAllEditors

private const val STATE_WAIT_SEARCH_CHAR = 0
private const val STATE_WAIT_KEY = 1

class GlobalWord1Finder : Finder {
    private var state = STATE_WAIT_SEARCH_CHAR

    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>? {
        state = STATE_WAIT_SEARCH_CHAR
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return when (state) {
            STATE_WAIT_SEARCH_CHAR -> {
                val pattern = Regex("(?i)\\b${Regex.escape(c.toString())}")
                state = STATE_WAIT_KEY
                return e.project.getMarksFromAllEditors(pattern)
            }
            STATE_WAIT_KEY -> advanceGlobalMarks(c, lastMarks)
            else -> throw IllegalStateException("Unexpected state: $state")
        }
    }
}
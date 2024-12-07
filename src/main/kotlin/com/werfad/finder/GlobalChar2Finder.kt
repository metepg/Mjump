package com.werfad.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.werfad.MarksCanvas
import com.werfad.utils.getMarksFromAllEditors

private const val STATE_WAIT_SEARCH_CHAR1 = 0
private const val STATE_WAIT_SEARCH_CHAR2 = 1
private const val STATE_WAIT_KEY = 2

class GlobalChar2Finder : Finder {
    private var state = STATE_WAIT_SEARCH_CHAR1
    private var firstChar: Char? = null

    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>? {
        state = STATE_WAIT_SEARCH_CHAR1
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark>? {
        return when (state) {
            STATE_WAIT_SEARCH_CHAR1 -> {
                firstChar = c
                state = STATE_WAIT_SEARCH_CHAR2
                null
            }
            STATE_WAIT_SEARCH_CHAR2 -> {
                val first = firstChar ?: throw IllegalStateException("First character is not set.")
                val pattern = Regex(Regex.escape("$first$c"), RegexOption.IGNORE_CASE)
                state = STATE_WAIT_KEY
                e.project.getMarksFromAllEditors(pattern)
            }
            STATE_WAIT_KEY -> advanceGlobalMarks(c, lastMarks)
            else -> throw IllegalStateException("Unexpected state: $state")
        }
    }
}

package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Char2Finder : Finder {
    private lateinit var state: InputState
    private var firstChar: Char? = null

    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>? {
        state = InputState.WAIT_SEARCH_CHAR1
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark>? {
        return when (state) {
            InputState.WAIT_SEARCH_CHAR1 -> {
                firstChar = c
                state = InputState.WAIT_SEARCH_CHAR2
                null
            }
            InputState.WAIT_SEARCH_CHAR2 -> {
                val first = firstChar ?: throw IllegalStateException("First character is not set.")
                val pattern = Regex(Regex.escape("$first$c"), RegexOption.IGNORE_CASE)
                state = InputState.WAIT_KEY
                e.project.getMarksFromAllEditors(pattern)
            }
            InputState.WAIT_KEY -> advanceMarks(c, lastMarks)
        }
    }
}

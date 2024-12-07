package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Char1Finder: Finder {
    private lateinit var state: InputState

    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>? {
        state = InputState.WAIT_SEARCH_CHAR1
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return when (state) {
            InputState.WAIT_SEARCH_CHAR1 -> {
                val pattern = Regex(Regex.escape(c.toString()), RegexOption.IGNORE_CASE)
                state = InputState.WAIT_KEY
                return e.project.getMarksFromAllEditors(pattern)
            }
            InputState.WAIT_KEY -> advanceMarks(c, lastMarks)
            else -> throw RuntimeException("Impossible.")
        }
    }
}
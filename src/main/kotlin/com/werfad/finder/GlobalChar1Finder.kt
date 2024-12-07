package com.werfad.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.werfad.MarksCanvas
import com.werfad.utils.getMarksFromAllEditors

class GlobalChar1Finder: Finder {
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
            InputState.WAIT_KEY -> advanceGlobalMarks(c, lastMarks)
            else -> throw RuntimeException("Impossible.")
        }
    }
}
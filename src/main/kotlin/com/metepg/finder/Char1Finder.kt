package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Char1Finder: Finder {
    private var state = InputState.ADD_CHAR1_TO_SEARCH

    override fun start(e: Editor): List<MarksCanvas.Mark>? {
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> = when (state) {
        InputState.ADD_CHAR1_TO_SEARCH -> addChar1ToSearch(e, c)
        InputState.SHOW_MARKS -> advanceMarks(c, lastMarks)
        else -> throw IllegalStateException("Unexpected state: $state")
    }

    private fun addChar1ToSearch(e: Editor, c: Char): List<MarksCanvas.Mark> {
        val pattern = Regex(Regex.escape(c.toString()), RegexOption.IGNORE_CASE)
        state = InputState.SHOW_MARKS
        return e.project.getMarksFromAllEditors(pattern)
    }
}
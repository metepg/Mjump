package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Char2Finder : Finder {
    private var state = InputState.ADD_CHAR1_TO_SEARCH
    private var firstChar: Char? = null

    override fun start(e: Editor): List<MarksCanvas.Mark>? {
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark>? = when (state) {
        InputState.ADD_CHAR1_TO_SEARCH -> addChar1ToSearch(c)
        InputState.ADD_CHAR2_TO_SEARCH -> addChar2ToSearch(e, c)
        InputState.SHOW_MARKS -> advanceMarks(c, lastMarks)
    }

    private fun addChar1ToSearch(c: Char): List<MarksCanvas.Mark>? {
        firstChar = c
        state = InputState.ADD_CHAR2_TO_SEARCH
        return null
    }

    private fun addChar2ToSearch(e: Editor, c: Char): List<MarksCanvas.Mark> {
        val first = firstChar ?: throw IllegalStateException("First character is not set.")
        val pattern = Regex(Regex.escape("$first$c"), RegexOption.IGNORE_CASE)
        state = InputState.SHOW_MARKS
        return e.project.getMarksFromAllEditors(pattern)
    }
}

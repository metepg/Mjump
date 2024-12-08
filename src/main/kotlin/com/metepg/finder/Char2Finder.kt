package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Char2Finder : Finder {
    private lateinit var state: InputState
    private var firstChar: Char? = null

    override fun start(e: Editor): List<MarksCanvas.Mark>? {
        state = InputState.AddChar1ToSearch
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark>? = when (state) {
        InputState.AddChar1ToSearch -> addChar1ToSearch(c)
        InputState.AddChar2ToSearch -> addChar2ToSearch(e, c)
        InputState.ShowMarks -> advanceMarks(c, lastMarks)
    }

    private fun addChar1ToSearch(c: Char): List<MarksCanvas.Mark>? {
        firstChar = c
        state = InputState.AddChar2ToSearch
        return null
    }

    private fun addChar2ToSearch(e: Editor, c: Char): List<MarksCanvas.Mark> {
        val first = firstChar ?: throw IllegalStateException("First character is not set.")
        val pattern = Regex(Regex.escape("$first$c"), RegexOption.IGNORE_CASE)
        state = InputState.ShowMarks
        return e.project.getMarksFromAllEditors(pattern)
    }
}

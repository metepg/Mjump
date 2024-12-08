package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

class Word1Finder : Finder {
    private lateinit var state: InputState

    override fun start(e: Editor): List<MarksCanvas.Mark>? {
        state = InputState.AddChar1ToSearch
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> = when (state) {
        InputState.AddChar1ToSearch -> addChar1ToSearch(e, c)
        InputState.ShowMarks -> advanceMarks(c, lastMarks)
        else -> throw IllegalStateException("Unexpected state: $state")
    }

    private fun addChar1ToSearch(e: Editor, c: Char): List<MarksCanvas.Mark> {
        val pattern = Regex("(?i)\\b${Regex.escape(c.toString())}")
        state = InputState.ShowMarks
        return e.project.getMarksFromAllEditors(pattern)
    }
}
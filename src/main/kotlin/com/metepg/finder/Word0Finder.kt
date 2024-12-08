package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

private val pattern = Regex("(?i)\\b\\w")

class Word0Finder : Finder {
    override fun start(e: Editor): List<MarksCanvas.Mark> {
        return e.project.getMarksFromAllEditors(pattern)
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return advanceMarks(c, lastMarks)
    }
}
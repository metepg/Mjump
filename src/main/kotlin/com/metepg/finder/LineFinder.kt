package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.metepg.MarksCanvas
import com.metepg.utils.getMarksFromAllEditors

private val pattern = Regex("(?m)^")

class LineFinder: Finder {
    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark> {
        return e.project.getMarksFromAllEditors(pattern)
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return advanceMarks(c, lastMarks)
    }
}
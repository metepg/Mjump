package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.metepg.MarksCanvas

interface Finder {
    /**
     * @return null - need more input to locate.
     *         not null - can be locate some data, empty represent without any matches.
     */
    fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>?

    /**
     * @return same with [.start]
     */
    fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark>?

    /**
     * @return Return the marks with the start character removed and the editors they belong to.
     */
    fun advanceMarks(c: Char, marks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return marks.filter { it.keyTag[it.advanceIndex] == c }
            .map {
                MarksCanvas.Mark(it.keyTag, it.offset, it.advanceIndex + 1, editor = it.editor)
            }
            .toList()
    }
}
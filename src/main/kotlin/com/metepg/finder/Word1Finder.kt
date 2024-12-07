package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.metepg.KeyTagsGenerator
import com.metepg.MarksCanvas
import com.metepg.UserConfig
import kotlin.math.abs

class Word1Finder : Finder {
    private lateinit var state: InputState
    private lateinit var s: String
    private lateinit var visibleRange: TextRange

    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>? {
        this.s = s
        this.visibleRange = visibleRange
        state = InputState.WAIT_SEARCH_CHAR1
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark> {
        return when (state) {
            InputState.WAIT_SEARCH_CHAR1 -> {
                val cOffset = e.caretModel.offset
                var find = if (c.isLowerCase()) "(?i)" else ""
                find += "\\b" + Regex.escape("" + c)
                val offsets = Regex(find)
                    .findAll(s)
                    .map { it.range.first + visibleRange.startOffset }
                    .sortedBy { abs(cOffset - it) }
                    .toList()

                val tags =
                    KeyTagsGenerator.createTagsTree(offsets.size, UserConfig.getDataBean().characters)
                state = InputState.WAIT_KEY
                return offsets.zip(tags)
                    .map { MarksCanvas.Mark(it.second, it.first) }
                    .toList()
            }
            InputState.WAIT_KEY -> advanceMarks(c, lastMarks)
            else -> throw RuntimeException("Impossible.")
        }
    }
}
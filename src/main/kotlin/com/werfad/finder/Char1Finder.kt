package com.werfad.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.werfad.KeyTagsGenerator
import com.werfad.MarksCanvas
import com.werfad.UserConfig
import com.werfad.utils.findAll
import kotlin.math.abs

class Char1Finder : Finder {
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
                val caretOffset = e.caretModel.offset
                val offsets = s.findAll(c, c.isLowerCase())
                    .map { it + visibleRange.startOffset }
                    .sortedBy { abs(it - caretOffset) }
                    .toList()

                val tags = KeyTagsGenerator.createTagsTree(offsets.size, UserConfig.getDataBean().characters)
                state = InputState.WAIT_KEY
                offsets.zip(tags)
                    .map { MarksCanvas.Mark(it.second, it.first) }
                    .toList()
            }
            InputState.WAIT_KEY -> advanceMarks(c, lastMarks)
            else -> throw RuntimeException("Impossible.")
        }
    }
}
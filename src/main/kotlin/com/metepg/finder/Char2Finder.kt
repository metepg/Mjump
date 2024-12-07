package com.metepg.finder

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.metepg.KeyTagsGenerator
import com.metepg.MarksCanvas
import com.metepg.UserConfig
import com.metepg.UserConfig.DataBean
import com.metepg.utils.findAll
import kotlin.math.abs

class Char2Finder : Finder {
    private lateinit var state: InputState
    private val config: DataBean = UserConfig.getDataBean()
    private lateinit var s: String
    private lateinit var visibleRange: TextRange
    private var firstChar = ' '

    override fun start(e: Editor, s: String, visibleRange: TextRange): List<MarksCanvas.Mark>? {
        this.s = s
        this.visibleRange = visibleRange
        state = InputState.WAIT_SEARCH_CHAR1
        return null
    }

    override fun input(e: Editor, c: Char, lastMarks: List<MarksCanvas.Mark>): List<MarksCanvas.Mark>? {
        return when (state) {
            InputState.WAIT_SEARCH_CHAR1 -> {
                firstChar = c
                state = InputState.WAIT_SEARCH_CHAR2
                null
            }
            InputState.WAIT_SEARCH_CHAR2 -> {
                val caretOffset = e.caretModel.offset
                val find = "" + firstChar + c
                val offsets = s.findAll(find, find.all { it.isLowerCase() })
                    .map { it + visibleRange.startOffset }
                    .sortedBy { abs(it - caretOffset) }
                    .toList()

                val tags = KeyTagsGenerator.createTagsTree(offsets.size, config.characters)
                state = InputState.WAIT_KEY
                offsets.zip(tags)
                    .map { MarksCanvas.Mark(it.second, it.first) }
                    .toList()
            }
            InputState.WAIT_KEY -> advanceMarks(c, lastMarks)
        }
    }
}
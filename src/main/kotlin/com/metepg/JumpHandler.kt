package com.metepg

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory
import com.intellij.openapi.util.TextRange
import com.metepg.finder.*

object JumpHandler : TypedActionHandler {
    private var mOldTypedHandler: TypedActionHandler? = null
    private var mOldEscActionHandler: EditorActionHandler? = null
    private var isStart = false
    private lateinit var finder: Finder
    private val mMarksCanvasMap = mutableMapOf<Editor, MarksCanvas>()
    private var lastMarks: List<MarksCanvas.Mark> = emptyList()

    override fun execute(e: Editor, c: Char, dc: DataContext) {
        val marks = finder.input(e, c, lastMarks)
        if (marks != null) {
            lastMarks = marks
            jumpOrShowCanvas(lastMarks)
        }
    }

    private val escActionHandler: EditorActionHandler = object : EditorActionHandler() {
        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
            stop()
        }
    }

    private fun jumpOrShowCanvas(marks: List<MarksCanvas.Mark>) {
        when {
            marks.isEmpty() -> stop()
            marks.size == 1 -> jumpToMark(marks[0])
            else -> handleMultipleMarks(marks)
        }
    }

    private fun jumpToMark(mark: MarksCanvas.Mark) {
        mark.editor?.let { editor ->
            editor.contentComponent.requestFocus()
            val caret = editor.caretModel.currentCaret
            if (caret.hasSelection()) {
                val downOffset = if (caret.selectionStart == caret.offset)
                    caret.selectionEnd
                else
                    caret.selectionStart
                caret.setSelection(downOffset, mark.offset)
            }
            // Shamelessly robbed from AceJump: https://github.com/acejump/AceJump/blob/99e0a5/src/main/kotlin/org/acejump/action/TagJumper.kt#L87
            CommandProcessor.getInstance().executeCommand(
                editor.project, {
                    IdeDocumentHistory.getInstance(editor.project!!).apply {
                        setCurrentCommandHasMoves()
                        includeCurrentCommandAsNavigation()
                        includeCurrentPlaceAsChangePlace()
                    }
                }, "MJumpHistoryAppender", DocCommandGroupId.noneGroupId(editor.document),
                UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION, editor.document
            )
            caret.moveToOffset(mark.offset)
            stop()
        }
    }

    /**
     * Processes marks by grouping them by editor, updating their canvases,
     * and removing canvases where tags are not found.
     */
    private fun handleMultipleMarks(marks: List<MarksCanvas.Mark>) {
        marks.groupBy { it.editor }.forEach { (editor, editorMarks) ->
            editor?.let {
                val canvas = mMarksCanvasMap.computeIfAbsent(it) {
                    MarksCanvas().apply {
                        sync(it)
                        it.contentComponent.add(this)
                        revalidate()
                        repaint()
                    }
                }
                canvas.setData(editorMarks.map { mark ->
                    MarksCanvas.Mark(
                        keyTag = mark.keyTag.drop(mark.advanceIndex),
                        offset = mark.offset,
                        editor = mark.editor
                    )
                })
                canvas.repaint()
                it.contentComponent.revalidate()
                it.contentComponent.repaint()
            }
        }

        val activeEditors = marks.mapNotNull { it.editor }.toSet()
        mMarksCanvasMap.keys.minus(activeEditors).forEach { editor ->
            mMarksCanvasMap.remove(editor)?.let { canvas ->
                editor.contentComponent.remove(canvas)
                editor.contentComponent.repaint()
            }
        }
    }

    fun start(mode: JumpMode, anActionEvent: AnActionEvent) {
        if (isStart) return
        isStart = true
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR) ?: return
        val manager = EditorActionManager.getInstance()
        val typedAction = TypedAction.getInstance()
        mOldTypedHandler = typedAction.rawHandler
        typedAction.setupRawHandler(this)
        mOldEscActionHandler = manager.getActionHandler(IdeActions.ACTION_EDITOR_ESCAPE)
        manager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, escActionHandler)
        finder = when (mode) {
            JumpMode.CHAR1 -> Char1Finder()
            JumpMode.CHAR2 -> Char2Finder()
            JumpMode.WORD0 -> Word0Finder()
            JumpMode.WORD1 -> Word1Finder()
            JumpMode.LINE -> LineFinder()
        }
        val marks = finder.start(editor, "", TextRange.EMPTY_RANGE)
        if (marks != null) {
            lastMarks = marks
            jumpOrShowCanvas(lastMarks)
        }
    }

    private fun stop() {
        if (isStart) {
            isStart = false
            val manager = EditorActionManager.getInstance()
            TypedAction.getInstance().setupRawHandler(mOldTypedHandler!!)
            if (mOldEscActionHandler != null) {
                manager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, mOldEscActionHandler!!)
            }
            mMarksCanvasMap.forEach { (editor, canvas) ->
                editor.contentComponent.remove(canvas)
                editor.contentComponent.repaint()
            }
            mMarksCanvasMap.clear()
        }
    }

}
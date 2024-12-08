package com.metepg

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.metepg.finder.*

object JumpHandler : TypedActionHandler {
    private var mOldTypedHandler: TypedActionHandler? = null
    private var mOldEscActionHandler: EditorActionHandler? = null
    private var isStart = false
    private lateinit var finder: Finder
    private val mMarksCanvasMap = mutableMapOf<Editor, MarksCanvas>()
    private var lastMarks: List<MarksCanvas.Mark> = emptyList()

    /**
     * Executed when an MJump action is triggered.
     */
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
        val marks = finder.start(editor)
        if (marks != null) {
            lastMarks = marks
            jumpOrShowCanvas(lastMarks)
        }
    }

    /**
     * Processes user input after the start() method is called.
     * Handles the character input to find marks
     */
    override fun execute(e: Editor, c: Char, dc: DataContext) {
        val marks = finder.input(e, c, lastMarks)
        if (marks != null) {
            lastMarks = marks
            jumpOrShowCanvas(lastMarks)
        }
    }

    private fun jumpOrShowCanvas(marks: List<MarksCanvas.Mark>) {
        when (marks.size) {
            0 -> stop()
            1 -> jumpToMark(marks.first())
            else -> handleMultipleMarks(marks)
        }
    }

    /**
     * Stops the execution of the plugin.
     * - Resets the state and restores any modified handlers to their original values.
     * - Removes all mark canvases from editors and clears the marks canvas map.
     */
    private fun stop() {
        if (!isStart) return

        isStart = false
        TypedAction.getInstance().setupRawHandler(mOldTypedHandler!!)
        if (mOldEscActionHandler != null) {
            EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, mOldEscActionHandler!!)
        }
        mMarksCanvasMap.forEach { (editor, canvas) ->
            editor.contentComponent.remove(canvas)
            editor.contentComponent.repaint()
        }
        mMarksCanvasMap.clear()
    }

    /**
     * Moves the caret to the mark's position and stops the plugin execution.
     */
    private fun jumpToMark(mark: MarksCanvas.Mark) {
        mark.editor.contentComponent.requestFocus()
        mark.editor.caretModel.currentCaret.moveToOffset(mark.offset)
        stop()
    }

    private val escActionHandler: EditorActionHandler = object : EditorActionHandler() {
        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
            stop()
        }
    }

    /**
     * Processes marks by grouping them by editor, updating their canvases,
     * and removing canvases where tags are not found.
     */
    private fun handleMultipleMarks(marks: List<MarksCanvas.Mark>) {
        marks.groupBy { it.editor }.forEach { (editor, editorMarks) ->
            editor.let {
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

        val activeEditors = marks.map { it.editor }.toSet()
        mMarksCanvasMap.keys.minus(activeEditors).forEach { editor ->
            mMarksCanvasMap.remove(editor)?.let { canvas ->
                editor.contentComponent.remove(canvas)
                editor.contentComponent.repaint()
            }
        }
    }

}
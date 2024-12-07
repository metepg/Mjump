package com.metepg

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.project.DumbAwareAction

abstract class BaseAction(private val handler: TypedActionHandler) : DumbAwareAction() {
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }

    /**
     * JumpHandler is used for actions on the currently focused editor.
     * GlobalJumpHandler is used for actions across all editors.
     */
    override fun actionPerformed(e: AnActionEvent) {
        when (handler) {
            is JumpHandler -> handler.start(getMode(), e)
            is GlobalJumpHandler -> handler.start(getMode(), e)
            else -> {
                throw IllegalArgumentException("Unsupported handler: ${handler::class}")
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    abstract fun getMode(): JumpMode
}

class Char1Action : BaseAction(JumpHandler) {
    override fun getMode() = JumpMode.CHAR1
}

class Char2Action : BaseAction(JumpHandler) {
    override fun getMode() = JumpMode.CHAR2
}

class Word0Action : BaseAction(JumpHandler) {
    override fun getMode() = JumpMode.WORD0
}

class Word1Action : BaseAction(JumpHandler) {
    override fun getMode() = JumpMode.WORD1
}

class LineAction : BaseAction(JumpHandler) {
    override fun getMode() = JumpMode.LINE
}

class GotoDeclarationWord1Action : BaseAction(JumpHandler) {
    override fun getMode() = JumpMode.WORD1_DECLARATION
}

class GlobalChar1Action : BaseAction(GlobalJumpHandler) {
    override fun getMode() = JumpMode.CHAR1
}

class GlobalChar2Action : BaseAction(GlobalJumpHandler) {
    override fun getMode() = JumpMode.CHAR2
}

class GlobalWord0Action : BaseAction(GlobalJumpHandler) {
    override fun getMode() = JumpMode.WORD0
}

class GlobalWord1Action : BaseAction(GlobalJumpHandler) {
    override fun getMode() = JumpMode.WORD1
}

class GlobalLineAction : BaseAction(GlobalJumpHandler) {
    override fun getMode() = JumpMode.LINE
}

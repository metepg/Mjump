# MJump

<!-- Plugin description -->
A powerful navigation plugin ported from vim-EasyMotion for JetBrains IDEs. MJump enhances your code navigation experience by providing quick jump capabilities to characters, words, and lines. It seamlessly integrates with IdeaVim for a more natural Vim-like experience.

[//]: # ([![Build]&#40;https://github.com/metepg/MJump/workflows/Build/badge.svg&#41;]&#40;https://github.com/a690700752/MJump/actions/workflows/build.yml&#41;)

[//]: # ([![Version]&#40;https://img.shields.io/jetbrains/plugin/v/15097-MJump.svg&#41;]&#40;https://plugins.jetbrains.com/plugin/15097-MJump&#41;)

[//]: # ([![Downloads]&#40;https://img.shields.io/jetbrains/plugin/d/15097-MJump.svg&#41;]&#40;https://plugins.jetbrains.com/plugin/15097-MJump&#41;)

# IMPORTANT!
- This is NOT my plugin. All rights and credit belong to github user `a690700752` for this amazing plugin.
- The official and original repository is at `https://github.com/a690700752/KJump` and is still maintained.
- Currently the only difference to KJump is that MJump is able to do the jumping across all open editors.

## Features

- Quick character jump: Jump to any character in the visible area
- Word navigation: Jump to any word or word starting with a specific character
- Line jumping: Quickly navigate to any line
- IdeaVim integration: Works seamlessly with IdeaVim
- Minimal keystrokes: Reach your target with just a few keystrokes

## Installation (manual)

1. Open your JetBrains IDE
2. Run `gradle buildPlugin`
3. Go to `Settings/Preferences → Plugins`
4. Click on `Settings` (gear icon)
5. Select "Install Plugin from Disk.."
6. Navigate to `build/distributions` and select the `MJump-{version}.zip`

## Usage

### Keyboard Shortcuts
There are no default activated shortcuts. You can assign MJump activation shortcuts in:
`Settings → Keymap → MJump`

Common shortcut suggestions:
- `Ctrl+,` for character jump
- `Ctrl+;` for word jump

### IdeaVim Integration
Add the following commands to your `~/.ideavimrc`:

```vimrc
" Basic jumps
nmap <leader><leader>s :action MJumpAction<cr>
nmap <leader><leader>w :action MJumpAction.Word0<cr>
nmap <leader><leader>l :action MJumpAction.Line<cr>

" Additional jumps
nmap <leader><leader>c :action MJumpAction.Char2<cr>
nmap <leader><leader>f :action MJumpAction.Word1<cr>
```

### Available Actions

| Name                | Action                  | Description                                                                        |
|---------------------|-------------------------|------------------------------------------------------------------------------------|
| MJump Char 1        | MJumpAction.Char1       | Input 1 character and jump to any same character                                   |
| MJump Char 2        | MJumpAction.Char2       | Input 2 characters and jump to any matching position                               |
| MJump Word 0        | MJumpAction.Word0       | Jump to any word                                                                   |
| MJump Word 1        | MJumpAction.Word1       | Input 1 character and jump to any word starting with this character                |
| MJump Line          | MJumpAction.Line        | Jump to any line                                                                   |

<!-- Plugin description end -->

## Tips
- Use Word0 for general word navigation
- Use Char2 for more precise jumps
- Line mode is great for long files
- Combine with IdeaVim for the best experience

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by [vim-EasyMotion](https://github.com/easymotion/vim-easymotion) and [KJump](https://github.com/easymotion/vim-easymotion)
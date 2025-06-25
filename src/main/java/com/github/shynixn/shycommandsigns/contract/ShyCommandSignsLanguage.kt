package com.github.shynixn.shycommandsigns.contract

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.LanguageProvider

interface ShyCommandSignsLanguage : LanguageProvider {
  var shyCommandSignsPlayerNotFoundMessage: LanguageItem

  var shyCommandSignsNoPermissionCommand: LanguageItem

  var shyCommandSignsReloadCommandHint: LanguageItem

  var shyCommandSignsReloadMessage: LanguageItem

  var shyCommandSignsCommonErrorMessage: LanguageItem

  var shyCommandSignsCommandSenderHasToBePlayer: LanguageItem

  var shyCommandSignsCommandUsage: LanguageItem

  var shyCommandSignsCommandDescription: LanguageItem

  var shyCommandSignsAddCommandHint: LanguageItem

  var shyCommandSignsNotFoundMessage: LanguageItem

  var shyCommandSignsBooleanNotFoundMessage: LanguageItem

  var shyCommandSignsRightClickOnSign: LanguageItem

  var shyCommandSignsRightClickOnSignSuccess: LanguageItem
}

package com.github.shynixn.shycommandsigns

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage

class ShyCommandSignsLanguageImpl : ShyCommandSignsLanguage {
 override val names: List<String>
  get() = listOf("en_us")
 override var shyCommandSignsPlayerNotFoundMessage = LanguageItem("[&9ShyCommandSigns&f] &cPlayer %shycommandsigns_param_1% not found.")

 override var shyCommandSignsNoPermissionCommand = LanguageItem("[&9ShyCommandSigns&f] &cYou do not have permission to execute this command.")

 override var shyCommandSignsReloadCommandHint = LanguageItem("Reloads all signs and configuration.")

 override var shyCommandSignsReloadMessage = LanguageItem("[&9ShyCommandSigns&f] Reloaded all signs and configuration.")

 override var shyCommandSignsCommonErrorMessage = LanguageItem("[&9ShyCommandSigns&f]&c A problem occurred. Check the console log for details.")

 override var shyCommandSignsCommandSenderHasToBePlayer = LanguageItem("[&9ShyCommandSigns&f] The command sender has to be a player if you do not specify the optional player argument.")

 override var shyCommandSignsCommandUsage = LanguageItem("[&9ShyCommandSigns&f] Use /shycommandsigns help to see more info about the plugin.")

 override var shyCommandSignsCommandDescription = LanguageItem("[&9ShyCommandSigns&f] All commands for the ShyCommandSign plugin.")

 override var shyCommandSignsAddCommandHint = LanguageItem("Adds a sign of the given sign type.")

 override var shyCommandSignsNotFoundMessage = LanguageItem("[&9ShyCommandSigns&f] &cSign %shycommandsigns_param_1% not found.")

 override var shyCommandSignsBooleanNotFoundMessage = LanguageItem("[&9ShyCommandSigns&f]&c Only true and false are allowed as values.")

 override var shyCommandSignsRightClickOnSign = LanguageItem("[&9ShyCommandSigns&f] Click on a sign to convert it into a %shycommandsigns_param_1% sign.")

 override var shyCommandSignsRightClickOnSignSuccess = LanguageItem("[&9ShyCommandSigns&f] Added a sign of type %shycommandsigns_param_1% sign.")
}

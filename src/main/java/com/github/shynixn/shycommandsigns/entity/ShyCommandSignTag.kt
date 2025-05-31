package com.github.shynixn.shycommandsigns.entity

import com.github.shynixn.mcutils.common.repository.Comment

class ShyCommandSignTag {
    @Comment("Unique tag key.")
    var key: String = ""

    @Comment("A tag value.")
    var value: String = ""
}

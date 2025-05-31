package com.github.shynixn.shycommandsigns.entity

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.repository.Comment

class ShyCommandSignLocation {
    @Comment("The location where the sign is placed.")
    var location: Vector3d = Vector3d()

    @Comment("Additional tags ")
    var tags: List<ShyCommandSignTag> = ArrayList()
}

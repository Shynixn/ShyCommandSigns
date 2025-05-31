package com.github.shynixn.shycommandsigns.contract

import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta

interface ShyCommandSignFactory {
    /**
     * Creates a new command sign from the given metadata.
     */
    fun createCommandSign( meta: ShyCommandSignMeta): ShyCommandSign
}

package com.github.shynixn.shycommandsigns.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Base BlockBall event where all BlockBall events inherit from.
 */
open class ShyCommandSignsEvent : Event(), Cancellable {
    private var cancelled: Boolean = false

    /**
     * Event.
     */
    companion object {
        private var handlers = HandlerList()

        /**
         * Handlerlist.
         */
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    /**
     * Returns all handles.
     */
    override fun getHandlers(): HandlerList {
        return ShyCommandSignsEvent.handlers
    }

    /**
     * Is the event cancelled.
     */
    override fun isCancelled(): Boolean {
        return cancelled
    }

    /**
     * Sets the event cancelled.
     */
    override fun setCancelled(flag: Boolean) {
        this.cancelled = flag
    }
}

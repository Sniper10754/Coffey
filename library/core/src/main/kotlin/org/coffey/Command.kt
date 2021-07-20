package org.coffey

interface Command : Identifiable, Runnable {
    fun run(args: Array<String>): Int
    override fun run() {
        run({ })
    }
}
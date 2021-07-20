package org.coffey.cli

class CLIManager(clazz: Class<Any>) {
    var boundClazz = clazz

    fun print(obj: Any) {
        kotlin.io.print(obj.toString())
    }

    fun println(obj: Any = "") {
        kotlin.io.println("[${boundClazz.simpleName}] $obj")
    }
}
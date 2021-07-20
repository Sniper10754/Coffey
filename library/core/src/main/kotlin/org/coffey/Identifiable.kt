package org.coffey

interface Identifiable {
    fun getName(): String = javaClass.simpleName
    fun getDescription(): String
}
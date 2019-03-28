package me.devoxin.kotlink


object Util {

    fun <T: Any> randomOrNull(list: List<T>): T? {
        return if (list.isEmpty()) {
            null
        } else {
            list.random()
        }
    }

}

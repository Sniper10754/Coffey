package org.coffey

class CoffeyShell {
    companion object {
        enum class ERROR_CODES(status: Int) {
            NO_ERROR(0),
            FEW_ARGUMENTS(1),
            TOO_MUCH_ARGUMENTS(2),
            COMMAND_NOT_FOUND(3);

            var stat = status
                get() = field
        }

        fun eval(args: Array<String>): Int {

            val mutableArgs = args.toMutableList()

            for (command in Properties.commands) {
                if (args[0].lowercase() == command.getName().lowercase()) {
                    mutableArgs.removeAt(0)
                    return command.run(mutableArgs.toTypedArray())
                }
            }
            return ERROR_CODES.COMMAND_NOT_FOUND.stat
        }
    }
}
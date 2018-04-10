package abs.message.lib.obj

abstract class ApplicationMessage<Type> : TypedMessage<Type> {
    constructor() : super()
    constructor(data: Type?) : super(data)
    constructor(vararg data: Any?) : super(*data)
}
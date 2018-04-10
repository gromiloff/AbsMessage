package abs.message.lib.obj

abstract class TypedMessage<Type> : IAbsoluteMessage {
    constructor() : super()
    constructor(data: Type?) : super(data)
    constructor(vararg data: Any?) : super(*data)

    open fun getObject() = getItem<Type>(0)
}

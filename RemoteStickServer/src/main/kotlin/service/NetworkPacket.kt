package main.kotlin.service


class NetworkPacket {
    var type: PacketTypes?
    var body: String

    constructor(data: String) {
        if (data.isNotEmpty()) {
            type = PacketTypes.fromByte((data[0] - '0').toByte())
            body = data.removeRange(0, 1)
        } else {
            type = null
            body = ""
        }
    }

    constructor(_type: PacketTypes, _body: String = "") {
        type = _type
        body = _body
    }

    fun toUTFBytes() = toString().toByteArray(Charsets.UTF_8)

    override fun toString(): String {
        return type?.toString() + body + '\n'
    }
}
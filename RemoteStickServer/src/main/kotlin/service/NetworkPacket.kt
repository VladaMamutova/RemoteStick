package main.kotlin.service

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException


class NetworkPacket {
    var type: PacketTypes? = null
    var body: JsonObject = JsonObject()

    constructor(data: String) {
        if (data.isNotEmpty()) {
            type = PacketTypes.fromByte((data[0] - '0').toByte())
            val dataBody = data.removeRange(0, 1)
            body = try {
                JsonParser().parse(dataBody) as JsonObject
            } catch (ex: JsonSyntaxException){
                JsonObject()
            }
        }
    }

    constructor(_type: PacketTypes, _body: JsonObject = JsonObject()) {
        type = _type
        body = _body
    }

    constructor(_type: PacketTypes, property: String, value: String) {
        type = _type
        body = JsonObject().apply { addProperty(property, value) }
    }

    fun toUTFBytes() = toString().toByteArray(Charsets.UTF_8)

    override fun toString(): String {
        return type?.toString() + body + '\n'
    }
}

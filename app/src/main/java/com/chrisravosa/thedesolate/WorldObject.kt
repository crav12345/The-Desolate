package com.chrisravosa.thedesolate

class WorldObject(
    var map: Array<Array<RoomObject>>,
    var numRooms: Int
) {
    constructor() : this (
        arrayOf(),
        0
    )
}
package com.chrisravosa.thedesolate

class RoomObject (
    var doors: Array<Boolean>,
    var y: Int,
    var x: Int,
    var hasBeenPathed: Boolean,
    var roomDescription: String,
    var hasBeenVisited: Boolean,
    var hasResources: Boolean
) {
    constructor() : this (
        arrayOf(false, false, false, false),
        0,
        0,
        false,
        "",
        false,
        false
    )
}
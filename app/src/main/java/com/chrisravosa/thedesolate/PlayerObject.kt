package com.chrisravosa.thedesolate

class PlayerObject(
    var hunger: Int,
    var thirst: Int,
    var stamina: Int,
    var health: Int,
    var isAlive: Boolean,
    var y: Int,
    var x: Int
) {
    constructor() : this (
        100,
        100,
        100,
        100,
        true,
        0,
        0,
    )
}
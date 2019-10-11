package com.example.pokemonlocaldemo

import android.location.Location

class  Pokemon{
    var name:String?=null
    var image:Int?=null
    var location:Location?=null
    var isCaught:Boolean?=null

    constructor(name:String,image:Int,lat:Double,lng:Double,isCaught:Boolean)
    {
        this.name = name
        this.image = image
        location=Location("")
        location!!.latitude=lat
        location!!.longitude = lng
        this.isCaught = isCaught
    }

}
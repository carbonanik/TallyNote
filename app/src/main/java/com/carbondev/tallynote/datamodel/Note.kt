package com.carbondev.tallynote.datamodel

data class Note(
    var key         : String = "",
    var createdAt   : String = "",
    var lastEdited  : Long   = 0,
    var content     : String = ""//,
)
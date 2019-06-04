package com.example.housepartyapp.api.party

import java.io.Serializable

class Party(var id: Int, var name: String, var start: String,
                    var end: String, var organizer_id: Int, var place: String,
                    var description: String, var alcohol_costs: Int,
                    var alcohol_free_costs: Int) : Serializable {
}
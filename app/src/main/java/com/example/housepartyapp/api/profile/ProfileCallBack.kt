package com.example.housepartyapp.api.profile

data class ProfileCallBack(val token: String, val userId: String, val full_name: String, val allergies: String,
                           val food_preferences: String, val alcohol_preferences: String) {
}
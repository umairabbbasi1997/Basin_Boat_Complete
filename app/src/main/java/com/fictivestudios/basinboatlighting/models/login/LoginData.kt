package com.fictivestudios.basinboatlighting.models.login

data class LoginData(
    var avatar: String="",
    var device_token: String="",
    var device_type: String="",
    var email: String="",
    var emergency_number: String="",
    var first_name: String="",
    var id: Int=0,
    var is_allowed_location: Int=0,
    var lang: String="",
    var last_name: String="",
    var lat: String="",
    var profile_completed: Int=0,
    var push_notification: Int=0
)
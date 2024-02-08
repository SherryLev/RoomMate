package org.example.controller

import org.example.model.UserModel
import org.example.userinterface.ViewEvent

class UserController(val model: UserModel) {
    // we can cast `Any` later since each event has an associated type
    fun invoke(event: ViewEvent, value: Any?) {
        when(event) {
            ViewEvent.FirstNameEvent -> model.firstname = value as String
            ViewEvent.LastNameEvent -> model.lastname = value as String
            ViewEvent.UppercaseEvent -> {
                model.firstname = model.firstname.uppercase()
                model.lastname = model.lastname.uppercase()
            }
            ViewEvent.LowercaseEvent -> {
                model.firstname = model.firstname.lowercase()
                model.lastname = model.lastname.lowercase()
            }
            ViewEvent.ResetEvent -> {
                model.firstname = ""
                model.lastname = ""
            }
        }
    }
}
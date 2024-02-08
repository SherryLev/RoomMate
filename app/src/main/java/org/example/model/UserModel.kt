package org.example.model

class UserModel : IPresenter() {
    var firstname: String = ""
        set(value) {
            field = value
            notifySubscribers()
        }

    var lastname: String = ""
        set(value) {
            field = value
            notifySubscribers()
        }
}
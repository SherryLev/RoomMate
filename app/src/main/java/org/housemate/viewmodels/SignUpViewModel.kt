package org.housemate.viewmodels

import org.housemate.model.User
import org.housemate.repositories.AuthRepository

class SignUpViewModel (private val repo:AuthRepository) {
    fun createUser(authUser: User) = repo.createUser(authUser)
}
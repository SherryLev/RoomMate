package org.housemate.repositories

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import org.housemate.model.User
import org.housemate.utils.AuthResultState

interface AuthRepository {
    fun createUser(
        auth: User
    )

    fun loginUser(
        auth: User
    )
}

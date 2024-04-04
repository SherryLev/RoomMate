package org.housemate.domain.use_cases

import org.housemate.domain.model.RegisterInputValidationType
import org.housemate.utils.containsNumber
import org.housemate.utils.containsSpecialChar
import org.housemate.utils.containsUpperCase

class ValidateRegisterInputUseCase {
    operator fun invoke(
        email: String,
        username: String,
        password: String,
        passwordRepeated: String
    ): RegisterInputValidationType {
        if(email.isEmpty() || password.isEmpty() || passwordRepeated.isEmpty()){
            return RegisterInputValidationType.EmptyField
        }
        if("@" !in email){
            return RegisterInputValidationType.NoEmail
        }
        if(username.count() > 15){
            return RegisterInputValidationType.UsernameTooLong
        }
        if(username.count() < 3){
            return RegisterInputValidationType.UsernameTooShort
        }
        if(password!= passwordRepeated){
            return RegisterInputValidationType.PasswordsDoNotMatch
        }
        if(password.count() < 8){
            return RegisterInputValidationType.PasswordTooShort
        }
        if(!password.containsNumber()){
            return RegisterInputValidationType.PasswordNumberMissing
        }
        if(!password.containsUpperCase()){
            return RegisterInputValidationType.PasswordUpperCaseMissing
        }
        if(!password.containsSpecialChar()){
            return RegisterInputValidationType.PasswordSpecialCharMissing
        }
        return RegisterInputValidationType.Valid
    }
}
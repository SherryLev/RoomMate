import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.housemate.domain.model.RegisterInputValidationType
import org.housemate.domain.use_cases.ValidateRegisterInputUseCase

class ValidateRegisterInputUseCaseTest {
    private lateinit var validateRegisterInputUseCase: ValidateRegisterInputUseCase
    @Before
    fun setUp() {
        validateRegisterInputUseCase = ValidateRegisterInputUseCase()
    }

    @Test
    fun `empty email should return EmptyField`() {
        val result = validateRegisterInputUseCase("", "username", "password", "password")
        assertEquals(RegisterInputValidationType.EmptyField, result)
    }

    @Test
    fun `empty password should return EmptyField`() {
        val result = validateRegisterInputUseCase("email@example.com", "username", "", "")
        assertEquals(RegisterInputValidationType.EmptyField, result)
    }

    @Test
    fun `empty repeated password should return EmptyField`() {
        val result = validateRegisterInputUseCase("email@example.com", "username", "password", "")
        assertEquals(RegisterInputValidationType.EmptyField, result)
    }

    @Test
    fun `no email should return NoEmail`() {
        val result = validateRegisterInputUseCase("emailexample.com", "username", "password", "password")
        assertEquals(RegisterInputValidationType.NoEmail, result)
    }

    @Test
    fun `username too long should return UsernameTooLong`() {
        val result = validateRegisterInputUseCase("email@example.com", "verylongusername1234", "password", "password")
        assertEquals(RegisterInputValidationType.UsernameTooLong, result)
    }

    @Test
    fun `username too short should return UsernameTooShort`() {
        val result = validateRegisterInputUseCase("email@example.com", "u", "password", "password")
        assertEquals(RegisterInputValidationType.UsernameTooShort, result)
    }

    @Test
    fun `passwords do not match should return PasswordsDoNotMatch`() {
        val result = validateRegisterInputUseCase("email@example.com", "username", "password", "differentpassword")
        assertEquals(RegisterInputValidationType.PasswordsDoNotMatch, result)
    }

    @Test
    fun `valid input should return Valid`() {
        val result = validateRegisterInputUseCase("email@example.com", "username", "Password1!", "Password1!")
        assertEquals(RegisterInputValidationType.Valid, result)
    }
}

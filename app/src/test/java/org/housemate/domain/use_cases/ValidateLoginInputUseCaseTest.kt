package org.housemate.domain.use_cases

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Before
import org.housemate.domain.model.LoginInputValidationType

class ValidateLoginInputUseCaseTest {
    private lateinit var validateLoginInputUseCase: ValidateLoginInputUseCase
    @Before
    fun setUp() {
        validateLoginInputUseCase = ValidateLoginInputUseCase()
    }

    @Test
    fun `empty email and password should return EmptyField`() {
        val result = validateLoginInputUseCase("", "")
        assertEquals(LoginInputValidationType.EmptyField, result)
    }

    @Test
    fun `empty email should return EmptyField`() {
        val result = validateLoginInputUseCase("", "password")
        assertEquals(LoginInputValidationType.EmptyField, result)
    }

    @Test
    fun `empty password should return EmptyField`() {
        val result = validateLoginInputUseCase("email@example.com", "")
        assertEquals(LoginInputValidationType.EmptyField, result)
    }

    @Test
    fun `invalid email should return NoEmail`() {
        val result = validateLoginInputUseCase("emailexample.com", "password")
        assertEquals(LoginInputValidationType.NoEmail, result)
    }

    @Test
    fun `valid email and password should return Valid`() {
        val result = validateLoginInputUseCase("email@example.com", "password")
        assertEquals(LoginInputValidationType.Valid, result)
    }
}

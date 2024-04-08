package org.housemate.domain.model

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChoreTest {

    lateinit var chore: Chore

    @Before
    fun setUp() {
        chore = Chore(
            userId = "user123",
            choreId = "chore456",
            choreName = "Clean the house",
            assignee = "John",
            assigneeId = "assignee123",
            category = "Household",
            repeat = "Weekly"
        )
    }

    @Test
    fun getUserId() {
        assertEquals("user123", chore.userId)
    }

    @Test
    fun getChoreId() {
        assertEquals("chore456", chore.choreId)
    }

    @Test
    fun getChoreName() {
        assertEquals("Clean the house", chore.choreName)
    }

    @Test
    fun getAssignee() {
        assertEquals("John", chore.assignee)
    }

    @Test
    fun getAssigneeId() {
        assertEquals("assignee123", chore.assigneeId)
    }

    @Test
    fun getCategory() {
        assertEquals("Household", chore.category)
    }

    @Test
    fun getRepeat() {
        assertEquals("Weekly", chore.repeat)
    }

    @After
    fun teardown() {
    }
}
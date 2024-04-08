import org.housemate.domain.model.User
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class UserTest {

    private lateinit var user: User

    @Before
    fun setUp() {
        user = User("test@example.com", "123456", "testUser", null, true)
    }

    @Test
    fun getEmail() {
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun getUid() {
        assertEquals("123456", user.uid)
    }

    @Test
    fun getUsername() {
        assertEquals("testUser", user.username)
    }

    @Test
    fun getGroupCode() {
        assertEquals(null, user.groupCode)
    }

    @Test
    fun getLoggedIn() {
        assertEquals(true, user.loggedIn)
    }
}

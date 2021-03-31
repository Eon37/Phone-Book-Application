package Phone_Book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class, PhoneRecordController.class})
class PhoneBookApplicationTests {

	@Autowired
	private UserController userController;
	@Autowired
	private PhoneRecordController phoneRecordController;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(phoneRecordController).isNotNull();
		assertThat(phoneRecordService).isNotNull();
	}

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userService;

	@MockBean
	private PhoneRecordService phoneRecordService;

	private static User u1;
	private static User u2;
	private static User u3;
	private static User u4;
	private static User uShortName;

	private static PhoneRecord u1pr1;
	private static PhoneRecord u1pr2;
	private static PhoneRecord u1pr3;
	private static PhoneRecord u2pr1;
	private static PhoneRecord u2pr2;
	private static PhoneRecord u3pr1;

	@BeforeAll
	static void init() {
		u1 = new User(1, "us1");
		u2 = new User(2, "us2");
		u3 = new User(3, "us3");
		u4 = new User(4, "us4");
		uShortName = new User(5, "us");

		u1pr1 = new PhoneRecord(1, "John Doe1", "8(800) 555-3535");
		u1pr2 = new PhoneRecord(2, "Incorrect number format", "+8-999-999-99-99");
		u1pr3 = new PhoneRecord(5, "John Doe2", "8(900) 555-3535");
		u2pr1 = new PhoneRecord(3, "Joh", "8(999) 999-9999");
		u2pr2 = new PhoneRecord(6, "John Doe1", "8(900) 555-3535");
		u3pr1 = new PhoneRecord(4, "John Doe from another country", "375(222) 222-2222");
	}

	@Test
	void getAllUsersCorrect() throws Exception {
		when(userService.getAllUsers()).thenReturn(List.of(u1, u2, u3, u4));

		this.mockMvc.perform(get("/api/users"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)))
				.andExpect(jsonPath("$[0].name", is("us1")))
				.andExpect(jsonPath("$[1].name", is("us2")))
				.andExpect(jsonPath("$[2].name", is("us3")))
				.andExpect(jsonPath("$[3].name", is("us4")));
	}

	@Test
	void getAllUsersWhenNoUsers() throws Exception {
		when(userService.getAllUsers()).thenReturn(List.of());

		this.mockMvc.perform(get("/api/users"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void saveUserCorrect() throws Exception {
		String requestJson = new ObjectMapper().writeValueAsString(u1);

		this.mockMvc.perform(post("/api/users")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.content(requestJson))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(requestJson));
	}

	@Test
	void saveUserEmptyRequestBody() throws Exception {
		this.mockMvc.perform(post("/api/users")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.content(""))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void saveUserShortName() throws Exception {
		String requestJson = new ObjectMapper().writeValueAsString(uShortName);

		this.mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void getByIdCorrect() throws Exception {
		when(userService.getById(1)).thenReturn(u1);
		String requestJson = new ObjectMapper().writeValueAsString(u1);

		mockMvc.perform(get("/api/users/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(requestJson));
	}

	@Test
	void getByIdNotFound() throws Exception {
		int id = 10;
		when(userService.getById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));

		mockMvc.perform(get("/api/users/" + id))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteUserCorrect() throws Exception {
		int id = 2;
		when(userService.getById(id)).thenReturn(u2);

		mockMvc.perform(delete("/api/users/" + 2))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteUserNotFound() throws Exception {
		int id = 10;
		when(userService.getById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));

		mockMvc.perform(delete("/api/users/" + id))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void updateUserCorrect() throws Exception {
		int id = 1;
		doCallRealMethod().when(userService).updateUser(id, u1);
		when(userService.getById(id)).thenReturn(u1);
		doNothing().when(userService).saveUser(u1);

		String requestJson = new ObjectMapper().writeValueAsString(u1);

		mockMvc.perform(put("/api/users/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void updateUserNotFound() throws Exception {
		int id = 10;
		doCallRealMethod().when(userService).updateUser(id, u1);
		when(userService.getById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));

		String requestJson = new ObjectMapper().writeValueAsString(u1);

		mockMvc.perform(put("/api/users/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void updateUserShortName() throws Exception {
		String requestJson = new ObjectMapper().writeValueAsString(uShortName);

		mockMvc.perform(put("/api/users/5")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isBadRequest());

	}

	@Test
	void getUsersByNameNoFound() throws Exception {
		String name = "r";
		when(userService.getUsersByName(name)).thenReturn(new ArrayList<>());

		mockMvc.perform(get("/api/users/search").param("name", "r"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void saveRecordCorrect() throws Exception {
		int id = 1;

		phoneRecordService.userService = mock(UserService.class);
		phoneRecordService.phoneRecordRepository = mock(PhoneRecordRepository.class);

		doCallRealMethod().when(phoneRecordService).savePhoneRecord(id, u1pr1);
		when(phoneRecordService.userService.getById(id)).thenReturn(u1);
		when(phoneRecordService.phoneRecordRepository.save(u1pr1)).thenReturn(u1pr1);

		String requestJson = new ObjectMapper().writeValueAsString(u1pr1);

		mockMvc.perform(post("/api/users/" + id + "/phone-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(requestJson));
	}

	@Test
	void saveRecordIncorrectFormat() throws Exception {
		int id = 1;

		String requestJson = new ObjectMapper().writeValueAsString(u1pr2);

		mockMvc.perform(post("/api/users/" + id + "/phone-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void saveRecordWhenNoUser() throws Exception {
		long id = 10;

		phoneRecordService.userService = mock(UserService.class);
		doCallRealMethod().when(phoneRecordService).savePhoneRecord(id, u1pr3);
		when(phoneRecordService.userService.getById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));

		String requestJson = new ObjectMapper().writeValueAsString(u1pr3);

		mockMvc.perform(post("/api/users/" + id + "/phone-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void getRecordByIdCorrect() throws Exception {
		int id = 1;
		int recordId = 1;

		when(phoneRecordService.getById(id, recordId)).thenReturn(u1pr1);

		String responseJson = new ObjectMapper().writeValueAsString(u1pr1);

		mockMvc.perform(get("/api/users/" + id + "/phone-records/" + recordId))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(responseJson));
	}

	@Test
	void getRecordByIdNotFound() throws Exception {
		int id = 1;
		int recordId = 4;

		when(phoneRecordService.getById(id, recordId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));

		mockMvc.perform(get("/api/users/" + id + "/phone-records/" + recordId))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteRecordCorrect() throws Exception{
		int id = 1;
		int recordId = 1;

		when(phoneRecordService.getById(id, recordId)).thenReturn(u1pr1);
		doNothing().when(phoneRecordService).delete(u1pr1);

		mockMvc.perform(delete("/api/users/" + id + "/phone-records/" + recordId))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteRecordNotFound() throws Exception {
		int id = 1;
		int recordId = 4;

		when(phoneRecordService.getById(id, recordId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found with id=" + id));

		mockMvc.perform(delete("/api/users/" + id + "/phone-records/" + recordId))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void updateRecordCorrect() throws Exception {
		int id = 1;
		int recordId = 1;

		doCallRealMethod().when(phoneRecordService).updateRecord(id, recordId, u1pr1);
		when(phoneRecordService.getById(id, recordId)).thenReturn(u1pr1);
		doNothing().when(phoneRecordService).savePhoneRecord(id, u1pr1);

		String requestJson = new ObjectMapper().writeValueAsString(u1pr1);

		mockMvc.perform(put("/api/users/" + id + "/phone-records/" + recordId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void updateRecordNotFound() throws Exception {
		int id = 1;
		int recordId = 7;

		doCallRealMethod().when(phoneRecordService).updateRecord(id, recordId, u1pr1);
		when(phoneRecordService.getById(id, recordId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found with ID=" + recordId));

		String requestJson = new ObjectMapper().writeValueAsString(u1pr1);

		mockMvc.perform(put("/api/users/" + id + "/phone-records/" + recordId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void getRecordByNumberCorrect() throws Exception {
		int id = 1;
		String number = "8(800) 555-3535";

		when(phoneRecordService.getRecordByNumber(id, number)).thenReturn(u1pr1);

		mockMvc.perform(get("/api/users/" + id + "/phone-records/search").param("number", number))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new ObjectMapper().writeValueAsString(u1pr1)));
	}

	@Test
	void getRecordByNumberIncorrectFormat() throws Exception {
		int id = 1;
		String number = "8 800 555-3535";

		when(phoneRecordService.getRecordByNumber(id, number)).thenCallRealMethod();

		mockMvc.perform(get("/api/users/" + id + "/phone-records/search").param("number", number))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void getBookByOwnerCorrect() throws Exception {
		int id = 1;

		phoneRecordService.userService = mock(UserService.class);
		when(phoneRecordService.getBookByOwner(id)).thenCallRealMethod();
		when(phoneRecordService.userService.getBookByOwner(id)).thenReturn(List.of(u1pr1, u1pr3));

		String responseJson = new ObjectMapper().writeValueAsString(u1pr1);
		String responseJson1 = new ObjectMapper().writeValueAsString(u1pr3);

		mockMvc.perform(get("/api/users/" + id + "/phone-records"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json("[" + responseJson + "," + responseJson1 + "]"));
	}

	@Test
	void getBookByOwnerIncorrectId() throws Exception {
		int id = 10;

		phoneRecordService.userService = mock(UserService.class);
		when(phoneRecordService.getBookByOwner(id)).thenCallRealMethod();
		when(phoneRecordService.userService.getBookByOwner(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));

		mockMvc.perform(get("/api/users/" + id + "/phone-records"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
}

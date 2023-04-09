package ch.gryphus.springboot.controller;

import ch.gryphus.springboot.TestUtils;
import ch.gryphus.springboot.model.Employee;
import ch.gryphus.springboot.repository.EmployeeRepository;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EmployeeController.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class EmployeeControllerTest {

    private final String URL = "/api/v1/employees/";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void test01CreateEmployee() throws Exception {

        Employee empStub = new Employee("Test", "Test", "test@abc.com");
        when(employeeRepository.save(any(Employee.class))).thenReturn(empStub);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(empStub))).andReturn();

        assertThat(result).isNotNull();

        Employee resultEmployee = TestUtils.jsonToObject(result.getResponse().getContentAsString(), Employee.class);
        assertThat(resultEmployee).hasFieldOrPropertyWithValue("emailId", "test@abc.com");
    }

    @Test
    void test02GetEmployeeWhenIdFound() throws Exception {

        Employee empStub = new Employee("Test", "Test", "test@abc.com");
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(empStub));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URL + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();

        assertThat(result).isNotNull();

        Employee resultEmployee = TestUtils.jsonToObject(result.getResponse().getContentAsString(), Employee.class);
        assertThat(resultEmployee).hasFieldOrPropertyWithValue("emailId", "test@abc.com");
    }

    @Test
    void test03GetAllEmployees() throws Exception {

        List<Employee> empList = buildEmployees();
        when(employeeRepository.findAll()).thenReturn(empList);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URL).accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status, "Incorrect Response Status");

        TypeToken<List<Employee>> token = new TypeToken<>() {
        };
        List empListResult = TestUtils.jsonToList(result.getResponse().getContentAsString(), token);

        assertNotNull(empListResult, "Employees not found");
        assertEquals(empList.size(), empListResult.size(), "Incorrect Employee List");
    }

    @Test
    void test04UpdateEmployee() throws Exception {

        Employee empStub = new Employee("TestUpdated", "TestUpdated", "testupdated@abc.com");
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(empStub));
        when(employeeRepository.save(any(Employee.class))).thenReturn(empStub);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(URL + "1").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(empStub))).andExpect(status().isOk())
                .andReturn();

        assertThat(result).isNotNull();

        Employee resultEmployee = TestUtils.jsonToObject(result.getResponse().getContentAsString(), Employee.class);
        assertThat(resultEmployee).hasFieldOrPropertyWithValue("emailId", "testupdated@abc.com");
    }

    @Test
    void test05DeleteEmployeeWhenIdFound() throws Exception {

        Employee empStub = new Employee("Test", "Test", "test@abc.com");
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(empStub));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(URL + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();

        assertThat(result).isNotNull();
        Object response = TestUtils.jsonToObject(result.getResponse().getContentAsString(), Object.class);
        assertThat(response).hasToString("{deleted=true}");
    }

    @Test
    void test06GetEmployeeWhenIdNotFound() throws Exception {

        int randomId = (new Random()).nextInt(1000);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URL + randomId))
                .andExpect(status().isNotFound()).andReturn();

        assertThat(result).isNotNull();
        assertThat(result.getResolvedException()).hasMessageContaining("not exist with id :" + randomId);
    }

    private List<Employee> buildEmployees() {
        Employee e1 = new Employee("Test1", "Test1", "test1@abc.com");
        Employee e2 = new Employee("Test2", "Test2", "test2@abc.com");
        return Arrays.asList(e1, e2);
    }
}
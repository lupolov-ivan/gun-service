package gun.service.controller;

import gun.service.entity.Unit;
import gun.service.repository.UnitRepository;
import gun.service.utils.TestUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static gun.service.entity.UnitState.*;
import static gun.service.entity.UnitType.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UnitControllerTest {

    @Value("${uri-builder.port}")
    private Integer port;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UnitRepository unitRepository;

    @After
    public void cleanUp() {
        unitRepository.deleteAll();
    }

    @Test
    public void whenCreateUnit_ThenMustBeCreated() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(post("/units")
                .contentType("application/json")
                .content(TestUtils.fromResource("controller/unit/create_unit.json")))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        int id = Integer.parseInt(response.getHeader("location").replace("http://localhost:"+ port +"/units/", ""));

        assertTrue(unitRepository.findById(id).isPresent());
    }

    @Test
    public void whenGetExistUnitById_ThenReturnUnitAndStatusOk() throws Exception {

        int unitId = unitRepository.save(new Unit(null, 2, 4, 100, AFC, ACTIVE)).getId();

        mockMvc.perform(get("/units/{id}", unitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posX", is(2)))
                .andExpect(jsonPath("$.posY", is(4)));
    }

    @Test
    public void whenGetNotExistUnitById_ThenReturn404Status() throws Exception {

        mockMvc.perform(get("/units/{id}", 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetListUnits_ThenReturnListUnitsAndStatusOk() throws Exception {

        unitRepository.save(new Unit(null, 2, 4, 100, AFC, ACTIVE));
        unitRepository.save(new Unit(null, 3, 5, 100, AFC, ACTIVE));

        mockMvc.perform(get("/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].posX", is(2)))
                .andExpect(jsonPath("$[0].posY", is(4)))
                .andExpect(jsonPath("$[1].posX", is(3)))
                .andExpect(jsonPath("$[1].posY", is(5)));
    }

    @Test
    public void whenGetEmptyListUnits_ThenReturnEmptyListAndStatusOk() throws Exception {

        mockMvc.perform(get("/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void whenDeleteExistUnitById_ThenUnitMustBeDeletedAnd204Status() throws Exception {

        int unitId = unitRepository.save(new Unit(null, 2, 4, 100, AFC, ACTIVE)).getId();

        mockMvc.perform(delete("/units/{id}", unitId))
                .andExpect(status().isNoContent());

        assertTrue(unitRepository.findAll().isEmpty());
    }

    @Test
    public void whenDeleteExistUnitById_ThenReturn404Status() throws Exception {

        mockMvc.perform(delete("/units/{id}", 666))
                .andExpect(status().isNotFound());
    }
}
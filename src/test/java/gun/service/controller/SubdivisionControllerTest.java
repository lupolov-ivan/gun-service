package gun.service.controller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import gun.service.entity.Subdivision;
import gun.service.entity.Unit;
import gun.service.repository.SubdivisionRepository;
import gun.service.repository.UnitRepository;
import gun.service.utils.TestUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static gun.service.entity.UnitState.ACTIVE;
import static gun.service.entity.UnitType.AFC;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SubdivisionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UnitRepository unitRepository;
    @Autowired
    SubdivisionRepository subdivisionRepository;

    @Rule
    public WireMockRule wireMockRuleForEnemy = new WireMockRule(8089);

    @After
    public void cleanUp() {
        unitRepository.deleteAll();
        subdivisionRepository.deleteAll();
    }

    @Test
    public void whenCreateSubdivision_ThenSubdivisionMustBeCreated() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/subdivisions")
                .contentType("application/json")
                .content(TestUtils.fromResource("controller/subdivision/create_subdivision.json")))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        int id = Integer.parseInt(response.getHeader("location").replace("http://localhost:8081/subdivisions/", ""));

        assertTrue(subdivisionRepository.findById(id).isPresent());
    }

    @Test
    public void whenGetExistSubdivision_ThenReturnSubdivisionAndStatusOk() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();

        mockMvc.perform(get("/subdivisions/{subdivisionId}", subdivisionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ALPHA")));
    }

    @Test
    public void whenGetNotExistSubdivision_ThenReturn404Status() throws Exception {

        mockMvc.perform(get("/subdivisions/{id}", 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetExistSubdivisionUnits_ThenReturnListUnitsAndStatusOk() throws Exception {

        int subdivisionId1 = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();
        int subdivisionId2 = subdivisionRepository.save(new Subdivision(null, "BETA")).getId();

        unitRepository.save(new Unit(null, 2, 4, 100, AFC, ACTIVE, subdivisionId1));
        unitRepository.save(new Unit(null, 3, 5, 100, AFC, ACTIVE, subdivisionId1));
        unitRepository.save(new Unit(null, 4, 6, 100, AFC, ACTIVE, subdivisionId2));

        mockMvc.perform(get("/subdivisions/{subdivisionId}/guns", subdivisionId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].posX", is(2)))
                .andExpect(jsonPath("$[0].posY", is(4)))
                .andExpect(jsonPath("$[1].posX", is(3)))
                .andExpect(jsonPath("$[1].posY", is(5)));
    }

    @Test
    public void whenGetExistSubdivisionUnits_ThenReturnEmptyListUnitsAndStatusOk() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();

        mockMvc.perform(get("/subdivisions/{subdivisionId}/guns", subdivisionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void whenGetNotExistSubdivisionUnits_ThenReturn404Status() throws Exception {

        mockMvc.perform(get("/subdivisions/{subdivisionId}/guns", 666))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void whenAddUnitToSubdivision_ThenReturn204Status() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();
        int unitId =  unitRepository.save(new Unit(null, 2, 4, 100, AFC, ACTIVE)).getId();

        mockMvc.perform(patch("/subdivisions/{subdivisionId}/units/{unitId}/add", subdivisionId, unitId))
                .andExpect(status().isNoContent());

        Unit unit = unitRepository.findById(unitId).get();
        int quantityUnits = unitRepository.findUnitsBySubdivisionId(subdivisionId).size();

        assertEquals(1, quantityUnits);
        assertEquals(subdivisionId, unit.getSubdivisionId());
    }

    @Test
    public void whenAddUnitToNotExistSubdivision_ThenReturn404Status() throws Exception {

        int unitId =  unitRepository.save(new Unit(null, 2, 4, 100, AFC, ACTIVE)).getId();

        mockMvc.perform(patch("/subdivisions/{subdivisionId}/units/{unitId}/add", 666, unitId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenAddNotExistUnitToSubdivisionById_ThenReturn404Status() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();

        mockMvc.perform(patch("/subdivisions/{subdivisionId}/units/{unitId}/add", subdivisionId, 666))
                .andExpect(status().isNotFound());
    }
}
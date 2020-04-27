package gun.service.controller;

import gun.service.entity.Subdivision;
import gun.service.entity.Unit;
import gun.service.entity.UnitState;
import gun.service.repository.SubdivisionRepository;
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

import static gun.service.entity.UnitState.ACTIVE;
import static gun.service.entity.UnitType.AFC;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SubdivisionControllerTest {

    @Value("${uri-builder.port}")
    private Integer port;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UnitRepository unitRepository;
    @Autowired
    SubdivisionRepository subdivisionRepository;

    @After
    public void cleanUp() {
        unitRepository.deleteAll();
        subdivisionRepository.deleteAll();
    }

    @Test
    public void whenCreateEmptySubdivision_ThenSubdivisionMustBeCreated() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(post("/subdivisions/empty")
                .contentType("application/json")
                .content(TestUtils.fromResource("controller/subdivision/create_empty_subdivision.json")))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        int id = Integer.parseInt(response.getHeader("location").replace("http://localhost:"+ port +"/subdivisions/", ""));

        assertTrue(subdivisionRepository.findById(id).isPresent());
    }

    @Test
    public void whenCreateFilledSubdivision_ThenSubdivisionMustBeCreated() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(post("/subdivisions/filled")
                .contentType("application/json")
                .content(TestUtils.fromResource("controller/subdivision/create_filled_subdivision.json")))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        int id = Integer.parseInt(response.getHeader("location").replace("http://localhost:"+ port +"/subdivisions/", ""));

        assertTrue(subdivisionRepository.findById(id).isPresent());
        assertEquals(4, unitRepository.findUnitsBySubdivisionId(id).size());
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

        unitRepository.save(Unit.builder().posX(2).posY(4).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId1).build());
        unitRepository.save(Unit.builder().posX(3).posY(5).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId1).build());
        unitRepository.save(Unit.builder().posX(4).posY(6).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId2).build());

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
    public void whenAddNotExistUnitToSubdivision_ThenReturn404Status() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();

        mockMvc.perform(patch("/subdivisions/{subdivisionId}/units/{unitId}/add", subdivisionId, 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenRemoveExistUnitFromSubdivision_ThenUnitDeleteAndReturn204Status() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();
        int unitId =  unitRepository.save(Unit.builder().posX(2).posY(4).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId).build()).getId();

        mockMvc.perform(patch("/subdivisions/units/{unitId}/remove", unitId))
                .andExpect(status().isNoContent());

        int quantityUnits = unitRepository.findUnitsBySubdivisionId(subdivisionId).size();
        Unit unit = unitRepository.findById(unitId).get();

        assertEquals(0, quantityUnits);
        assertNull(unit.getSubdivisionId());
    }

    @Test
    public void whenRemoveNotExistUnitFromSubdivision_ThenReturn404Status() throws Exception {

        mockMvc.perform(patch("/subdivisions/units/{unitId}/remove", 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSetUnitsDeadState_ThenAllUnitsGetDeadStateAndReturn204Status() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();
        int unitId1 =  unitRepository.save(Unit.builder().posX(2).posY(4).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId).build()).getId();
        int unitId2 =  unitRepository.save(Unit.builder().posX(3).posY(5).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId).build()).getId();

        mockMvc.perform(patch("/subdivisions/{subdivisionId}/units/state/dead", subdivisionId))
                .andExpect(status().isNoContent());

        Unit unit1 = unitRepository.findById(unitId1).get();
        Unit unit2 = unitRepository.findById(unitId2).get();

        assertEquals(UnitState.DEAD, unit1.getUnitState());
        assertEquals(UnitState.DEAD, unit2.getUnitState());
    }

    @Test
    public void whenSetUnitsDeadStateInNotExistSubdivision_ThenReturn404Status() throws Exception {

        mockMvc.perform(patch("/subdivisions//{subdivisionId}/units/state/dead", 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteSubdivision_ThenAllUnitsSetNullSubdivisionIdAndReturn204Status() throws Exception {

        int subdivisionId = subdivisionRepository.save(new Subdivision(null, "ALPHA")).getId();
        int unitId1 =  unitRepository.save(Unit.builder().posX(2).posY(4).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId).build()).getId();
        int unitId2 =  unitRepository.save(Unit.builder().posX(3).posY(5).protectionLevel(100).unitType(AFC).unitState(ACTIVE).subdivisionId(subdivisionId).build()).getId();

        mockMvc.perform(delete("/subdivisions/{subdivisionId}", subdivisionId))
                .andExpect(status().isNoContent());

        Unit unit1 = unitRepository.findById(unitId1).get();
        Unit unit2 = unitRepository.findById(unitId2).get();

        assertNull(unit1.getSubdivisionId());
        assertNull(unit2.getSubdivisionId());
    }

    @Test
    public void whenDeleteNotExistSubdivision_ThenReturn404Status() throws Exception {

        mockMvc.perform(delete("/subdivisions/{subdivisionId}", 666))
                .andExpect(status().isNotFound());
    }


}
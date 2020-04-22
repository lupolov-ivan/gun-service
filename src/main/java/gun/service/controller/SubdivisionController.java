package gun.service.controller;

import gun.service.dto.UnitDto;
import gun.service.entity.Subdivision;
import gun.service.entity.Unit;
import gun.service.service.SubdivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subdivisions")
public class SubdivisionController {

    private final SubdivisionService subdivisionService;

    @PostMapping
    public ResponseEntity<Subdivision> createSubdivision(@RequestBody Subdivision subdivision) {

        subdivisionService.createSubdivision(subdivision);

        return ResponseEntity
                .created(URI.create("/subdivisions"+ subdivision.getId()))
                .build();
    }

    @GetMapping("{id}/guns")
    public ResponseEntity<List<UnitDto>> getSubdivisionUnitsById(@PathVariable Integer id) {
        return ResponseEntity.ok(subdivisionService.getSubdivisionUnitsById(id));
    }

    @PatchMapping("{subdivisionId}/units/{unitId}/add")
    public ResponseEntity<?> addUnitToSubdivisions(@PathVariable Integer subdivisionId, @PathVariable Integer unitId) {
        subdivisionService.addUnitToSubdivisions(unitId, subdivisionId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/units/{unitId}/remove")
    public ResponseEntity<?> removeUnitToSubdivisions(@PathVariable Integer unitId) {
        subdivisionService.removeUnitFromSubdivisions(unitId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSubdivision(@PathVariable Integer id) {
        subdivisionService.deleteSubdivision(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/battle/{battleId}/start")
    public ResponseEntity<?> startPatrolling(@PathVariable Integer id, @PathVariable Integer battleId) {
        subdivisionService.startPatrolling(id, battleId);
        return ResponseEntity.noContent().build();
    }
}

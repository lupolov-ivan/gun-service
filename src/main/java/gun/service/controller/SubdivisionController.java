package gun.service.controller;

import gun.service.dto.UnitDto;
import gun.service.entity.Subdivision;
import gun.service.exceptions.NotFoundException;
import gun.service.service.SubdivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestController
@RequiredArgsConstructor
@RequestMapping("/subdivisions")
public class SubdivisionController {

    @Value("${uri-builder.scheme}")
    private String scheme;
    @Value("${uri-builder.host}")
    private String host;
    @Value("${uri-builder.port}")
    private Integer port;

    private final SubdivisionService subdivisionService;

    @PostMapping
    public ResponseEntity<Subdivision> createSubdivision(@RequestBody Subdivision subdivision) {

        subdivisionService.createSubdivision(subdivision);

        return ResponseEntity
                .created(createUriBuilder("/subdivisions/{id}").build(subdivision.getId()))
                .build();
    }

    @GetMapping("{subdivisionId}")
    public ResponseEntity<Subdivision> getSubdivisionById(@PathVariable Integer subdivisionId) {
        return ResponseEntity.ok(subdivisionService.getSubdivisionById(subdivisionId));
    }

    @GetMapping("{subdivisionId}/guns")
    public ResponseEntity<List<UnitDto>> getSubdivisionUnitsById(@PathVariable Integer subdivisionId) {
        return ResponseEntity.ok(subdivisionService.getSubdivisionUnitsById(subdivisionId));
    }

    @PatchMapping("{subdivisionId}/units/{unitId}/add")
    public ResponseEntity<?> addUnitToSubdivisions(@PathVariable Integer subdivisionId, @PathVariable Integer unitId) {
        subdivisionService.addUnitToSubdivisions(unitId, subdivisionId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/units/{unitId}/remove")
    public ResponseEntity<?> removeUnitFromSubdivisions(@PathVariable Integer unitId) {
        subdivisionService.removeUnitFromSubdivisions(unitId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{subdivisionId}/units/state/dead")
    public ResponseEntity<?> setStateUnitsDeadBySubdivisionId(@PathVariable Integer subdivisionId) {
        subdivisionService.setStateUnitsDeadBySubdivisionId(subdivisionId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{subdivisionId}")
    public ResponseEntity<?> deleteSubdivision(@PathVariable Integer subdivisionId) {
        subdivisionService.deleteSubdivision(subdivisionId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("{subdivisionId}/battle/{battleId}/start")
    public ResponseEntity<?> startPatrolling(@PathVariable Integer subdivisionId, @PathVariable Integer battleId) {
        subdivisionService.startPatrolling(subdivisionId, battleId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = NOT_FOUND, reason = "Resource doesn't exist or has been deleted")
    public void handleNotFound() { }

    private UriComponentsBuilder createUriBuilder(String uriTemplate) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(uriTemplate);
    }
}

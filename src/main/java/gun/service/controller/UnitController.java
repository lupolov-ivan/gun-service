package gun.service.controller;

import gun.service.entity.Unit;
import gun.service.exceptions.NotFoundException;
import gun.service.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    @Value("${uri-builder.scheme}")
    private String scheme;
    @Value("${uri-builder.host}")
    private String host;
    @Value("${uri-builder.port}")
    private Integer port;

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) {
        unitService.saveUnit(unit);
        return ResponseEntity
                .created(createUriBuilder("/units/{id}").build(unit.getId()))
                .build();
    }

    @GetMapping("{unitId}")
    private ResponseEntity<Unit> getUnitById(@PathVariable Integer unitId) {
        return ResponseEntity.ok(unitService.findById(unitId).orElseThrow(NotFoundException::new));
    }

    @GetMapping
    private ResponseEntity<List<Unit>> getAllUnits() {
        return ResponseEntity.ok(unitService.getUnits());
    }

    @DeleteMapping("{unitId}")
    public ResponseEntity<?> removeUnitById(@PathVariable Integer unitId) {
        unitService.removeUnitById(unitId);
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

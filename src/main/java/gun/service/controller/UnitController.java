package gun.service.controller;

import gun.service.entity.Unit;
import gun.service.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) {
        unitService.saveUnit(unit);
        return ResponseEntity
                .created(URI.create("units/"+ unit.getId()))
                .build();
    }

    @GetMapping
    private ResponseEntity<List<Unit>> getUnits() {
        return ResponseEntity.ok(unitService.getUnits());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> removeUnitById(@PathVariable Integer id) {
        unitService.removeUnitById(id);

        return ResponseEntity.noContent().build();
    }
}

package gun.service.controller;

import gun.service.entity.Unit;
import gun.service.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/guns")
@RequiredArgsConstructor
public class GunController {

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<Unit> createGun(@RequestBody Unit unit) {
        unitService.createGun(unit);
        return ResponseEntity
                .created(URI.create("guns/"+ unit.getId()))
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register() {
        unitService.registerUnitsOnBattlefield();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/patrol")
    public ResponseEntity<?> startPatrolling() {
        unitService.startPatrolling();
        return ResponseEntity.noContent().build();
    }

}

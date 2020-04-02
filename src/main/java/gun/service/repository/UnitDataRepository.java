package gun.service.repository;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UnitDataRepository {

    private final RestTemplate restTemplate;

    public Optional<UnitDto> getByCoordinate(Integer posX, Integer posY) {

        String url = "http://localhost:8080/units/x/"+ posX +"/y/" + posY;

        return Optional.ofNullable(restTemplate.getForObject(url, UnitDto.class));
    }

    public void damage(UnitDamageDto damageDto) {

        String url = "http://localhost:8080/units/damage";
        HttpEntity<UnitDamageDto> request = new HttpEntity<>(damageDto);

        restTemplate.patchForObject(url,request, Void.class);
    }

    public void registerOnBattlefield(UnitDto unitDto) {

        String url = "http://localhost:8080/units/";
        HttpEntity<UnitDto> request = new HttpEntity<>(unitDto);

        restTemplate.postForObject(url,request, Void.class);
    }
}

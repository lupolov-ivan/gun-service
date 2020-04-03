package gun.service.repository;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.service.systems.Battlefield;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
@RequiredArgsConstructor
public class BattleManagerRepository {

    private final RestTemplate restTemplate;

    public UnitDto findUnitByCoordinate(Integer posX, Integer posY) {

        String url = "http://localhost:8080/units/x/"+ posX +"/y/" + posY;

        try {
            ResponseEntity<UnitDto> response = restTemplate.getForEntity(url, UnitDto.class);
            return response.getBody();
        } catch (HttpClientErrorException ignored) { }

        return null;
    }

    public void setUnitDamage(UnitDamageDto damageDto) {

        String url = "http://localhost:8080/units/damage";
        HttpEntity<UnitDamageDto> request = new HttpEntity<>(damageDto);

        restTemplate.patchForObject(url,request, Void.class);
    }

    public void registerUnitOnBattlefield(UnitDto unitDto) {

        String url = "http://localhost:8080/units/";
        HttpEntity<UnitDto> request = new HttpEntity<>(unitDto);

        restTemplate.postForObject(url,request, Void.class);
    }

    public Battlefield getBattlefield() {

        String url = "http://localhost:8080/battlefield/info";

        return restTemplate.getForObject(url, Battlefield.class);
    }
}

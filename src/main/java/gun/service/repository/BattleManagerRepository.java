package gun.service.repository;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.service.gun.systems.Battlefield;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
@RequiredArgsConstructor
public class BattleManagerRepository {

    @Value("${battle-manager-server.host}")
    private String host;
    @Value("${battle-manager-server.port}")
    private Integer port;

    private String template = "http://"+ host +":"+ port +"/units";
    private final RestTemplate restTemplate;

    public UnitDto findUnitByCoordinateAndBattleId(Integer posX, Integer posY, Integer battleId) {

        String url = template +"/x/"+ posX +"/y/"+ posY +"/battle/"+ battleId;

        try {
            ResponseEntity<UnitDto> response = restTemplate.getForEntity(url, UnitDto.class);
            return response.getBody();
        } catch (HttpClientErrorException ignored) { }

        return null;
    }

    public void setUnitDamage(UnitDamageDto damageDto, Integer battleId) {

        String url = template +"/battle/"+ battleId +"/units/damage";
        HttpEntity<UnitDamageDto> request = new HttpEntity<>(damageDto);

        restTemplate.patchForObject(url,request, Void.class);
    }

    public Battlefield getBattlefield() {

        String url = template +"/battlefield/info";

        return restTemplate.getForObject(url, Battlefield.class);
    }
}

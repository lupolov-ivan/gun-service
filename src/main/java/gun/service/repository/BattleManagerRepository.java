package gun.service.repository;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.service.gun.systems.Battlefield;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class BattleManagerRepository {

    private final String template;
    private final RestTemplate restTemplate;

    public BattleManagerRepository(@Value("${battle-service-server.host}") String host,
                                   @Value("${battle-service-server.port}") Integer port,
                                   RestTemplate restTemplate) {
        this.template = "http://"+ host +":"+ port;
        this.restTemplate = restTemplate;
    }

    public UnitDto findUnitByCoordinateAndBattleId(Integer posX, Integer posY, Integer battleId) {

        String url = template +"/units/x/"+ posX +"/y/"+ posY +"/battle/"+ battleId;

        try {
            ResponseEntity<UnitDto> response = restTemplate.getForEntity(url, UnitDto.class);
            return response.getBody();
        } catch (HttpClientErrorException ignored) { }

        return null;
    }

    public void setUnitDamage(UnitDamageDto damageDto, Integer battleId) {

        String url = template +"/units/damage/battle/"+ battleId;
        HttpEntity<UnitDamageDto> request = new HttpEntity<>(damageDto);

        restTemplate.patchForObject(url,request, Void.class);
    }

    public Battlefield getBattlefield() {

        String url = template +"/battlefield/info";

        return restTemplate.getForObject(url, Battlefield.class);
    }
}

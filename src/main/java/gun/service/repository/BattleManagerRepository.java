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

import java.util.Arrays;
import java.util.List;

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

    public List<UnitDto> getAllUnitsOnTheBattlefield(Integer battleId) {

        String url = template +"/battles/"+ battleId +"/units";

        ResponseEntity<UnitDto[]> response = restTemplate.getForEntity(url, UnitDto[].class);
        return Arrays.asList(response.getBody());
    }

    public void setUnitDamage(UnitDamageDto damageDto, Integer battleId) {

        String url = template +"/battles/"+ battleId +"/units/damage";
        HttpEntity<UnitDamageDto> request = new HttpEntity<>(damageDto);

        restTemplate.patchForObject(url, request, Void.class);
    }

    public Battlefield getBattlefield() {

        String url = template +"/battlefield/info";

        return restTemplate.getForObject(url, Battlefield.class);
    }
}

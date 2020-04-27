package gun.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateSubdivisionDto {

    private String name;

    private Integer protectionLevel;

    private Integer loadCassetteTime;
    private Integer disconnectCassetteTime;
    private Integer shotPeriod;

    private Integer quantityBurstingCassette;
    private Integer capacityBurstingCassette;
    private Integer quantityArmorPiercingCassette;
    private Integer capacityArmorPiercingCassette;

    private List<Location> unitLocation;
}

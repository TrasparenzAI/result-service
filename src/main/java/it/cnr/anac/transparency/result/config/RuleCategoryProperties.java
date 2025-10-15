package it.cnr.anac.transparency.result.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.anac.transparency.result.v1.dto.RuleDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties
@Getter
@Setter
@NoArgsConstructor
public class RuleCategoryProperties {
    protected String slice;

    private List<RuleDto> rules;

    @PostConstruct
    public void postConstruct() {
        Optional.ofNullable(slice)
                .ifPresent(s -> {
                    try {
                        rules = new ObjectMapper().readValue(slice, new TypeReference<Map<String,List<RuleDto>>>(){})
                                .get("dettagli");
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
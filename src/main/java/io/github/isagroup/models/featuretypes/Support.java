package io.github.isagroup.models.featuretypes;

import java.util.Map;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Support extends Feature {

    @Override
    public Map<String, Object> serializeFeature() {

        Map<String, Object> attributes = featureAttributesMap();
        attributes.put("type", FeatureType.SUPPORT.toString());
        return attributes;
    }

    @Override
    public String toString() {
        return "Support[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue + ", value: "
                + value + "]";
    }

}

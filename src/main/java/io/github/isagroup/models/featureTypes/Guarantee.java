package io.github.isagroup.models.featuretypes;

import java.util.Map;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Guarantee extends Feature {
    private String docURL;

    @Override
    public Map<String, Object> serializeFeature() {
        Map<String, Object> attributes = featureAttributesMap();
        attributes.put("type", FeatureType.GUARANTEE.toString());
        if (docURL != null) {
            attributes.put("docUrl", docURL);
        }
        return attributes;
    }

    @Override
    public String toString() {
        return "Guarantee[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue + ", value: "
                + value + ", docURL: " + docURL + "]";
    }
}

package io.github.isagroup.models.usagelimittypes;

import java.util.Map;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonRenewable extends UsageLimit {

    @Override
    public Map<String, Object> serializeUsageLimit() {

        Map<String, Object> attributes = usageLimitAttributes();
        attributes.put("type", UsageLimitType.NON_RENEWABLE.toString());
        return attributes;
    }

    @Override
    public String toString() {
        return "NonRenewable[valueType: " + this.getValueType() + ", defaultValue: " + this.getDefaultValue()
                + ", value: " + this.getValue() + ", linkedFeature: " + this.getLinkedFeatures() + "]";
    }

}
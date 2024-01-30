package io.github.isagroup.models.usagelimittypes;

import java.util.Map;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NonRenewable extends UsageLimit {

    public NonRenewable() {
        this.type = UsageLimitType.NON_RENEWABLE;
    }

    @Override
    public String toString() {
        return "NonRenewable[valueType: " + this.getValueType() + ", defaultValue: " + this.getDefaultValue()
                + ", value: " + this.getValue() + ", linkedFeature: " + this.getLinkedFeatures() + "]";
    }

}

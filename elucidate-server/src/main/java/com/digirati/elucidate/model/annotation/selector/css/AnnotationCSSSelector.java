package com.digirati.elucidate.model.annotation.selector.css;

import java.io.Serializable;

import com.digirati.elucidate.model.annotation.selector.AbstractAnnotationSelector;

@SuppressWarnings("serial")
public class AnnotationCSSSelector extends AbstractAnnotationSelector implements Serializable {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

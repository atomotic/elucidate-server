package com.digirati.elucidate.web.converter.oa.annotation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import com.digirati.elucidate.common.model.annotation.oa.OAAnnotation;
import com.digirati.elucidate.model.JSONLDProfile;
import com.digirati.elucidate.model.JSONLDProfile.Format;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

@Component
public class JSONLDOAAnnotationMessageConverter extends AbstractOAAnnotationMessageConverter {

    private String[] defaultContexts;

    @Autowired
    public JSONLDOAAnnotationMessageConverter(@Value("${annotation.oa.contexts}") String[] defaultContexts) throws IOException {
        super(APPLICATION_JSON_LD);
        this.defaultContexts = defaultContexts;
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String getStringRepresentation(OAAnnotation oaAnnotation, MediaType contentType) throws Exception {
        Map<String, Object> jsonMap = oaAnnotation.getJsonMap();

        JSONLDProfile jsonLdProfile = getJsonLdProfile(contentType, defaultContexts);

        Format format = jsonLdProfile.getFormats().get(0);
        if (format.equals(Format.COMPACTED)) {
            jsonMap = JsonLdProcessor.compact(jsonMap, jsonLdProfile.getContexts(), jsonLdOptions);
        } else if (format.equals(Format.EXPANDED)) {
            List<Object> jsonList = JsonLdProcessor.expand(jsonMap, jsonLdOptions);
            jsonMap = (Map<String, Object>) jsonList.get(0);
        } else if (format.equals(Format.FLATTENED)) {
            jsonMap = (Map<String, Object>) JsonLdProcessor.flatten(jsonMap, jsonLdProfile.getContexts(), jsonLdOptions);
        } else {
            throw new HttpMediaTypeNotSupportedException(contentType, getSupportedMediaTypes());
        }

        return JsonUtils.toPrettyString(jsonMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected OAAnnotation getObjectRepresentation(String jsonStr, MediaType contentType) throws Exception {
        Map<String, Object> jsonMap = (Map<String, Object>) JsonUtils.fromString(jsonStr);
        List<Object> jsonList = (List<Object>) JsonLdProcessor.expand(jsonMap, jsonLdOptions);
        jsonMap = (Map<String, Object>) jsonList.get(0);

        OAAnnotation oaAnnotation = new OAAnnotation();
        oaAnnotation.setJsonMap(jsonMap);
        return oaAnnotation;
    }
}
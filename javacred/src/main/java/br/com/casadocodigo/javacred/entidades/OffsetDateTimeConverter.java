package br.com.casadocodigo.javacred.entidades;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Converter(autoApply = true)
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, Date> {
    @Override
    public Date convertToDatabaseColumn(OffsetDateTime attribute) {
        if(attribute == null) return null;

        return Date.from(attribute.toInstant());
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(Date dbData) {
        if(dbData == null) return null;

        return OffsetDateTime.from(dbData.toInstant().atOffset(ZoneOffset.UTC));
    }
}

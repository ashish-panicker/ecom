package com.example.authservice.security.id.generators;

import com.example.authservice.security.id.comp.SnowflakeComponent;
import com.example.authservice.security.id.providers.ApplicationContextProvider;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        // Access the bean from the application context
        SnowflakeComponent generator = ApplicationContextProvider.getBean(SnowflakeComponent.class);
        return generator.nextId();
    }
}

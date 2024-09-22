package org.xmlresolver;

import org.xmlresolver.spi.SchemeResolverManager;
import org.xmlresolver.spi.SchemeResolverProvider;

public class ExampleSchemeResolverProvider implements SchemeResolverProvider {
    @Override
    public SchemeResolverManager create() {
        return new ExampleSchemeResolver();
    }
}
